package com.iitb.vpeub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		statusView = (TextView)findViewById(R.id.statusText);
		msgView = (TextView)findViewById(R.id.welomeText);
		circleLoad = (ProgressBar)findViewById(R.id.progressCircle);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		nextButton = (Button)findViewById(R.id.button1);
		
		nextButton.setOnClickListener(this);
		
		final Decompressor d = new Decompressor(this);
		new Thread((new Runnable() {
			
			@Override
			public void run() {
				d.doYourJob();
				
			}
		})).start();
		
		
		
		/*try {
			d.unzip();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	public void filesCopied() {
		statusView.setText(R.string.copy_busybox);
	}
	
	public void busyboxCopied() {
		
		statusView.setText(R.string.unzip);
		circleLoad.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		
	}
	
	public void envReady() {
		progressBar.setVisibility(View.INVISIBLE);
		nextButton.setVisibility(View.VISIBLE);
		msgView.setText(R.string.env_ready);
		statusView.setText(R.string.have_fun);
	}
	public void setProg(int p) {
		progressBar.setProgress(p);
	}

	@Override
	public void onClick(View view) {
		if(view == nextButton) {
			Intent intent = new Intent(this, VPEUB.class);
			startActivity(intent);

		}
		
	}


}

