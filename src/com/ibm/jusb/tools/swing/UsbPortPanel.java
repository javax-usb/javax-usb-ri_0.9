package com.ibm.jusb.tools.swing;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import javax.usb.*;
import javax.usb.os.*;
import javax.usb.util.*;
import javax.usb.event.*;

/**
 * Class to display UsbPort info.
 * @author Dan Streetman
 */
public class UsbPortPanel extends UsbPanel
{
	public UsbPortPanel(UsbPort port)
	{
		super();
		usbPort = port;
		string = "UsbPort " + port.getPortNumber();
		refresh();
	}

	protected void refresh()
	{
		clear();
		appendln(string);
		initText();
	}

	protected void initText()
	{
		appendln("Port Number : " + usbPort.getPortNumber());
		appendln("Is Device Attached : " + usbPort.isUsbDeviceAttached());
	}

	private UsbPort usbPort = null;
}
