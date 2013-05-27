package com.iitb.vpeub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.R.bool;
import android.app.Activity;
import android.os.StatFs;
import android.util.Log;

public class Decompressor {
	String input,output;
	StartActivity activity;
	String fileDir;
	String compFileName;
	String busyboxFileName;
	String tarFileName;
	String folderName;
	String compFileFullPath;
	String folderFullPath;
	String TAG = "Decompressor";
	String sep;
	int noOfFiles;

	public Decompressor(StartActivity act) {

		//input = inp;
		//output = out;
		activity = act;

		fileDir = activity.getFilesDir().getAbsolutePath();
		compFileName = activity.getResources().getString(R.string.comp_file);

		busyboxFileName = activity.getResources().getString(R.string.busybox_file);
		tarFileName = activity.getResources().getString(R.string.tar_file);
		folderName = activity.getResources().getString(R.string.folder_file);
		noOfFiles = activity.getResources().getInteger(R.integer.no_of_files);
		sep = File.separator;
		compFileFullPath = fileDir + sep + compFileName;
		folderFullPath = fileDir + sep + folderName;

	}

	public void doYourJob() {
		copyFiles();
		try {
			unzip();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void copyFiles() {
		File f = new File(fileDir,compFileName);
		Log.d(TAG,"Starting to Copy");
		if(! f.exists()) {

			copyFileByID(R.raw.local,compFileName );
			Log.d(TAG,"Compressed File copied");
		}
		else {
			Log.d(TAG,"Compressed File present, not Copying");
		}
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.filesCopied();

			}
		});


		f = new File(fileDir,busyboxFileName);
		if(! f.exists()) {

			copyFileByID(R.raw.busybox,busyboxFileName );
			Log.d(TAG,"Busybox File copied");
		}
		else {
			Log.d(TAG,"Busybox File present, not Copying");
		}
		f.setExecutable(true);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.busyboxCopied();

			}
		});


	}
	public void unzip() throws FileNotFoundException,IOException {
		int size;
		Log.d(TAG,"Unzip started");
		File outFile = new File(fileDir,folderName);
		String fileDir = activity.getFilesDir().getAbsolutePath();
		if( ! outFile.exists() ) {
			Log.d(TAG,"Starting");
			//Process p = Runtime.getRuntime().exec("file:///android_asset/avr_gcc");
			String command = fileDir + sep + busyboxFileName + " tar -C " + fileDir + " -xvf " + fileDir + sep + compFileName;
			Log.d(TAG,"Command = " + command);
			Process process = Runtime.getRuntime().exec(command);
			
			InputStream is = process.getInputStream();

			BufferedReader br=new BufferedReader(new InputStreamReader(is));
			String line =null;
			int i=0;
			File file;
			int bytes = 0;
			while((line=br.readLine())!=null)
			{	
				
				file = new File(fileDir,line);
				bytes += file.length();
				final int b = bytes;
				Log.d(TAG,"path = " + file.getAbsolutePath());
				//able to read line only when database name like abc,datastore etc...
				Log.d(TAG,"Progress = "+ bytes);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.setProg(b);

					}
				});
				i++;
				//Log.d(TAG,line);
			}

			Log.d(TAG,"Completed extracting");
		}
		else {
			Log.d(TAG,"Folder present, unzip skipped");
		}
		
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.envReady();

			}
		});

	}
	public void copyFileByID(int id,String fn){
		InputStream ins  = activity.getResources().openRawResource(id);

		try {
			//byte[] buffer = new byte[ins.available()];
			//ins.read(buffer);
			//ins.close();
			FileOutputStream fos = activity.openFileOutput(fn, Activity.MODE_PRIVATE);


			byte[] buf = new byte[8192];
			int size;
			while ((size = ins.read(buf)) != -1)
				fos.write(buf, 0, size);

			fos.close();
			ins.close();


		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}



