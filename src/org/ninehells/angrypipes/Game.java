package org.ninehells.angrypipes;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import us.gorges.viewaclue.TwoDScrollView;
import org.ninehells.angrypipes.Board;
import org.ninehells.angrypipes.ViewBoard;

public class Game extends Activity
{
	@Override
	public void onCreate (Bundle state)
	{
		super.onCreate(state);

		SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
		String boardString = prefs.getString("board", "");
		mConfig = new Config(this, state);

		mBoard = new Board(mConfig, boardString.getBytes());
		mBoardView = new ViewBoard(this, mBoard);

		TwoDScrollView scrollView = new TwoDScrollView(this);
		scrollView.addView(mBoardView);

		Button undoButton = new Button(this);
		undoButton.setText(R.string.undo);
		undoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mBoard.undo();
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
	}

	@Override
	public void onPause()
	{
		super.onPause();

		mBoard.config().save(this, mBoard.isSolved() ? "" : new String(mBoard.serialize()));
		mTimerHandler.removeCallbacks(mTimerTask);
	}

	private Board mBoard;
	private ViewBoard mBoardView;
	private TextView mTimeLabel;

	private Config mConfig;
	private Handler mTimerHandler = new Handler();
	private Runnable mTimerTask = new Runnable() {
		public void run() {
			++mConfig.seconds_elapsed;
			int seconds = (int)(mConfig.seconds_elapsed);
			int minutes = (int)(seconds/60);  seconds -= 60*minutes;
			int hours   = (int)(minutes/60);  minutes -= 60*hours;
			int days    = (int)(hours  /24);  hours   -= 24*days;

			String s = "";
			if (days  > 0) s += String.format("%dd ", days);
			if (hours > 0) s += String.format("%dh ", hours);
			s += String.format("%d:%02d", minutes, seconds);
			mTimeLabel.setText(s);

			mTimerHandler.postDelayed(this, 1000);
		}
	};
}

// vim600:fdm=syntax:fdn=2:nu:
