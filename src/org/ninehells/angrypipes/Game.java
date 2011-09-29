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
	public void onCreate(Bundle state)
	{
		super.onCreate(state);

		SharedPreferences pref = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		mBoard = new Board(
				pref.getInt("width",  20),
				pref.getInt("height", 30),
				pref.getString("board", "").getBytes()
		);

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
		ed.putInt("width",  mBoard.width());
		ed.putInt("height", mBoard.height());
		ed.putString("board", new String(mBoard.serialize()));
		ed.commit();
	}

	private Board mBoard;
}

// vim600:fdm=syntax:fdn=2:nu:
