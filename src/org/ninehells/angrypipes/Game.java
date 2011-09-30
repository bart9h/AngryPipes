package org.ninehells.angrypipes;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;

import us.gorges.viewaclue.TwoDScrollView;
import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.View;

public class Game extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{
		super.onCreate(state);

		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		String boardString = prefs.getString("board", "");
		Config cfg = new Config(this);
		cfg.width          = prefs.getInt    ("width",          cfg.width);
		cfg.height         = prefs.getInt    ("height",         cfg.height);
		cfg.torus_mode     = prefs.getBoolean("torus_mode",     cfg.torus_mode);
		cfg.no_cross_mode  = prefs.getBoolean("no_cross_mode",  cfg.no_cross_mode);
		cfg.challenge_mode = prefs.getBoolean("challenge_mode", cfg.challenge_mode);

		mBoard = new Board(cfg, boardString.getBytes());

		View view = new View(this, mBoard);

		TwoDScrollView scroll = new TwoDScrollView(this);
		scroll.addView(view);
		setContentView(scroll);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		Config cfg = mBoard.config();
		ed.putInt    ("width",          cfg.width);
		ed.putInt    ("height",         cfg.height);
		ed.putBoolean("torus_mode",     cfg.torus_mode);
		ed.putBoolean("no_cross_mode",  cfg.no_cross_mode);
		ed.putBoolean("challenge_mode", cfg.challenge_mode);
		ed.putString("board", new String(mBoard.serialize()));
		ed.commit();
	}

	private Board mBoard;
}

// vim600:fdm=syntax:fdn=2:nu:
