package com.ibm.jusb;

/**
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import javax.usb.*;

/**
 * UsbIrpFactory implementation.
 * @author Dan Streetman
 */
public class UsbIrpImpFactory implements UsbIrpFactory
{
	/** @return A new UsbIrp */
	public UsbIrp createUsbIrp() { return createUsbIrpImp(); }

	/** @return A new UsbIrpImp */
	public UsbIrpImp createUsbIrpImp() { return new UsbIrpImp(); }

}
