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

import javax.usb.event.*;

import com.ibm.jusb.util.*;

/**
 * Helper class to handle multiplexing UsbServicesEvents to listeners.
 * @author Dan Streetman
 */
public class UsbServicesEventHelper extends EventListenerHelper implements UsbServicesListener
{
	/** UsbDevices attached */
	public void usbDeviceAttached(UsbServicesEvent event)
	{
		if (!hasListeners())
			return;

		addRunnable( new AttachEvent(event) );
	}

	/** UsbDevices detached */
	public void usbDeviceDetached(UsbServicesEvent event)
	{
		if (!hasListeners())
			return;

		addRunnable( new DetachEvent(event) );
	}

//FIXME - event firing should use a list copy or be sync'd with add/remove of listeners

	private class AttachEvent extends EventRunnable
	{
		public AttachEvent() { super(); }
		public AttachEvent(EventObject e) { super(e); }

		public void run()
		{
			List list = getEventListeners();
			for (int i=0; i<list.size(); i++)
				((UsbServicesListener)list.get(i)).usbDeviceAttached((UsbServicesEvent)event);
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
				((UsbServicesListener)list.get(i)).usbDeviceDetached((UsbServicesEvent)event);
		}
	}
}                                                                             
