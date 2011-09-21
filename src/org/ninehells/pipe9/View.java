package org.ninehells.pipe9;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

class View extends SurfaceView
{
	public View(Context context)
	{
		super(context);
		setWillNotDraw(false);
	}

	public void draw(Canvas canvas)
	{
		Paint paint = new Paint();
		canvas.drawRGB(0, 0, 0);
		paint.setARGB(0xff, 0xff, 0xff, 0xff);
		canvas.drawLine(0, 0, 100, 300, paint);
		canvas.drawLine(0, 300, 100, 0, paint);
	}
}
