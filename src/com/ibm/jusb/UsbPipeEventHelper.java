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
import javax.usb.event.*;
import com.ibm.jusb.util.*;


/**
 * Helper class to handle multiplexing UsbPipeEvents to listeners.
 * @author Dan Streetman
 */
public class UsbPipeEventHelper extends EventListenerHelper implements UsbPipeListener
{
	/** @param event The Event to fire. */
	public void errorEventOccurred(UsbPipeErrorEvent event)
	{
		if (!hasListeners())
			return;

		addRunnable( new ErrorEvent(event) );
	}

	/** @param event The Event to fire. */
	public void dataEventOccurred(UsbPipeDataEvent event)
	{
		if (!hasListeners())
			return;

		addRunnable( new DataEvent(event) );
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
				((UsbPipeListener)list.get(i)).errorEventOccurred((UsbPipeErrorEvent)event);
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
				((UsbPipeListener)list.get(i)).dataEventOccurred((UsbPipeDataEvent)event);
		}
	}
}                                                                             
