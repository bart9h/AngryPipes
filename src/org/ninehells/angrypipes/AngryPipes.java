package org.ninehells.angrypipes;

//{//  import

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.ninehells.angrypipes.GameActivity;
import org.ninehells.angrypipes.BoardActivity;

//}//

public class AngryPipes extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{//
		super.onCreate(state);

		setContentView(R.layout.main);

		mNewGameButton = (Button)findViewById(R.id.new_game_button);
		mNewGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (hasGame()) {
					//TODO confirmation
					BoardData b = new BoardData(AngryPipes.this);
					b.save(AngryPipes.this, "");
				}
				startActivity(new Intent(AngryPipes.this, GameActivity.class));
			}
		});

		mResumeGameButton = (Button)findViewById(R.id.resume_game_button);
		mResumeGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(AngryPipes.this, GameActivity.class));
			}
		});

		Button settings = (Button)findViewById(R.id.settings_button);
		settings.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(AngryPipes.this, BoardActivity.class));
			}
		});
	}//

	@Override
	public void onResume()
	{//
		super.onResume();

		mResumeGameButton.setEnabled(hasGame());
	}//

	private boolean hasGame()
	{//
		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		return (prefs.getString("board", "") != "");
	}//

	Button mNewGameButton;
	Button mResumeGameButton;
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
