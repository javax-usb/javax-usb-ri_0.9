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

//FIXME - create default implementation in API util pacakge
import com.ibm.jusb.RequestImp;

/**
 * Class to display Request information.
 * @author Dan Streetman
 */
public class RequestPanel extends JPanel implements Cloneable
{
	public RequestPanel()
	{
		setLayout( new BorderLayout());

		refreshButton.addActionListener(refreshListener);
		clearButton.addActionListener(clearListener);

		JPanel setupPacketPanel = new JPanel( new GridLayout(4,2,2,2));
		setupPacketPanel.add(bmRequestTypeLabel);
		setupPacketPanel.add(bmRequestTypeField);
		setupPacketPanel.add(bRequestLabel);
		setupPacketPanel.add(bRequestField);
		setupPacketPanel.add(wValueLabel);
		setupPacketPanel.add(wValueField);
		setupPacketPanel.add(wIndexLabel);
		setupPacketPanel.add(wIndexField);
		
		JPanel panel = new JPanel( new BorderLayout());
		panel.add(setupPacketPanel, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(2,2,4,2));
		
		buttonPanel.add(syncCheckBox);
		buttonPanel.add(refreshButton);
		buttonPanel.add(clearButton);

		JPanel rightPanel = new JPanel(new BorderLayout());
		
		rightPanel.add(packetDataScroll, BorderLayout.CENTER);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(2,2,3,2));

		add(panel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.CENTER);
	}

	public String toString() { return "Buffer @" + UsbUtil.toHexString(hashCode()); }

	public Object clone()
	{
		RequestPanel newPanel = new RequestPanel();
		newPanel.syncCheckBox.setSelected(syncCheckBox.isSelected());
		newPanel.packetDataTextArea.setText(packetDataTextArea.getText());
		return newPanel;
	}

	public void submit(UsbOperations operations) throws UsbException,NumberFormatException
	{
		lastData = getData();
		RequestFactory factory = UsbHostManager.getInstance().getUsbServices().getRequestFactory();

		RequestImp requestImp = new RequestImp(null);
		requestImp.setRequestType((byte)Integer.decode(bmRequestTypeField.getText()).intValue());
		requestImp.setRequestCode((byte)Integer.decode(bRequestField.getText()).intValue());
		requestImp.setValue((short)Integer.decode(wValueField.getText()).intValue());
		requestImp.setIndex((short)Integer.decode(wIndexField.getText()).intValue());
		requestImp.setData(getData());

		if (syncCheckBox.isSelected())
			operations.syncSubmit(requestImp);
		else
			operations.asyncSubmit(requestImp);
	}

	protected byte[] getData()
	{
		java.util.List list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(packetDataTextArea.getText());
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			byte b;
			try {
				b = (byte)Integer.decode(token).intValue(); /* truncate without error anything greater than 1 byte */
			} catch ( NumberFormatException nfE ) {
				/* If character specified as 'X' or "X", then use the raw character specified */
				if ((3 == token.length()) && ((0x22 == token.charAt(0) && 0x22 == token.charAt(2)) || (0x27 == token.charAt(0) && 0x27 == token.charAt(2))))
					b = (byte)token.charAt(1);
				else
					throw nfE;
			}
			list.add(new Byte(b));
		}

		byte[] data = new byte[list.size()];
		for (int i=0; i<data.length; i++)
			data[i] = ((Byte)list.get(i)).byteValue();

		return data;
	}

	protected void refresh()
	{
		if (null == lastData)
			return;

		if (!Arrays.equals(lastData, getData())) {
			packetDataTextArea.setText("");
			for (int i=0; i<lastData.length; i++)
				packetDataTextArea.append("0x" + UsbUtil.toHexString(lastData[i]) + " ");
		}
	}

	protected void clear()
	{
		packetDataTextArea.setText("");
	}

	private JPanel packetOptionsPanel = new JPanel();
	protected JCheckBox syncCheckBox = new JCheckBox("Sync", true);
	private JPanel buttonPanel = new JPanel();
	private Vector requestTypeVector = new Vector();
	private Box bmRequestTypeBox = new Box(BoxLayout.X_AXIS);
	private JLabel bmRequestTypeLabel = new JLabel("bmReqType");
	private JPanel bmRequestTypePanel = new JPanel();
	protected JTextField bmRequestTypeField = new JTextField("00", 4);
	private Box bRequestBox = new Box(BoxLayout.X_AXIS);
	private JLabel bRequestLabel = new JLabel("bRequest");
	private JPanel bRequestPanel = new JPanel();
	protected JTextField bRequestField = new JTextField("00", 4);
	private Box wValueBox = new Box(BoxLayout.X_AXIS);
	private JLabel wValueLabel = new JLabel("wValue");
	private JPanel wValuePanel = new JPanel();
	protected JTextField wValueField = new JTextField("0000", 6);
	private Box wIndexBox = new Box(BoxLayout.X_AXIS);
	private JLabel wIndexLabel = new JLabel("wIndex");
	private JPanel wIndexPanel = new JPanel();
	protected JTextField wIndexField = new JTextField("0000", 6);
	private JButton refreshButton = new JButton("Refresh");
	private JButton clearButton = new JButton("Clear");
	protected JTextArea packetDataTextArea = new JTextArea();
	private JScrollPane packetDataScroll = new JScrollPane(packetDataTextArea);

	private byte[] lastData = null;

	private ActionListener refreshListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { refresh(); } };
	private ActionListener clearListener = new ActionListener() { public void actionPerformed(ActionEvent aE) { clear(); } };

}
