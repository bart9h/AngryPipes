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

	private int segmentSize = 12;
	private int border = 2;
	private int cellSize = 2*border+2*segmentSize;
	private int iDown = -1, jDown = -1;

	public View(Context context)
	{
		super(context);
		setWillNotDraw(false);
	}

	public void draw(Canvas canvas)
	{
		Paint paint = new Paint();
		canvas.drawRGB(0, 0, 0);

		paint.setARGB(0x60, 0x60, 0x60, 0x60);
		for (int j = 0; j <= board.height(); ++j)
			canvas.drawLine(0, j*cellSize, board.width()*cellSize, j*cellSize, paint);
		for (int i = 0; i <= board.width();  ++i)
			canvas.drawLine(i*cellSize, 0, i*cellSize, board.height()*cellSize, paint);

		paint.setARGB(0xff, 0xff, 0xff, 0xff);
		for (int j = 0; j < board.height(); ++j)
		for (int i = 0; i < board.width();  ++i) {
			int x = i*cellSize+border+segmentSize;
			int y = j*cellSize+border+segmentSize;
			if (board.right(i,j)) canvas.drawLine(x, y, x+segmentSize, y, paint);
			if (board.up   (i,j)) canvas.drawLine(x, y, x, y-segmentSize, paint);
			if (board.left (i,j)) canvas.drawLine(x, y, x-segmentSize, y, paint);
			if (board.down (i,j)) canvas.drawLine(x, y, x, y+segmentSize, paint);
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
