package com.iitb.vpeconfig;

import java.io.OutputStream;

/*
 * The configuration classfor boards
 * Copied from boards.txt
 * 
 * Incase anyone wants to support more boards, he/she should add to this
 */
public class Config {
	/*
	public static  int i;
	public static final Target uno = new Target( );
	static {
		uno.name="Arduino Uno";
		uno.protocol="arduino";
		uno.maximum_size=32256;
		uno.speed=115200;
		uno.low_fuses=0xff;
		uno.high_fuses=0xde;
		uno.extended_fuses=0x05;
		uno.path="optiboot";
		uno.file="optiboot_atmega328.hex";
		uno.unlock_bits=0x3F;
		uno.lock_bits=0x0F;
		uno.mcu="atmega328p";
		uno.f_cpu=16000000L;
		uno.core="arduino";
		uno.variant="standard";
	}*/
	public final static Target uno;
	static {
		uno = new Uno();
	}

}


