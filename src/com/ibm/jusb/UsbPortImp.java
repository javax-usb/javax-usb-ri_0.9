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

/**
 * UsbPort implementation.
 * @author E. Michael Maximilien
 * @author Dan Streetman
 */
public class UsbPortImp extends AbstractUsbInfo implements UsbPort
{
    /**
	 * Constructor.
     * @param usbHub The parent UsbHubImp.
     * @param number The (1-based) port number.
     */
    public UsbPortImp( UsbHubImp hub, byte number )
    {
		usbHubImp = hub;
		portNumber = number;
    }

	//**************************************************************************
	// Public methods

    /** @return name of this UsbInfo object */
    public String getName() 
    {
        if( super.getName().equals( "" ) )
            setName( USB_PORT_NAME_STRING + getPortNumber() );
        
        return super.getName();
    }

	/**
	 * Return the number of this port.
	 * <p>
	 * The port number is 1-based, so it is one greater than its index
	 * in {@link com.ibm.jusb.UsbHubImp#getUsbPortImp(byte) UsbHubImp.getUsbPortImp(number)}.
	 * @return The number of this port.
	 */
    public byte getPortNumber() { return portNumber; }

    /** @return The parent UsbHub */
    public UsbHub getUsbHub() { return getUsbHubImp(); }

    /** @return The parent UsbHubImp */
    public UsbHubImp getUsbHubImp() { return usbHubImp; }

    /** @return true if a device is attached to this port */
    public boolean isUsbDeviceAttached() { return (getUsbDevice() != null); }

    /** @return The attached UsbDevice or null if none attached */
    public UsbDevice getUsbDevice() { return getUsbDeviceImp(); }

    /** @return The attached UsbDeviceImp or null if none attached */
    public UsbDeviceImp getUsbDeviceImp() { return usbDeviceImp; }

    /**
     * Attaches the UsbDeviceImp to this port.
     * @param device The UsbDeviceImp to attach.
	 * @throws javax.usb.UsbRuntimeException If there is already a device attached.
     */
    public synchronized void attachUsbDeviceImp( UsbDeviceImp device )
    {
		if (isUsbDeviceAttached())
			throw new UsbRuntimeException( USB_PORT_DEVICE_ALREADY_ATTACHED );

        usbDeviceImp = device;
    }

    /** 
     * Detaches the attached UsbDeviceImp from this port.
	 * @param device the UsbDeviceImp to detach.
	 * @throws javax.usb.UsbRuntimeException If the UsbDeviceImp is not already attached.
     */
    public synchronized void detachUsbDeviceImp( UsbDeviceImp device )
	{
		try {
			if (!getUsbDeviceImp().equals( device ))
				throw new UsbRuntimeException( USB_PORT_DEVICE_NOT_ATTACHED );
		} catch ( NullPointerException npE ) {
			throw new UsbRuntimeException( USB_PORT_DEVICE_NOT_ATTACHED );
		}

		usbDeviceImp = null;
	}

    /**
     * Visitor.accept method
     * @param visitor the UsbInfoVisitor visiting this UsbInfo
     */
    public void accept( UsbInfoVisitor visitor ) { visitor.visitUsbPort( this ); }

	//**************************************************************************
    // Instance variables

    private byte portNumber = 0;

    private UsbHubImp usbHubImp = null;
    private UsbDeviceImp usbDeviceImp = null;

	//**************************************************************************
	// Class constants

    public static final String USB_PORT_NAME_STRING = "port";

	private static final String USB_PORT_DEVICE_ALREADY_ATTACHED = "UsbPort already has a UsbDeviceImp attached";
	private static final String USB_PORT_DEVICE_NOT_ATTACHED = "Specified UsbDeviceImp not attached";
}
