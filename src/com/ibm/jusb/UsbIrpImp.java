package com.ibm.jusb;

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
import javax.usb.util.*;
import javax.usb.event.*;

import com.ibm.jusb.os.*;
import com.ibm.jusb.util.*;

/**
 * UsbIrp implementation.
 * <p>
 * The user must provide some fields:
 * <ul>
 * <li>{@link #getData() data} via its {@link #setData(byte[]) setter}.</li>
 * <li>{@link #getAcceptShortPacket() short packet policy} via its {@link #setAcceptShortPacket(boolean) setter}.</li>
 * </ul>
 * <p>
 * The os-indpendent implementation will set fields:
 * <ul>
 * <li>The {@link #getNumber() number} via its {@link #setNumber(long) setter}.</li>
 * <li>The {@link #getSequenceNumber() sequence number} via its {@link #setSequenceNumber(long) setter}.</li>
 * <li>{@link #getUsbPipeImp() UsbPipeImp} via its {@link #setUsbPipeImp(UsbPipeImp) setter}.</li>
 * </ul>
 * <p>
 * The os implementation will then process this.
 * If processing is successful, the implementation will set the
 * {@link #getDataLength() data length} via its {@link #setDataLength(int) setter};
 * if unsuccessful, the implementation will set the
 * {@link #getUsbException() UsbException} via its {@link #setUsbException(UsbException) setter}.
 * In either case the implementation will then set this {@link #complete() completed}.
 * <p>
 * If the user provided their own UsbIrp implementation, then the UsbPipeImp will 'wrap' their
 * implementation with this UsbIrpImp by {@link #setUsbIrp(UsbIrp) setting} the local
 * {@link #getUsbIrp() UsbIrp}.  If this has a local UsbIrp when it is
 * {@link #complete() completed}, this will set the proper fields on the wrapped UsbIrp.
 * @author Dan Streetman
 */
public class UsbIrpImp implements UsbIrp,UsbPipe.SubmitResult,UsbSubmission
{
	/** Constructor. */
	public UsbIrpImp() { }

	/**
	 * Constructor.
	 * @param irp The UsbIrp this should wrap.
	 */
	public UsbIrpImp(UsbIrp irp) { setUsbIrp(irp); }

	//*************************************************************************
	// Public methods

	/**
	 * @return a unique number for this submission.
	 */
	public long getNumber() { return number; }

	/** @return the sequence number of this submission */
	public long getSequenceNumber() { return sequenceNumber; }

	/** @return the UsbPipe associated with this submission */
	public UsbPipe getUsbPipe() { return getUsbPipeImp(); }

	/** @return the UsbPipeImp */
	public UsbPipeImp getUsbPipeImp() { return usbPipeImp; }

	/** @return the data associated with this submission */
	public byte[] getData() { return data; }

	/** @param data the data associated with the submission */
	public void setData( byte[] newData ) { data = newData; }

	/**
	 * If this UsbIrp is in progress on a UsbPipe.
	 * @return if this UsbIrp is active
	 */
	public boolean isActive() { return active; }

	/** @return true if this submit has completed */
	public boolean isCompleted() { return completed; }

	/** @return if a UsbException occured during submission */
	public boolean isInUsbException() { return ( null != getUsbException() ); }

	/** @return the number of bytes transmitted in this submission */
	public int getDataLength() { return dataLength; }

	/** Wait (block) until this submission is completed */
	public void waitUntilCompleted() { waitUntilCompleted( 0 ); }

	/** Wait (block) until this submission is completed */
	public void waitUntilCompleted( long msecs ) { waitUntilCompleted( msecs, 0 ); }

	/** Wait (block) until this submission is completed */
	public void waitUntilCompleted( long msecs, int nsecs )
	{
		if (isCompleted())
			return;

		synchronized ( waitLock ) {
			waitCount++;
			while (!isCompleted()) {
				try { waitLock.wait( msecs, nsecs ); }
				catch ( InterruptedException iE ) { }
			}
			waitCount--;
		}
	}

	/** @return the UsbException that occured during submission */
	public UsbException getUsbException() { return usbException; }

	/**
	 * @return if a short packet during this submission should be accepted (no error)
	 */
	public boolean getAcceptShortPacket() { return acceptShortPacket; }

	/**
	 * @param accept if a short packet during this submission should be accepted (no error)
	 */
	public void setAcceptShortPacket( boolean accept ) { acceptShortPacket = accept; }

	/**
	 * Recycle this UsbIrpImp
	 * <p>
	 * This should be called when the UsbIrpImp is no longer needed
	 */
	public void recycle() { }

	/**
	 * Sets the number for this submission
	 * @param i the new number
	 */
	public void setNumber( long l )
	{
		number = l;

		try { getUsbIrp().setNumber(l); }
		catch ( NullPointerException npE ) { }
	}

	/** @param the seqeunce number of this submision */
	public void setSequenceNumber( long sn )
	{
		sequenceNumber = sn;

		try { getUsbIrp().setSequenceNumber(sn); }
		catch ( NullPointerException npE ) { }
	}

	/**
	 * Sets the pipe for this submission
	 * @param pipe the pipe to use
	 */
	public void setUsbPipe( UsbPipe pipe ) { setUsbPipeImp((UsbPipeImp)pipe); }

	/**
	 * Sets the pipe for this submission
	 * @param pipe the pipe to use
	 */
	public void setUsbPipeImp( UsbPipeImp pipe ) { usbPipeImp = pipe; }

	/**
	 * Set isActive.
	 * <p>
	 * This indicates whether the UsbIrp is in progress on a UsbPipe.
	 * @param b whether this is active
	 */
	public void setActive( boolean b ) { active = b; }

	/**
	 * Sets isCompleted
	 * @param b whether this is done
	 */
	public void setCompleted( boolean b ) { completed = b; }

	/**
	 * Complete this submission.
	 * <p>
	 * This will wake up all
	 * {@link #waitUntilCompleted() waiting Threads}.
	 */
	public void complete()
	{
		setCompleted(true);

		notifyCompleted();

//FIXME - the user's UsbIrp methods could block or generate Exception/Error which will cause problems
		try {
			UsbIrp irp = getUsbIrp();
			irp.setUsbException(getUsbException());
			irp.setDataLength(getDataLength());
			irp.setCompleted(true);
		} catch ( NullPointerException npE ) { }

		getUsbPipeImp().usbIrpImpCompleted(this);
	}

	/**
	 * Sets the complted result
	 * @param i the result
	 */
	public void setDataLength( int i ) { dataLength = i; }

	/**
	 * Sets the UsbException that occured during submission
	 * @param exception the UsbException
	 */
	public void setUsbException( UsbException exception ) { usbException = exception; }

	/**
	 * Set the UsbIrp to wrap.
	 * @param irp The UsbIrp.
	 */
	public void setUsbIrp(UsbIrp irp) { usbIrp = irp; }

	/**
	 * Get the UsbIrp this is wrapping.
	 * @return The UsbIrp or null.
	 */
	public UsbIrp getUsbIrp() { return usbIrp; }

	//*************************************************************************
	// Protected methods

	protected void notifyCompleted()
	{
		if (0 < waitCount) {
			synchronized ( waitLock ) {
				waitLock.notifyAll();
			}
		}
	}

	//*************************************************************************
	// Instance variables

	private UsbIrp usbIrp = null;

	private Object waitLock = new Object();
	private int waitCount = 0;

	private long number = -1;
	private long sequenceNumber = -1;
	private UsbPipeImp usbPipeImp = null;
	private byte[] data = null;
	private boolean active = false;
	private boolean completed = false;
	private boolean acceptShortPacket = true;
	private int dataLength = -1;
	private UsbException usbException = null;

}
