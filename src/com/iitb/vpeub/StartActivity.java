package com.iitb.vpeub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iitb.vpeconfig.Config;
import com.iitb.vpeconfig.Target;


/*
 * This activity handles The setting up of the compiler Environment
 * It also displays a welcome message
 * TODO Add help button
 */
public class StartActivity extends Activity implements OnClickListener  {


	String TAG = "StartActivity";
	TextView statusView;
	TextView msgView;
	static final int COPYCOMPLETE = 0;
	static final int BUSYCOMPLETE = 1;
	static final int PROGRESS = 2;
	ProgressBar circleLoad;
	ProgressBar progressBar;
	Button nextButton;
	boolean init = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Target x = Config.uno;
		Log.d(TAG,"speed = " + x.getSpeed() ); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		statusView = (TextView)findViewById(R.id.statusText);
		msgView = (TextView)findViewById(R.id.welomeText);
		circleLoad = (ProgressBar)findViewById(R.id.progressCircle);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		nextButton = (Button)findViewById(R.id.button1);

		nextButton.setOnClickListener(this);

		/*
		 * Create a decompressor object in a new thread so that UI does not block
		 */
		final Decompressor d = new Decompressor(this);
		new Thread((new Runnable() {

			@Override
			public void run() {
				d.doYourJob();

			}
		})).start();





	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	/*
	 * Compressed files are copied, now copy busybox file
	 */
	public void filesCopied() {
		statusView.setText(R.string.copy_busybox);
	}

	/*
	 * Busybox is copied, now display progressbar for decompression
	 */
	public void busyboxCopied() {

		statusView.setText(R.string.unzip);
		circleLoad.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);

	}

	/*
	 * Everything is set up
	 */
	public void envReady() {
		progressBar.setVisibility(View.INVISIBLE);
		nextButton.setVisibility(View.VISIBLE);
		msgView.setText(R.string.env_ready);
		statusView.setText(R.string.have_fun);
	}

	/*
	 * Set progress bar
	 */
	public void setProg(int p) {
		progressBar.setProgress(p);
	}

	/*
	 * Start Blockly Activity
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		if(view == nextButton) {
			Intent intent = new Intent(this, VPEUB.class);
			startActivity(intent);

		}

	}


}

