package org.ninehells.angrypipes;

//{//  import

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import us.gorges.viewaclue.TwoDScrollView;
import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.BoardView;
import org.ninehells.angrypipes.SettingsActivity;

//}//

public class GameActivity extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{//
		super.onCreate(state);

		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		String boardString = prefs.getString("board", "");

		mGameData = new GameData(this);
		if (boardString == "") {
			mGameData.seconds_elapsed = -1;
			mGameData.mistake_count = 0;
		}

		mBoardData = new BoardData(this);
		mBoard = new Board(mBoardData, Base64.decode(boardString, 0));
		mBoardView = new BoardView(this, mBoard);

		TwoDScrollView scrollView = new TwoDScrollView(this);
		scrollView.addView(mBoardView);

		Button undoButton = new Button(this);
		undoButton.setText(R.string.undo);
		undoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBoard.undo();
				mBoardView.invalidate();
			}
		});

		mTimeLabel = new TextView(this);

		ZoomControls zoomControls = new ZoomControls(this);
		zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBoardView.zoomIn();
			}
		});
		zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBoardView.zoomOut();
			}
		});

		LinearLayout buttonBar = new LinearLayout(this);
		buttonBar.addView(undoButton);
		buttonBar.addView(zoomControls);
		buttonBar.addView(mTimeLabel);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(buttonBar);
		layout.addView(scrollView);
		setContentView(layout);

		mTimerHandler.postDelayed(mTimerTask, 1000);
	}//

	@Override
	public void onPause()
	{//
		super.onPause();

		mBoard.data().save(this, mBoard.gameOver() ? "" : Base64.encodeToString(mBoard.serialize(), 0));
		mGameData.save(this);
		mTimerHandler.removeCallbacks(mTimerTask);
	}//

	@Override
	public void onResume()
	{//
		super.onResume();

		mGameData = new GameData(this);
		mBoard.setGameData(mGameData);
		mBoard.setSettingsData(new SettingsData(this));

		mTimerHandler.postDelayed(mTimerTask, 1000);
	}//

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{//
		menu.add(Menu.NONE, R.integer.save_checkpoint, Menu.NONE, R.string.save_checkpoint);
		mRestoreMenuItem = menu.add(Menu.NONE, R.integer.restore_checkpoint, Menu.NONE, R.string.restore_checkpoint);
		mRestoreMenuItem.setEnabled(mCheckpointBoard != null);
		menu.add(Menu.NONE, R.integer.open_preferences, Menu.NONE, R.string.open_preferences);
		return true;
	}//

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{//
		switch (item.getItemId()) {
			case R.integer.save_checkpoint:
				mCheckpointBoard = mBoard.serialize();
				mRestoreMenuItem.setEnabled(true);
				return true;
			case R.integer.restore_checkpoint:
				mBoard.serialize(mCheckpointBoard);
				mBoardView.invalidate();
				return true;
			case R.integer.open_preferences:
				startActivity(new Intent(GameActivity.this, SettingsActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}//

	private Board mBoard;
	private BoardView mBoardView;
	private TextView mTimeLabel;
	private byte[] mCheckpointBoard;
	private MenuItem mRestoreMenuItem;

	private GameData mGameData;
	private BoardData mBoardData;
	private Handler mTimerHandler = new Handler();
	private Runnable mTimerTask = new Runnable()
	{//
		public void run() {

			if (!mBoard.gameOver())
				++mGameData.seconds_elapsed;

			int seconds = (int)(mGameData.seconds_elapsed);
			int minutes = (int)(seconds/60);  seconds -= 60*minutes;
			int hours   = (int)(minutes/60);  minutes -= 60*hours;
			int days    = (int)(hours  /24);  hours   -= 24*days;

			String s = "";
			if (days  > 0) s += String.format("%dd ", days);
			if (hours > 0) s += String.format("%dh ", hours);
			s += String.format("%d:%02d", minutes, seconds);
			if (!mBoardData.challenge_mode && mGameData.mistake_count>0)
				s += String.format(", %d %s",
						mGameData.mistake_count,
						mGameData.mistake_count>1 ? "misses" : "miss");
			mTimeLabel.setText(s);

			if (mBoard.gameOver()) {
				mGameData.seconds_elapsed = -1;
				mGameData.mistake_count = 0;
			}
			else
				mTimerHandler.postDelayed(this, 1000);
		}
	}//
	;
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
