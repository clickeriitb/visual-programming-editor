package com.iitb.vpeub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.util.Log;

import com.iitb.vpeconfig.Config;
import com.iitb.vpeconfig.Target;
public class Compiler {

	//Directory where code is placed (ino)
	File codeDir;

	//Directory where built files are placed
	String buildDir;

	//Absolute path to the code
	String codePath;

	//File name of the cpp file to be generated
	String fileName;

	//Folder for all arduino headers
	File libFolder;

	//path to the core folder corresponding to the board
	String corePath;

	//path to the variant corresponding to the board
	String variantPath;

	String avrBasePath;
	PdePreprocessor preprocessor;
	Activity activity;

	// Absolute path of included header files
	ArrayList<File> importedLibraries;

	//where to build the code
	String buildPath;

	// Maps each Header file to its absolute path
	static HashMap<String, File> importToLibraryTable;

	//The target board
	Target target;


	String TAG = "Compiler Class";


	public Compiler(Activity act) {
		activity = act;
		fileName = activity.getString(R.string.code_file_name);
		codeDir = new File(activity.getFilesDir(),activity.getString(R.string.code_folder));
		libFolder = new File(activity.getFilesDir(),activity.getString(R.string.lib_folder));
		avrBasePath = new File(activity.getFilesDir(),activity.getString(R.string.avr_path)).getAbsolutePath();
		target = Config.uno;
		corePath = new File(activity.getFilesDir(),activity.getString(R.string.core_folder)).getAbsolutePath()+File.separator+target.getCore();
		variantPath =  new File(activity.getFilesDir(),activity.getString(R.string.variant_folder)).getAbsolutePath()+File.separator+target.getVariant();
		buildPath =new File(activity.getFilesDir(),activity.getString(R.string.build_folder)).getAbsolutePath();



	}

	/**
	 * Preprocess sketch.ino and output the cpp file in build folder
	 * @throws Exception
	 */
	public  void preProcess() throws Exception  {

		Log.d(TAG,"Preprocessing Started");
		preprocessor = new PdePreprocessor(activity);

		String code = readFile(new File(codeDir,fileName));


		preprocessor.writePrefix(code , activity.getString(R.string.code_file_name), null);
		preprocessor.write();

		Log.d(TAG,"pre Ex imports" + preprocessor.getExtraImports());


		//All files/folders in libFolder
		String list[] = libFolder.list(null);
		
		for(String l:list) {
			Log.d(TAG,"lib file = " +l);
		}

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
		

		Log.d(TAG,"Header Table" + importToLibraryTable.toString());
		Log.d(TAG,"Preprocessing Ended");


		Log.d(TAG,"Listing imports");

		importedLibraries = new ArrayList<File>();

		for (String item : preprocessor.getExtraImports()) {
			File libPath = (File) importToLibraryTable.get(item);

			if (libPath != null && !importedLibraries.contains(libPath)) {
				importedLibraries.add(libPath);
				System.out.println(libPath);
				//libraryPath += File.pathSeparator + libFolder.getAbsolutePath();
			}
		}

		Log.d(TAG,"Listing imports...done");


	}
	
	/***
	 * Do everything necessary for compilation
	 * Many steps are needed to get the final hex file,
	 * Comments inside the function will make it clear
	 */
	public void compile() {


		Log.d(TAG,"Compilation started");
		
		//List of all the include paths for all the .h files
		List<String> includePaths = new ArrayList<String>();
		
		//This path contains Ardhuino.h for the core, along with other files
		includePaths.add(corePath);

		
		//This path contains header files containing pin mappings
		if (variantPath != null) includePaths.add(variantPath);
		for (File file : importedLibraries) {
			includePaths.add(file.getPath());
		}
		
		Log.d(TAG, "INC paths = " + importedLibraries.toString());

		Log.d(TAG,"Compiling user's source files");
		List<File> objectFiles = new ArrayList<File>();
		
		// Compile users source files, in this case it will compile only one file , 
		// the file generated through Blockly
		objectFiles.addAll(
				compileFiles(avrBasePath, buildPath, includePaths,
						findFilesInPath(buildPath, "S", false),
						findFilesInPath(buildPath, "c", false),
						findFilesInPath(buildPath, "cpp", false),
						target));
		//sketchIsCompiled = true;

		Log.d(TAG,"Compilation ended");
	}
	
	
	/**
	 * Read a file and output as a string
	 * @param f - the File
	 * @return - the String
	 * @throws IOException
	 */
	private String readFile(File f) throws IOException {
		InputStream in = new FileInputStream(f);
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

	/**
	 * Returns list of strings of files in path with ".h" extension
	 * @param path - the path to be searched
	 * @return - array of atrings
	 * @throws IOException
	 */
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

	/**
	 * Returns List of files in path with required extension
	 * @param path - the path to search
	 * @param extension - extension to be searched
	 * @param recurse - whether or not to recurse
	 * @return - ArrayList of Files
	 */
	static public ArrayList<File> findFilesInPath(String path, String extension,
			boolean recurse) {
		return findFilesInFolder(new File(path), extension, recurse);
	}
	/**
	 * 
	 * @param folder -  file pointing to folder to search for
	 * @param extension - extension to search for
	 * @param recurse - whether or not to recurse
	 * @return - ArrayList of Files
	 */
	static public ArrayList<File> findFilesInFolder(File folder, String extension,
			boolean recurse) {
		ArrayList<File> files = new ArrayList<File>();

		if (folder.listFiles() == null) return files;

		for (File file : folder.listFiles()) {
			if (file.getName().startsWith(".")) continue; // skip hidden files

			if (file.getName().endsWith("." + extension))
				files.add(file);

			if (recurse && file.isDirectory()) {
				files.addAll(findFilesInFolder(file, extension, true));
			}
		}

		return files;
	}

	/**
	 * Invokes the compiler to compile the c,s and cpp files into the builpath
	 * @param avrBasePath - path where compiler resides
	 * @param buildPath - path where to output the compiled files
	 * @param includePaths - path for all headers
	 * @param sSources - C Files
	 * @param cSources - S Files
	 * @param cppSources -  cpp files
	 * @param target - the board to compile for
	 * @return - List of File of the object files formed
	 */
	private List<File> compileFiles(String avrBasePath,
			String buildPath, List<String> includePaths,
			List<File> sSources, 
			List<File> cSources, List<File> cppSources,
			Target target)
			{

		List<File> objectPaths = new ArrayList<File>();
		
		
		
		for (File file : sSources) {
			String objectPath = buildPath + File.separator + file.getName() + ".o";
			objectPaths.add(new File(objectPath));
			List<String> comm = getCommandCompilerS(avrBasePath, includePaths,
					file.getAbsolutePath(),
					objectPath,
					target);

			Log.d(TAG,comm.toString());
			execute(comm);

		}

		for (File file : cSources) {
			String objectPath = buildPath + File.separator + file.getName() + ".o";
			File objectFile = new File(objectPath);
			objectPaths.add(objectFile);

			List<String> comm = getCommandCompilerC(avrBasePath, includePaths,
					file.getAbsolutePath(),
					objectPath,
					target);

			Log.d(TAG,comm.toString());
			execute(comm);
		}
		 
		for (File file : cppSources) {
			String objectPath = buildPath + File.separator + file.getName() + ".o";
			File objectFile = new File(objectPath);
			objectPaths.add(objectFile);
			List<String> comm = getCommandCompilerCPP(avrBasePath, includePaths,
					file.getAbsolutePath(),
					objectPath,
					target);

			Log.d(TAG,comm.toString());
			execute(comm);
		}

		return objectPaths;
			}

	/**
	 * Take the parameters and generate a command to invoke gcc , g++ etc.
	 * Generates command for compiling S files
	 * @param avrBasePath - path to gcc, etc.
	 * @param includePaths - paths to include for header files
	 * @param sourceName - name of .S file
	 * @param objectName - name of .o file
	 * @param t - the target object
	 * @return
	 */
	private List<String> getCommandCompilerS(String avrBasePath, List<String> includePaths,
			String sourceName, String objectName, Target t) {
		List<String> baseCommandCompiler = new ArrayList<String>(Arrays.asList(new String[] {
				avrBasePath +File.separator + "avr-gcc",
				"-c", // compile, don't link
				"-g", // include debugging info (so errors include line numbers)
				"-assembler-with-cpp",
				"-mmcu=" + t.getMCU(),
				"-DF_CPU=" + t.getFCPU(),      
				"-DARDUINO=" + Config.REVISION,
				"-DUSB_VID=" + t.getVID(),
				"-DUSB_PID=" + t.getPID(),
		}));

		for (int i = 0; i < includePaths.size(); i++) {
			baseCommandCompiler.add("-I" + (String) includePaths.get(i));
		}

		baseCommandCompiler.add(sourceName);
		baseCommandCompiler.add("-o"+ objectName);

		return baseCommandCompiler;
	}

	/**
	 * Take the parameters and generate a command to invoke gcc , g++ etc.
	 * Generates command for compiling C files
	 * @param avrBasePath - path to gcc, etc.
	 * @param includePaths - paths to include for header files
	 * @param sourceName - name of .S file
	 * @param objectName - name of .o file
	 * @param t - the target object
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static private List<String> getCommandCompilerC(String avrBasePath, List includePaths,
			String sourceName, String objectName, Target t) {

		List baseCommandCompiler = new ArrayList(Arrays.asList(new String[] {
				avrBasePath +File.separator+ "avr-gcc",
				"-c", // compile, don't link
				"-g", // include debugging info (so errors include line numbers)
				"-Os", // optimize for size
				Config.verbose ? "-Wall" : "-w", // show warnings if verbose
						"-ffunction-sections", // place each function in its own section
						"-fdata-sections",
						"-mmcu=" + t.getMCU(),
						"-DF_CPU=true ? " + t.getFCPU(),
						"-MMD", // output dependancy info
						"-DUSB_VID=" + t.getVID(),
						"-DUSB_PID=" + t.getPID(),
						"-DARDUINO=" + Config.REVISION, 
		}));

		for (int i = 0; i < includePaths.size(); i++) {
			baseCommandCompiler.add("-I" + (String) includePaths.get(i));
		}

		baseCommandCompiler.add(sourceName);
		baseCommandCompiler.add("-o");
		baseCommandCompiler.add(objectName);

		return baseCommandCompiler;
	}


	
	/**
	 * Take the parameters and generate a command to invoke gcc , g++ etc.
	 * Generates command for compiling CPP files
	 * @param avrBasePath - path to gcc, etc.
	 * @param includePaths - paths to include for header files
	 * @param sourceName - name of .S file
	 * @param objectName - name of .o file
	 * @param t - the target object
	 * @return
	 */
	static private List<String> getCommandCompilerCPP(String avrBasePath,
			List<String> includePaths, String sourceName, String objectName,
			Target t) {

		List<String> baseCommandCompilerCPP = new ArrayList<String>(Arrays.asList(new String[] {
				avrBasePath +File.separator+ "avr-g++",
				"-c", // compile, don't link
				"-g", // include debugging info (so errors include line numbers)
				"-Os", // optimize for size
				Config.verbose  ? "-Wall" : "-w", // show warnings if verbose
						"-fno-exceptions",
						"-ffunction-sections", // place each function in its own section
						"-fdata-sections",
						"-mmcu=" + t.getMCU(),
						"-DF_CPU=" + t.getFCPU(),
						"-MMD", // output dependancy info
						"-DUSB_VID=" + t.getVID(),
						"-DUSB_PID=" + t.getPID(),      
						"-DARDUINO=" + Config.REVISION,
						"-save-temps=obj",
		}));

		for (int i = 0; i < includePaths.size(); i++) {
			baseCommandCompilerCPP.add("-I" + (String) includePaths.get(i));
		}

		baseCommandCompilerCPP.add(sourceName);
		baseCommandCompilerCPP.add("-o");
		baseCommandCompilerCPP.add(objectName);

		return baseCommandCompilerCPP;
	}

	/**
	 * Execute the command specified
	 * Compiler may generate error messages, they will be spewed out on the LogCat
	 * @param commandList - List of commands "avr-gcc,-c,-whatever etc etc "
	 */
	private void execute(List<String> commandList) {
		String[] command = new String[commandList.size()];
		commandList.toArray(command);
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(command);
			InputStream is = process.getErrorStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(is));
			String line = br.readLine();
			while(line!=null)
			{
				Log.d(TAG,"Process says - "+line);
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG,"Failure to exec");
		}

		

	}
}


