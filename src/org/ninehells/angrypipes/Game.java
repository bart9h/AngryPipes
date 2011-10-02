package org.ninehells.angrypipes;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
		Config cfg = new Config(this, state);

		mBoard = new Board(cfg, boardString.getBytes());
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

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(buttonBar);
		layout.addView(scrollView);
		setContentView(layout);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		mBoard.config().save(this, mBoard.isSolved() ? "" : new String(mBoard.serialize()));
	}

	private Board mBoard;
	private ViewBoard mBoardView;
}

// vim600:fdm=syntax:fdn=2:nu:
