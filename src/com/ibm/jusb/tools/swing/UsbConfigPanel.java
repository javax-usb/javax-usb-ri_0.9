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
 * Class to display UsbConfig info.
 * @author Dan Streetman
 */
public class UsbConfigPanel extends UsbPanel
{
	public UsbConfigPanel(UsbConfig config)
	{
		super();
		usbConfig = config;
		string = "UsbConfig " + config.getConfigNumber();
		
		add(Box.createVerticalGlue());
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(Box.createRigidArea(new Dimension(0,10)));
		add(panel);
		
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
		appendln("Config Number : " + UsbUtil.unsignedInt(usbConfig.getConfigNumber()));
		appendln("Is Active : " + usbConfig.isActive());
		appendln("Config String : " + usbConfig.getConfigString());
		appendln("Attributes : " + UsbUtil.toHexString(usbConfig.getAttributes()));
		appendln("Max Power (mA) : " + (2 * UsbUtil.unsignedInt(usbConfig.getMaxPower()))); /* units are 2mA */
		appendln("Number of UsbInterfaces : " + UsbUtil.unsignedInt(usbConfig.getNumInterfaces()));
	}

	private UsbConfig usbConfig = null;
}
