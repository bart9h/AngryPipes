package org.ninehells.pipe9;

import android.app.Activity;
import android.os.Bundle;

import org.ninehells.pipe9.View;

public class Pipe9 extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		View view = new View(this);
		setContentView(view);
	}
}
