package org.ninehells.angrypipes;

import android.content.Context;
import android.content.res.Resources;

public class Config
{
	public int width;
	public int height;
	boolean torus_mode;

	Config (Context context)
	{
		Resources res = context.getResources();
		width  = res.getInteger(R.integer.medium_width);
		height = res.getInteger(R.integer.medium_height);
		torus_mode = res.getBoolean(R.bool.torus_mode);
	}
}
