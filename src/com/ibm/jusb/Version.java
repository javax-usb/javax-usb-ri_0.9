package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

/**
 * Version class that prints the current version numbers.
 * <p>
 * This maintains the version number of the current javax.usb Platform-Independent Reference Implementation.
 * The RI version number does not have to match the API version number.
 * @author Dan Streetman
 */
public class Version
{
	/**
	 * Print this text to stdout (with appropriate version numbers):
	 * <p>
	 * javax.usb API version %lt;getApiVersion()><br>
	 * javax.usb Platform-Independent RI version &lt;getRiVersion()><br>
	 * @param args a String[] of arguments.
	 */
	public static void main( String[] args )
	{
		System.out.println( "javax.usb API version " + getApiVersion() );
		System.out.println( "javax.usb Platform-Independent RI version " + getRiVersion() );
	}

	/**
	 * Get the version number of the API that this implements.
	 * <p>
	 * This should match a version number found in javax.usb.Version.
	 * @return The version number of the API this implements.
	 */
	public static String getApiVersion() { return VERSION_API; }

	/**
	 * Get the version number of this RI.
	 * <p>
	 * The format of this is &lt;major>.&lt;minor>[.&lt;revision>]
	 * <p>
	 * The revision number is optional; a missing revision
	 * number (i.e., version X.X) indicates the revision number is zero
	 * (i.e., version X.X.0).
	 * @return The version number of this Platform-Independent RI.
	 */
	public static String getRiVersion() { return VERSION_PLATFORM_INDEPENDENT_RI; }

	private static final String VERSION_API = "0.9.1";
	private static final String VERSION_PLATFORM_INDEPENDENT_RI = "0.9.3";
}
