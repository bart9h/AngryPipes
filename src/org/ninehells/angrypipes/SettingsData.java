package org.ninehells.angrypipes;

//{//  import

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;

//}//

class SettingsData
{
	boolean  auto_pan;
	boolean  auto_lock;
	boolean  light_theme;
	boolean  haptic_feedback;

	SettingsData (Context context)
	{//
		Resources res   = context.getResources();
		auto_pan        = res.getBoolean(R.bool.auto_pan);
		auto_lock       = res.getBoolean(R.bool.auto_lock);
		light_theme     = res.getBoolean(R.bool.light_theme);
		haptic_feedback = res.getBoolean(R.bool.haptic_feedback);

		SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
		auto_pan        = prefs.getBoolean("auto_pan",        auto_pan);
		auto_lock       = prefs.getBoolean("auto_lock",       auto_lock);
		light_theme     = prefs.getBoolean("light_theme",     light_theme);
		haptic_feedback = prefs.getBoolean("haptic_feedback", haptic_feedback);
	}//

	void save (Context context, String board)
	{//
		Resources res  = context.getResources();
		SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putBoolean("auto_pan",        auto_pan);
		ed.putBoolean("auto_lock",       auto_lock);
		ed.putBoolean("light_theme",     light_theme);
		ed.putBoolean("haptic_feedback", haptic_feedback);
		ed.commit();
	}//

}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
