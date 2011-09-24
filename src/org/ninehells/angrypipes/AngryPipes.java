package org.ninehells.angrypipes;

import android.app.Activity;
import android.os.Bundle;

import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.View;

public class AngryPipes extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		View view = new View(this);
		view.board = new Board(5, 5);

		setContentView(view);
	}
}
