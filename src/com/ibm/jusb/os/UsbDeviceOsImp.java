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
 * Interface for Platform-specific implementation of UsbDevice.
 * <p>
 * All methods are synchronized in the Platform-Independent layer; the
 * implementation does not need to make them Thread-safe.
 * @author Dan Streetman
 */
public interface UsbDeviceOsImp
{
	/**
	 * Synchronously submit a RequestImp.
	 * <p>
	 * This will block until the RequestImp has completed.
	 * The implementation should throw the UsbException if one occurs.
	 * The implementation must perform all actions as specified in the
	 * {@link com.ibm.jusb.RequestImp RequestImp documentation}.
	 * @param requestImp The RequestImp.
	 * @throws UsbException If the submission is unsuccessful.
	 */
	public void syncSubmit(RequestImp requestImp) throws UsbException;

	/**
	 * Synchronously submit a List of RequestImps.
	 * <p>
	 * All items in the List will be RequestImps.  Every RequestImp
	 * should be handled as if passed via
	 * {@link #syncSubmit(RequestImp) single RequestImp syncSubmit},
	 * with the only difference being an error for one of the RequestImps does
	 * not require the implementation to throw a UsbException for the entire List.
	 * The implementation may, at its option, continue processing RequestImps.
	 * In either case (return from the method or throw an UsbException),
	 * all UsbIrpImps must be handled as specified in the
	 * {@link com.ibm.jusb.UsbIrpImp UsbIrpImp documentation}, even if they are not processed.
	 * <p>
	 * Note that the implementation may call each RequestImp's
	 * {@link com.ibm.jusb.RequestImp#complete() complete} method as the RequestImp
	 * completes or after processing all RequestImps in the list.
	 * @param list The List.
	 * @throws UsbException If the one (or more) submissions are unsuccessful (optional).
	 */
	public void syncSubmit(List list) throws UsbException;

	/**
	 * Asynchronously submit a RequestImp.
	 * <p>
	 * This should return as soon as possible.  If a UsbException is
	 * thrown, no other action is required.  Otherwise, the RequestImp is
	 * accepted for processing.  The RequestImp should then be handled the same as
	 * {@link #syncSubmit(RequestImp) single RequestImp syncSubmit}.
	 * @param requestImp The RequestImp.
	 * @throws UsbException If the submission was not accepted by the implementation.
	 */
	public void asyncSubmit(RequestImp requestImp) throws UsbException;
}
