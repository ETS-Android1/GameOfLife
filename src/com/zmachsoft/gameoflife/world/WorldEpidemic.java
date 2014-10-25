package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.zmachsoft.gameoflife.world.setting.EpidemicSetting;
import com.zmachsoft.gameoflife.world.setting.WorldSetting;

/**
 * World with conway rules adapted to epidemic simulation.
 * 
 * @author Master
 *
 */
public class WorldEpidemic extends GameWorld 
{
	private int[][] datas = null;

	public WorldEpidemic()
	{
		super(new EpidemicSetting());
	}
	
	public WorldEpidemic(WorldSetting setting)
	{
		super(setting);
	}
	
	@Override
	public void initContent()
	{
		Log.i("GOL", "World init datas");
		double density = ((EpidemicSetting)setting).getDensity();
		datas = new int[setting.getNbTiles()][setting.getNbTiles()];
		for (int r = 0; r < setting.getNbTiles(); r++)
		{
			for (int c = 0; c < setting.getNbTiles(); c++)
			{
				double random = Math.random(); 
				// 10 : ill
				// 1 : ok
				// 0 : empty
				datas[r][c] = random >=density ? 0 : 1;
			}
		}
		
		// only a few are ill
		int nbSickPoints = ((EpidemicSetting)setting).getNbSickPoints();
		for (int nb=0;nb<nbSickPoints;nb++)
		{
			int r = getRandom(setting.getNbTiles());
			int c= getRandom(setting.getNbTiles());
			datas[r][c] = 10;
			Log.i("GOL", "Ill at " + r + "," + c);
		}
//		datas[0][0] = 10;
//		datas[0][1] = 10;
//		datas[1][0] = 10;
//		datas[1][1] = 10;
	}

	@Override
	public void nextStep() throws NoChangeException
	{
		Log.i("GD", "World next step");
		// copy the array in a temporary one (to be modified during algo application)
		int[][] datasClone = new int[setting.getNbTiles()][setting.getNbTiles()];
		for (int r = 0; r < setting.getNbTiles(); r++)
			// deep copy
			System.arraycopy(datas[r], 0, datasClone[r], 0, datas[r].length);

		// loop over all cells
		for (int r = 0; r < setting.getNbTiles(); r++)
		{
			for (int c = 0; c < setting.getNbTiles(); c++)
			{
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
	private void applyGameRule(int[][] datasClone, int r, int c)
	{
		// compute the number of neightbors ok and ill
		int[] neightbors = computeNeightbors(r, c);
		int nbOk = neightbors[0];
		int nbIll = neightbors[1];

		// -------------------------------------------------------
		// apply modified conway rules
		//
		// is it a dead cell ?
		if (datas[r][c] == 0)
		{
			// 3 alive neightbors OK and no ill ? So time to born.
//			if (nbOk == 3 && nbIll==0)
//			if ((nbOk-nbIll) == 3 || (nbOk-nbIll) == 4 || (nbOk-nbIll) == 5)
//			if (nbOk == 3 || (nbOk-nbIll) == 3)
			if (nbOk == 3)
//			if ((nbOk-nbIll) == 3)
				datasClone[r][c] = 1;
		}
		// or a live cell (ok or ill) ?
		else
		{
			// to few neightbors (ok or ill) ? Die
			if ((nbOk+nbIll) <= 1)
				datasClone[r][c] = 0;
//			// to many neightbors (ok or ill) ? Die
//			else if ((nbOk+nbIll) >= 5)
//				datasClone[r][c] = 0;
//			// to many ill neightbors ? Die
//			else if (nbIll >= 4)
//				datasClone[r][c] = 0;
			// OK cell
			else 
			if (datas[r][c] == 1)
			{
				if (nbIll>0)
				{
					// epidemic behavior : 10% to be ill at contact plus 5% per ill neightbor
					int epidemicFactor = 10 + nbIll*20;
					if (getRandom(100)<=epidemicFactor)
						datasClone[r][c] = 10;
				}
			}
			// Ill cell
			else
			{
				// some could be finally healed
				if (getRandom(100)<=20)
				{
					datasClone[r][c] = 1;
				}
				else
				{
					// chance to die increase by 5% at each turn ill
					int chanceToDie = (datas[r][c] - 10)*20;
					if (getRandom(100)<=chanceToDie)
					{
						datasClone[r][c] = 0;	// dead
					}
					else
						datasClone[r][c] = datas[r][c]+1;	// one more turn ill
				}
			}
		}		
	}
	
	private static int getRandom(int max)
	{
		return (int) (Math.random()*Double.valueOf(max));
	}

	/**
	 * Compute the number of neightbors of a given cell
	 * 
	 * @param r
	 * @param c
	 * @return array of two integers : the number of neighbors ok, the number of ill ones
	 */
	private int[] computeNeightbors(int r, int c)
	{
		int[] stats = new int[2];
		int nbOk = 0;
		int nbIll = 0;
		
		if (r > 0 && c > 0 && datas[r - 1][c - 1] >= 1)
		{
			if (datas[r - 1][c - 1] == 1)
				nbOk++;
			else if (datas[r - 1][c - 1] >= 10)
				nbIll++;
		}
			
		if (r > 0 && datas[r - 1][c] >= 1)
		{
			if (datas[r - 1][c] == 1)
				nbOk++;
			else if (datas[r - 1][c] >= 10)
				nbIll++;
		}
		if (r > 0 && c < (setting.getNbTiles()-1) && datas[r - 1][c + 1] >= 1)
		{
			if (datas[r - 1][c + 1] == 1)
				nbOk++;
			else if (datas[r - 1][c + 1] >= 10)
				nbIll++;
		}
		if (c < (setting.getNbTiles()-1) && datas[r][c + 1] >= 1)
		{
			if (datas[r][c + 1] == 1)
				nbOk++;
			else if (datas[r][c + 1] >= 10)
				nbIll++;
		}
		if (r < (setting.getNbTiles()-1) && c < (setting.getNbTiles()-1) && datas[r + 1][c + 1] >= 1)
		{
			if (datas[r + 1][c + 1] == 1)
				nbOk++;
			else if (datas[r + 1][c + 1] >= 10)
				nbIll++;
		}
		if (r < (setting.getNbTiles()-1) && datas[r + 1][c] >= 1)
		{
			if (datas[r + 1][c] == 1)
				nbOk++;
			else if (datas[r + 1][c] >= 10)
				nbIll++;
		}
		if (r < (setting.getNbTiles()-1) && c > 0 && datas[r + 1][c - 1] >= 1)
		{
			if (datas[r + 1][c - 1] == 1)
				nbOk++;
			else if (datas[r + 1][c - 1] >= 10)
				nbIll++;
		}
		if (c > 0 && datas[r][c - 1] >= 1)
		{
			if (datas[r][c - 1] == 1)
				nbOk++;
			else if (datas[r][c - 1] >= 10)
				nbIll++;
		}
		
		stats[0] = nbOk;
		stats[1] = nbIll;
		return stats;
	}

	@Override
	/**
	 * Render the world to an off-screen bitmap
	 */
	public void render(Canvas canvas)
	{
		Log.i("GD", "Render world");
		for (int r = 0; r < setting.getNbTiles(); r++)
		{
			for (int c = 0; c < setting.getNbTiles(); c++)
			{
				renderCell(r, c, canvas);
			}
		}
	}

	private void renderCell(int r, int c, Canvas canvas)
	{
		// what color to use
		int color = Color.GRAY;
		if (datas[r][c] == 1)
			color = Color.GREEN;
		else if (datas[r][c] >= 10)
		{
			int rank = datas[r][c]>20 ? 20 : datas[r][c];
			color = Color.rgb(255-(rank-10)*15, 0, 0);
		}
		Paint paint = new Paint();
		paint.setColor(color);

		// what coordinates
//		float top = r * setting.getTileSize() + 1;
//		float bottom = top + setting.getTileSize() - 1;
//		float left = c * setting.getTileSize() + 1;
//		float right = left + setting.getTileSize() - 1;
//		canvas.drawRoundRect(new RectF(left, top, right, bottom), 2, 2, paint);
		
		float top = r * setting.getTileSize() ;
		float bottom = top + setting.getTileSize() ;
		float left = c * setting.getTileSize();
		float right = left + setting.getTileSize() ;
		canvas.drawRect(new RectF(left, top, right, bottom), paint);
	}
	
	@Override
	public String toString()
	{
		return "World epidemic - id=" + uniqueId;
	}
}

