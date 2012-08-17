package org.ninehells.angrypipes;

//{//  import

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;

import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.Position;
import org.ninehells.angrypipes.Theme;

//}//

class BoardView extends SurfaceView
{
	BoardView (Context context, Board board)
	{//
		super(context);

		mBoard = board;

		Resources res  = context.getResources();
		mZoomLevels = res.getIntArray(R.array.zoom_levels);
		mZoomLevel = res.getInteger(R.integer.zoom_level);

		setWillNotDraw(false);

		mThemes[0] = new Theme();
		mThemes[0].background = 0xff000000;
		mThemes[0].grid       = 0xff303030;
		mThemes[0].solved     = 0xff00ff00;
		mThemes[0].badFill    = 0xffff4040;
		mThemes[0].filled     = 0xffffff40;
		mThemes[0].pipe       = 0xffffffff;
		mThemes[0].locked     = 0x20008000;
		mThemes[0].torus      = 0x40808080;
		mThemes[0].cursor     = 0xffff0000;

		mThemes[1] = new Theme();
		mThemes[1].background = 0xffffffff;
		mThemes[1].grid       = 0x20000000;
		mThemes[1].solved     = 0xff0099cc;
		mThemes[1].badFill    = 0xffcc0000;
		mThemes[1].filled     = 0xffff8800;
		mThemes[1].pipe       = 0xff000000;
		mThemes[1].locked     = 0x10000000;
		mThemes[1].torus      = 0x40000000;
		mThemes[1].cursor     = 0xffff0000;
	}//

	void zoomIn()
	{//
		if (mZoomLevel+1 < mZoomLevels.length) {
			++mZoomLevel;
			requestLayout();
		}
	}

	void zoomOut()
	{
		//TODO: don't if board already fits
		if (mZoomLevel > 0) {
			--mZoomLevel;
			requestLayout();
		}
	}//

	@Override
	public void onMeasure (int w, int h)
	{//
		int torus = mBoard.data().torus_mode ? 1 : 0;
		double scale = (mZoomLevels[mZoomLevel]*1.0)/100.0;
		setMeasuredDimension(
				(int)(scale*(2 + mCellSize*(2*torus + mBoard.data().width))),
				(int)(scale*(2 + mCellSize*(2*torus + mBoard.data().height)))
		);
	}//

	@Override
	public void computeScroll()
	{//
		mComputeScroll = true;
	}//

	@Override
	public boolean onTouchEvent (MotionEvent event)
	{//
		double scale = (mZoomLevels[mZoomLevel]*1.0)/100.0;
		int torus = mBoard.data().torus_mode ? 1 : 0;
		int i = -torus+ (int)(event.getX()/(scale*mCellSize));
		int j = -torus+ (int)(event.getY()/(scale*mCellSize));
		int W = mBoard.data().width;
		int H = mBoard.data().height;
		if (mBoard.data().torus_mode) {
			i = (i + W) % W;
			j = (j + H) % H;
		}
		else {
			if (i < 0) i = 0;  if (i >= W) i = W-1;
			if (j < 0) j = 0;  if (j >= H) j = H-1;
		}

		mMovePos.set(i, j);

		int longPressTimeoutMillis = 400;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mComputeScroll = false;
			mDownPos.set(i, j);
			mTimerHandler.postDelayed(mTimerTask, longPressTimeoutMillis);
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			mTimerHandler.removeCallbacks(mTimerTask);
			if (mDownPos.valid && event.getEventTime() - event.getDownTime() >= longPressTimeoutMillis) {
				mBoard.setCursor(i, j);
				if (!mComputeScroll && mBoard.toggleLock(mDownPos))
					invalidate();
			}
			else if (mDownPos.equals(i, j)) {
				if (mAutoRotate || mBoard.cursor().equals(mDownPos))
					mBoard.rotate(mDownPos);
				mBoard.setCursor(i, j);
				invalidate();
			}
			mDownPos.reset();
		}

		return true;
	}//

	@Override
	public void draw (Canvas canvas)
	{//
		canvas.save();
		double scale = (mZoomLevels[mZoomLevel]*1.0)/100.0;
		canvas.scale((float)scale, (float)scale);

		int w = mBoard.data().width;
		int h = mBoard.data().height;
		int torus = mBoard.data().torus_mode ? 1 : 0;

		Theme theme = mThemes[mBoard.settings().light_theme ? 1 : 0];

		Paint paint = new Paint();
		canvas.drawRGB(
				Color.red  (theme.background),
				Color.green(theme.background),
				Color.blue (theme.background)
		);

		Rect r = canvas.getClipBounds();
		int i0 = -torus+ r.left/mCellSize;
		int j0 = -torus+ r.top /mCellSize;
		int i1 = +torus+ Math.min(r.right /mCellSize, w-1);
		int j1 = +torus+ Math.min(r.bottom/mCellSize, h-1);

		w += 2*torus;
		h += 2*torus;

		paint.setColor(theme.grid);

		// grid
		for (int j = j0; j <= j1+1; ++j)
			canvas.drawLine(0, j*mCellSize, w*mCellSize-1, j*mCellSize, paint);
		for (int i = i0; i <= i1+1; ++i)
			canvas.drawLine(i*mCellSize, 0, i*mCellSize, h*mCellSize-1, paint);

		// border
		if (!mBoard.data().torus_mode) {
			canvas.drawLine(3, 3, w*mCellSize-3-1, 3, paint);
			canvas.drawLine(3, 3, 3, h*mCellSize-3-1, paint);
			canvas.drawLine(w*mCellSize-3-1, h*mCellSize-3-1, w*mCellSize-3-1, 3, paint);
			canvas.drawLine(w*mCellSize-3-1, h*mCellSize-3-1, 3, h*mCellSize-3-1, paint);
		}

		// pipes
		for (int j = j0; j <= j1; ++j)
		for (int i = i0; i <= i1; ++i) {
			float x0 = (i+torus)*mCellSize+mBorder,  y0 = (j+torus)*mCellSize+mBorder;
			float xc = x0+mSegmentSize,  yc = y0+mSegmentSize;
			float x1 = xc+mSegmentSize,  y1 = yc+mSegmentSize;

			/* set pipe color */
			if (mBoard.isSolved())
				paint.setColor(theme.solved);
			else if (mBoard.filled(i,j)) {
				if (mBoard.isBadFill())
					paint.setColor(theme.badFill);
				else
					paint.setColor(theme.filled);
			}
			else
				paint.setColor(theme.pipe);

			/* draw pipe */
			boolean simple = (scale < .5);
			if (!simple) canvas.drawCircle(xc, yc, 3, paint);
			if (mBoard.locked(i,j)) drawLock(xc, yc, simple, canvas, paint);
			if (mBoard.right(i,j)) drawSegment(xc, yc, x1, yc, simple, canvas, paint);
			if (mBoard.up   (i,j)) drawSegment(xc, yc, xc, y0, simple, canvas, paint);
			if (mBoard.left (i,j)) drawSegment(xc, yc, x0, yc, simple, canvas, paint);
			if (mBoard.down (i,j)) drawSegment(xc, yc, xc, y1, simple, canvas, paint);

			if (mBoard.moved(i,j) || mBoard.locked(i,j)) {
				paint.setColor(theme.locked);
				canvas.drawRect(x0, y0, x1, y1, paint);
			}

			/* torus border */
			if (i<0 || j<0 || i>=mBoard.data().width || j>=mBoard.data().height) {
				paint.setColor(theme.torus);
				canvas.drawRect(x0, y0, x1, y1, paint);
			}

			/* cursor */
			if (mBoard.cursor().equals(i, j)) {
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(theme.cursor);
				canvas.drawRect(x0+1, y0+1, x1-1, y1-1, paint);
				paint.setStyle(Paint.Style.FILL);
			}
		}

		canvas.restore();
	}//

	private void drawSegment (float x, float y, float x1, float y1, boolean simple, Canvas canvas, Paint paint)
	{//
		canvas.drawLine(x, y, x1, y1, paint);
		if (simple)
			return;

		paint.setAlpha(0x80);
		if (x == x1) {
			int d = y<y1 ? -1 : 1;
			canvas.drawLine(x-1, y, x-1, y1+d, paint);
			canvas.drawLine(x+1, y, x+1, y1+d, paint);
		}
		else if (y == y1) {
			int d = x<x1 ? -1 : 1;
			canvas.drawLine(x, y-1, x1+d, y-1, paint);
			canvas.drawLine(x, y+1, x1+d, y+1, paint);
		}
	}//

	private void drawLock (float x, float y, boolean simple, Canvas canvas, Paint paint)
	{//
		if (simple) return;
		paint.setAlpha(0x60);
		canvas.drawLine(x-5, y-5, x+5, y-5, paint);
		canvas.drawLine(x+5, y-5, x+5, y+5, paint);
		canvas.drawLine(x+5, y+5, x-5, y+5, paint);
		canvas.drawLine(x-5, y+5, x-5, y-5, paint);
	}//

	private Board mBoard = null;
	private Position mDownPos = new Position();
	private Position mMovePos = new Position();
	private boolean mAutoRotate = true;

	private int[] mZoomLevels;
	private int mZoomLevel;

	private final int mSegmentSize = 21;
	private final int mBorder = 1;
	private final int mCellSize = 2*mBorder+2*mSegmentSize;

	private Theme[] mThemes = new Theme[2];
	private boolean mComputeScroll = false;
	private Handler mTimerHandler = new Handler();
	private Runnable mTimerTask = new Runnable()
	{//
		public void run() {
			if (mMovePos.equals(mDownPos)) {
				if (!mComputeScroll && mBoard.toggleLock(mDownPos)) {
					mBoard.setCursor(mDownPos.i, mDownPos.j);
					invalidate();
				}
			}
			mDownPos.reset();
		}
	}//
	;
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
