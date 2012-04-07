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

public class Settings extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{//
		super.onCreate(state);

		setContentView(R.layout.settings);

		final Resources res = getResources();
		mConfig = new Config(this, state);
		final int minimal = R.id.minimal_radio;
		final int small   = R.id.small_radio;
		final int medium  = R.id.medium_radio;
		final int big     = R.id.big_radio;
		final int max     = R.id.max_radio;

		RadioGroup size = (RadioGroup) findViewById(R.id.size_radio_group);
		if (mConfig.width == res.getInteger(R.integer.min_width) &&
				mConfig.height == res.getInteger(R.integer.min_height))
			size .check(minimal);

		else if (mConfig.width  == res.getInteger(R.integer.small_width) &&
				mConfig.height == res.getInteger(R.integer.small_height))
			size.check(small);

		else if (mConfig.width == res.getInteger(R.integer.medium_width) &&
				mConfig.height == res.getInteger(R.integer.medium_height))
			size.check(medium);

		else if (mConfig.width == res.getInteger(R.integer.big_width) &&
				mConfig.height == res.getInteger(R.integer.big_height))
			size.check(big);

		else if (mConfig.width == res.getInteger(R.integer.max_width) &&
				mConfig.height == res.getInteger(R.integer.max_height))
			size.check(max);

		size.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == minimal) {
					mConfig.width  = res.getInteger(R.integer.min_width);
					mConfig.height = res.getInteger(R.integer.min_height);
				}
				else if (checkedId == small) {
					mConfig.width  = res.getInteger(R.integer.small_width);
					mConfig.height = res.getInteger(R.integer.small_height);
				}
				else if (checkedId == medium) {
					mConfig.width  = res.getInteger(R.integer.medium_width);
					mConfig.height = res.getInteger(R.integer.medium_height);
				}
				else if (checkedId == big) {
					mConfig.width  = res.getInteger(R.integer.big_width);
					mConfig.height = res.getInteger(R.integer.big_height);
				}
				else if (checkedId == max) {
					mConfig.width  = res.getInteger(R.integer.max_width);
					mConfig.height = res.getInteger(R.integer.max_height);
				}
			}
		});

		CheckBox torus = (CheckBox) findViewById(R.id.torus_button);
		torus.setChecked(mConfig.torus_mode);
		torus.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.torus_mode = isChecked;
			}
		});

		CheckBox nocross = (CheckBox) findViewById(R.id.no_cross_button);
		nocross.setChecked(mConfig.no_cross_mode);
		nocross.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.no_cross_mode = isChecked;
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

		CheckBox challenge = (CheckBox) findViewById(R.id.challenge_button);
		challenge.setChecked(mConfig.challenge_mode);
		challenge.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.challenge_mode = isChecked;
				autolock.setEnabled(!isChecked);
			}
		});

		Button play = (Button) findViewById(R.id.new_game_settings_button);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveAndRun();
			}
		});
	}//

	private void saveAndRun()
	{//
		mConfig.save(this, "");

		startActivity(new Intent(Settings.this, Game.class));
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
