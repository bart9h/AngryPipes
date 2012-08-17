package org.ninehells.angrypipes;

//{//  import

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;

//}//

class GameData
{
	int seconds_elapsed;
	int mistake_count;

	GameData (Context context)
	{//
		seconds_elapsed = -1;
		mistake_count = 0;

		Resources res = context.getResources();
		SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
		seconds_elapsed = prefs.getInt    ("seconds_elapsed", seconds_elapsed);
		mistake_count   = prefs.getInt    ("mistake_count",   mistake_count);
	}//

	void save (Context context)
	{//
		Resources res = context.getResources();
		SharedPreferences prefs = context.getSharedPreferences(res.getString(R.string.app_name), Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putInt    ("seconds_elapsed", seconds_elapsed);
		ed.putInt    ("mistake_count",   mistake_count);
		ed.commit();
	}//

}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
