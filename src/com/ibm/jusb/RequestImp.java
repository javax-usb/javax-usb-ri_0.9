package com.ibm.jusb;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;

import com.ibm.jusb.os.*;

/**
 * Request implementation.
 * <p>
 * The user must provide some fields:
 * <ul>
 * <li>{@link #getRequestType() bmRequestType} via its {@link #setRequestType(byte) setter}.</li>
 * <li>{@link #getRequestCode() bRequest} via its {@link #setRequestCode(byte) setter}.</li>
 * <li>{@link #getValue() wValue} via its {@link #setValue(short) setter}.</li>
 * <li>{@link #getIndex() wIndex} via its {@link #setIndex(short) setter}.</li>
 * <li>{@link #getData() actual data} via its {@link #setData(byte[]) setter}.</li>
 * </ul>
 * <p>
 * The os-independent implementation will first set the
 * {@link #getNumber() number} via its {@link #setNumber(int) setter} and
 * {@link #getUsbDeviceImp() UsbDeviceImp} via its {@link #setUsbDeviceImp(UsbDeviceImp) setter}.
 * <p>
 * The os implementation will then process this.
 * If the processing is sucessful, the {@link #getDataLength() data length}
 * is set via its {@link #setDataLength(int) setter}.
 * If the processing is not successful, the {@link #getUsbException() UsbException}
 * is set via its {@link #setUsbException(UsbException) setter}.
 * In either case this is finally {@link #isCompleted() completed}
 * via the {@link #complete() complete} method.
 * <p>
 * If the application has passed their own implementation of Request, the UsbDeviceImp will
 * 'wrap' their implementation with this by {@link #setRequest setting} this RequestImp's
 * {@link #getRequest() local Request}.  If the local Request is set when this is
 * {@link #complete() completed}, this RequestImp's
 * {@link javax.usb.Request#setUsbException(UsbException) UsbException} and
 * {@link javax.usb.Request#setDataLength(int) data length} are copied
 * to the original Request, and the original Request is
 * {@link javax.usb.Request#setCompleted(boolean} completed}.
 * @author Dan Streetman
 * @author E. Michael Maximilien
 */
public class RequestImp implements Request,UsbOperations.SubmitResult,UsbSubmission
{
	/** Constructor */
	public RequestImp( RequestImpFactory factory ) { requestImpFactory = factory; }

	/** @return The UsbDevice */
	public UsbDevice getUsbDevice() { return getUsbDeviceImp(); }

	/** @return The UsbDeviceImp */
	public UsbDeviceImp getUsbDeviceImp() { return usbDeviceImp; }

	/** @param device The UsbDeviceImp */
	public void setUsbDeviceImp(UsbDeviceImp device) { usbDeviceImp = device; }

	/** @return the bmRequestType bitmap byte for this Request */
	public byte getRequestType() { return bmRequestType; }

	/** @param type the bmRequestType bitmap byte for this Request */
	public void setRequestType(byte type) { bmRequestType = type; }

	/** @return the Request code byte for this request */
	public byte getRequestCode() { return bRequest; }

	/** @param r the Request code byte for this request */
	public void setRequestCode(byte r) { bRequest = r; }

	/** @return the wValue for this request */
	public short getValue() { return wValue; }

	/** @param v the wValue for this request */
	public void setValue(short v) { wValue = v; }

	/** @return the wIndex for this request */
	public short getIndex() { return wIndex; }

	/** @param i the wIndex for this request */
	public void setIndex(short i) { wIndex = i; }

	/** @return the length of the <i>data</i> (not including setup bytes) for this request. */
	public short getLength() { return (short)getData().length; }

	/** @return the length of valid data */
	public int getDataLength() { return dataLength; }

	/** @param len the length of valid data */
	public void setDataLength(int len) { dataLength = len; }

	/** @return the data byte[] for this request */
	public byte[] getData() { return data; }

	/** @param d the data byte[] for this request */
	public void setData(byte[] d) { data = d; }

	/** Clean this object */
	public void clean() { }

	/** Recycle this object */
	public void recycle() { }

	/** @return The number */
	public long getNumber() { return number; }

	/** @param num The Number */
	public void setNumber(long num) { number = num; }

	/** @return The UsbException */
	public UsbException getUsbException() { return usbException; }

	/** @param uE The UsbException */
	public void setUsbException( UsbException uE ) { usbException = uE; }

	/** @return If in UsbException */
	public boolean isUsbException() { return null != usbException; }

	/** @return If completed */
	public boolean isCompleted() { return completed; }

	/** Wait (block) until this submission is completed */
	public void waitUntilCompleted() { waitUntilCompleted(0); }

	/** Wait (block) until this submission is completed */
	public void waitUntilCompleted( long msecs )
	{
		if (isCompleted())
			return;

		synchronized ( waitLock ) {
			waitCount++;
			while (!isCompleted()) {
				try { waitLock.wait( msecs ); }
				catch ( InterruptedException iE ) { }
			}
			waitCount--;
		}
	}

	/**
	 * Get the Request.
	 * <p>
	 * If the user passes a Request that is not a RequestImp, this will
	 * 'wrap' that object so that lower (platform) layers can handle
	 * a standardized object (the RequestImp) with setters instead of
	 * a user-supplied object without setters.  The platform layers always use a
	 * RequestImp, not Request.
	 * @return The Request.
	 */
	public Request getRequest() { return request; }

	/**
	 * Set this as completed.
	 * @param c If completed
	 */
	public void setCompleted(boolean c) { completed = c; }

	/**
	 * Complete this submission.
	 * <p>
	 * This will perform all required completion activities, such as waking up
	 * {@link #waitUntilCompleted(long) waiting Threads} and (if needed) setting the
	 * {@link #getRequest() Request}'s params.
	 */
	public void complete()
	{
		setCompleted(true);

		notifyCompleted();

//FIXME - the user Request's method(s) may block or generate Exception/Error which will cause problems
		try {
			getRequest().setDataLength(getDataLength());
			getRequest().setUsbException(getUsbException());
			getRequest().setCompleted(true);
		} catch ( NullPointerException npE ) { }

		getUsbDeviceImp().requestImpCompleted(this);
	}

	public byte[] toBytes()
	{
		bytes = new byte[ REQUEST_HEADER_LENGTH + getLength() ];

		bytes[ 0 ] = getRequestType();
		bytes[ 1 ] = getRequestCode();

		bytes[ 2 ] = (byte)getValue();
		bytes[ 3 ] = (byte)( getValue() >> 8 );

		bytes[ 4 ] = (byte)getIndex();
		bytes[ 5 ] = (byte)( getIndex() >> 8 );

		bytes[ 6 ] = (byte)getLength();
		bytes[ 7 ] = (byte)( getLength() >> 8 );

		System.arraycopy(data, 0, bytes, REQUEST_HEADER_LENGTH, getLength());

		return bytes;
	}

	public byte[] getBytes() { return bytes; }

	/** @return If this request is a SET_CONFIGURATION request */
	public boolean isSetConfigurationRequest()
	{ return REQUESTTYPE_SET_CONFIGURATION == getRequestType() && REQUEST_SET_CONFIGURATION == getRequestCode(); }

	/** @return If this request is a SET_INTERFACE request */
	public boolean isSetInterfaceRequest()
	{ return REQUESTTYPE_SET_INTERFACE == getRequestType() && REQUEST_SET_INTERFACE == getRequestCode(); }

	/**
	 * If this request is an Interface request.
	 * <p>
	 * The USB specification is unclear on this; depending on the Type
	 * setting of the bmRequestType, the Recipient setting of the bmRequestType
	 * may not be valid.  This implementation assumes only Standard and Class
	 * Type requests are applicable, not Vendor or Reserved Type requests.
	 * @return If this request is an Interface request
	 */
	public boolean isInterfaceRequest()
	{
		boolean standardRequest =
			RequestConst.REQUESTTYPE_TYPE_STANDARD == (RequestConst.REQUESTTYPE_TYPE_MASK & getRequestType());
		boolean classRequest =
			RequestConst.REQUESTTYPE_TYPE_CLASS == (RequestConst.REQUESTTYPE_TYPE_MASK & getRequestType());
		boolean ifaceRequest =
			RequestConst.REQUESTTYPE_RECIPIENT_INTERFACE == (RequestConst.REQUESTTYPE_RECIPIENT_MASK & getRequestType());

		return (standardRequest || classRequest) && ifaceRequest;
	}

	//**************************************************************************
	// Protected methods

	/** Notify all Threads waiting for completion */
	protected void notifyCompleted()
	{
		if (0 < waitCount) {
			synchronized ( waitLock ) {
				waitLock.notifyAll();
			}
		}
	}

	/**
	 * Set the Request.
	 * @param r The Request.
	 */
	protected void setRequest(Request r) { request = r; }

	//**************************************************************************
	// Instance variables

	private Request request = null;

	private UsbDeviceImp usbDeviceImp = null;

	private byte bmRequestType = 0x00;
	private byte bRequest = 0x00;
	private short wValue = 0x0000;
	private short wIndex = 0x0000;
	private byte[] data = null;
	private byte[] bytes = null;
	private int dataLength = 0;

	private RequestImpFactory requestImpFactory = null;

	private long number = 0;
	private UsbException usbException = null;
	private boolean completed = false;

	private Object waitLock = new Object();
	private int waitCount = 0;

	//**************************************************************************
	// Class constants

	public static final int REQUEST_HEADER_LENGTH = 8;

	private static final byte REQUESTTYPE_SET_CONFIGURATION =
		RequestConst.REQUESTTYPE_DIRECTION_OUT |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_DEVICE;

	private static final byte REQUEST_SET_CONFIGURATION =
		RequestConst.REQUEST_SET_CONFIGURATION;

	private static final byte REQUESTTYPE_SET_INTERFACE =
		RequestConst.REQUESTTYPE_DIRECTION_OUT |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_INTERFACE;

	private static final byte REQUEST_SET_INTERFACE =
		RequestConst.REQUEST_SET_INTERFACE;

}
