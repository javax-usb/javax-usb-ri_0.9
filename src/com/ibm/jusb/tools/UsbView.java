package com.ibm.jusb.tools;

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

import com.ibm.jusb.tools.text.*;
import com.ibm.jusb.tools.swing.*;

/**
 * Class to display the USB device topology tree.
 * @author Dan Streetman
 */
public class UsbView
{
	/** Main */
	public static void main( String[] argv ) throws Exception
	{
		try {
			SwingUsbView.main(argv);
		} catch ( InternalError iE ) {
			TextUsbView.main(argv);
		}
	}

}
