package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;
import javax.usb.util.*;

import com.ibm.jusb.os.*;

/**
 * UsbHub implementation.
 * <p>
 * This must be set up before use and/or connection to the topology tree.  To set up,
 * see {@link com.ibm.jusb.UsbDeviceImp UsbDeviceImp documentation}.  The number of ports may
 * be set in the constructor, or it will default to 1.  The number of ports can be dynamically
 * {@link resize(int) resized} if needed.
 * <p>
 * The port numbering is 1-based, not 0-based.
 * @author Dan Streetman
 * @author E. Michael Maximilien
 */
public class UsbHubImp extends UsbDeviceImp implements UsbHub
{
	/**
	 * Constructor
	 * @param desc This device's descriptor.
	 * @param device The platform device implementaiton.
	 */
	public UsbHubImp(DeviceDescriptor desc, UsbDeviceOsImp device) { this( 1, desc, device ); }

	/**
	 * Constructor
	 * @param ports The initial number of ports.
	 * @param desc This device's descriptor.
	 * @param device The platform device implementation.
	 */
	public UsbHubImp( int ports, DeviceDescriptor desc, UsbDeviceOsImp device )
	{
		super(desc, device);
		resize( ports );
	}

	//**************************************************************************
	// Public methods

	/**
	 * Resizes to the specified number of ports.
	 * <p>
	 * If resizing decreases the number of ports and there are
	 * devices attached to lost ports, this will remove only down to the
	 * port with a device attached.  The device(s) need to be removed before the port(s)!
	 * @param ports The total number of ports to resize to.
	 */
	public synchronized void resize( int ports )
	{
		int oldports = UsbUtil.unsignedInt( getNumberOfPorts() );

		if ( ports == oldports )
			return;

		if ( ports > USB_HUB_MAX_PORTS )
			ports = USB_HUB_MAX_PORTS;

		if ( ports < USB_HUB_MIN_PORTS )
			ports = USB_HUB_MIN_PORTS;

		if ( ports < oldports ) {
			/* Remove ports */
			for (int i=oldports; i>ports; i--) {
				if ( getUsbPortImp((byte)i).isUsbDeviceAttached() )
					return; /* Cannot remove in-use port */
				else
					portList.removeUsbInfo(i-1);
			}
		} else {
			/* Add ports */
			for (int i = oldports; i < ports; i++)
				portList.addUsbInfo( new UsbPortImp( this, (byte)(i+1) ) );
		}

	}

	/**
	 * Attach this device to this hub at the specified port.
	 * @param usbDeviceImp the UsbDeviceImp to attach.
	 * @param portNumber the number (1-based) of the port to attach the device to.
	 * @exception javax.usb.UsbException If the port is already occupied.
	 */
	public synchronized void addUsbDeviceImp( UsbDeviceImp usbDeviceImp, byte portNumber ) throws UsbException
	{
		if ( UsbUtil.unsignedInt( portNumber ) >= UsbUtil.unsignedInt( getNumberOfPorts() ) )
			resize( portNumber );

		UsbPortImp usbPortImp = getUsbPortImp( portNumber );

		usbPortImp.attachUsbDeviceImp( usbDeviceImp );
	}

	/**
	 * Remove the device from this hub at the specified port.
	 * @param usbDeviceImp The UsbDeviceImp to remove.
	 * @param portNumber The number (1-based) of the port the device is attached to.
	 * @exception javax.usb.UsbException if the device is not already attached
	 * to the port it is being removed from.
	 */
	public synchronized void removeUsbDeviceImp( UsbDeviceImp usbDeviceImp, byte portNumber ) throws UsbException
	{
		UsbPortImp usbPortImp = getUsbPortImp( portNumber );

		/* UsbPortImp does checking */
		usbPortImp.detachUsbDeviceImp( usbDeviceImp );
	}

	/** @return true if this is a UsbHub and false otherwise */
	public boolean isUsbHub() { return true; }

	/** @return true if this is the root hub */
	public boolean isUsbRootHub() { return false;  }

	/** @return the number of ports for this hub */
	public byte getNumberOfPorts() { return (byte)portList.size(); }

	/** @return an iteration of UsbPort objects attached to this hub */
	public UsbInfoListIterator getUsbPorts() { return portList.usbInfoListIterator(); }

	/**
	 * Get the specified port.
	 * @param number The number (1-based) of the port to get.
	 * @return The port with the specified number.
	 * @throws UsbRuntimeException If the port number if not valid.
	 */
	public UsbPort getUsbPort( byte number ) { return getUsbPortImp( number ); }

	/**
	 * Get the specified port.
	 * @param number The number (1-based) of the port to get.
	 * @return The port with the specified number.
	 * @throws UsbRuntimeException If the port number if not valid.
	 */
	public synchronized UsbPortImp getUsbPortImp( byte number )
	{
		int num = UsbUtil.unsignedInt(number);

		if (0 >= num || num > UsbUtil.unsignedInt(getNumberOfPorts()))
			throw new UsbRuntimeException( USB_HUB_PORT_OUT_OF_RANGE + num );

		return (UsbPortImp)portList.getUsbInfo(num - 1);
	}

	/** @return an iteration of devices currently attached to this hub */
	public synchronized UsbInfoListIterator getAttachedUsbDevices()
	{
		UsbInfoList attachedDevices = new DefaultUsbInfoList();

		for (int i=0; i<portList.size(); i++) {
			UsbPortImp portImp = (UsbPortImp)portList.getUsbInfo(i);
			UsbDeviceImp device = portImp.getUsbDeviceImp();
			if (null != device)
				attachedDevices.addUsbInfo(device);
		}

		return attachedDevices.usbInfoListIterator();
	}

	/** @return A HubClassOperations object */
	public HubClassOperations getHubClassOperations() { return getUsbOperationsImp(); }

	/**
	 * Visitor.accept method
	 * @param visitor the UsbInfoVisitor visiting this UsbInfo
	 */
	public void accept( UsbInfoVisitor visitor ) { visitor.visitUsbHub( this ); }

	//**************************************************************************
	// Instance variables

	protected UsbInfoList portList = new DefaultUsbInfoList();

	//**************************************************************************
	// Class constants

	public static final int USB_HUB_MIN_PORTS = 0x01;
	public static final int USB_HUB_MAX_PORTS = 0xff - 1; /* USB 1.1 spec table 11.8 - max of 255 ports, but 1-based numbering makes it 254! */

	public static final String USB_HUB_NAME = "hub";

	private static final String USB_HUB_PORT_OUT_OF_RANGE = "No such port number on this hub : ";
	private static final String USB_DEVICE_NOT_ATTACHED = "The UsbDevice is not attached on the specified port";
}
