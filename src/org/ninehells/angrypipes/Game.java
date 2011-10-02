package org.ninehells.angrypipes;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;

import us.gorges.viewaclue.TwoDScrollView;
import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.ViewBoard;

public class Game extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{
		super.onCreate(state);

		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		String boardString = prefs.getString("board", "");
		Config cfg = new Config(this, state);

		mBoard = new Board(cfg, boardString.getBytes());

		ViewBoard view = new ViewBoard(this, mBoard);

		TwoDScrollView scroll = new TwoDScrollView(this);
		scroll.addView(view);
		setContentView(scroll);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		mBoard.config().save(this, mBoard.isSolved() ? "" : new String(mBoard.serialize()));
	}

	private Board mBoard;
}

// vim600:fdm=syntax:fdn=2:nu:
