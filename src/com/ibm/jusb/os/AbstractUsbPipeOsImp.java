package com.ibm.jusb.os;

/**
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
 * Abstract implementation for UsbPipeOsImp.
 * <p>
 * This is an optional abstract class that handles all optional methods.  Those
 * methods may be overridden by the implementation if desired.	The implementation
 * does not have to extend this abstract class.
 * @author Dan Streetman
 */
public abstract class AbstractUsbPipeOsImp implements UsbPipeOsImp
{
	/**
	 * Open this pipe.
	 * <p>
	 * This is implemented as a no-op.
	 */
	public void open() throws UsbException { }

	/**
	 * Close the pipe.
	 * <p>
	 * This is implemented as a no-op.
	 */
	public void close() { }

	/**
	 * Synchronously submits this UsbIrpImp to the platform implementation.
	 * <p>
	 * This is implemented using {@link #asyncSubmit(UsbIrpImp) asyncSubmit(UsbIrpImp)}.
	 * @param irp the UsbIrpImp to use for this submission.
	 * @exception javax.usb.UsbException If the data transfer was unsuccessful.
	 */
	public void syncSubmit( UsbIrpImp irp ) throws UsbException
	{
		asyncSubmit(irp);

		irp.waitUntilCompleted();

		if (irp.isInUsbException())
			throw irp.getUsbException();
	}

	/**
	 * Synchronously submits a List of UsbIrpImps to the platform implementation.
	 * <p>
	 * This is implemented using {@link #syncSubmit(UsbIrpImp) syncSubmit(UsbIrpImp)}.
	 * This implementation does not throw UsbException; errors are set on a per-UsbIrpImp basis
	 * but overall execution continues.	 Persistent errors will cause all remaining UsbIrpImps to
	 * fail and have their UsbException set, but no UsbException will be thrown.
	 * @param list the UsbIrpImps to use for this submission.
	 */
	public void syncSubmit( List list ) throws UsbException
	{
		for (int i=0; i<list.size(); i++) {
			try { syncSubmit((UsbIrpImp)list.get(i)); }
			catch ( UsbException uE ) { /* continue processing list */ }
		}
	}

	/**
	 * Asynchronously submits a List of UsbIrpImps to the platform implementation.
	 * <p>
	 * This is implemented using {@link #asyncSubmit(UsbIrpImp) asyncSubmit(UsbIrpImp)}.
	 * If a UsbException is thrown while submitting a UsbIrpImp, the failed UsbIrpImp and
	 * all further UsbIrpImps in the list are set to that UsbException and not submitted.
	 * Already submitted UsbIrpImps will continue to their normal completion.
	 * The UsbException is then thrown.
	 * @param list The List of UsbIrpImps.
	 * @exception javax.usb.UsbException If one of the UsbIrpImps was not accepted by the implementation.
	 */
	public void asyncSubmit( List list ) throws UsbException
	{
		int i = 0;

		try {
			for (i=0; i<list.size(); i++)
				asyncSubmit((UsbIrpImp)list.get(i));
		} catch ( UsbException uE ) {
			for (int j=i; j<list.size(); j++) {
				((UsbSubmission)list.get(j)).setUsbException(uE);
				((UsbSubmission)list.get(j)).complete();
			}
			throw uE;
		}
	}

	/**
	 * Asynchronously submits this UsbIrpImp to the platform implementation.
	 * <p>
	 * The OS-implementation must implement this method.
	 * @param irp the UsbIrpImp to use for this submission
	 * @exception javax.usb.UsbException If the initial submission was unsuccessful.
	 */
	public abstract void asyncSubmit( UsbIrpImp irp ) throws UsbException;

	/**
	 * Stop all submissions in progress.
	 * <p>
	 * The OS-implementation must implement this method.
	 */
	public abstract void abortAllSubmissions();

	private UsbIrpImpFactory usbIrpImpFactory = new UsbIrpImpFactory();
}
