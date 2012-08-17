package org.ninehells.angrypipes;

//{//  import

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;

//}//

class Config
{
	int      width;
	int      height;
	boolean  auto_lock;
	boolean  torus_mode;
	boolean  no_cross_mode;
	boolean  challenge_mode;
	boolean  light_theme;
	boolean  auto_pan;
	int seconds_elapsed;
	int mistake_count;

	Config (Context context)
	{//
		Resources res   = context.getResources();
		width           = res.getInteger(R.integer.medium_width);
		height          = res.getInteger(R.integer.medium_height);
		torus_mode      = res.getBoolean(R.bool   .torus_mode);
		no_cross_mode   = res.getBoolean(R.bool   .no_cross_mode);
		auto_lock       = res.getBoolean(R.bool   .auto_lock);
		challenge_mode  = res.getBoolean(R.bool   .challenge_mode);
		light_theme     = res.getBoolean(R.bool   .light_theme);
		auto_pan        = res.getBoolean(R.bool   .auto_pan);
		seconds_elapsed = -1;
		mistake_count = 0;

		SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
		width           = prefs.getInt    ("width",           width);
		height          = prefs.getInt    ("height",          height);
		torus_mode      = prefs.getBoolean("torus_mode",      torus_mode);
		no_cross_mode   = prefs.getBoolean("no_cross_mode",   no_cross_mode);
		auto_lock       = prefs.getBoolean("auto_lock",       auto_lock);
		challenge_mode  = prefs.getBoolean("challenge_mode",  challenge_mode);
		light_theme     = prefs.getBoolean("light_theme",     light_theme);
		auto_pan        = prefs.getBoolean("auto_pan",        auto_pan);
		seconds_elapsed = prefs.getInt    ("seconds_elapsed", seconds_elapsed);
		mistake_count   = prefs.getInt    ("mistake_count",   mistake_count);
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
		ed.putBoolean("auto_lock",       auto_lock);
		ed.putBoolean("challenge_mode",  challenge_mode);
		ed.putBoolean("light_theme",     light_theme);
		ed.putBoolean("auto_pan",        auto_pan);
		ed.putInt    ("seconds_elapsed", seconds_elapsed);
		ed.putInt    ("mistake_count",   mistake_count);
		if (board != null)
			ed.putString("board", board);
		ed.commit();
	}//

}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
