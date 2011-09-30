package org.ninehells.angrypipes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;

import org.ninehells.angrypipes.Game;
import org.ninehells.angrypipes.Settings;

public class AngryPipes extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{
		super.onCreate(state);

		TextView text = new TextView(this);
		text.setText(R.string.intro);

		mNewGameButton = new Button(this);
		mNewGameButton.setText(R.string.new_game);
		mNewGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
				boolean hasGame = (prefs.getString("board", "") != "");
				if (hasGame) {
					//TODO confirmation
					Config cfg = new Config(AngryPipes.this, null);
					cfg.save(AngryPipes.this, "");
				}
				startActivity(new Intent(AngryPipes.this, Game.class));
			}
		});

		mResumeGameButton = new Button(this);
		mResumeGameButton.setText(R.string.resume_game);
		mResumeGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(AngryPipes.this, Game.class));
			}
		});

		Button settings = new Button(this);
		settings.setText(R.string.settings);
		settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(AngryPipes.this, Settings.class));
			}
		});

		Button about = new Button(this);
		about.setText(R.string.about);

		LinearLayout group = new LinearLayout(this);
		group.setOrientation(LinearLayout.VERTICAL);
		group.setGravity(Gravity.CENTER);
		group.addView(text);
		group.addView(mResumeGameButton);
		group.addView(mNewGameButton);
		group.addView(settings);
		group.addView(about);
		setContentView(group);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		boolean hasGame = (prefs.getString("board", "") != "");
		mResumeGameButton.setEnabled(hasGame);
	}

	Button mNewGameButton;
	Button mResumeGameButton;
}

// vim600:fdm=syntax:fdn=2:nu:
