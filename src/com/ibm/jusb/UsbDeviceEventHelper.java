package com.ibm.jusb;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;

import javax.usb.event.*;

import com.ibm.jusb.util.*;

/**
 * Helper class to handle multiplexing UsbDeviceEvents to listeners.
 * @author Dan Streetman
 */
public class UsbDeviceEventHelper extends EventListenerHelper implements UsbDeviceListener
{
	/** @param event The Event to fire. */
	public void errorEventOccurred(UsbDeviceErrorEvent event)
	{
		if (!hasListeners())
			return;

		addRunnable( new ErrorEvent(event) );
	}

	/** @param event The Event to fire. */
	public void dataEventOccurred(UsbDeviceDataEvent event)
	{
		if (!hasListeners())
			return;

		addRunnable( new DataEvent(event) );
	}

	/** @param event The Event to fire. */
	public void usbDeviceDetached(UsbDeviceEvent event)
	{
		if (!hasListeners())
			return;

		addRunnable( new DetachEvent(event) );
	}

//FIXME - event firing should use a list copy or be sync'd with add/remove of listeners

	private class ErrorEvent extends EventRunnable
	{
		public ErrorEvent() { super(); }
		public ErrorEvent(EventObject e) { super(e); }

		public void run()
		{
			List list = getEventListeners();
			for (int i=0; i<list.size(); i++)
				((UsbDeviceListener)list.get(i)).errorEventOccurred((UsbDeviceErrorEvent)event);
		}
	}

	private class DataEvent extends EventRunnable
	{
		public DataEvent() { super(); }
		public DataEvent(EventObject e) { super(e); }

		public void run()
		{
			List list = getEventListeners();
			for (int i=0; i<list.size(); i++)
				((UsbDeviceListener)list.get(i)).dataEventOccurred((UsbDeviceDataEvent)event);
		}
	}

	private class DetachEvent extends EventRunnable
	{
		public DetachEvent() { super(); }
		public DetachEvent(EventObject e) { super(e); }

		public void run()
		{
			List list = getEventListeners();
			for (int i=0; i<list.size(); i++)
				((UsbDeviceListener)list.get(i)).usbDeviceDetached((UsbDeviceEvent)event);
		}
	}
}
