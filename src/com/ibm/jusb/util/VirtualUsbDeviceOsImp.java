package com.ibm.jusb.util;

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
import com.ibm.jusb.os.*;

/**
 * Virtual UsbDeviceOsImp implementation.
 * @author Dan Streetman
 */
public class VirtualUsbDeviceOsImp extends AbstractUsbDeviceOsImp implements UsbDeviceOsImp
{
	public VirtualUsbDeviceOsImp() { }

	/**
	 * Synchronously submit a RequestImp.
	 * @param requestImp The RequestImp.
	 * @return The number of bytes transferred.
	 * @throws UsbException If the submission is unsuccessful.
	 */
	public void syncSubmit(RequestImp requestImp) throws UsbException
	{
		byte[] data = requestImp.getData();
		byte bmRequestType = data[0];
		byte bRequest = data[1];
		short wValue = (short)((data[3] << 8) | data[2]);
		short wIndex = (short)((data[5] << 8) | data[4]);

		/* handle requests */

		throw new UsbException("Not implemented");
	}

	/**
	 * Asynchronously submit a RequestImp.
	 * @param requestImp The RequestImp.
	 */
	public void asyncSubmit(RequestImp requestImp) throws UsbException
	{
		runnableManager.add(new AsyncRunnable(requestImp));
	}

	//**************************************************************************
	// Instance methods

	private RunnableManager runnableManager = new RunnableManager();

	//**************************************************************************
	// Class constants

	public static final String VIRTUAL_ROOT_HUB_MANUFACTURER = "JSR80 Reference Implementation (platform-independent section)";
	public static final String VIRTUAL_ROOT_HUB_PRODUCT = "JSR80 Virtual Root Hub";
	public static final String VIRTUAL_ROOT_HUB_SERIALNUMBER = "19741113";

	//**************************************************************************
	// Inner classes

	private class AsyncRunnable implements Runnable
	{
		public AsyncRunnable(RequestImp r) { requestImp = r; }

		public void run()
		{
			try { syncSubmit(requestImp); }
			catch ( UsbException uE ) { requestImp.setUsbException(uE); }
		}

		private RequestImp requestImp = null;
	}

}
