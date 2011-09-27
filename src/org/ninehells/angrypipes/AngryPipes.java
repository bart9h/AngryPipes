package org.ninehells.angrypipes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.View;

public class AngryPipes extends Activity
{
	@Override
	public void onCreate(Bundle state)
	{
		super.onCreate(state);

		mPreferences = getPreferences(MODE_PRIVATE);
		mBoard = new Board(
				mPreferences.getInt("width",  7),
				mPreferences.getInt("height", 11),
				mPreferences.getString("board", "").getBytes()
		);

		View view = new View(this);
		view.setBoard(mBoard);
		setContentView(view);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		SharedPreferences.Editor ed = mPreferences.edit();
		ed.putInt("width",  mBoard.width());
		ed.putInt("height", mBoard.height());
		ed.putString("board", new String(mBoard.serialize()));
		ed.commit();
	}

	private Board mBoard;
	private SharedPreferences mPreferences;
}

// vim600:fdm=syntax:fdn=2:nu:
