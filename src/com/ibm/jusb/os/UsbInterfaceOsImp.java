package com.ibm.jusb.os;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;

import com.ibm.jusb.*;

/**
 * UsbInterface platform implementation.
 * @author Dan Streetman
 */
public interface UsbInterfaceOsImp
{
	/**
	 * Claim this interface.
	 * @exception UsbException if the interface could not be claimed.
	 */
	public void claim() throws UsbException;

	/**
	 * Release this interface.
	 */
	public void release();

	/**
	 * Indicate if this interface is claimed.
	 * <p>
	 * The claim must represent at least {@link #claim() Java claims}
	 * and depending on implementation may represent native platform claims.
	 * @return if this interface is claimed.
	 */
	public boolean isClaimed();

}
