package com.ibm.jusb.os;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;

import javax.usb.*;

import com.ibm.jusb.*;

/**
 * Abstract implementation for UsbDeviceOsImp.
 * <p>
 * This is an optional abstract class that handles all optional methods.  Those
 * methods may be overridden by the implementation if desired.  The implementation
 * is not required to extend this abstract class.
 * @author Dan Streetman
 */
public abstract class AbstractUsbDeviceOsImp implements UsbDeviceOsImp
{
	/**
	 * Synchronously submit a RequestImp.
	 * <p>
	 * This method is implemented using {@link #asyncSubmit(RequestImp) asyncSubmit(RequestImp)}.
	 * @param requestImp The RequestImp.
	 * @throws UsbException If the submission is unsuccessful.
	 */
	public void syncSubmit(RequestImp requestImp) throws UsbException
	{
		asyncSubmit(requestImp);

		requestImp.waitUntilCompleted();

		if (requestImp.isUsbException())
			throw requestImp.getUsbException();
	}

	/**
	 * Synchronously submit a List of RequestImps.
	 * <p>
	 * This method is implemented using {@link #syncSubmit(RequestImp) syncSubmit(RequestImp)}.
	 * This implementation does not throw UsbException; errors are set on a per-RequestImp basis
	 * but overall execution continues.  Persistent errors will cause all remaining RequestImps to
	 * fail and have their UsbException set, but no UsbException will be thrown.
	 * @param list The List.
	 */
	public void syncSubmit(List list) throws UsbException
	{
		for (int i=0; i<list.size(); i++) {
			try { syncSubmit((RequestImp)list.get(i)); }
			catch ( UsbException uE ) { /* continue processing list */ }
		}
	}

	/**
	 * Asynchronously submit a RequestImp.
	 * <p>
	 * The OS-implementation must implement this method.
	 * @param requestImp The RequestImp.
	 * @throws UsbException If the submission is unsuccessful.
	 */
	public abstract void asyncSubmit(RequestImp requestImp) throws UsbException;
}
