package org.ninehells.angrypipes;

import android.content.Context;
import android.content.res.Resources;

public class Config
{
	int      width;
	int      height;
	boolean  torus_mode;
	boolean  no_cross_mode;

	Config (Context context)
	{
		Resources res = context.getResources();
		width         = res.getInteger(R.integer.medium_width);
		height        = res.getInteger(R.integer.medium_height);
		torus_mode    = res.getBoolean(R.bool   .torus_mode);
		no_cross_mode = res.getBoolean(R.bool   .no_cross_mode);
	}
}
