package com.iitb.vpeub;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CompilerActivity extends Activity {	
	
	String TAG = "CompilerActivity";
	TextView compileProgress;
	ProgressBar horizontalBar;
	ProgressBar circularBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compiler);
		
		
		compileProgress = (TextView)findViewById(R.id.statusText1);
		horizontalBar = (ProgressBar)findViewById(R.id.progressBar1);
		circularBar = (ProgressBar)findViewById(R.id.progressCircle1);
		
		
		final Compiler c = new Compiler(this);
		
		Thread compilethread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				
				
				try {
					c.preProcess();
					c.compile();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
		
		
		compilethread.start();	
		
	}
		


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compiler, menu);
		return true;
	}
	
	
	// pre processing is complete
	public void preprocesscomplete()
	{
		
		compileProgress.setText(R.string.preprocess_complete);
		
	}
	
	
	/**
	 * Function to change horizontal progress bar
	 * @param p- integer which changes the horizontal progress bar status
	 */
	public void changeProgress(int p)
	{
		Log.wtf(TAG, "Progress = " + p);
		horizontalBar.setProgress(p);
	}
	
	/**
	 *  Function to set the message in the Text View
	 * @param id - id of the message to be shown in the Text View
	 */
	public void setMessage(int id)
	{
		
		compileProgress.setText(id);
	}
	
	public void removeCirularBar()
	{
		
		circularBar.setVisibility(View.GONE);
	}
	
	

}
