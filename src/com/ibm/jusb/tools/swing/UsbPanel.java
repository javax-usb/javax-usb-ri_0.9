package com.ibm.jusb.tools.swing;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.awt.event.*;

import javax.swing.*;

/**
 * Class to display Usb info.
 * @author Dan Streetman
 */
public abstract class UsbPanel extends JPanel
{
	public UsbPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		textArea.setEditable(false);
		refreshButton.addActionListener(refreshListener);

		refreshPanel.add(refreshButton);

		add(refreshPanel);
		add(textScroll);
	}

	public String toString() { return string; }

	protected abstract void refresh();

	protected void clear() { textArea.setText(""); }
	protected void append(String s) { textArea.append(s); }
	protected void appendln(String s) { append(s + "\n"); }

	protected JPanel refreshPanel = new JPanel();
	protected JButton refreshButton = new JButton("Refresh");
	protected ActionListener refreshListener = new ActionListener() {
			public void actionPerformed(ActionEvent aE) { refresh(); }
		};

	protected JTextArea textArea = new JTextArea();
	protected JScrollPane textScroll = new JScrollPane(textArea);
	protected String string;
}
