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
 * UsbEndpoint implementation.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The UsbInterfaceImp must be set either in the constructor or by its {@link #setUsbInterfaceImp(UsbInterfaceImp) setter}.</li>
 * <li>The EndpointDescriptor must be set either in the constructor or by its {@link #setEndpointDescriptor(EndpointDescriptor) setter}.</li>
 * <li>The UsbPipeImp must be set by its {@link #setUsbPipeImp(UsbPipeImp) setter}.
 *     Note the UsbPipeImp will automatically do this inside its {@link com.ibm.jusb.UsbPipeImp#setUsbEndpointImp(UsbEndpointImp) setUsbEndpointImp} method.</li>
 * </ul>
 * @author Dan Streetman
 * @author E. Michael Maximilien
 */
public class UsbEndpointImp extends AbstractUsbInfo implements UsbEndpoint
{
	/**
	 * Constructor.
	 * @param iface The parent interface.
	 * @param desc This endpoint's descriptor.
	 */
	public UsbEndpointImp( UsbInterfaceImp iface, EndpointDescriptor desc )
	{
		setUsbInterfaceImp(iface);
		setEndpointDescriptor(desc);
	}

	//**************************************************************************
    // Public methods

    /** @return name of this UsbInfo object */
    public String getName() 
    {
        if( super.getName().equals( "" ) )
            setName( USB_ENDPOINT_NAME_STRING + (byte)(getEndpointAddress() & UsbInfoConst.ENDPOINT_NUMBER_MASK) );
        
        return super.getName();
    }

    /** @return the unique address of this endpoint */
    public byte getEndpointAddress() { return getEndpointDescriptor().getEndpointAddress(); }

    /**
     * @return direction of this endpoint (i.e. in [from device to host] or out
     * [from host to device])
     */
    public byte getDirection() { return (byte)(getEndpointAddress() & UsbInfoConst.ENDPOINT_DIRECTION_MASK); }

    /** @return the attribute of this endpoint */
    public byte getAttributes() { return getEndpointDescriptor().getAttributes(); }

    /** @return this endpoint's type */
    public byte getType() { return (byte)(getAttributes() & UsbInfoConst.ENDPOINT_TYPE_MASK); }

    /** @return the max packet size required for this endpoint */
    public short getMaxPacketSize() { return getEndpointDescriptor().getMaxPacketSize(); }

    /** @return this endpoint interval */
    public byte getInterval() { return getEndpointDescriptor().getInterval(); }

    /** @return the UsbDevice associated with this Endpoint */
    public UsbDevice getUsbDevice() { return getUsbDeviceImp(); }

	/** @return The UsbDeviceImp */
	public UsbDeviceImp getUsbDeviceImp() { return getUsbInterfaceImp().getUsbDeviceImp(); }

    /** @return The UsbInterface */
    public UsbInterface getUsbInterface() { return getUsbInterfaceImp(); }

	/** @return The UsbInterfaceImp */
	public UsbInterfaceImp getUsbInterfaceImp() { return usbInterfaceImp; }

	/**
	 * Set the UsbInterfaceImp.
	 * <p>
	 * This will also add this to the parent UsbInterfaceImp.
	 * @param iface The interface
	 */
    public void setUsbInterfaceImp( UsbInterfaceImp iface )
	{
		usbInterfaceImp = iface;

		if (null != iface)
			iface.addUsbEndpointImp(this);
	}

	/** @return The UsbPipe */
    public UsbPipe getUsbPipe() { return getUsbPipeImp(); }

	/** @return The UsbPipeImp */
	public UsbPipeImp getUsbPipeImp() { return usbPipeImp; }

	/** @param pipe The pipe */
	public void setUsbPipeImp(UsbPipeImp pipe) { usbPipeImp = pipe; }

	/** @return the endpoint descriptor for this endpoint */
	public EndpointDescriptor getEndpointDescriptor() { return (EndpointDescriptor)getDescriptor(); }

    /**
     * Visitor.accept method
     * @param visitor the UsbInfoVisitor visiting this UsbInfo
     */
    public void accept( UsbInfoVisitor visitor ) { visitor.visitUsbEndpoint( this ); }

	/** @param desc the endpoint descriptor */
	public void setEndpointDescriptor( EndpointDescriptor desc ) { setDescriptor( desc ); }

	//**************************************************************************
    // Instance variables

    private UsbInterfaceImp usbInterfaceImp = null;
    private UsbPipeImp usbPipeImp = null;

	//**************************************************************************
	// Class constants

    public static final String USB_ENDPOINT_NAME_STRING = "endpoint";

}
