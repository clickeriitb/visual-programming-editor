package com.iitb.vpeub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.util.Log;

/*
 * This class handled the file copying and decompression
 */
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

	/*
	 * Set up File variables wrt /data/data/....blah blah
	 */
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

	/*
	 * Wrapping all methods into one
	 */
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

	/*
	 * Copy Files from raw folder to phone, to be executed
	 * 
	 * local.tar.lzma is the compressed file containing the compressed
	 * files for compiler
	 * 
	 * Busybox provides linux utilities
	 * See : http://www.busybox.net/
	 */
	public void copyFiles() {

		//Check for and copy compressed file
		File f = new File(fileDir,compFileName);
		Log.d(TAG,"Starting to Copy");
		if(! f.exists()) {

			copyFileByID(R.raw.archive,compFileName );
			Log.d(TAG,"Compressed File copied");
		}
		else {
			Log.d(TAG,"Compressed File present, not Copying");
		}

		//Cause other threads cant update UI
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.filesCopied();

			}
		});

		//Check for and copy busybox file
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

	/*
	 * Invoke Busybox to decompress
	 * LZMA seems to provide best compression
	 */
	public void unzip() throws FileNotFoundException,IOException {
		Log.d(TAG,"Unzip started");
		File outFile = new File(fileDir,folderName);
		String fileDir = activity.getFilesDir().getAbsolutePath();
		if( ! outFile.exists() ) {
			Log.d(TAG,"Starting");

			// See tar documentation
			String command = fileDir + sep + busyboxFileName + " tar -C " + fileDir + " -xvf " + fileDir + sep + compFileName;
			Log.d(TAG,"Command = " + command);

			//Create new process to invoke "busybox tar"
			Process process = Runtime.getRuntime().exec(command);

			InputStream is = process.getInputStream();

			BufferedReader br=new BufferedReader(new InputStreamReader(is));
			String line =null;
			File file;
			int bytes = 0;

			//tar spews out file names, compute total uncompressed size from them
			// to update the progress bar
			while((line=br.readLine())!=null)
			{	

				file = new File(fileDir,line);
				bytes += file.length();
				final int b = bytes;
				Log.d(TAG,"path = " + file.getAbsolutePath());
				Log.d(TAG,"Progress = "+ bytes);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						activity.setProg(b);

					}
				});
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

	/*
	 * Copy files by their id in raw folder to the files directory
	 */
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



