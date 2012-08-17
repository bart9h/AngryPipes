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

import org.ninehells.angrypipes.SettingsData;
import org.ninehells.angrypipes.GameActivity;

//}//

public class SettingsActivity extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{//
		super.onCreate(state);

		setContentView(R.layout.preferences);

		final Resources res = getResources();
		mSettingsData = new SettingsData(this);

		CheckBox light_theme = (CheckBox) findViewById(R.id.light_theme_button);
		light_theme.setChecked(mSettingsData.light_theme);
		light_theme.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mSettingsData.light_theme = isChecked;
			}
		});

		CheckBox auto_pan = (CheckBox) findViewById(R.id.auto_pan_button);
		auto_pan.setChecked(mSettingsData.auto_pan);
		auto_pan.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mSettingsData.auto_pan = isChecked;
			}
		});

		final CheckBox autolock = (CheckBox) findViewById(R.id.auto_lock_button);
		autolock.setChecked(mSettingsData.auto_lock);
		autolock.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mSettingsData.auto_lock = isChecked;
			}
		});
	}//

	@Override
	public void onPause()
	{//
		super.onPause();

		mSettingsData.save(this, null);
	}//

	private SettingsData mSettingsData;
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
