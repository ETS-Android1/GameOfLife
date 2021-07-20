package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.zmachsoft.gameoflife.world.setting.BoidsSetting;
import com.zmachsoft.gameoflife.world.setting.ExcitableMediaSetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

import java.util.Random;

/**
 * Details in
 *
 * @author Master
 */
public class WorldBoids extends GameWorld {

    private int[][] datas = null;
    private Random random = new Random(System.currentTimeMillis());

    public WorldBoids() {
        super(new ExcitableMediaSetting());
    }

    public WorldBoids(WorldSetting setting) {
        super(setting);
    }

    @Override
    public void initContent() {
        Log.i("GOL", "World init data");
        Log.i("GOL", "Nb of boids : " + ((BoidsSetting) setting).getNbBoids());

        Random random = new Random(System.currentTimeMillis());
        datas = new int[setting.getNbTiles()][setting.getNbTiles()];
        for (int r = 0; r < setting.getNbTiles(); r++) {
            for (int c = 0; c < setting.getNbTiles(); c++) {
//                if (random.nextDouble() <= MINIMAL_EXCITATION_PROBABILITY) {
//                    this.datas[r][c] = STATUS_EXCITED;
//                    nbCellsInitiallyExcited++;
//                }
            }
        }
    }

    @Override
    public void nextStep() throws NoChangeException {
        Log.i("GD", "World next step");

        // we must init a new data array to store datas for the next step.
        // We use current to do the computations.
        boolean changeOccured = false;
        // copy the array in a temporary one (to be modified during algo application)
        int[][] datasClone = new int[setting.getNbTiles()][setting.getNbTiles()];
        for (int r = 0; r < setting.getNbTiles(); r++)
            System.arraycopy(datas[r], 0, datasClone[r], 0, datas[r].length);

        for (int r = 0; r < this.setting.getNbTiles(); r++) {
            for (int c = 0; c < this.setting.getNbTiles(); c++) {
                datasClone[r][c] = this.datas[r][c];

                // apply the rules :
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
     * Modify a given cell in datasClone applying game rules
     *
     * @return true if some change occured
     */
    private boolean applyGameRule(int[][] datasClone, int r, int c) {
//        // is current cell in refractory period ?
//        if (datasClone[r][c] > STATUS_EXCITED) {
//            if (datasClone[r][c] == lastStatusOfRefractoryPeriod) {
//                // back to normal
//                datasClone[r][c] = STATUS_NORMAL;
//                return true;
//            }
//
//            // increment status until the refractory period is over
//            datasClone[r][c]++;
//            return true;
//        }
//
//        // already excited ? Then start the refractory period
//        if (datasClone[r][c] == STATUS_EXCITED) {
//            datasClone[r][c]++;
//            return true;
//        }
//
//        // count the number of excited neighbours
//        int nbExcitedCells = this.computeExcitedNeightbors(r, c);
//
//        // compute probability for current cell to be excited
//        double proba = affineA * nbExcitedCells + affineB;
//        if (random.nextDouble() < proba) {
//            // make the cell excited
//            datasClone[r][c] = STATUS_EXCITED;
//            return true;
//        }

        // no change occurred
        return false;
    }

//    private int computeExcitedNeightbors(int r, int c) {
//        int nbExcitedCells = 0;
//        int r1 = r > 0 ? r - 1 : r;
//        int r2 = Math.min((r + 1), (setting.getNbTiles() - 1));
//        int c1 = c > 0 ? c - 1 : c;
//        int c2 = Math.min((c + 1), (setting.getNbTiles() - 1));
//        for (int rp = r1; rp <= r2; rp++) {
//            for (int cp = c1; cp <= c2; cp++) {
//                if (rp == r && cp == c) continue; // ignore central cell
//                if (this.datas[rp][cp] == STATUS_EXCITED) {
//                    nbExcitedCells++;
//                }
//            }
//        }
//        return nbExcitedCells;
//    }

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
        int color = allColors[random.nextInt(8)];
        Paint paint = new Paint();
        paint.setColor(color);

        // what coordinates
        float top = topShift + r * setting.getTileSize();
        float bottom = top + setting.getTileSize();
        float left = leftShift + c * setting.getTileSize();
        float right = left + setting.getTileSize();
        canvas.drawRect(new RectF(left, top, right, bottom), paint);
    }

    @Override
    public String toString() {
        return "Boids media - id=" + uniqueId;
    }
}

