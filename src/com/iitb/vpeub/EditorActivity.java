package com.iitb.vpeub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends Activity implements OnClickListener {

	private static final int REQUEST_SAVE = 0;

	final Context context = this;

	Button menu;
	//Button save;
	//Button newFile;

	//Button openFile;


	EditText text;

	EditText userInput;



	String s;


	String line,line1 = "";
	//String fileName = "";
	String TAG="editor";

	String s2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		
		
		
		//////////////////////////////////////////////////////
		

        final int ID_SAVE = 0;
		ActionItem saveItem      = new ActionItem(ID_SAVE, "Save", getResources().getDrawable(R.drawable.ic_add));
        final int ID_COMPILE = 1;
		ActionItem compileItem   = new ActionItem(ID_COMPILE, "Compile", getResources().getDrawable(R.drawable.ic_accept));
        final int ID_LOAD = 2;
		ActionItem loadItem   = new ActionItem(ID_LOAD, "Load Arduino Code", getResources().getDrawable(R.drawable.ic_up));
		
		
		
		final int ID_CREATEFILE = 3;
		ActionItem createfileItem      = new ActionItem(ID_CREATEFILE, "Create new file", getResources().getDrawable(R.drawable.ic_add));
		
		final int ID_SHOWEXAMPLE = 4;
		ActionItem showexampleItem      = new ActionItem(ID_SHOWEXAMPLE, "Show examples", getResources().getDrawable(R.drawable.ic_add));
		
		
        //use setSticky(true) to disable QuickAction dialog being dismissed after an item is clicked
        //uploadItem.setSticky(true);

        final QuickAction mQuickAction  = new QuickAction(this);

        mQuickAction.addActionItem(saveItem);
        mQuickAction.addActionItem(compileItem);
        mQuickAction.addActionItem(loadItem);
        
        mQuickAction.addActionItem(createfileItem);
        
        mQuickAction.addActionItem(showexampleItem);
        
        

        //setup the action item click listener
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override	
            public void onItemClick(QuickAction quickAction, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);

                if (actionId == ID_SAVE) {
                   //Toast.makeText(getApplicationContext(), "Add item selected", Toast.LENGTH_SHORT).show();
                	
                	saveToFile();
                	
                	
                } else if(actionId == ID_COMPILE) {
                 //   Toast.makeText(getApplicationContext(), actionItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
                	saveToFile();
                	startCompile();
                
                }
                
                else if(actionId == ID_LOAD)
                {
                	
                	findFileLocation();
                }
                
                
                else if(actionId == ID_CREATEFILE)
                {
                	
                	setFileName();
                }
                
                
                else if(actionId == ID_SHOWEXAMPLE)
                {
                	
                	show_Examples();
                }
                
                
            }
        });

        mQuickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
            @Override
            public void onDismiss() {
                Toast.makeText(getApplicationContext(), "dismissed", Toast.LENGTH_SHORT).show();
            }
        });

//        Button btn1 = (Button) this.findViewById(R.id.btn1);
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @QuickAction.OnActionItemClickListener()Override
//            public void onClick(View v) {
//                mQuickAction.show(v);
//            }
//        })

//        Button btn2 = (Button) this.findViewById(R.id.btn2);
//        btn2.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mQuickAction.show(v);
//                mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
//            }
//        });
		
		
		
       
        
        
		//////////////////////////////////////////////////////
		
		
		

//		compile = (Button)findViewById(R.id.button1);
//		compile.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//startCompile();
//
//			}
//		});

		copyText();
		text = (EditText)findViewById(R.id.editText1);
		text.setText(line1);

		menu = (Button)findViewById(R.id.button1);
		menu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//saveToFile();	
				mQuickAction.show(v);
			}
		});

//		newFile = (Button)findViewById(R.id.button3);
//		newFile.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//setFileName();
//
//			}
//		});

//		openFile = (Button)findViewById(R.id.button4);
//		openFile.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//findFileLocation();
//			}
//		});


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
	public void startCompile()
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
	 * Saves the contents of Edit Text to sketch.ino 
	 */
	public void saveToFile()
	{


		s= text.getText().toString();
		File file1 = new File(getFilesDir(),"code/sketch.ino");
		try
		{
			FileOutputStream f = new FileOutputStream(file1);
			f.write(s.getBytes());
			f.close();


			Context context = getApplicationContext();
			CharSequence text = "File Saved Successfully";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
	
	
//////////////////////////////////////////////////////
	
	public void show_Examples()
	{


		Intent intent = new Intent(getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, getFilesDir()+File.separator+"examples");

		//can user select directories or not
		intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

		//alternatively you can set file filter
		//intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

		startActivityForResult(intent, REQUEST_SAVE);
		
		Log.d("show examples","examples showing");

	}
	

///////////////////////////////////////////////////////////////
	
	
	
	
	
	

	public void setFileName()
	{



		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.prompts, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set prompts.xml to alert dialog builder
		alertDialogBuilder.setView(promptsView);

		userInput = (EditText)promptsView.findViewById(R.id.editTextResult);

		// set title
		alertDialogBuilder.setTitle("Enter File Name");

		// set dialog message
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK",this)
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});



		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		s= text.getText().toString();
		s2 = userInput.getText().toString();
		File file2 = new File(getFilesDir(),"code"+File.separator + s2 + ".ino");
		try
		{
			FileOutputStream f2 = new FileOutputStream(file2);
			f2.write(s.getBytes());
			f2.close();

			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, "File Created Successfully", duration);
			toast.show();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}


	}



	public void findFileLocation()
	{


		Intent intent = new Intent(getBaseContext(), FileDialog.class);
		intent.putExtra(FileDialog.START_PATH, getFilesDir()+File.separator+"code");

		//can user select directories or not
		intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

		//alternatively you can set file filter
		//intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

		startActivityForResult(intent, REQUEST_SAVE);

	}



	public synchronized void onActivityResult(final int requestCode,
			int resultCode, final Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			// if (requestCode == REQUEST_SAVE) {
			System.out.println("Saving...");
			// } else if (requestCode == REQUEST_LOAD) {
			System.out.println("Loading...");
		}

		String filePath = data.getStringExtra(FileDialog.RESULT_PATH);

		//} else if (resultCode == Activity.RESULT_CANCELED) {
		//  Logger.getLogger(AccelerationChartRun.class.getName()).log(
		//               Level.WARNING, "file not selected");

		Log.e(TAG, filePath);






		//s= text.getText().toString();
		//s2 = userInput.getText().toString();
		//File file3 = new File(filePath);


		try
		{   
			line1 = "";
			File file = new File(filePath);
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

		text.setText(line1);
	}

}




