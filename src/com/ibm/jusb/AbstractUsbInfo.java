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
 * Abstract class implementing the UsbInfo interface
 * @author E. Michael Maximilien
 * @author Dan Streetman
 * @version 0.0.1 (JDK 1.1.x)
 */
public abstract class AbstractUsbInfo implements UsbInfo
{
    //-------------------------------------------------------------------------
    // Public methods
    //

    /** @return name of this UsbInfo object */
    public String getName() { return name; }

	/** @return the descriptor for this USB device model object */
	public Descriptor getDescriptor() { return descriptor; }

    //-------------------------------------------------------------------------
    // Abstract public methods
    //

    /**
     * Visitor.accept method
     * @param visitor the UsbInfoVisitor visiting this UsbInfo
     */
    public abstract void accept( UsbInfoVisitor visitor );

    //-------------------------------------------------------------------------
    // Package and protected methods
    //

    /**
     * Sets the name for this UsbInfo object
     * @param s the String name
     */
    void setName( String s ) { name = s; }

	/**
	 * Sets the descriptor for this UsbInfo object
	 * @param descriptor the new Descriptor
	 */
	void setDescriptor( Descriptor descriptor ) { this.descriptor = descriptor; }

    //-------------------------------------------------------------------------
    // Instance variables
    //

    private String name = "";
	private Descriptor descriptor = null;
}
