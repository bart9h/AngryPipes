package org.ninehells.angrypipes;

//{//  import

import android.content.Context;
import android.view.SurfaceView;

//}//

class ZoomView extends SurfaceView
{
	ZoomView (Context context)
	{//
		super(context);
		mScroller = new Scroller(context);
		setWillNotDraw(false);
	}//

	private:
	Scroller mScroller;
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
