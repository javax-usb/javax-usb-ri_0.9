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
import java.io.*;

import javax.usb.*;
import javax.usb.event.*;
import javax.usb.util.*;

import com.ibm.jusb.os.*;
import com.ibm.jusb.util.*;

/**
 * UsbDevice platform-independent implementation.
 * <p>
 * This must be set up before use and/or connection to the topology tree.
 * <ul>
 * <li>The DeviceDescriptor must be set, either in the constructor or by its {@link #setDeviceDescriptor(DeviceDescriptor) setter}.</li>
 * <li>The UsbDeviceOsImp must be set, either in the constructor or by its {@link #setUsbDeviceOsImp(UsbDeviceOsImp) setter}.</li>
 * <li>The speed string must be set by its {@link #setSpeedString(String) setter}.</li>
 * <li>All UsbConfigImps must be {@link #addUsbConfigImp(UsbConfigImp) added}.</li>
 * <li>The active config number must be {@link #setActiveUsbConfigNumber(byte) set}.</li>
 * </ul>
 * After setup, this may be connected to the topology tree by using the {@link #connect(UsbHubImp,byte) connect} method.
 * If the connect method is not used, there are additional steps:
 * <ul>
 * <li>Set the parent UsbPortImp by the {@link #setUsbPortImp(UsbPortImp) setter}.</li>
 * <li>Set this on the UsbPortImp by its {@link com.ibm.jusb.UsbPortImp#attachUsbDeviceImp(UsbDeviceImp) setter}.</li>
 * </ul>
 * If the parent UsbHubImp is not large enough, it can be {@link com.ibm.jusb.UsbHubImp@resize(int) resized}.  This should
 * only be necessary for root hubs, or more likely the virtual root hub.  Alternately, this may be added using the UsbHubImp's
 * {@link com.ibm.jusb.UsbHubImp@addUsbDeviceImp(UsbDeviceImp,byte) addUsbDeviceImp} method, which resizes if needed and
 * sets up the UsbPortImp.
 * @author Dan Streetman
 * @author E. Michael Maximilien
 */
public class UsbDeviceImp extends AbstractUsbInfo implements UsbDevice
{
	/**
	 * Constructor.
	 * @param desc This device's Descriptor.
	 * @param device The UsbDeviceOsImp.
	 */
	public UsbDeviceImp(DeviceDescriptor desc, UsbDeviceOsImp device)
	{
		setDeviceDescriptor(desc);
		setUsbDeviceOsImp(device);
	}

	//**************************************************************************
	// Public methods

	/** @return the associated UsbDeviceImp */
	public UsbDeviceOsImp getUsbDeviceOsImp() { return usbDeviceOsImp; }

	/** @param the UsbDeviceOsImp to use */
	public void setUsbDeviceOsImp( UsbDeviceOsImp deviceImp ) { usbDeviceOsImp = deviceImp; }

	/** @return the port that this device is attached to */
	public UsbPort getUsbPort() { return getUsbPortImp(); }

	/** @return the port that this device is attached to */
	public UsbPortImp getUsbPortImp() { return usbPortImp; }

	/** @param The parent port */
	public void setUsbPortImp( UsbPortImp port ) { usbPortImp = port; }

	/** @return true if this is a UsbHub and false otherwise */
	public boolean isUsbHub() { return false; }

	/** @return the manufacturer of this device */
	public String getManufacturer()
	{
		try { return getString( getDeviceDescriptor().getManufacturerIndex() ); } catch ( UsbException uE ) { return null; }
//FIXME - this should throw UsbException
	}

	/** @return the serial number of this device */
	public String getSerialNumber()
	{
		try { return getString( getDeviceDescriptor().getSerialNumberIndex() ); } catch ( UsbException uE ) { return null; }
//FIXME - this should throw UsbException
	}

	/** @return a String describing the speed of this device */
	public String getSpeedString() { return speedString; }

	/** @return a String describing this product */
	public String getProductString()
	{
		try { return getString( getDeviceDescriptor().getProductIndex() ); } catch ( UsbException uE ) { return null; }
//FIXME - this should throw UsbException
	}

	/** @return the USB device class */
	public byte getDeviceClass() { return getDeviceDescriptor().getDeviceClass(); }

	/** @return the USB device subclass */
	public byte getDeviceSubClass() { return getDeviceDescriptor().getDeviceSubClass(); }

	/** @returnt the USB device protocol */
	public byte getDeviceProtocol() { return getDeviceDescriptor().getDeviceProtocol(); }

	/** @return the maximum packet size */
	public byte getMaxPacketSize() { return getDeviceDescriptor().getMaxPacketSize(); }

	/** @return the number of configurations */
	public byte getNumConfigs() { return getDeviceDescriptor().getNumConfigs(); }

	/** @return the vendor ID */
	public short getVendorId() { return getDeviceDescriptor().getVendorId(); }

	/** @return the product ID */
	public short getProductId() { return getDeviceDescriptor().getProductId(); }

	/** @return the BCD USB version for this device */
	public short getBcdUsb() { return getDeviceDescriptor().getBcdUsb(); }

	/** @return the BCD revision number for this device */
	public short getBcdDevice() { return getDeviceDescriptor().getBcdDevice(); }

	/** @return the UsbConfig objects associated with this UsbDevice */
	public UsbInfoListIterator getUsbConfigs() { return configs.usbInfoListIterator(); }

	/** @return the UsbConfig with the specified number as reported by getConfigNumber() */
	public UsbConfig getUsbConfig( byte number ) { return getUsbConfigImp(number); }

	/** @return the UsbConfigImp with the specified number as reported by getConfigNumber() */
	public UsbConfigImp getUsbConfigImp( byte number )
	{
		synchronized ( configs ) {
			for (int i=0; i<configs.size(); i++) {
				UsbConfigImp config = (UsbConfigImp)configs.getUsbInfo(i);

				if (number == config.getConfigNumber())
					return config;
			}
		}

		throw new UsbRuntimeException( "No UsbConfig with number " + UsbUtil.unsignedInt( number ) );
	}

	/** @return if the specified UsbConfig is contained in this UsbDevice */
	public boolean containsUsbConfig( byte number )
	{
		try { getUsbConfig( number ); }
		catch ( UsbRuntimeException urE ) { return false; }

		return true;
	}

	/** @return if this device is configured */
	public boolean isConfigured() { return 0 != getActiveUsbConfigNumber(); }

	/** @return the active UsbConfig number */
	public byte getActiveUsbConfigNumber() { return activeConfigNumber; }

	/** @return the active UsbConfig object */
	public UsbConfig getActiveUsbConfig() { return getActiveUsbConfigImp(); }

	/** @return the active UsbConfigImp object */
	public UsbConfigImp getActiveUsbConfigImp() { return getUsbConfigImp( getActiveUsbConfigNumber() ); }

	/** @return the device descriptor for this device */
	public DeviceDescriptor getDeviceDescriptor() { return (DeviceDescriptor)getDescriptor(); }

	/*
	 * @return the specified string descriptor
	 * @param the index of the string descriptor to get
	 * @throws javax.usb.UsbException if an error occurrs while getting the StringDescriptor.
	 */
	public StringDescriptor getStringDescriptor( byte index ) throws UsbException
	{
		/* There is no StringDescriptor for index 0 */
		if (0 == index)
			return null;

		StringDescriptor desc = getCachedStringDescriptor( index );

		if ( null == desc ) {
			requestStringDescriptor( index );
			desc = getCachedStringDescriptor( index );
		}

		return desc;
	}

	/**
	 * @return the String from the specified STringDescriptor
	 * @throws javax.usb.UsbException if an error occurrs while getting the StringDescriptor.
	 */
	public String getString( byte index ) throws UsbException
	{
		StringDescriptor desc = getStringDescriptor( index );

		return ( null == desc ? null : desc.getString() );
	}

	/** @return A UsbOperationsImp object */
	public UsbOperationsImp getUsbOperationsImp() { return usbOperationsImp; }

	/** @return A StandardOperations object */
	public StandardOperations getStandardOperations() { return getUsbOperationsImp(); }

	/** @return A ClassOperations object */
	public ClassOperations getClassOperations() { return getUsbOperationsImp(); }

	/** @return A VendorOperations object */
	public VendorOperations getVendorOperations() { return getUsbOperationsImp(); }

	/** @param requestImp The RequestImp that completed. */
	public void requestImpCompleted(RequestImp requestImp)
	{
		if (requestImp.isUsbException()) {
			fireErrorEvent(requestImp.getUsbException());
		} else {
			if (requestImp.isSetConfigurationRequest()) {
				try { setActiveUsbConfigNumber((byte)requestImp.getValue()); }
				catch ( Exception e ) { /* log? */ }
			} else if (requestImp.isSetInterfaceRequest()) {
				try { getActiveUsbConfigImp().getUsbInterfaceImp((byte)requestImp.getIndex()).setActiveAlternateSettingNumber((byte)requestImp.getValue()); }
				catch ( Exception e ) { /* log? */ }
			}

			fireDataEvent(requestImp.getData(),requestImp.getDataLength());
		}
	}

	/** @param the listener to add */
	public void addUsbDeviceListener( UsbDeviceListener listener ) 
	{
		usbDeviceEventHelper.addEventListener(listener);
	}

	/** @param the listener to remove */
	public void removeUsbDeviceListener( UsbDeviceListener listener )
	{
		usbDeviceEventHelper.removeEventListener(listener);
	}

	/**
	 * Visitor.accept method
	 * @param visitor the UsbInfoVisitor visiting this UsbInfo
	 */
	public void accept( UsbInfoVisitor visitor ) { visitor.visitUsbDevice( this ); }

	/** @param desc the new device descriptor */
	public void setDeviceDescriptor( DeviceDescriptor desc ) { setDescriptor( desc ); }

	/**
	 * @param index the index of the new string descriptor
	 * @param desc the new string descriptor
	 */
	public void setStringDescriptor( byte index, StringDescriptor desc )
	{
		stringDescriptors.put( new Byte( index ), desc );
	}

	/**
	 * @return the specified StringDescriptor.
	 */
	public StringDescriptor getCachedStringDescriptor( byte index )
	{
		return (StringDescriptor)stringDescriptors.get( new Byte( index ) );
	}

	/**
	 * @return the String from the specified STringDescriptor
	 */
	public String getCachedString( byte index )
	{
		StringDescriptor desc = getCachedStringDescriptor( index );

		return ( null == desc ? null : desc.getString() );
	}

	/**
	 * Sets the speed of this device 
	 * @param s the String argument
	 */
	public void setSpeedString( String s ) { speedString = s; }

	/**
	 * Sets the active config index
	 * @param num the active config number (0 if device has been unconfigured)
	 */
	public void setActiveUsbConfigNumber( byte num ) { activeConfigNumber = num; }

	/** @param the configuration to add */
	public void addUsbConfigImp( UsbConfigImp config )
	{
		if (!configs.contains(config))
			configs.addUsbInfo( config );
	}

	/**
	 * Connect to the parent UsbHubImp.
	 * @param hub The parent.
	 * @param portNumber The port on the parent this is connected to.
	 */
	public void connect(UsbHubImp hub, byte portNumber) throws UsbException
	{
		hub.addUsbDeviceImp( this, portNumber );

		setUsbPortImp(hub.getUsbPortImp(portNumber));
	}

	/**
	 * Disconnect UsbDeviceImp.
	 * <p>
	 * Only call this if the device was connected to the topology tree;
	 * i.e. the UsbPortImp has been {@link #setUsbPortImp(UsbPortImp) set}.
	 * This will fire
	 * {@link javax.usb.event.UsbDeviceListener#usbDeviceDetached(UsbDeviceEvent) usbDeviceDetached}
	 * events to all listeners.
	 * <p>
	 * The implementation does not have to call this method, it is only a convienience method
	 * to disconnect this device and fire events to listeners; the implementation can do those
	 * things itself instead of this method, if desired.
	 */
	public void disconnect()
	{
		getUsbPortImp().detachUsbDeviceImp( this );

		fireDetachEvent();
	}

	/** Compare this to another UsbDeviceImp */
	public boolean equals(Object object)
	{
		UsbDeviceImp device = null;

		try { device = (UsbDeviceImp)object; }
		catch ( ClassCastException ccE ) { return false; }

		if (!getSpeedString().equals(device.getSpeedString()))
			return false;

		if (!getDeviceDescriptor().equals(device.getDeviceDescriptor()))
			return false;

//FIXME - check config/interface/endpoints too
		return true;
	}

	//**************************************************************************
	// Protected methods

	/**
	 * Fire an error event.
	 * @param uE The UsbException.
	 */
	protected void fireErrorEvent(UsbException uE)
	{
		UsbDeviceErrorEvent udeE = new UsbDeviceErrorEvent(this,/*FIXME - no sn*/(long)0,uE.getErrorCode(),uE);

		usbDeviceEventHelper.errorEventOccurred(udeE);
	}

	/**
	 * Fire a data event.
	 * @param data The data.
	 * @param len The data length.
	 */
	protected void fireDataEvent(byte[] data, int len)
	{
		UsbDeviceDataEvent uddE = new UsbDeviceDataEvent(this,/*FIXME - no sn*/(long)0,data,len);

		usbDeviceEventHelper.dataEventOccurred(uddE);
	}

	/** Fire detach event. */
	protected void fireDetachEvent()
	{
		UsbDeviceEvent udE = new UsbDeviceEvent(this);

		usbDeviceEventHelper.usbDeviceDetached(udE);
	}

	/** @return the device's default langID */
	protected short getLangId() throws UsbException
	{
		if (0x0000 == langId) {
			byte[] data = new byte[256];

			getStandardOperations().getDescriptor( (short)(DescriptorConst.DESCRIPTOR_TYPE_STRING << 8), (short)0x0000, data );

			if (4 > data[0])
				throw new UsbException("Strings not supported by device");

			langId = (short)((data[3] << 8) | data[2]);
		}

		return langId;
	}

	/**
	 * Convert byte[] to String
	 * @param data StringDescriptor bytes.
	 * @param len Length of unicode string.
	 */
//FIXME - move byte[] translation to StringDescriptorImp
	protected String bytesToString(byte[] data, int len)
	{
		for (int i=0; i<ENCODING.length; i++) {
			try { return new String( data, 2, len, ENCODING[i] ); }
			catch ( UnsupportedEncodingException ueE ) { }
		}

		/* Fallback to 8BIT encoding - ignore high byte */
		byte[] s = new byte[len/2];

		for (int i=0; i<s.length; i++)
			s[i] = data[2 + i*2];

		try {
			return new String( s, ENCODING_8BIT );
		} catch ( UnsupportedEncodingException ueE ) {
			/* No encodings supported!  Fallback to platform, but String will likely be corrupted here */
			return new String( s );
		}
	}

	/** Update the StringDescriptor at the specified index. */
	protected void requestStringDescriptor( byte index ) throws UsbException
	{
		byte[] data = new byte[256];

		Request request = getStandardOperations().getDescriptor( (short)((DescriptorConst.DESCRIPTOR_TYPE_STRING << 8) | (index)), getLangId(), data );

		/* requested string not present */
		if (2 > request.getDataLength())
			return;

		/* claimed length must be at least 2; length byte and type byte are mandatory. */
		if (2 > UsbUtil.unsignedInt(data[0]))
			throw new UsbException("String Descriptor length byte is an invalid length, minimum length must be 2, claimed length " + UsbUtil.unsignedInt(data[0]));

		/* string length (descriptor len minus 2 for header) */
		int len = UsbUtil.unsignedInt(data[0]) - 2;

		if (request.getDataLength() < (len + 2))
			throw new UsbException("String Descriptor length byte is longer than Descriptor data");

		setStringDescriptor( index, new StringDescriptorImp( data[0], data[1], bytesToString(data,len) ) );
	}

	//-------------------------------------------------------------------------
	// Instance variables
	//

	private UsbDeviceOsImp usbDeviceOsImp = null;

	private Hashtable stringDescriptors = new Hashtable();
	private short langId = 0x0000;

	private String speedString = "";
    
	private UsbInfoList configs = new DefaultUsbInfoList();
	private byte activeConfigNumber = 0;

	private UsbPortImp usbPortImp = null;

	private UsbOperationsImp usbOperationsImp = new UsbOperationsImp(this);

	private UsbDeviceEventHelper usbDeviceEventHelper = new UsbDeviceEventHelper();

	//-------------------------------------------------------------------------
	// Class constants
	//

	public static final String USB_DEVICE_NAME = "device";

//FIXME - move to StringDescriptorImp
	/**
	 * For all encodings supported by Java, see:
	 * <p><a href="http://java.sun.com/products/jdk/1.1/docs/guide/intl/encoding.doc.html">Java 1 (1.1) Supported Encodings</a>
	 * <p><a href="http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html">Java 2 (1.3) Supported Encodings</a>
	 * <p><a href="http://java.sun.com/j2se/1.3/docs/api/java/lang/package-summary.html#charenc">Java 2 (1.3) Required Encodings</a>
	 * <p>
	 * Conversion attempts to use encodings in this order:
	 * <ul>
	 * <li><code>UnicodeLittleUnmarked</code> (16-bit)</li>
	 * <li><code>UnicodeLittle</code> (16-bit)</li>
	 * <li><code>UTF-16LE</code> (16-bit)</li>
	 * <li><code>ASCII</code> (8-bit)</li>
	 * <li>Platform default (8-bit)</li>
	 * </ul>
	 * The high bytes are discarded before attempting the 8-bit encodings.
	 */
	public static final String[] ENCODING = {
		"UnicodeLittleUnmarked", /* Present in Sun Java 1.3 rt.jar (not 1.1) */
		"UnicodeLittle", /* Present in Sun Java 1.3 rt.jar and Sun Java 1.1 i18n.jar */
		"UTF-16LE", /* Required by Sun Java 1.3 Package Specifications */
	};
	/** Fallback encoding if no 16-bit encoding is supported */
	public static final String ENCODING_8BIT = "ASCII"; /* Present in Sun Java 1.3 rt.jar and Sun Java 1.1 i18n.jar */
}
