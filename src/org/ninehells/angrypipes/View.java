package org.ninehells.angrypipes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import org.ninehells.angrypipes.Board;

class View extends SurfaceView
{
	public View(Context context)
	{
		super(context);
		setWillNotDraw(false);
	}

	public void setBoard (Board board)
	{
		mBoard = board;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int i = (int)event.getX()/mCellSize;
		int j = (int)event.getY()/mCellSize;

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

	public void draw(Canvas canvas)
	{
		Paint paint = new Paint();
		canvas.drawRGB(0, 0, 0);

		paint.setARGB(0xff, 0x40, 0x40, 0x40);
		for (int j = 0; j <= mBoard.height(); ++j)
			canvas.drawLine(0, j*mCellSize, mBoard.width()*mCellSize, j*mCellSize, paint);
		for (int i = 0; i <= mBoard.width();  ++i)
			canvas.drawLine(i*mCellSize, 0, i*mCellSize, mBoard.height()*mCellSize, paint);

		for (int j = 0; j < mBoard.height(); ++j)
		for (int i = 0; i < mBoard.width();  ++i) {
			int x = i*mCellSize+mBorder+mSegmentSize;
			int y = j*mCellSize+mBorder+mSegmentSize;
			if (mBoard.isSolved())
				paint.setARGB(0xff, 0x00, 0xff, 0x00);
			else
				paint.setARGB(0xff, 0xff, 0xff, mBoard.fixed(i,j)?0xa0:0xff);
			canvas.drawCircle(x, y, 3, paint);
			if (mBoard.right(i,j)) drawSegment(x, y, x+mSegmentSize, y, canvas, paint);
			if (mBoard.up   (i,j)) drawSegment(x, y, x, y-mSegmentSize, canvas, paint);
			if (mBoard.left (i,j)) drawSegment(x, y, x-mSegmentSize, y, canvas, paint);
			if (mBoard.down (i,j)) drawSegment(x, y, x, y+mSegmentSize, canvas, paint);
		}
	}

	public void drawSegment(int x, int y, int x1, int y1, Canvas canvas, Paint paint)
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
