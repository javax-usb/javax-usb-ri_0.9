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
import javax.usb.util.*;

import com.ibm.jusb.os.*;

/**
 * UsbRootHub implementation.
 * <p>
 * This must be set up before use and/or connection to the topology tree.  To set up,
 * see {@link com.ibm.jusb.UsbHubImp UsbHubImp documentation}.
 * @author Dan Streetman
 * @author E. Michael Maximilien
 */
public class UsbRootHubImp extends UsbHubImp implements UsbRootHub
{
	/**
	 * Constructor
	 * @param desc This device's descriptor.
	 * @param device The platform device implementation.
	 */
	public UsbRootHubImp( DeviceDescriptor desc, UsbDeviceOsImp device ) { super(desc,device); }

	/**
	 * Constructor
	 * @param ports The initial number of ports.
	 * @param desc This device's descriptor.
	 * @param device The platform device implementation.
	 */
	public UsbRootHubImp( int ports, DeviceDescriptor desc, UsbDeviceOsImp device ) { super(ports,desc,device); }

	//**************************************************************************
	// Public methods

    /** @return true if this is the root hub */
    public boolean isUsbRootHub() { return true;  }

    /**
     * Visitor.accept method
     * @param visitor the UsbInfoVisitor visiting this UsbInfo
     */
    public void accept( UsbInfoVisitor visitor ) { visitor.visitUsbRootHub( this ); }

	//**************************************************************************
	// Class constants

	public static final String USB_ROOT_HUB_NAME = "*hub";
}
