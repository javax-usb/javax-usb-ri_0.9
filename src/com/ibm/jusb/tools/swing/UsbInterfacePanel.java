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
 * Class to display UsbInterface info.
 * @author Dan Streetman
 */
public class UsbInterfacePanel extends UsbPanel
{
	public UsbInterfacePanel(UsbInterface iface)
	{
		super();
		usbInterface = iface;
		createClaimPanel();
		string = "UsbInterface " + iface.getInterfaceNumber();
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
		appendln("Interface Number : " + UsbUtil.unsignedInt(usbInterface.getInterfaceNumber()));
		appendln("Is Active : " + usbInterface.isActive());
		appendln("Is Claimed : " + usbInterface.isClaimed());
		appendln("Alternate Setting : " + UsbUtil.unsignedInt(usbInterface.getAlternateSettingNumber()));
		appendln("Active Alternate Setting Number : " + UsbUtil.unsignedInt(usbInterface.getActiveAlternateSettingNumber()));
		appendln("Interface Class : " + UsbUtil.toHexString(usbInterface.getInterfaceClass()));
		appendln("Interface Subclass : " + UsbUtil.toHexString(usbInterface.getInterfaceSubClass()));
		appendln("Interface Protocol : " + UsbUtil.toHexString(usbInterface.getInterfaceProtocol()));
		appendln("Interface String : " + usbInterface.getInterfaceString());
		appendln("Number of Alternate Settings : " + UsbUtil.unsignedInt(usbInterface.getNumAlternateSettings()));
		appendln("Number of UsbEndpoints : " + UsbUtil.unsignedInt(usbInterface.getNumEndpoints()));
	}

	protected void createClaimPanel()
	{
		claimButton.addActionListener(claimListener);
		releaseButton.addActionListener(releaseListener);

		claimPanel.add(claimButton);
		claimPanel.add(releaseButton);

		add(claimPanel);
	}

	private UsbInterface usbInterface = null;

	private JPanel claimPanel = new JPanel();
	private JButton claimButton = new JButton("Claim");
	private ActionListener claimListener = new ActionListener() {
			public void actionPerformed(ActionEvent aE)
			{
				try { usbInterface.claim(); }
				catch ( UsbException uE ) { JOptionPane.showMessageDialog(null, "Could not claim UsbInterface : " + uE.getMessage()); }
				catch ( NotActiveException naE ) { JOptionPane.showMessageDialog(null, "Could not claim UsbInterface : " + naE.getMessage()); }
				refresh();
			}
		};
	private JButton releaseButton = new JButton("Release");
	private ActionListener releaseListener = new ActionListener() {
			public void actionPerformed(ActionEvent aE)
			{
				try { usbInterface.release(); }
				catch ( UsbException uE ) { JOptionPane.showMessageDialog(null, "Could not release UsbInterface : " + uE.getMessage()); }
				refresh();
			}
		};
}
