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
 * Class to display UsbEndpoint info.
 * @author Dan Streetman
 */
public class UsbEndpointPanel extends UsbPanel
{
	public UsbEndpointPanel(UsbEndpoint ep)
	{
		super();
		usbEndpoint = ep;
		string = "UsbEndpoint 0x" + UsbUtil.toHexString(ep.getEndpointAddress());
		
		// add empty space, make the UI more consistent
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
		String type = null;
		switch (usbEndpoint.getType()) {
		case UsbInfoConst.ENDPOINT_TYPE_CONTROL: type = "Control"; break;
		case UsbInfoConst.ENDPOINT_TYPE_BULK: type = "Bulk"; break;
		case UsbInfoConst.ENDPOINT_TYPE_INT: type = "Interrupt"; break;
		case UsbInfoConst.ENDPOINT_TYPE_ISOC: type = "Isochronous"; break;
		default: type = "Unknown"; break;
		}

		String direction = null;
		switch (usbEndpoint.getDirection()) {
		case UsbInfoConst.ENDPOINT_DIRECTION_IN: direction = "IN"; break;
		case UsbInfoConst.ENDPOINT_DIRECTION_OUT: direction = "OUT"; break;
		default: direction = "Unknown"; break;
		}

		appendln("Endpoint Address : 0x" + UsbUtil.toHexString(usbEndpoint.getEndpointAddress()));
		appendln("Type : " + UsbUtil.toHexString(usbEndpoint.getType()) + " (" + type + ")");
		appendln("Direction : " + direction);
		appendln("Interval : " + UsbUtil.unsignedInt(usbEndpoint.getInterval()));
		appendln("Max Packet Size : " + UsbUtil.unsignedInt(usbEndpoint.getMaxPacketSize()));
		appendln("Attributes : " + UsbUtil.toHexString(usbEndpoint.getAttributes()));
	}

	private UsbEndpoint usbEndpoint = null;
}
