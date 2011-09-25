package org.ninehells.angrypipes;

import android.app.Activity;
import android.os.Bundle;

import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.View;

public class AngryPipes extends Activity
{
	private Board m_board;

	@Override
	public void onCreate(Bundle state)
	{
		super.onCreate(state);

		View view = new View(this);

		if (state == null) {
			m_board = new Board(10, 10, null);
		}
		else {
			m_board = new Board(
					state.getInt("width",  10),
					state.getInt("height", 10),
					state.getByteArray("board")
			);
		}

		view.board = m_board;

		setContentView(view);
	}

	@Override
	public void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);

		state.putInt("width",  m_board.width());
		state.putInt("height", m_board.height());
		state.putByteArray("board", m_board.serialize());
	}
}
