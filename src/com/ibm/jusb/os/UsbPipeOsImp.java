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
 * Interface for Platform-specific implementation of UsbPipe.
 * <p>
 * All methods are synchronized in the Platform-Independent layer; the
 * implementation does not need to make them Thread-safe.
 * @author Dan Streetman
 */
public interface UsbPipeOsImp
{
	/**
	 * Open this pipe.
	 * <p>
	 * The platform can perform whatever operations it likes.
	 * This method does not currently require the platform to guarantee
	 * anything after returning.
	 * @exception javax.usb.UsbException If this pipe could not be opened.
	 */
	public void open() throws UsbException;

	/**
	 * Synchronously submits this UsbIrpImp to the platform implementation.
	 * <p>
	 * The implementation must perform all actions specified in the
	 * {@link com.ibm.jusb.UsbIrpImp UsbIrpImp's documentation}.  In addition,
	 * if a UsbException occurs, the implementation should throw it.
	 * @param irp the UsbIrpImp to use for this submission.
	 * @exception javax.usb.UsbException If the data transfer was unsuccessful.
	 */
	public void syncSubmit( UsbIrpImp irp ) throws UsbException;

	/**
	 * Synchronously submits a List of UsbIrpImps to the platform implementation.
	 * <p>
	 * All items in the List will be UsbIrpImps.  Each UsbIrpImp should be
	 * handled in the same manner as {@link #syncSubmit(UsbIrpImp) syncSubmit},
	 * with the only difference being an error for one of the UsbIrpImps does
	 * not require the implementation to throw a UsbException for the entire List.
	 * The implementation may, at its option, continue processing UsbIrpImps.
	 * In either case (return from the method or throw an UsbException),
	 * all UsbIrpImps must be handled as specified in the
	 * {@link com.ibm.jusb.UsbIrpImp UsbIrpImp documentation}, even if they are not processed.
	 * <p>
	 * The implementation may call each UsbIrpImp's {@link com.ibm.jusb.UsbIrpImp#complete() complete}
	 * method as each UsbIrpImp completes, or defer calling until all
	 * UsbIrpImps in the list have been processed.
	 * @param list the UsbIrpImps to use for this submission.
	 * @exception javax.usb.UsbException If any of the UsbIrpImps were unsuccessful (optional).
	 */
	public void syncSubmit( List list ) throws UsbException;

	/**
	 * Asynchronously submits this UsbIrpImp to the platform implementation.
	 * <p>
	 * This should return as soon as possible.	The implementation must perform
	 * all actions specified in the
	 * {@link com.ibm.jusb.UsbIrpImp UsbIrpImp's documentation}.  In addition,
	 * if the UsbIrpImp is not accepted for processing (i.e. before returning from
	 * this method), a UsbException should be thrown.  Otherwise (i.e. after returning
	 * from this method) no UsbException can be thrown (but should be set on the UsbIrpImp).
	 * @param irp the UsbIrpImp to use for this submission
	 * @exception javax.usb.UsbException If the UsbIrpImp was not accepted by the implementation.
	 */
	public void asyncSubmit( UsbIrpImp irp ) throws UsbException;

	/**
	 * Asynchronously submits a List of UsbIrpImps to the platform implementation.
	 * <p>
	 * This should return as soon as possible.	If any of the UsbIrpImps
	 * were not accepted by the implementation, no further UsbIrpImps should be attempted,
	 * and the UsbException should be thrown.  UsbIrpImps already accepted should continue
	 * their normal execution.	The implementation must perform all actions specified in the
	 * {@link com.ibm.jusb.UsbIrpImp UsbIrpImp's documentation} for all UsbIrpImps in the list.
	 * <p>
	 * Note that the UsbIrpImp that fails (if any) and all those after it may be completed immediately,
	 * which will result in UsbIrpImps in the beginning of the List being in-progress, while the UsbIrpImps
	 * in the end of the List will be complete.
	 * @param irp the UsbIrpImp to use for this submission
	 * @exception javax.usb.UsbException If one of the UsbIrpImps was not accepted by the implementation.
	 */
	public void asyncSubmit( List list ) throws UsbException;

	/**
	 * Stop all submissions in progress.
	 * <p>
	 * This should not return until all submissions have been aborted
	 * and <i>are no longer in progress</i> (i.e., the pipe is in a non-busy state).
	 * <p>
	 * The implementation may assume no more submissions will occur while this is executing.
	 */
	public void abortAllSubmissions();

	/**
	 * Close the pipe.
	 */
	public void close();
}
