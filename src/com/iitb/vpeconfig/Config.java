package com.iitb.vpeconfig;


/*
 * The configuration classfor boards
 * Copied from boards.txt
 * 
 * Incase anyone wants to support more boards, he/she should add to this
 */
public class Config {
	
	public static final int REVISION = 105;
	public final static Target uno = new Uno();
	static public boolean verbose = false;

}


