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

		m_prefs = getPreferences(MODE_PRIVATE);
		m_board = new Board(
				m_prefs.getInt("width",  10),
				m_prefs.getInt("height", 10),
				m_prefs.getString("board", "").getBytes()
		);

		View view = new View(this);
		view.setBoard(m_board);
		setContentView(view);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		SharedPreferences.Editor ed = m_prefs.edit();
		ed.putInt("width",  m_board.width());
		ed.putInt("height", m_board.height());
		ed.putString("board", new String(m_board.serialize()));
		ed.commit();
	}

	private Board m_board;
	private SharedPreferences m_prefs;
}

// vim600:fdm=syntax:fdn=2:nu:
