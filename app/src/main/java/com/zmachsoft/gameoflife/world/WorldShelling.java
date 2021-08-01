package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.zmachsoft.gameoflife.world.setting.ShellingSetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

/**
 * World with Shelling rules about population natural segregational behaviour.
 * <p>
 * <p>
 * http://www.nymphomath.ch/pj/automates/chapitre12.pdf
 * <p>
 * <p>
 * 2-dimensional array with values :
 * > 0 = dead
 * > 1,2,3 = different people
 *
 * @author Master
 */
public class WorldShelling extends GameWorld {
    private int[][] datas = null;
    private int nbAlive = 0;


    public WorldShelling() {
        super(new ShellingSetting());
    }

    public WorldShelling(WorldSetting setting) {
        super(setting);
    }

    @Override
    public void initContent() {
        Log.i("GOL", "World init datas");
        datas = new int[setting.getNbTiles()][setting.getNbTiles()];
        for (int r = 0; r < setting.getNbTiles(); r++) {
            for (int c = 0; c < setting.getNbTiles(); c++) {
                this.datas[r][c] = randomPopulation(true);
            }
        }
        countAlive();
    }

    /**
     * @return a random value of a population
     */
    private int randomPopulation(boolean includeNone) {
        int value = 0;
        int nbCommunities = ((ShellingSetting) setting).getNbCommunities();
        if (includeNone)
            value = (int) (Math.random() * Double.valueOf(nbCommunities + 1));        // 0 <= value < 4
        else
            value = (int) (Math.random() * Double.valueOf(nbCommunities)) + 1;    // 1 <= value < 4
        return value;
    }

    @Override
    public void nextStep() throws NoChangeException {
        Log.i("GOL", "World next step");

        // we must init a new data array to store datas for the next step.
        // We use current to do the computations.
        boolean changeOccured = false;
        // copy the array in a temporary one (to be modified during algo application)
        int[][] datasClone = new int[setting.getNbTiles()][setting.getNbTiles()];
        for (int r = 0; r < setting.getNbTiles(); r++)
            System.arraycopy(datas[r], 0, datasClone[r], 0, datas[r].length);

        for (int r = 0; r < this.setting.getNbTiles(); r++) {
            for (int c = 0; c < this.setting.getNbTiles(); c++) {
                // Does this data could live or not ? Apply rule.
                // For rules, see : http://en.wikipedia.org/wiki/Conway's_Game_of_Life
                datasClone[r][c] = this.datas[r][c];

                // apply the rules
                boolean changeForThisCell = this.applyGameRule(datasClone, r, c);
                changeOccured = changeOccured || changeForThisCell;
            }
        }

        // finally assign the modified array to replace the global one
        datas = datasClone;

        // no change ? Stop the simulation
        if (!changeOccured)
            throw new NoChangeException();
    }

    /**
     * Count the number of alive cells
     */
    private void countAlive() {
        this.nbAlive = 0;
        for (int r = 0; r < this.setting.getNbTiles(); r++)
            for (int c = 0; c < this.setting.getNbTiles(); c++)
                if (this.datas[r][c] > 0)
                    this.nbAlive++;
    }

    /**
     * Modify a given cell in datasClone applying game rules
     *
     * @param datasClone
     * @param r
     * @param c
     * @return true if some change occured
     */
    private boolean applyGameRule(int[][] datasClone, int r, int c) {
        // adjust this.nbAlive according to settlement or move
        float settledPercent = Float.valueOf(nbAlive) / Float.valueOf(setting.getNbTiles() * setting.getNbTiles());

        // is it an empty cell ?
        if (this.datas[r][c] == 0 && settledPercent < 0.99) {
            // random occupation
//			double value = Math.random();
//			datasClone[r][c] = value>0.66 ? 3 : value>0.33 ? 2 : 1;
            datasClone[r][c] = randomPopulation(false);

            // increment the total population
            this.nbAlive++;

            // a change occured
            return true;
        }
        // or an occupied cell ?
        else if (this.datas[r][c] > 0) {
            // count the percentage of people of different community
            float percentForeigners = this.computeNeightbors(r, c);

            // more than 70%, then move away
            if (percentForeigners > 0.70) {
                datasClone[r][c] = 0;

                // decrement the total population
                this.nbAlive--;

                // a change occured
                return true;
            }
        }

        // no change occured
        return false;
    }

    /**
     * Count non community neightbors percent of a cell
     *
     * @param r
     * @param c
     * @return
     */
    private float computeNeightbors(int r, int c) {
        int community = this.datas[r][c];
        int nbForeigners = 0;
        int nbNeightbors = 0;
        int r1 = r > 0 ? r - 1 : r;
        int r2 = (r + 1) < (setting.getNbTiles() - 1) ? r + 1 : setting.getNbTiles() - 1;
        int c1 = c > 0 ? c - 1 : c;
        int c2 = (c + 1) < (setting.getNbTiles() - 1) ? c + 1 : setting.getNbTiles() - 1;
        for (int rp = r1; rp <= r2; rp++) {
            for (int cp = c1; cp <= c2; cp++) {
                if (rp == r && cp == c) continue;
                if (this.datas[rp][cp] == 0) continue;

                if (this.datas[rp][cp] != community)
                    nbForeigners++;
                nbNeightbors++;
            }
        }
        return Float.valueOf(nbForeigners) / Float.valueOf(nbNeightbors);
    }

    @Override
    /**
     * Render the world to an off-screen bitmap
     */
    public void render(Canvas canvas) {
        Log.i("GD", "Surface onDraw");
        // compute shifts to center the drawing
        int leftShift = (getBoardWidth() - setting.getNbTiles() * setting.getTileSize()) / 2;
        int topShift = (getBoardheight() - setting.getNbTiles() * setting.getTileSize()) / 2;

        for (int r = 0; r < setting.getNbTiles(); r++) {
            for (int c = 0; c < setting.getNbTiles(); c++) {
                renderCell(r, c, canvas, leftShift, topShift);
            }
        }
    }

    private void renderCell(int r, int c, Canvas canvas, int leftShift, int topShift) {
        // what color to use
//		int color = datas[r][c] == 1 ? Color.RED : datas[r][c] == 2 ? Color.GREEN : datas[r][c] == 3 ? Color.BLUE : Color.GRAY;
        int color = allColors[datas[r][c]];
        Paint paint = new Paint();
        paint.setColor(color);

        // what coordinates
//		float top = r * setting.getTileSize() + 1;
//		float bottom = top + setting.getTileSize() - 1;
//		float left = c * setting.getTileSize() + 1;
//		float right = left + setting.getTileSize() - 1;
//		canvas.drawRoundRect(new RectF(left, top, right, bottom), 2, 2, paint);

        float top = topShift + r * setting.getTileSize();
        float bottom = top + setting.getTileSize();
        float left = leftShift + c * setting.getTileSize();
        float right = left + setting.getTileSize();
        canvas.drawRect(new RectF(left, top, right, bottom), paint);
    }

    @Override
    public String toString() {
        return "World Shelling - id=" + uniqueId;
    }
}

