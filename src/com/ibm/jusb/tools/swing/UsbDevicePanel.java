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
 * Class to display UsbDevice info.
 * @author Dan Streetman
 */
public class UsbDevicePanel extends UsbPanel
{
	public UsbDevicePanel() { super(); }

	public UsbDevicePanel(UsbDevice device)
	{
		super();
		usbDevice = device;
		string = "UsbDevice";
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
		appendln("Vendor ID : 0x" + UsbUtil.toHexString(usbDevice.getVendorId()));
		appendln("Product ID : 0x" + UsbUtil.toHexString(usbDevice.getProductId()));
		appendln("Speed : " + usbDevice.getSpeedString());
		appendln("Manufacturer : " + usbDevice.getManufacturer());
		appendln("Product : " + usbDevice.getProductString());
		appendln("Serial Number : " + usbDevice.getSerialNumber());
		appendln("Device Class : 0x" + UsbUtil.toHexString(usbDevice.getDeviceClass()));
		appendln("Device Subclass : 0x" + UsbUtil.toHexString(usbDevice.getDeviceSubClass()));
		appendln("Device Protocol : 0x" + UsbUtil.toHexString(usbDevice.getDeviceProtocol()));
		appendln("BCD Device : " + UsbUtil.toHexString(usbDevice.getBcdDevice()));
		appendln("BCD USB : " + UsbUtil.toHexString(usbDevice.getBcdUsb()));
		appendln("Max Packet Size : " + UsbUtil.unsignedInt(usbDevice.getMaxPacketSize()));
		appendln("Is Configured : " + usbDevice.isConfigured());
		appendln("Active UsbConfig Number : " + UsbUtil.unsignedInt(usbDevice.getActiveUsbConfigNumber()));
		appendln("Number of UsbConfigs : " + UsbUtil.unsignedInt(usbDevice.getNumConfigs()));
	}

	protected void initPanels()
	{
		usbDevice.addUsbDeviceListener(deviceListener);

		outputTextArea.setEditable(false);

		clearButton.addActionListener(clearListener);
		submitButton.addActionListener(submitListener);
		newPacketButton.addActionListener(newPacketListener);
		copyPacketButton.addActionListener(copyPacketListener);
		removeButton.addActionListener(removeListener);
		upButton.addActionListener(upListener);
		downButton.addActionListener(downListener);

		packetJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		packetJList.addListSelectionListener(packetListListener);

		clearPanel.add(outputScroll, BorderLayout.CENTER);
		JPanel panel = new JPanel();
		panel.add(clearButton);
		clearPanel.add(panel, BorderLayout.EAST);
		clearPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

		requestPanel.setLayout(requestLayout);

		packetPanel.add(packetJList);

		buttonsPanel.add(upButton);
		buttonsPanel.add(newPacketButton);
		buttonsPanel.add(downButton);
		buttonsPanel.add(copyPacketButton);
		buttonsPanel.add(submitButton);
		buttonsPanel.add(removeButton);

		submitPanel.add(packetListScroll, BorderLayout.CENTER);
		panel = new JPanel();
		panel.add(buttonsPanel);
		submitPanel.add(panel, BorderLayout.EAST);
		submitPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		
		add(clearPanel);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(submitPanel);
		add(requestPanel);
		
		refreshButtons();
	}

	/**
	 * Method init.
	 */
	private void refreshButtons() {
		
		submitButton.setEnabled( packetList.size() > 0 );
		upButton.setEnabled( packetList.size() > 0 && getSelectedIndex() > 0 );
		downButton.setEnabled( packetList.size() > 0 && getSelectedIndex() != packetList.size() - 1 );
		removeButton.setEnabled( packetList.size() > 0 );
		copyPacketButton.setEnabled( packetList.size() > 0 );
		newPacketButton.setEnabled( true );

	}

	protected int getSelectedIndex()
	{
		if (packetJList.isSelectionEmpty() && !packetList.isEmpty())
			packetJList.setSelectedIndex(0);
		return packetJList.getSelectedIndex();
	}

	protected void updateSelection()
	{
		if (!packetList.isEmpty())
			requestLayout.show(requestPanel, packetList.get(getSelectedIndex()).toString());
		validate();
		refreshButtons();
	}

	protected void submit()
	{
		RequestPanel panel = null;

		try {
			for (int i=0; i<packetList.size(); i++) {
				panel = (RequestPanel)packetList.get(i);
//FIXME - remove this standard/class/vendor/etc... crap
				panel.submit(usbDevice.getStandardOperations());
			}
		} catch ( UsbException uE ) {
			JOptionPane.showMessageDialog(null, "UsbException while submitting " + panel + " : " + uE.getMessage());
		} catch ( NumberFormatException nfE ) {
			JOptionPane.showMessageDialog(null, "NumberFormatException in " + panel + " : " + nfE.getMessage());
		}
	}

	protected void addPacket(RequestPanel newPanel)
	{
		int index = packetJList.getSelectedIndex();
		packetList.add(newPanel);
		packetJList.setListData(packetList);
		requestPanel.add(newPanel, newPanel.toString());
		if (0 <= index)
			packetJList.setSelectedIndex(index);
		updateSelection();
		refreshButtons();
	}

	protected void copyPacket()
	{
		if (packetJList.isSelectionEmpty())
			return;

		addPacket((RequestPanel)((RequestPanel)packetList.get(packetJList.getSelectedIndex())).clone());
	}

	protected void removePacket()
	{
		int index = packetJList.getSelectedIndex();
		if (0 <= index) {
			packetList.remove(index);
			packetJList.setListData(packetList);
			if (packetList.size() <= index)
					index--;
			if (0 <= index)
					packetJList.setSelectedIndex(index);
			updateSelection();
		}
		refreshButtons();
	}

	protected void upPacket()
	{
		if (packetJList.isSelectionEmpty())
			return;

		int index = packetJList.getSelectedIndex();
		if (0 < index) {
			packetList.set(index, packetList.set(index-1, packetList.get(index)));
			packetJList.setListData(packetList);
			packetJList.setSelectedIndex(index-1);
			updateSelection();
		}
		refreshButtons();
	}

	protected void downPacket()
	{
		if (packetJList.isSelectionEmpty())
			return;

		int index = packetJList.getSelectedIndex();
		if (packetList.size() > (index+1)) {
			packetList.set(index, packetList.set(index+1, packetList.get(index)));
			packetJList.setListData(packetList);
			packetJList.setSelectedIndex(index+1);
			updateSelection();
		}
		refreshButtons();
	}

	protected void gotData(byte[] data, int len)
	{
		for (int i=0; i<len && i<data.length; i++)
			outputTextArea.append(UsbUtil.toHexString(data[i]) + " ");
		outputTextArea.append("\n");
		outputTextArea.setCaretPosition(outputTextArea.getText().length());
		validate();
	}

	protected void gotError(int error, UsbException uE)
	{
		JOptionPane.showMessageDialog(null, "Got UsbDeviceErrorEvent code " + error + " : " + uE.getMessage());
	}

	private JPanel clearPanel = new JPanel(new BorderLayout());
	private JTextArea outputTextArea = new JTextArea();
	private JPanel submitPanel = new JPanel( new BorderLayout());
	private JScrollPane outputScroll = new JScrollPane(outputTextArea);
	private Vector packetList = new Vector();
	private JList packetJList = new JList();
	private JPanel packetPanel = new JPanel();
	private JScrollPane packetListScroll = new JScrollPane(packetPanel);
	private Box submitBox = new Box(BoxLayout.X_AXIS);
	private JPanel buttonsPanel = new JPanel( new GridLayout(3,2,2,2));
	private JPanel submitButtonLeftPanel = new JPanel();
	private JPanel submitButtonRightPanel = new JPanel();
	private JPanel requestPanel = new JPanel();
	private CardLayout requestLayout = new CardLayout();

	private JButton clearButton = new JButton("Clear");
	private JButton submitButton = new JButton("Submit");
	private JButton newPacketButton = new JButton("New");
	private JButton copyPacketButton = new JButton("Copy");
	private JButton removeButton = new JButton("Remove");
	private JButton upButton = new JButton("Up");
	private JButton downButton = new JButton("Down");

	private ActionListener clearListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { outputTextArea.setText(""); } };
	private ActionListener submitListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { submit(); } };
	private ActionListener newPacketListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { addPacket(new RequestPanel()); } };
	private ActionListener copyPacketListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { copyPacket(); } };
	private ActionListener removeListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { removePacket(); } };
	private ActionListener upListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { upPacket(); } };
	private ActionListener downListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { downPacket(); } };

	private ListSelectionListener packetListListener =
		new ListSelectionListener() { public void valueChanged(ListSelectionEvent lsE) { updateSelection(); } };

	private UsbDeviceListener deviceListener = new UsbDeviceListener() {
			public void dataEventOccurred(UsbDeviceDataEvent uddE) { gotData(uddE.getData(), uddE.getDataLength()); }
			public void errorEventOccurred(UsbDeviceErrorEvent udeE) { gotError(udeE.getErrorCode(), udeE.getUsbException()); }
			public void usbDeviceDetached(UsbDeviceEvent udE) { }
		};

	protected UsbDevice usbDevice = null;
}
