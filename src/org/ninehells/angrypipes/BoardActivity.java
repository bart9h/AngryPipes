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

import org.ninehells.angrypipes.BoardData;
import org.ninehells.angrypipes.GameActivity;

//}//

public class BoardActivity extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{//
		super.onCreate(state);

		setContentView(R.layout.settings);

		final Resources res = getResources();

		mBoardData = new BoardData(this);
		final int minimal = R.id.minimal_radio;
		final int small   = R.id.small_radio;
		final int medium  = R.id.medium_radio;
		final int big     = R.id.big_radio;
		final int max     = R.id.max_radio;

		RadioGroup size = (RadioGroup) findViewById(R.id.size_radio_group);
		if (mBoardData.width == res.getInteger(R.integer.min_width) &&
				mBoardData.height == res.getInteger(R.integer.min_height))
			size .check(minimal);

		else if (mBoardData.width  == res.getInteger(R.integer.small_width) &&
				mBoardData.height == res.getInteger(R.integer.small_height))
			size.check(small);

		else if (mBoardData.width == res.getInteger(R.integer.medium_width) &&
				mBoardData.height == res.getInteger(R.integer.medium_height))
			size.check(medium);

		else if (mBoardData.width == res.getInteger(R.integer.big_width) &&
				mBoardData.height == res.getInteger(R.integer.big_height))
			size.check(big);

		else if (mBoardData.width == res.getInteger(R.integer.max_width) &&
				mBoardData.height == res.getInteger(R.integer.max_height))
			size.check(max);

		size.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == minimal) {
					mBoardData.width  = res.getInteger(R.integer.min_width);
					mBoardData.height = res.getInteger(R.integer.min_height);
				}
				else if (checkedId == small) {
					mBoardData.width  = res.getInteger(R.integer.small_width);
					mBoardData.height = res.getInteger(R.integer.small_height);
				}
				else if (checkedId == medium) {
					mBoardData.width  = res.getInteger(R.integer.medium_width);
					mBoardData.height = res.getInteger(R.integer.medium_height);
				}
				else if (checkedId == big) {
					mBoardData.width  = res.getInteger(R.integer.big_width);
					mBoardData.height = res.getInteger(R.integer.big_height);
				}
				else if (checkedId == max) {
					mBoardData.width  = res.getInteger(R.integer.max_width);
					mBoardData.height = res.getInteger(R.integer.max_height);
				}
			}
		});

		CheckBox torus = (CheckBox) findViewById(R.id.torus_button);
		torus.setChecked(mBoardData.torus_mode);
		torus.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mBoardData.torus_mode = isChecked;
			}
		});

		CheckBox nocross = (CheckBox) findViewById(R.id.no_cross_button);
		nocross.setChecked(mBoardData.no_cross_mode);
		nocross.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mBoardData.no_cross_mode = isChecked;
			}
		});

		CheckBox challenge = (CheckBox) findViewById(R.id.challenge_button);
		challenge.setChecked(mBoardData.challenge_mode);
		challenge.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton box, boolean isChecked) {
				mBoardData.challenge_mode = isChecked;
			}
		});

		Button play = (Button) findViewById(R.id.new_game_settings_button);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveAndRun();
			}
		});
	}//

	@Override
	public void onPause()
	{//
		super.onPause();

		mBoardData.save(this, null);
	}//

	private void saveAndRun()
	{//
		mBoardData.save(this, "");

		startActivity(new Intent(BoardActivity.this, GameActivity.class));
	}//

	private BoardData mBoardData;
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
