package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.Vector;

import javax.usb.*;
import javax.usb.os.*;
import javax.usb.util.*;
import javax.usb.event.*;

import com.ibm.jusb.util.*;

/**
 * Basic abstract class implementing the UsbServices interface
 * @author E. Michael Maximilien
 * @author Dan Streetman
 */
public abstract class AbstractUsbServices implements UsbServices
{
	/** @return The UsbRootHub. */
	public UsbRootHub getUsbRootHub() throws UsbException { return getUsbRootHubImp(); }

	/** @return The UsbRootHubImp. */
	public UsbRootHubImp getUsbRootHubImp() { return usbRootHubImp; }

    /**
     * Adds a new UsbServicesListener object to receive events when the USB host
     * has changes.  For instance a new device is plugged in or unplugged.
     * @param l the UsbServicesListener to register     
     */
    public synchronized void addUsbServicesListener( UsbServicesListener l ) { usbServicesEventHelper.addEventListener( l ); }

    /**
     * Adds a new UsbServicesListener object to receive events when the USB host
     * has changes.  For instance a new device is plugged in or unplugged.
     * @param l the UsbServicesListener to register     
     */
    public synchronized void removeUsbServicesListener( UsbServicesListener l ) { usbServicesEventHelper.removeEventListener( l ); }

	/** @return the RequestFactory used to create Request object for the USB operations */ 
	public RequestFactory getRequestFactory() { return requestFactory; }

	/** @return a new instance of a RequestFactory */
	public RequestFactory getNewRequestFactory() { return new RequestImpFactory(); }

	/** @return the current UsbIrpFactory being used */
	public UsbIrpFactory getUsbIrpFactory() { return usbIrpFactory; }

	/** @return a new instance of a UsbIrpFactory */
	public UsbIrpFactory getNewUsbIrpFactory() { return new UsbIrpImpFactory(); }

	/**
	 * Return all UsbDevices under the specificed UsbHub (including itself) in BFS order.
	 * <p>
	 * NOTE: <i>since UsbHub are UsbDevice then they are also included in return list.</i>
	 * @return a UsbInfoListIterator of UsbDevice in breadth-first search (BFS) order.
	 * @param usbHub the UsbHub object whose children will be queried.
	 */
	public UsbInfoIterator bfsUsbDevices( UsbHub usbHub )
	{
		UsbInfoList usbDevices = new DefaultUsbInfoList();
		UsbInfoList list;
		int depth = 0;

		do {
			list = bfs( usbHub, depth++ );
			usbDevices.addUsbInfoList( list );
		} while ( !list.isEmpty() );

		return usbDevices.usbInfoListIterator();
	}

	/**
	 * Return all UsbDevices under the specificed UsbHub (including itself) in DFS order.
	 * <p>
	 * NOTE: <i>since UsbHub are UsbDevice then they are also included in return list.</i>
	 * @return a UsbInfoListIterator of UsbDevice in depth-first search (DFS) order.
	 * @param usbHub the UsbHub object whose children will be queried.
	 */
	public UsbInfoIterator dfsUsbDevices( UsbHub usbHub )
	{
		return dfs( usbHub ).usbInfoListIterator();
	}

	/**
	 * Recursive DFS method.
	 * @param usbHub the UsbHub to get UsbDevices from.
	 * @return a UsbInfoList of all UsbDevices 
	 */
	protected UsbInfoList dfs( UsbHub usbHub )
	{
		UsbInfoList dfsDevices = new DefaultUsbInfoList();

		dfsDevices.addUsbInfo( usbHub );

		UsbInfoListIterator usbDevices = usbHub.getAttachedUsbDevices();

		while ( usbDevices.hasNext() ) {
			UsbDevice device = (UsbDevice)usbDevices.nextUsbInfo();

			if ( device.isUsbHub() )
				dfsDevices.addUsbInfoList( dfs( (UsbHub)device ) );
			else
				dfsDevices.addUsbInfo( device );
		}

		return dfsDevices;
	}

	/**
	 * Recursive BFS method.
	 * <p>
	 * This recurses until it reaches the specified depth (the depth is 0)
	 * or it runs out of UsbHubs.  A BFS list is returned of all UsbDevices
	 * at the specified depth.
	 * @param usbHub the UsbHub to get UsbDevices from.
	 * @param depth the depth to return UsbDevices at.
	 * @return a UsbInfoList of all devices at the requested depth under the UsbHub.
	 */
	protected UsbInfoList bfs( UsbHub usbHub, int depth )
	{
		UsbInfoList bfsDevices = new DefaultUsbInfoList();

		if ( 0 == depth ) {
			bfsDevices.addUsbInfo( usbHub );
			return bfsDevices;
		}

		UsbInfoListIterator usbDevices = usbHub.getAttachedUsbDevices();

		while (usbDevices.hasNext())
		{
			UsbDevice device = (UsbDevice)usbDevices.nextUsbInfo();

			if ( device.isUsbHub() )
				bfsDevices.addUsbInfoList( bfs( (UsbHub)device, depth-1 ) );
		}

		return bfsDevices;
	}

	//**************************************************************************
	// Protected methods

	/**
	 * Fire a device attached event
	 * @param event The event to fire.
	 */
	protected void fireDeviceAttachedEvent(UsbServicesEvent event)
	{
		usbServicesEventHelper.usbDeviceAttached(event);
	}

	/**
	 * Fire a device detached event.
	 * @param event The event to fire.
	 */
	protected void fireDeviceDetachedEvent(UsbServicesEvent event)
	{
		usbServicesEventHelper.usbDeviceDetached(event);
	}

    //**************************************************************************
    // Instance variables

	private UsbRootHubImp usbRootHubImp = new VirtualUsbRootHubImp();
	private UsbServicesEventHelper usbServicesEventHelper = new UsbServicesEventHelper();
	private RequestFactory requestFactory = new RequestImpFactory();
	private UsbIrpFactory usbIrpFactory = new UsbIrpImpFactory();

}
