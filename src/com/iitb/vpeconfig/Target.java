package com.iitb.vpeconfig;


/*
 * Class which holds config details for each board type
 */
public class Target {
	protected String name;
	protected String protocol;
	protected int maximum_size=0;
	protected int speed;
	protected int low_fuses;
	protected int high_fuses;
	protected int extended_fuses=0x05;
	protected String path;
	protected String file;
	protected int unlock_bits;
	protected int lock_bits;
	protected String mcu;
	protected long f_cpu;
	protected String core;
	protected String variant;
	
	
	public String getName() {
		return name;
	}

	public String getProtocol() {
		return protocol;
	}

	public int getMaxSize() {
		return maximum_size;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getLowFuses() {
		return low_fuses;
	}

	public int getHighFuses() {
		return high_fuses;
	}
	
	public int getExFuses() {
		return extended_fuses;
	}
	
	public String getPath() {
		return path;
	}

	public String getFile() {
		return file;
	}
	
	public int getUnlockBits() {
		return unlock_bits;
	}
	
	public int getLockBits() {
		return lock_bits;
	}

	public String getMCU() {
		return mcu;
	}
	
	public long getFCPU() {
		return f_cpu;
	}
	
	public String fetCore() {
		return core;
	}
	
	public String getVariant() {
		return variant;
	}
}

class Uno extends Target {
	Uno (){
		speed = 100;
	}

	
}