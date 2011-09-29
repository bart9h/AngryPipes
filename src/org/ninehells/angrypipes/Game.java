package org.ninehells.angrypipes;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.View;

public class Game extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{
		super.onCreate(state);

		SharedPreferences pref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		String boardString = pref.getString("board", "");
		Config cfg = new Config(this);
		cfg.width  = pref.getInt("width",  cfg.width);
		cfg.height = pref.getInt("height", cfg.height);
		cfg.torus_mode = pref.getBoolean("torus_mode", cfg.torus_mode);

		mBoard = new Board(cfg, boardString.getBytes());

		View view = new View(this, mBoard);

		ScrollView verticalScroll = new ScrollView(this);
		HorizontalScrollView horizontalScroll = new HorizontalScrollView(this);
		horizontalScroll.addView(view);
		verticalScroll.addView(horizontalScroll);
		setContentView(verticalScroll);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		SharedPreferences pref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		SharedPreferences.Editor ed = pref.edit();
		Config cfg = mBoard.config();
		ed.putInt("width",  cfg.width);
		ed.putInt("height", cfg.height);
		ed.putBoolean("torus_mode", cfg.torus_mode);
		ed.putString("board", new String(mBoard.serialize()));
		ed.commit();
	}

	private Board mBoard;
}

// vim600:fdm=syntax:fdn=2:nu:
