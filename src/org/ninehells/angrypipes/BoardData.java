package org.ninehells.angrypipes;

//{//  import

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;

//}//

class BoardData
{
	int      width;
	int      height;
	boolean  torus_mode;
	boolean  no_cross_mode;
	boolean  challenge_mode;

	BoardData (Context context)
	{//
		Resources res   = context.getResources();
		width           = res.getInteger(R.integer.medium_width);
		height          = res.getInteger(R.integer.medium_height);
		torus_mode      = res.getBoolean(R.bool   .torus_mode);
		no_cross_mode   = res.getBoolean(R.bool   .no_cross_mode);
		challenge_mode  = res.getBoolean(R.bool   .challenge_mode);

		SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
		width           = prefs.getInt    ("width",           width);
		height          = prefs.getInt    ("height",          height);
		torus_mode      = prefs.getBoolean("torus_mode",      torus_mode);
		no_cross_mode   = prefs.getBoolean("no_cross_mode",   no_cross_mode);
		challenge_mode  = prefs.getBoolean("challenge_mode",  challenge_mode);
	}//

	void save (Context context, String board)
	{//
		Resources res  = context.getResources();
		SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt    ("width",           width);
		ed.putInt    ("height",          height);
		ed.putBoolean("torus_mode",      torus_mode);
		ed.putBoolean("no_cross_mode",   no_cross_mode);
		ed.putBoolean("challenge_mode",  challenge_mode);
		if (board != null)
			ed.putString("board", board);
		ed.commit();
	}//

}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
