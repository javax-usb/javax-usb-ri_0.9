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
 * Class to display UsbPipe info.
 * @author Dan Streetman
 */
public class UsbPipePanel extends UsbPanel
{
	public UsbPipePanel(UsbPipe pipe)
	{
		super();
		usbPipe = pipe;
		string = "UsbPipe";
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
		appendln("Is Active : " + usbPipe.isActive());
		appendln("Is Open : " + usbPipe.isOpen());
		appendln("Max Packet Size : " + UsbUtil.unsignedInt(usbPipe.getMaxPacketSize()));
	}

	protected void initPanels()
	{
		outputTextArea.setEditable(false);

		openButton.addActionListener(openListener);
		closeButton.addActionListener(closeListener);
		clearButton.addActionListener(clearListener);
		submitButton.addActionListener(submitListener);
		newPacketButton.addActionListener(newPacketListener);
		copyPacketButton.addActionListener(copyPacketListener);
		removeButton.addActionListener(removeListener);
		upButton.addActionListener(upListener);
		downButton.addActionListener(downListener);

		packetJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		packetJList.addListSelectionListener(packetListListener);

		openClosePanel.add(openButton);
		openClosePanel.add(closeButton);
		openClosePanel.add(clearButton);

		submitButtonLeftPanel.setLayout(new BoxLayout(submitButtonLeftPanel, BoxLayout.Y_AXIS));
		submitButtonLeftPanel.add(submitButton);
		submitButtonLeftPanel.add(newPacketButton);
		submitButtonLeftPanel.add(copyPacketButton);
		submitButtonRightPanel.setLayout(new BoxLayout(submitButtonRightPanel, BoxLayout.Y_AXIS));
		submitButtonRightPanel.add(removeButton);
		submitButtonRightPanel.add(upButton);
		submitButtonRightPanel.add(downButton);

		irpPanel.setLayout(irpLayout);

		packetPanel.add(packetJList);

		submitBox.add(submitButtonLeftPanel);
		submitBox.add(packetListScroll);
		submitBox.add(submitButtonRightPanel);

		add(openClosePanel);
		add(outputScroll);
		add(submitBox);
		add(irpPanel);
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
			irpLayout.show(irpPanel, packetList.get(getSelectedIndex()).toString());
		validate();
	}

	protected void open()
	{
		if (usbPipe.isOpen())
			return;

		usbPipe.addUsbPipeListener(pipeListener);
		try {
			usbPipe.open();
			refresh();
		} catch ( UsbException uE ) {
			JOptionPane.showMessageDialog(null, "Could not open UsbPipe : " + uE.getMessage());
		}
	}

	protected void close()
	{
		if (!usbPipe.isOpen())
			return;

		usbPipe.removeUsbPipeListener(pipeListener);
		try {
			usbPipe.close();
			refresh();
		} catch ( UsbException uE ) {
			JOptionPane.showMessageDialog(null, "Could not close UsbPipe : " + uE.getMessage());
		}
	}

	protected void submit()
	{
		UsbIrpPanel panel = null;

		try {
			for (int i=0; i<packetList.size(); i++) {
				panel = (UsbIrpPanel)packetList.get(i);
				panel.submit(usbPipe);
			}
		} catch ( UsbException uE ) {
			JOptionPane.showMessageDialog(null, "UsbException while submitting " + panel + " : " + uE.getMessage());
		} catch ( NumberFormatException nfE ) {
			JOptionPane.showMessageDialog(null, "NumberFormatException in " + panel + " : " + nfE.getMessage());
		}
	}

	protected void addPacket(UsbIrpPanel newPanel)
	{
		int index = packetJList.getSelectedIndex();
		packetList.add(newPanel);
		packetJList.setListData(packetList);
		irpPanel.add(newPanel, newPanel.toString());
		if (0 <= index)
			packetJList.setSelectedIndex(index);
		updateSelection();
	}

	protected void copyPacket()
	{
		if (packetJList.isSelectionEmpty())
			return;

		addPacket((UsbIrpPanel)((UsbIrpPanel)packetList.get(packetJList.getSelectedIndex())).clone());
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
		JOptionPane.showMessageDialog(null, "Got UsbPipeErrorEvent code " + error + " : " + uE.getMessage());
	}

	private JPanel openClosePanel = new JPanel();
	private JTextArea outputTextArea = new JTextArea();
	private JScrollPane outputScroll = new JScrollPane(outputTextArea);
	private Vector packetList = new Vector();
	private JList packetJList = new JList();
	private JPanel packetPanel = new JPanel();
	private JScrollPane packetListScroll = new JScrollPane(packetPanel);
	private Box submitBox = new Box(BoxLayout.X_AXIS);
	private JPanel submitButtonLeftPanel = new JPanel();
	private JPanel submitButtonRightPanel = new JPanel();
	private JPanel irpPanel = new JPanel();
	private CardLayout irpLayout = new CardLayout();

	private JButton openButton = new JButton("Open");
	private JButton closeButton = new JButton("Close");
	private JButton clearButton = new JButton("Clear");
	private JButton submitButton = new JButton("Submit");
	private JButton newPacketButton = new JButton("New");
	private JButton copyPacketButton = new JButton("Copy");
	private JButton removeButton = new JButton("Remove");
	private JButton upButton = new JButton("Up");
	private JButton downButton = new JButton("Down");

	private ActionListener openListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { open(); } };
	private ActionListener closeListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { close(); } };
	private ActionListener clearListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { outputTextArea.setText(""); } };
	private ActionListener submitListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { submit(); } };
	private ActionListener newPacketListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { addPacket(new UsbIrpPanel()); } };
	private ActionListener copyPacketListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { copyPacket(); } };
	private ActionListener removeListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { removePacket(); } };
	private ActionListener upListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { upPacket(); } };
	private ActionListener downListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { downPacket(); } };

	private ListSelectionListener packetListListener =
		new ListSelectionListener() { public void valueChanged(ListSelectionEvent lsE) { updateSelection(); } };

	private UsbPipeListener pipeListener = new UsbPipeListener() {
			public void dataEventOccurred(UsbPipeDataEvent updE) { gotData(updE.getData(), updE.getDataLength()); }
			public void errorEventOccurred(UsbPipeErrorEvent upeE) { gotError(upeE.getErrorCode(), upeE.getUsbException()); }
		};

	private UsbPipe usbPipe = null;
}
