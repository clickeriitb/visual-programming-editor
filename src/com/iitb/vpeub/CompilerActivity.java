package com.iitb.vpeub;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CompilerActivity extends Activity {	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compiler);
		Compiler c = new Compiler(this);
		try {
			c.preProcess();
			c.compile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compiler, menu);
		return true;
	}

}
