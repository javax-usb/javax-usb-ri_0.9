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
 * Class to display UsbHub info.
 * @author Dan Streetman
 */
public class UsbHubPanel extends UsbDevicePanel
{
	public UsbHubPanel(UsbHub hub)
	{
		super();
		usbDevice = hub;
		usbHub = hub;
		string = hub.isUsbRootHub() ? "UsbRootHub" : "UsbHub";
		initPanels();
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
		appendln("Number of Ports : " + usbHub.getNumberOfPorts());
		super.initText();
	}

	private UsbHub usbHub = null;
}
