package org.ninehells.angrypipes;

//{//  import

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import org.ninehells.angrypipes.Config;
import org.ninehells.angrypipes.Game;

//}//

public class Preferences extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{//
		super.onCreate(state);

		setContentView(R.layout.preferences);

		final Resources res = getResources();
		mConfig = new Config(this, state);

		CheckBox light_theme = (CheckBox) findViewById(R.id.light_theme_button);
		light_theme.setChecked(mConfig.light_theme);
		light_theme.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.light_theme = isChecked;
			}
		});

		CheckBox auto_pan = (CheckBox) findViewById(R.id.auto_pan_button);
		auto_pan.setChecked(mConfig.no_cross_mode);
		auto_pan.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.auto_pan = isChecked;
			}
		});

		final CheckBox autolock = (CheckBox) findViewById(R.id.auto_lock_button);
		autolock.setChecked(mConfig.auto_lock);
		autolock.setEnabled(!mConfig.challenge_mode);
		autolock.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.auto_lock = isChecked;
			}
		});
	}//

	@Override
	public void onSaveInstanceState (Bundle state)
	{//
		super.onSaveInstanceState(state);

		mConfig.save(state, null);
	}//

	private Config mConfig;
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
