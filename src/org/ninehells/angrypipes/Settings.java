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
import android.widget.RadioButton;
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

		final Resources res = getResources();
		mConfig = new Config(this, state);

		final RadioButton minimal = new RadioButton(this);
		final RadioButton small   = new RadioButton(this);
		final RadioButton medium  = new RadioButton(this);
		final RadioButton big     = new RadioButton(this);
		final RadioButton max     = new RadioButton(this);
		minimal.setText(R.string.size_minimal);
		small  .setText(R.string.size_small);
		medium .setText(R.string.size_medium);
		big    .setText(R.string.size_big);
		max    .setText(R.string.size_max);
		RadioGroup size = new RadioGroup(this);
		size.addView(minimal);
		size.addView(small);
		size.addView(medium);
		size.addView(big);
		size.addView(max);
		if (mConfig.width == res.getInteger(R.integer.min_width) &&
				mConfig.height == res.getInteger(R.integer.min_height))
			size.check(minimal.getId());
		else if (mConfig.width  == res.getInteger(R.integer.small_width) &&
				mConfig.height == res.getInteger(R.integer.small_height))
			size.check(small.getId());
		else if (mConfig.width == res.getInteger(R.integer.medium_width) &&
				mConfig.height == res.getInteger(R.integer.medium_height))
			size.check(medium.getId());
		else if (mConfig.width == res.getInteger(R.integer.big_width) &&
				mConfig.height == res.getInteger(R.integer.big_height))
			size.check(big.getId());
		else if (mConfig.width == res.getInteger(R.integer.max_width) &&
				mConfig.height == res.getInteger(R.integer.max_height))
			size.check(max.getId());
		size.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == minimal.getId()) {
					mConfig.width  = res.getInteger(R.integer.min_width);
					mConfig.height = res.getInteger(R.integer.min_height);
				}
				else if (checkedId == small.getId()) {
					mConfig.width  = res.getInteger(R.integer.small_width);
					mConfig.height = res.getInteger(R.integer.small_height);
				}
				else if (checkedId == medium.getId()) {
					mConfig.width  = res.getInteger(R.integer.medium_width);
					mConfig.height = res.getInteger(R.integer.medium_height);
				}
				else if (checkedId == big.getId()) {
					mConfig.width  = res.getInteger(R.integer.big_width);
					mConfig.height = res.getInteger(R.integer.big_height);
				}
				else if (checkedId == max.getId()) {
					mConfig.width  = res.getInteger(R.integer.max_width);
					mConfig.height = res.getInteger(R.integer.max_height);
				}
			}
		});

		CheckBox torus = new CheckBox(this);
		torus.setText(R.string.torus_mode);
		torus.setChecked(mConfig.torus_mode);
		torus.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.torus_mode = isChecked;
			}
		});

		CheckBox nocross = new CheckBox(this);
		nocross.setText(R.string.no_cross_mode);
		nocross.setChecked(mConfig.no_cross_mode);
		nocross.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.no_cross_mode = isChecked;
			}
		});

		CheckBox challenge = new CheckBox(this);
		challenge.setText(R.string.challenge_mode);
		challenge.setChecked(mConfig.challenge_mode);
		challenge.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mConfig.challenge_mode = isChecked;
			}
		});

		Button play = new Button(this);
		play.setText(R.string.new_game);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveAndRun();
			}
		});

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER);
		layout.addView(size);
		layout.addView(torus);
		layout.addView(nocross);
		layout.addView(challenge);
		layout.addView(play);
		setContentView(layout);
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
