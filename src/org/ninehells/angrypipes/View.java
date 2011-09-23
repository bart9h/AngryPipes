package org.ninehells.angrypipes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

import org.ninehells.angrypipes.Board;

class View extends SurfaceView
{
	public Board board;

	public View(Context context)
	{
		super(context);
		setWillNotDraw(false);
	}

	public void draw(Canvas canvas)
	{
		Paint paint = new Paint();
		canvas.drawRGB(0, 0, 0);

		int segmentSize = 12;
		int border = 2;
		int cellSize = 2*border+2*segmentSize;

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
}
