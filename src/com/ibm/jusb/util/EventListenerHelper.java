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

/**
 * Helper class to handle multiplexing Events to listeners.
 * @author Dan Streetman
 */
public class EventListenerHelper implements EventListener
{
	//*************************************************************************
	// Public methods

	/**
	 * Add a listener.
	 * @param listener the listener to add.
	 */
	public void addEventListener( EventListener listener )
	{
		listeners.add( listener );

		manager.setMaxSize(listeners.size() + 1);
	}

	/**
	 * Remove a listener.
	 * @param listener the listener to remove.
	 */
	public void removeEventListener( EventListener listener )
	{
		listeners.remove( listener );

		manager.setMaxSize(listeners.size() + 1);
	}

	//*************************************************************************
	// Protected methods

	/**
	 * Get the listeners.
	 * @return the listeners.
	 */
	protected List getEventListeners() { return Collections.unmodifiableList( listeners ); }

	/**
	 * @return If this has listeners.
	 */
	protected boolean hasListeners() { return !listeners.isEmpty(); }

	/**
	 * Add a Runnable to be executed.
	 * @param runnable The Runnable to be run.
	 */
	protected void addRunnable(Runnable runnable) { manager.add(runnable); }

	//**************************************************************************
	// Instance variables

	private List listeners = new Vector();

	private RunnableManager manager = new RunnableManager();

	//**************************************************************************
	// Inner classes

	public static abstract class EventRunnable implements Runnable
	{
		public EventRunnable() { }
		public EventRunnable(EventObject e) { event = e; }

		public abstract void run();

		public EventObject event = null;
	}
}
