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
import javax.usb.util.*;

/**
 * UsbConfig implementation.
 * <p>
 * This must be set up before use.
 * <ul>
 * <li>The UsbDeviceImp must be set either in the constructor or by the {@link #setUsbDeviceImp(UsbDeviceImp) setter}.</li>
 * <li>The ConfigDescriptor must be set either in the constructor or by the {@link #setConfigDescriptor(ConfigDescriptor) setter}.</li>
 * <li>All UsbInterfaceImp settings (active and inactive) must be {@link #addUsbInterfaceImp(UsbInterfaceImp) added}.</li>
 * </ul>
 * @author Dan Streetman
 * @author E. Michael Maximilien
 */
public class UsbConfigImp extends AbstractUsbInfo implements UsbConfig
{
	/**
	 * Constructor.
	 * @param device The parent device.
	 * @param desc This config's descriptor.
	 */
	public UsbConfigImp( UsbDeviceImp device, ConfigDescriptor desc )
	{
		setUsbDeviceImp( device );
		setConfigDescriptor( desc );
	}

	//**************************************************************************
	// Public methods

	/** @return name of this UsbInfo object */
	public String getName() 
	{
		if( super.getName().equals( "" ) )
			setName( USB_CONFIG_NAME_STRING + getConfigNumber() );
        
		return super.getName();
	}

	/** @return this configuration's number */
	public byte getConfigNumber() { return getConfigDescriptor().getConfigValue(); }

	/** @return this configuration's number of UsbInterface */
	public byte getNumInterfaces() { return getConfigDescriptor().getNumInterfaces(); }

	/** @return the attributes code for this configuration */
	public byte getAttributes() { return getConfigDescriptor().getAttributes(); }

	/** @return the maximum power needed for this configuration */
	public byte getMaxPower() { return getConfigDescriptor().getMaxPower(); }

	/** @return if this UsbConfig is active */
	public boolean isActive() { return getUsbDevice().getActiveUsbConfigNumber() == getConfigNumber(); }

	/** @return an iteration of USB device interfaces for this configuration */
	public UsbInfoListIterator getUsbInterfaces()
	{
		synchronized ( interfaces ) {
			UsbInfoList list = new DefaultUsbInfoList();
			Iterator iterator = interfaces.values().iterator();

			while (iterator.hasNext())
				list.addUsbInfo((UsbInfo)((List)iterator.next()).get(0));

			return list.usbInfoListIterator();
		}
	}

	/**
	 * @param The number of the interface to get
	 * @return A UsbInterface with the given number
	 */
	public UsbInterface getUsbInterface( byte number ) { return getUsbInterfaceImp(number); }

	/**
	 * @param The number of the interface to get.
	 * @return A UsbInterfaceImp with the given number.
	 */
	public UsbInterfaceImp getUsbInterfaceImp( byte number )
	{
		synchronized ( interfaces ) {
			Byte key = new Byte(number);

			if (!interfaces.containsKey(key))
				throw new UsbRuntimeException( "No UsbInterface with number " + UsbUtil.unsignedInt( number ) );

			return (UsbInterfaceImp)((List)interfaces.get(new Byte(number))).get(0);
		}
	}

	/**
	 * @param number the number of the UsbInterface to check.
	 * @return if this config contains the specified UsbInterface.
	 */
	public boolean containsUsbInterface( byte number )
	{
		try { getUsbInterface(number); }
		catch ( UsbRuntimeException urE ) { return false; }

		return true;
	}

	/**
	 * Add a UsbInterfaceImp.
	 * <p>
	 * The first setting for a particular interface number will default as the active setting.
	 * If the setting being added has already been added,
	 * it will be changed to be the active setting for the interface number.
	 * @param setting The UsbInterfaceImp to add.
	 */
	public void addUsbInterfaceImp( UsbInterfaceImp setting )
	{
		synchronized ( interfaces ) {
			Byte key = new Byte(setting.getInterfaceNumber());

			if (!interfaces.containsKey(key))
				interfaces.put(key, new ArrayList());

			List list = (List)interfaces.get(key);

			synchronized (list) {
				if (list.contains(setting)) {
					list.remove(setting);
					list.add(0, setting);
				} else {
					list.add(setting);
				}
			}
		}
	}

	/**
	 * Change an interface setting to be the active alternate setting.
	 * <p>
	 * This behaves identical to {@link #addUsbInterfaceImp(UsbInterfaceImp) addUsbInterfaceImp}.
	 * @param setting The UsbInterfaceImp setting to change.
	 */
	public void setActiveUsbInterfaceImpSetting(UsbInterfaceImp setting) { addUsbInterfaceImp(setting); }

	/**
	 * Get the List of settings for the specified interface numer.
	 * @param number The interface number.
	 * @return The List of settings, or null if no such interface number exists.
	 */
	public List getUsbInterfaceSettingList(byte number)
	{
		synchronized (interfaces) {
			return (List)interfaces.get(new Byte(number));
		}
	}

	/** @return The parent UsbDevice */
	public UsbDevice getUsbDevice() { return getUsbDeviceImp(); }

	/** @return The parent UsbDeviceImp */
	public UsbDeviceImp getUsbDeviceImp() { return usbDeviceImp; }

	/**
	 * Set the UsbDeviceImp.
	 * <p>
	 * This will also add this to the parent UsbDeviceImp.
	 * @param device The parent UsbDeviceImp
	 */
	public void setUsbDeviceImp(UsbDeviceImp device)
	{
		usbDeviceImp = device;

		if (null != device)
			device.addUsbConfigImp(this);
	}

	/** @return the config descriptor for this config */
	public ConfigDescriptor getConfigDescriptor() { return (ConfigDescriptor)getDescriptor(); }

	/** @return the String description of this config */
	public String getConfigString()
	{
		try {
			return getUsbDeviceImp().getString( getConfigDescriptor().getConfigIndex() );
		} catch ( UsbException uE ) {
//FIXME - this method should throw UsbException
			return null;
		}
	}

	/**
	 * Visitor.accept method
	 * @param visitor the UsbInfoVisitor visiting this UsbInfo
	 */
	public void accept( UsbInfoVisitor visitor ) { visitor.visitUsbConfig( this ); }

	/** @param desc the new config descriptor */
	public void setConfigDescriptor( ConfigDescriptor desc ) { setDescriptor( desc ); }

	//**************************************************************************
	// Instance variables

	private UsbDeviceImp usbDeviceImp = null;

	private HashMap interfaces = new HashMap();

	//**************************************************************************
	// Class constants

	public static final String USB_CONFIG_NAME_STRING = "config";
}
