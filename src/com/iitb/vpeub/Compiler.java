package com.iitb.vpeub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class Compiler {

	String codeDir;
	String buildDir;
	String fileName;
	String libDir;
	PdePreprocessor preprocessor;
	
	
	// Absolute path of included header files
	ArrayList<File> importedLibraries;
	
	// Maps each Header file to its absolute path
	static HashMap<String, File> importToLibraryTable;

	public static void main(String args[]) throws Exception {
		//Compiler c = new Compiler("blink.ino");
		//c.preProcess();
		//Decompressor d = new Decompressor("local.tar.xz", "local.tar");
		//d.start();
	}

	public Compiler(String fn) {
		fileName = fn;
		buildDir = System.getProperty("user.dir") + "/build/";
		codeDir = System.getProperty("user.dir") + "/code/";
		libDir = System.getProperty("user.dir") + "/lib/";
	}

	public  void preProcess() throws Exception  {
		preprocessor = new PdePreprocessor();

		String code = readFile(codeDir + fileName);

		 
		preprocessor.writePrefix(code,buildDir , "sketch", null);
		preprocessor.write();

		File libFolder = new File(libDir);
		
		//All files/folders in libFolder
		String list[] = libFolder.list(null);

		importToLibraryTable = new HashMap<String,File>();

		//Map all .h files to 
		for (String libraryName : list) {
			File subfolder = new File(libFolder, libraryName);

			//libraries.add(subfolder);
			try {
				String packages[] =
						headerListFromIncludePath(subfolder.getAbsolutePath());
				for (String pkg : packages) {
					importToLibraryTable.put(pkg, subfolder);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		

		
	    importedLibraries = new ArrayList<File>();

	    for (String item : preprocessor.getExtraImports()) {
	      File libPath = (File) importToLibraryTable.get(item);

	      if (libPath != null && !importedLibraries.contains(libPath)) {
	        importedLibraries.add(libPath);
	        System.out.println(libPath);
	        //libraryPath += File.pathSeparator + libFolder.getAbsolutePath();
	      }
	    }





	}
	private String readFile(String fn) throws IOException {
		InputStream in = new FileInputStream(fn);
		byte[] b  = new byte[in.available()];
		int len = b.length;
		int total = 0;

		while (total < len) {
			int result = in.read(b, total, len - total);
			if (result == -1) {
				break;
			}
			total += result;
		}
		in.close();
		return new String( b );

	}


	static public String[] headerListFromIncludePath(String path) throws IOException {
		FilenameFilter onlyHFiles = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".h");
			}
		};

		String[] list = (new File(path)).list(onlyHFiles);
		if (list == null) {
			throw new IOException();
		}
		return list;
	}


}


