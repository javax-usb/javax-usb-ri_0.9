package com.ibm.jusb.util;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;

import com.ibm.jusb.*;
import com.ibm.jusb.os.*;

/**
 * Virtual UsbRootHub implementation.
 * @author Dan Streetman
 */
public class VirtualUsbRootHubImp extends UsbRootHubImp implements UsbRootHub
{
	public VirtualUsbRootHubImp()
	{
		super(virtualDeviceDescriptor, new VirtualUsbDeviceOsImp());
		setSpeedString(SPEED_STRING);
		init();
	}

	//**************************************************************************
	// Public methods

	public void init()
	{
		UsbConfigImp virtualConfig = new UsbConfigImp(this, virtualConfigDescriptor);
		UsbInterfaceImp virtualInterface = new UsbInterfaceImp(virtualConfig, virtualInterfaceDescriptor, new AbstractUsbInterfaceOsImp());

		virtualConfig.addUsbInterfaceImp(virtualInterface);

		addUsbConfigImp(virtualConfig);
		setActiveUsbConfigNumber(CONFIG_NUM);
	}

	/** No connect operation */
	public void connect(UsbHubImp hub, byte portNumber) throws UsbException
	{
		throw new UsbException("Cannot connect Virtual UsbRootHub");
	}

	/** No disconnect */
	public void disconnect()
	{
		throw new UsbRuntimeException("Cannot disconnect Virtual UsbRootHub");
	}

	/** No UsbPort use */
	public void setUsbPortImp(UsbPortImp port)
	{
		throw new UsbRuntimeException("Virtual UsbRootHub cannot have parent UsbPort");
	}

	/** No UsbPort use */
	public UsbPortImp getUsbPortImp()
	{
		throw new UsbRuntimeException("Virtual UsbRootHub has no parent UsbPort");
	}

	//**************************************************************************
	// Class constants

	public static final short VENDOR_ID = (short)0xffff;
	public static final short PRODUCT_ID = (short)0xffff;
	public static final short DEVICE_BCD = (short)0x0000;
	public static final short USB_BCD = (short)0x0101;
	public static final String SPEED_STRING = "1.5 Mbps";

	public static final byte CONFIG_NUM = (byte)0x01;
	public static final short CONFIG_TOTAL_LEN = (short)0x00ff;

	public static final byte INTERFACE_NUM = (byte)0x00;
	public static final byte SETTING_NUM = (byte)0x00;

	public static final DeviceDescriptorImp virtualDeviceDescriptor =
		new DeviceDescriptorImp( DescriptorConst.DESCRIPTOR_MIN_LENGTH_DEVICE,
								 DescriptorConst.DESCRIPTOR_TYPE_DEVICE,
								 DescriptorConst.DEVICE_CLASS_HUB,
								 (byte)0x00, /* subclass */
								 (byte)0x00, /* protocol */
								 (byte)0x08, /* maxpacketsize */
								 (byte)0x00, /* man index */
								 (byte)0x00, /* prod index */
								 (byte)0x00, /* serial index */
								 (byte)0x01, /* n configs */
								 VENDOR_ID,
								 PRODUCT_ID,
								 DEVICE_BCD,
								 USB_BCD );

	public static final ConfigDescriptorImp virtualConfigDescriptor =
		new ConfigDescriptorImp( DescriptorConst.DESCRIPTOR_MIN_LENGTH_CONFIG,
								 DescriptorConst.DESCRIPTOR_TYPE_CONFIG,
								 (short)CONFIG_TOTAL_LEN,
								 (byte)0x01, /* n interfaces */
								 CONFIG_NUM,
								 (byte)0x00, /* config index */
								 (byte)0x80, /* attr */
								 (byte)0x00 ); /* maxpower */

	public static final InterfaceDescriptorImp virtualInterfaceDescriptor =
		new InterfaceDescriptorImp( DescriptorConst.DESCRIPTOR_MIN_LENGTH_INTERFACE,
									DescriptorConst.DESCRIPTOR_TYPE_INTERFACE,
									INTERFACE_NUM,
									SETTING_NUM,
									(byte)0x00, /* num endpoints */
									DescriptorConst.DEVICE_CLASS_HUB,
									(byte)0x00, /* subclass */
									(byte)0x00, /* protocol */
									(byte)0x00 ); /* iface index */

}
