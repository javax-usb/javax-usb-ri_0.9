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

/**
 * Interface representing a single submission.
 * @author Dan Streetman
 */
public interface UsbSubmission
{
	/**
	 * Get the length of transferred data.
	 * @return The length of transferred data.
	 */
	public int getDataLength();

	/**
	 * Set the length of transferred data.
	 * @param len The length of transferred data.
	 */
	public void setDataLength(int len);

	/**
	 * Get the UsbException.
	 * @return The UsbException, or null if none occurred.
	 */
	public UsbException getUsbException();

	/**
	 * Set the UsbException.
	 * @param uE The UsbException.
	 */
	public void setUsbException(UsbException uE);

	/**
	 * Complete this submission.
	 */
	public void complete();

}
