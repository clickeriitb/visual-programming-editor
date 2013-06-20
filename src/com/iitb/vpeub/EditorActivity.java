package com.iitb.vpeub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditorActivity extends Activity {

	Button compile;
	Button save;
	EditText text;
	String line,line1 = "";
	String TAG="editor";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);

		compile = (Button)findViewById(R.id.button1);
		compile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startCompile(v);

			}
		});

		copyText();
		text = (EditText)findViewById(R.id.editText1);
		text.setText(line1);

		save = (Button)findViewById(R.id.button2);
		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				saveToFile();

			}
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editor, menu);
		return true;

	}

	/*
	 * Function to start the compiler Activity
	 */
	public void startCompile(View view)
	{
		Intent intent = new Intent(this,CompilerActivity.class);	
		startActivity(intent);

	}

	/*
	 * Copies the content of sketch.ino file (which has arduino code) in string line1
	 */

	public void copyText()
	{
		try
		{
			File file = new File(getFilesDir(),"code/sketch.ino");
			InputStream instream = new FileInputStream(file);
			InputStreamReader inputreader = new InputStreamReader(instream); 
			BufferedReader buffreader = new BufferedReader(inputreader); 

			while ((line = buffreader.readLine()) != null)
				line1+=line+"\n";

			buffreader.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}			

	}


	/*
	 * Saves the contents of Edit Text to sketcch.ino 
	 */
	public void saveToFile()
	{

		String s;
		s= text.getText().toString();
		File file1 = new File(getFilesDir(),"code/sketch.ino");
		try
		{
			FileOutputStream f = new FileOutputStream(file1);
			f.write(s.getBytes());
			f.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}



