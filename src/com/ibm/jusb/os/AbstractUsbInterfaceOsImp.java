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
 * Default implementation for a UsbInterfaceOsImp.
 * <p>
 * This does local in-Java claiming only.  If the platform supports claiming, it
 * should implement the methods.
 * @author Dan Streetman
 */
public class AbstractUsbInterfaceOsImp implements UsbInterfaceOsImp
{
	/**
	 * Claim this interface (in Java only).
	 */
	public void claim() throws UsbException { claimed = true; }

	/**
	 * Release this interface (in Java only).
	 */
	public void release() { claimed = false; }

	/**
	 * Indicate if this interface is claimed (in Java only).
	 * @return if this interface is claimed.
	 */
	public boolean isClaimed() { return claimed; }

	private boolean claimed = false;

}
