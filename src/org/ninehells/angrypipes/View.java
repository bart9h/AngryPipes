package org.ninehells.angrypipes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import org.ninehells.angrypipes.Board;

class View extends SurfaceView
{
	public View (Context context, Board board)
	{
		super(context);

		mBoard = board;
		setWillNotDraw(false);
	}

	@Override
	public void onMeasure (int w, int h)
	{
		int torus = mBoard.config().torus_mode ? 1 : 0;
		setMeasuredDimension(
				mCellSize*(2*torus + mBoard.config().width),
				mCellSize*(2*torus + mBoard.config().height)
		);
	}

	@Override
	public boolean onTouchEvent (MotionEvent event)
	{
		int torus = mBoard.config().torus_mode ? 1 : 0;
		int i = -torus+ (int)event.getX()/mCellSize;
		int j = -torus+ (int)event.getY()/mCellSize;
		if (mBoard.config().torus_mode) {
			i = (i + mBoard.config().width ) % mBoard.config().width;
			j = (j + mBoard.config().height) % mBoard.config().height;
		}

		if (event.getAction() == event.ACTION_DOWN) {
			iDown = i;
			jDown = j;
		}
		else if (event.getAction() == event.ACTION_UP) {
			if (iDown == i && jDown == j) {
				if (mBoard.isSolved())
					mBoard.randomize();
				else
					mBoard.rotate(i, j);
				invalidate();
			}
			iDown = -1;
			jDown = -1;
		}

		return true;
	}

	public void draw (Canvas canvas)
	{
		int w = mBoard.config().width;
		int h = mBoard.config().height;
		int torus = mBoard.config().torus_mode ? 1 : 0;

		Paint paint = new Paint();
		canvas.drawRGB(0, 0, 0);

		Rect r = canvas.getClipBounds();
		int i0 = -torus+ r.left/mCellSize;
		int j0 = -torus+ r.top /mCellSize;
		int i1 = +torus+ Math.min(r.right /mCellSize, w-1);
		int j1 = +torus+ Math.min(r.bottom/mCellSize, h-1);

		w += 2*torus;
		h += 2*torus;

		paint.setARGB(0xff, 0x30, 0x30, 0x30);
		for (int j = j0; j <= j1; ++j)
			canvas.drawLine(0, j*mCellSize, w*mCellSize, j*mCellSize, paint);
		for (int i = i0; i <= i1;  ++i)
			canvas.drawLine(i*mCellSize, 0, i*mCellSize, h*mCellSize, paint);

		for (int j = j0; j <= j1; ++j)
		for (int i = i0; i <= i1; ++i) {
			float x = (i+torus)*mCellSize+mBorder+mSegmentSize;
			float y = (j+torus)*mCellSize+mBorder+mSegmentSize;
			if (mBoard.isSolved())
				paint.setARGB(0xff, 0x00, 0xff, 0x00);
			else
				paint.setARGB(0xff, 0xff, 0xff, mBoard.fixed(i,j)?0x40:0xff);
			canvas.drawCircle(x, y, 3, paint);
			if (mBoard.right(i,j)) drawSegment(x, y, x+mSegmentSize, y, canvas, paint);
			if (mBoard.up   (i,j)) drawSegment(x, y, x, y-mSegmentSize, canvas, paint);
			if (mBoard.left (i,j)) drawSegment(x, y, x-mSegmentSize, y, canvas, paint);
			if (mBoard.down (i,j)) drawSegment(x, y, x, y+mSegmentSize, canvas, paint);
			if (i<0 || j<0 || i>=mBoard.config().width || j>=mBoard.config().height) {
				paint.setARGB(0x40, 0x80, 0x80, 0x80);
				canvas.drawRect(x-mSegmentSize, y-mSegmentSize, x+mSegmentSize, y+mSegmentSize, paint);
			}
		}
	}

	private void drawSegment (float x, float y, float x1, float y1, Canvas canvas, Paint paint)
	{
		canvas.drawLine(x, y, x1, y1, paint);
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
	}


	private Board mBoard = null;
	private int iDown = -1, jDown = -1;

	private final int mSegmentSize = 21;
	private final int mBorder = 1;
	private final int mCellSize = 2*mBorder+2*mSegmentSize;
}

// vim600:fdm=syntax:fdn=2:nu:
