package org.ninehells.angrypipes;

import android.app.Activity;
import android.os.Bundle;

import org.ninehells.angrypipes.View;

public class AngryPipes extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		View view = new View(this);
		setContentView(view);
	}
}
