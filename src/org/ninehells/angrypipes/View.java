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

	public View(Context context)
	{
		super(context);
		setWillNotDraw(false);
	}

	public void setBoard (Board board)
	{
		m_board = board;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int i = (int)event.getX()/m_cellSize;
		int j = (int)event.getY()/m_cellSize;

		if (event.getAction() == event.ACTION_DOWN) {
			iDown = i;
			jDown = j;
		}
		else if (event.getAction() == event.ACTION_UP) {
			if (iDown == i && jDown == j) {
				m_board.rotate(i, j);
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
		for (int j = 0; j <= m_board.height(); ++j)
			canvas.drawLine(0, j*m_cellSize, m_board.width()*m_cellSize, j*m_cellSize, paint);
		for (int i = 0; i <= m_board.width();  ++i)
			canvas.drawLine(i*m_cellSize, 0, i*m_cellSize, m_board.height()*m_cellSize, paint);

		for (int j = 0; j < m_board.height(); ++j)
		for (int i = 0; i < m_board.width();  ++i) {
			int x = i*m_cellSize+m_border+m_segmentSize;
			int y = j*m_cellSize+m_border+m_segmentSize;
			paint.setARGB(0xff, 0xff, 0xff, m_board.fixed(i,j)?0xa0:0xff);
			canvas.drawCircle(x, y, 3, paint);
			if (m_board.right(i,j)) drawSegment(x, y, x+m_segmentSize, y, canvas, paint);
			if (m_board.up   (i,j)) drawSegment(x, y, x, y-m_segmentSize, canvas, paint);
			if (m_board.left (i,j)) drawSegment(x, y, x-m_segmentSize, y, canvas, paint);
			if (m_board.down (i,j)) drawSegment(x, y, x, y+m_segmentSize, canvas, paint);
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


	private Board m_board = null;
	private int iDown = -1, jDown = -1;

	private final int m_segmentSize = 15;
	private final int m_border = 1;
	private final int m_cellSize = 2*m_border+2*m_segmentSize;
}

// vim600:fdm=syntax:fdn=2:
