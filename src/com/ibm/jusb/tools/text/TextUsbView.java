package com.ibm.jusb.tools.text;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;
import javax.usb.event.*;
import javax.usb.util.*;

/**
 * Class to display the USB device topology tree using standard out.
 * @author Dan Streetman
 */
public class TextUsbView
{
	/** Main */
	public static void main( String[] argv ) throws UsbException
	{
		UsbRootHub rootHub = UsbHostManager.getInstance().getUsbServices().getUsbRootHub();

		for (int i=0; i<argv.length; i++) {
			if ("-v".equals(argv[i]) || "--verbose".equals(argv[i]))
				verbose++;
		}

		displayUsbDevice(rootHub, "");
	}

	protected static void displayUsbDevice(UsbDevice device, String offset)
	{
		print((device.isUsbHub() ? "UsbHub" : "UsbDevice"));

		if (1 < verbose) print(" " + device.getManufacturer());

		if (0 < verbose) print(" " + device.getProductString());

		if (2 < verbose) print(" " + device.getSerialNumber());

		println("");

		if (device.isUsbHub()) {
			UsbHub hub = (UsbHub)device;
			int ports = UsbUtil.unsignedInt(hub.getNumberOfPorts());

			offset += "  ";

			for (int i=0; i<ports; i++) {
				UsbPort port = hub.getUsbPort((byte)(i+1));

				if (port.isUsbDeviceAttached()) {
					print(offset);

					displayUsbDevice(port.getUsbDevice(), offset);
				} else {
					if (2 < verbose) println(offset + "UsbPort " + port.getPortNumber());
				}
			}
		}
	}

	protected static void print(String s) { System.out.print(s); }

	protected static void println(String s) { System.out.println(s); }

	private static int verbose = 0;
}
