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
	public Board board;

	private int segmentSize = 15;
	private int border = 1;
	private int cellSize = 2*border+2*segmentSize;
	private int iDown = -1, jDown = -1;

	private boolean growing = true;

	public View(Context context)
	{
		super(context);
		setWillNotDraw(false);
	}

	public void drawSegment(int x, int y, int x1, int y1, Canvas canvas, Paint paint)
	{
		canvas.drawLine(x, y, x1, y1, paint);
		paint.setARGB(0xff, 0xa0, 0xa0, 0xa0);
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

	public void draw(Canvas canvas)
	{
		Paint paint = new Paint();
		canvas.drawRGB(0, 0, 0);

		paint.setARGB(0xff, 0x40, 0x40, 0x40);
		for (int j = 0; j <= board.height(); ++j)
			canvas.drawLine(0, j*cellSize, board.width()*cellSize, j*cellSize, paint);
		for (int i = 0; i <= board.width();  ++i)
			canvas.drawLine(i*cellSize, 0, i*cellSize, board.height()*cellSize, paint);

		for (int j = 0; j < board.height(); ++j)
		for (int i = 0; i < board.width();  ++i) {
			int x = i*cellSize+border+segmentSize;
			int y = j*cellSize+border+segmentSize;
			paint.setARGB(0xff, 0xff, 0xff, 0xff);
			canvas.drawCircle(x, y, 3, paint);
			if (board.right(i,j)) drawSegment(x, y, x+segmentSize, y, canvas, paint);
			if (board.up   (i,j)) drawSegment(x, y, x, y-segmentSize, canvas, paint);
			if (board.left (i,j)) drawSegment(x, y, x-segmentSize, y, canvas, paint);
			if (board.down (i,j)) drawSegment(x, y, x, y+segmentSize, canvas, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int i = (int)event.getX()/cellSize;
		int j = (int)event.getY()/cellSize;

		if (event.getAction() == event.ACTION_DOWN) {
			iDown = i;
			jDown = j;
		}
		else if (event.getAction() == event.ACTION_UP) {
			if (iDown == i && jDown == j) {
				board.rotate(i, j);
				invalidate();
			}
			iDown = -1;
			jDown = -1;
		}

		return true;
	}
}

// vim600:fdm=syntax:fdn=2:
