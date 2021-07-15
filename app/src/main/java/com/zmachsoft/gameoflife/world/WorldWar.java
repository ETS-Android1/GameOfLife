package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.zmachsoft.gameoflife.world.setting.WarSetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

/**
 * World with war rules about population conflicts.
 * Two populations filling empty spaces.
 * Conflict are simple : a cell of population 1 is left alive if the maximum number of neightbors is of type 1. Or it dies.
 *
 * @author Master
 */
public class WorldWar extends GameWorld {
    private int[][] datas = null;

    public WorldWar() {
        super(new WarSetting());
    }

    public WorldWar(WorldSetting setting) {
        super(setting);
    }

    @Override
    public void initContent() {
        Log.i("GOL", "World init datas");
        datas = new int[setting.getNbTiles()][setting.getNbTiles()];
        for (int r = 0; r < setting.getNbTiles(); r++)
            for (int c = 0; c < setting.getNbTiles(); c++)
                this.datas[r][c] = randomPopulation(true);
    }

    /**
     * @return a random value of a population
     */
    private int randomPopulation(boolean includeNone) {
        int value = 0;
        int nbArmy = ((WarSetting) setting).getNbArmy();
        if (includeNone)
            value = (int) (Math.random() * Double.valueOf(nbArmy + 1));        // 0 <= value < 4
        else
            value = (int) (Math.random() * Double.valueOf(nbArmy)) + 1;    // 1 <= value < 4
        return value;
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
     * Modify a given cell in datasClone applying game rules
     *
     * @param datasClone
     * @param r
     * @param c
     * @return true if some change occured
     */
    private boolean applyGameRule(int[][] datasClone, int r, int c) {
        WarSetting wSetting = (WarSetting) setting;
        double occupationProbability = Double.valueOf(wSetting.getEmptyCellOccupationMode()) / 100.0;
        double dyingProbability = Double.valueOf(wSetting.getOccupiedCellDyingMode()) / 100.0;

        int nbArmy = wSetting.getNbArmy();

        // count the percentage of people of different community
        int[] neighborhood = this.computeNeightbors(r, c);

        int totalNeighbors = neighborhood[nbArmy + 1];
        int totalNeighborsOccupied = 0;
        for (int i = 1; i <= nbArmy; i++)
            totalNeighborsOccupied += neighborhood[i];
        int friends = 0;
        int emptyCells = neighborhood[0];
        if (this.datas[r][c] != 0) {
            for (int i = 1; i <= nbArmy; i++)
                if (i == this.datas[r][c]) {
                    friends += neighborhood[i];
                    break;
                }
        }

        // find the two most numerous armies
        int indexMax = 0, indexSecond = 0;
        int max = 0, maxSecond = 0;
        for (int i = 1; i <= nbArmy; i++) {
            if (neighborhood[i] > max) {
                max = neighborhood[i];
                indexMax = i;
            } else if (neighborhood[i] > maxSecond) {
                maxSecond = neighborhood[i];
                indexSecond = i;
            }
        }

        // is it an empty cell ?
        if (this.datas[r][c] == 0) {
            // the same number ? random occupation.
            if ((max - maxSecond) == 0) {
                // random occupation mode
                if (Math.random() > occupationProbability)
                    datasClone[r][c] = indexMax;
                return true;
            }
            // only one is missing between the two most numerous, then get it
            else if ((max - maxSecond) == 1) {
                datasClone[r][c] = indexSecond;
                return true;
            }
            // max is the most numerous - if some ennemies around, then enforce the position
            else if (maxSecond > 0) {
                datasClone[r][c] = indexMax;
                return true;
            }
        }
        // or an occupied cell ?
        else if (this.datas[r][c] > 0) {
            // add current cell to top armies
            if (indexMax == this.datas[r][c])
                max++;
            else if (indexSecond == this.datas[r][c])
                maxSecond++;

            // maybe we should swap top and topSecond now
            if (maxSecond > max) {
                int tmpIndex = indexSecond;
                int tempMax = maxSecond;
                indexSecond = indexMax;
                maxSecond = max;
                indexMax = tmpIndex;
                max = tempMax;
            }

            // resolve conflict (expect some ennemies around)
            if (totalNeighborsOccupied != friends && totalNeighborsOccupied > 0) {
                // if most numerous army is not friend, then die
                if (indexMax != this.datas[r][c] && (indexSecond != this.datas[r][c] || max > maxSecond)) {
                    datasClone[r][c] = indexMax;
                    return true;
                }
                // the same number - random die
                else if ((indexMax != this.datas[r][c] || indexSecond == this.datas[r][c]) && max == maxSecond) {
                    // random die ?
                    if (Math.random() > dyingProbability)
                        datasClone[r][c] = 0;
//					if (wSetting.getSameNumberRandomDie()==WarSetting.EQUALITY_DIE_RANDOM)
//						datasClone[r][c] = Math.random()>0.5 ? indexSecond : indexMax;
//					// always die ?
//					else if (wSetting.getSameNumberRandomDie()==WarSetting.EQUALITY_DIE_ALWAYS)
//						datasClone[r][c] = 0;
                    // else don't die <==> do nothing
                    return true;
                }
            }
            // no ennemy - could release the unit
            else if (totalNeighborsOccupied == friends) {
                datasClone[r][c] = 0;
                return true;
            }
            // no neightbors at all --> release the unit
            else if (totalNeighborsOccupied == 0) {
                datasClone[r][c] = 0;
                return true;
            }
            // so lonely
            else {
                datasClone[r][c] = 0;
                return true;
            }
        }

        // no change occured
        return false;
    }

    /**
     * Count neightbors of a cell
     * 0 --> nb empty cells
     * 1, 2, ..., nbArmy --> nb of each army
     * nbArmy+1 --> total nb of neightbors
     *
     * @param r
     * @param c
     * @return
     */
    private int[] computeNeightbors(int r, int c) {
        int nbArmy = ((WarSetting) setting).getNbArmy();
        int[] stats = new int[nbArmy + 2];    // one more for the empty cells / one more for the total number of neightbor cells
        int r1 = r > 0 ? r - 1 : r;
        int r2 = (r + 1) < (setting.getNbTiles() - 1) ? r + 1 : setting.getNbTiles() - 1;
        int c1 = c > 0 ? c - 1 : c;
        int c2 = (c + 1) < (setting.getNbTiles() - 1) ? c + 1 : setting.getNbTiles() - 1;
        for (int rp = r1; rp <= r2; rp++) {
            for (int cp = c1; cp <= c2; cp++) {
                if (rp == r && cp == c) continue;
                // empty cell ?
                if (this.datas[rp][cp] == 0)
                    stats[0]++;
                    // increase this kind of population (empty:0, population1, ...)
                else
                    stats[this.datas[rp][cp]]++;
                stats[nbArmy + 1]++;
            }
        }
        return stats;
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
        return "World war - id=" + uniqueId;
    }
}

