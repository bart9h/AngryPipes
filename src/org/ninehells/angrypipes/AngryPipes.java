package org.ninehells.angrypipes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;

import org.ninehells.angrypipes.Game;

public class AngryPipes extends Activity
{
	@Override
	public void onCreate(Bundle state)
	{
		super.onCreate(state);

		TextView text = new TextView(this);
		text.setText(R.string.intro);

		Button play = new Button(this);
		play.setText(R.string.play);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(AngryPipes.this, Game.class));
			}
		});

		Button settings = new Button(this);
		settings.setText(R.string.settings);

		Button about = new Button(this);
		about.setText(R.string.about);

		LinearLayout group = new LinearLayout(this);
		group.setOrientation(LinearLayout.VERTICAL);
		group.setGravity(Gravity.CENTER);
		group.addView(text);
		group.addView(play);
		group.addView(settings);
		group.addView(about);
		setContentView(group);
	}
}

// vim600:fdm=syntax:fdn=2:nu:
