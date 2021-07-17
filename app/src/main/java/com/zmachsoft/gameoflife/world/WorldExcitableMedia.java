package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.zmachsoft.gameoflife.world.setting.ExcitableMediaSetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

import java.util.Random;

/**
 * Details in https://math.libretexts.org/Bookshelves/Scientific_Computing_Simulations_and_Modeling/Book%3A_Introduction_to_the_Modeling_and_Analysis_of_Complex_Systems_(Sayama)/11%3A_Cellular_Automata_I__Modeling/11.05%3A_Examples_of_Biological_Cellular_Automata_Models
 *
 * @author Master
 */
public class WorldExcitableMedia extends GameWorld {
    private static final double MINIMAL_EXCITATION_PROBABILITY = 0.03;
    private static final double MAXIMAL_EXCITATION_PROBABILITY = 0.95;
    private static final int STATUS_NORMAL = 0;
    private static final int STATUS_EXCITED = 1;

    private int[][] datas = null;
    private double affineA;
    private double affineB;
    private int[] greys;
    private int lastStatusOfRefractoryPeriod;
    private Random random = new Random(System.currentTimeMillis());

    public WorldExcitableMedia() {
        super(new ExcitableMediaSetting());
    }

    public WorldExcitableMedia(WorldSetting setting) {
        super(setting);
    }

    @Override
    public void initContent() {
        Log.i("GOL", "World init data");
        int nbCellsInitiallyExcited = 0;
        Random random = new Random(System.currentTimeMillis());

        datas = new int[setting.getNbTiles()][setting.getNbTiles()];
        for (int r = 0; r < setting.getNbTiles(); r++) {
            for (int c = 0; c < setting.getNbTiles(); c++) {
                if (random.nextDouble() <= MINIMAL_EXCITATION_PROBABILITY) {
                    this.datas[r][c] = STATUS_EXCITED;
                    nbCellsInitiallyExcited++;
                }
            }
        }

        // no excited cell ? Not an interesting game :)
        if (nbCellsInitiallyExcited == 0) {
            Log.e("GOL", "World init data error - no cell initially excited");
        }

        ExcitableMediaSetting emSetting = (ExcitableMediaSetting) setting;
        Log.i("GOL", "Setting / excitation probability : " + emSetting.getExcitationProbability());
        Log.i("GOL", "Setting / length of refractory period : " + emSetting.getLengthRefractoryPeriod());
        // probability of excitation is an affine function of the number of excited neighbours
        affineA = (emSetting.getExcitationProbability() - MINIMAL_EXCITATION_PROBABILITY) / (8 - 0);
        affineB = emSetting.getExcitationProbability() - affineA * 8;
        // prepare a grey color palette for all steps in refractory period
        prepareGreyColors(emSetting.getLengthRefractoryPeriod());

        // Refractory period starts at status = 2, then 3 ... until reach 2 + lengthRefractoryPeriod
        int lengthRefractoryPeriod = emSetting.getLengthRefractoryPeriod();
        this.lastStatusOfRefractoryPeriod = STATUS_EXCITED + lengthRefractoryPeriod;
        Log.i("GOL", "Last status of refractory period : " + this.lastStatusOfRefractoryPeriod);
    }

    private void prepareGreyColors(int lengthRefractoryPeriod) {
        greys = new int[lengthRefractoryPeriod];
        int step = 256 / lengthRefractoryPeriod;

        for (int i = 0; i < lengthRefractoryPeriod; i++) {
            int decimalGrey = 0 + (i + 1) * step;
            greys[i] = Color.rgb(decimalGrey, decimalGrey, decimalGrey);
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

                // What is this cell's status ?
                // apply the rules : https://math.libretexts.org/Bookshelves/Scientific_Computing_Simulations_and_Modeling/Book%3A_Introduction_to_the_Modeling_and_Analysis_of_Complex_Systems_(Sayama)/11%3A_Cellular_Automata_I__Modeling/11.05%3A_Examples_of_Biological_Cellular_Automata_Models
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
        // is current cell in refractory period ?
        if (datasClone[r][c] > STATUS_EXCITED) {
            if (datasClone[r][c] == lastStatusOfRefractoryPeriod) {
                // back to normal
                datasClone[r][c] = STATUS_NORMAL;
                return true;
            }

            // increment status until the refractory period is over
            datasClone[r][c]++;
            return true;
        }

        // already excited ? Then start the refractory period
        if (datasClone[r][c] == STATUS_EXCITED) {
            datasClone[r][c]++;
            return true;
        }

        // count the number of excited neighbours
        int nbExcitedCells = this.computeExcitedNeightbors(r, c);

        // compute probability for current cell to be excited
        double proba = affineA * nbExcitedCells + affineB;
        if (random.nextDouble() < proba) {
            // make the cell excited
            datasClone[r][c] = STATUS_EXCITED;
            return true;
        }

        // no change occurred
        return false;
    }

    private int computeExcitedNeightbors(int r, int c) {
        int nbExcitedCells = 0;
        int r1 = r > 0 ? r - 1 : r;
        int r2 = Math.min((r + 1), (setting.getNbTiles() - 1));
        int c1 = c > 0 ? c - 1 : c;
        int c2 = Math.min((c + 1), (setting.getNbTiles() - 1));
        for (int rp = r1; rp <= r2; rp++) {
            for (int cp = c1; cp <= c2; cp++) {
                if (rp == r && cp == c) continue; // ignore central cell
                if (this.datas[rp][cp] == STATUS_EXCITED) {
                    nbExcitedCells++;
                }
            }
        }
        return nbExcitedCells;
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
        int color = whichGrey(datas[r][c]);
        Paint paint = new Paint();
        paint.setColor(color);

        // what coordinates
        float top = topShift + r * setting.getTileSize();
        float bottom = top + setting.getTileSize();
        float left = leftShift + c * setting.getTileSize();
        float right = left + setting.getTileSize();
        canvas.drawRect(new RectF(left, top, right, bottom), paint);
    }

    private int whichGrey(int datum) {
        return datum == STATUS_NORMAL ? Color.WHITE :
                datum == STATUS_EXCITED ? Color.BLACK :
                        greys[datum - 2];
    }

    @Override
    public String toString() {
        return "World excitable media - id=" + uniqueId;
    }
}

