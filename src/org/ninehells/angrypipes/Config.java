package org.ninehells.angrypipes;

//{//  import

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;

//}//

class Config
{
	int      width;
	int      height;
	boolean  torus_mode;
	boolean  no_cross_mode;
	boolean  challenge_mode;
	int seconds_elapsed;
	int mistake_count;

	Config (Context context, Bundle state)
	{//
		Resources res   = context.getResources();
		width           = res.getInteger(R.integer.medium_width);
		height          = res.getInteger(R.integer.medium_height);
		torus_mode      = res.getBoolean(R.bool   .torus_mode);
		no_cross_mode   = res.getBoolean(R.bool   .no_cross_mode);
		challenge_mode  = res.getBoolean(R.bool   .challenge_mode);
		seconds_elapsed = -1;
		mistake_count = 0;

		if (state != null) {
			width           = state.getInt    ("width",           width);
			height          = state.getInt    ("height",          height);
			torus_mode      = state.getBoolean("torus_mode",      torus_mode);
			no_cross_mode   = state.getBoolean("no_cross_mode",   no_cross_mode);
			challenge_mode  = state.getBoolean("challenge_mode",  challenge_mode);
			seconds_elapsed = state.getInt    ("seconds_elapsed", seconds_elapsed);
			mistake_count   = state.getInt    ("mistake_count",   mistake_count);
		}
		else {
			SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
			width           = prefs.getInt    ("width",           width);
			height          = prefs.getInt    ("height",          height);
			torus_mode      = prefs.getBoolean("torus_mode",      torus_mode);
			no_cross_mode   = prefs.getBoolean("no_cross_mode",   no_cross_mode);
			challenge_mode  = prefs.getBoolean("challenge_mode",  challenge_mode);
			seconds_elapsed = prefs.getInt    ("seconds_elapsed", seconds_elapsed);
			mistake_count   = prefs.getInt    ("mistake_count",   mistake_count);
		}

	}//

	void save (Context context, String board)
	{//
		Resources res  = context.getResources();
		SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt    ("width",          width);
		ed.putInt    ("height",         height);
		ed.putBoolean("torus_mode",     torus_mode);
		ed.putBoolean("no_cross_mode",  no_cross_mode);
		ed.putBoolean("challenge_mode", challenge_mode);
		ed.putInt    ("seconds_elapsed", seconds_elapsed);
		ed.putInt    ("mistake_count",   mistake_count);
		if (board != null)
			ed.putString("board", board);
		ed.commit();
	}//

	void save (Bundle state, String board)
	{//
		state.putInt    ("width",          width);
		state.putInt    ("height",         height);
		state.putBoolean("torus_mode",     torus_mode);
		state.putBoolean("no_cross_mode",  no_cross_mode);
		state.putBoolean("challenge_mode", challenge_mode);
		state.putInt    ("seconds_elapsed", seconds_elapsed);
		state.putInt    ("mistake_count",   mistake_count);
		if (board != null)
			state.putString("board", board);
	}//
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
