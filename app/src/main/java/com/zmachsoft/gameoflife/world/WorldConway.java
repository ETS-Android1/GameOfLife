package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.zmachsoft.gameoflife.world.setting.ConwaySetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

/**
 * World with classical conway rules.
 *
 * @author Master
 */
public class WorldConway extends GameWorld {
    private int[][] datas = null;

    public WorldConway() {
        super(new ConwaySetting());
    }

    public WorldConway(WorldSetting setting) {
        super(setting);
    }

    @Override
    public void initContent() {
        Log.i("GOL", "World init datas");
        double density = Double.valueOf(((ConwaySetting) setting).getDensity()) / 100.0;
        datas = new int[setting.getNbTiles()][setting.getNbTiles()];
        for (int r = 0; r < setting.getNbTiles(); r++) {
            for (int c = 0; c < setting.getNbTiles(); c++) {
                datas[r][c] = Math.random() >= density ? 0 : 1;
            }
        }
    }

    @Override
    public void nextStep() throws NoChangeException {
        Log.i("GOL", "World next step");
        // copy the array in a temporary one (to be modified during algo application)
        int[][] datasClone = new int[setting.getNbTiles()][setting.getNbTiles()];
        for (int r = 0; r < setting.getNbTiles(); r++)
            System.arraycopy(datas[r], 0, datasClone[r], 0, datas[r].length);

        // loop over all cells
        for (int r = 0; r < setting.getNbTiles(); r++) {
            for (int c = 0; c < setting.getNbTiles(); c++) {
                // call for modification
                applyGameRule(datasClone, r, c);
            }
        }

        // finally assign the modified array to replace the global one
        datas = datasClone;
    }

    /**
     * Modify a given cell in datasClone applying game rules
     *
     * @param datasClone
     * @param r
     * @param c
     */
    private void applyGameRule(int[][] datasClone, int r, int c) {
//		String buf = "";
        // compute the number of neightbors
        int aliveNeightbors = computeNeightbors(r, c);
//		if (r<=1 && c<=3)
//			buf += r + "," + c + " --> " + aliveNeightbors;

        // -------------------------------------------------------
        // apply conway rules
        //
        // is it a dead cell ?
        if (datas[r][c] == 0) {
            // 3 alive neightbors ? So time to born.
            if (aliveNeightbors == 3)
                datasClone[r][c] = 1;
            else
                datasClone[r][c] = 0;
        }
        // or a live cell ?
        else {
            // to few neightbors ? Die
            if (aliveNeightbors <= 1)
                datasClone[r][c] = 0;
                // to many neightbors ? Die
            else if (aliveNeightbors >= 4)
                datasClone[r][c] = 0;
                // everything's fine.
            else
                datasClone[r][c] = 1;
        }

//		if (r<=1 && c<=3)
//			Log.i("GD", buf + " --> " + datasClone[r][c]);
    }

    /**
     * Compute the number of neightbors of a given cell
     *
     * @param r
     * @param c
     * @return
     */
    private int computeNeightbors(int r, int c) {
        int nb = 0;

        if (r > 0 && c > 0 && datas[r - 1][c - 1] == 1)
            nb++;
        if (r > 0 && datas[r - 1][c] == 1)
            nb++;
        if (r > 0 && c < (setting.getNbTiles() - 1) && datas[r - 1][c + 1] == 1)
            nb++;
        if (c < (setting.getNbTiles() - 1) && datas[r][c + 1] == 1)
            nb++;
        if (r < (setting.getNbTiles() - 1) && c < (setting.getNbTiles() - 1) && datas[r + 1][c + 1] == 1)
            nb++;
        if (r < (setting.getNbTiles() - 1) && datas[r + 1][c] == 1)
            nb++;
        if (r < (setting.getNbTiles() - 1) && c > 0 && datas[r + 1][c - 1] == 1)
            nb++;
        if (c > 0 && datas[r][c - 1] == 1)
            nb++;
        return nb;
    }

    @Override
    /**
     * Render the world to an off-screen bitmap
     */
    public void render(Canvas canvas) {
        Log.i("GD", "Render world Conway with " + setting.getNbTiles() + " tiles of size " + setting.getTileSize());

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
        int color = datas[r][c] == 1 ? Color.rgb(230, 20, 20) : Color.GRAY;
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
        return "World Conway - id=" + uniqueId;
    }
}

