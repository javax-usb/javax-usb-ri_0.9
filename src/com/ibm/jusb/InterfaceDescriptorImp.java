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
import javax.usb.util.UsbUtil;

/**
 * InterfaceDescriptor implementation.
 * @author E. Michael Maximilien
 * @author Dan Streetman
 */
public class InterfaceDescriptorImp extends AbstractDescriptor implements InterfaceDescriptor
{
	/**
	 * Constructor.
	 * @param len Descriptor length.
	 * @param type Descriptor type.
	 * @param iNum Interface number.
	 * @param alt Alternate setting number.
	 * @param nEps Number of endpoints.
	 * @param iClass Interface Class.
	 * @param iSubClass Interface SubClass.
	 * @param iProto Interface protocol.
	 * @param iInd Interface index.
	 */
	public InterfaceDescriptorImp( byte len, byte type, byte iNum, byte alt, byte nEps, byte iClass, byte iSubClass, byte iProto, byte iInd )
	{
		setLength(len);
		setType(type);
		setInterfaceNumber(iNum);
		setAlternateSetting(alt);
		setNumEndpoints(nEps);
		setInterfaceClass(iClass);
		setInterfaceSubClass(iSubClass);
		setInterfaceProtocol(iProto);
		setInterfaceIndex(iInd);
	}

    /** @return the interface number */
    public byte getInterfaceNumber() { return interfaceNumber; }

    /** @return the alternate setting for this interface */
    public byte getAlternateSetting() { return alternateSetting; }

    /** @return the number of endpoints used by this interface (excludes endpoint 0) */
    public byte getNumEndpoints() { return numEndpoints; }

    /** @return the interface class code */
    public byte getInterfaceClass() { return interfaceClass; }

    /** @return the interface subclass code */
    public byte getInterfaceSubClass() { return interfaceSubClass; }

    /** @return the interface protocol code */
    public byte getInterfaceProtocol() { return interfaceProtocol; }

    /** @return the interface StringDescriptor index code */
    public byte getInterfaceIndex() { return interfaceIndex; }

	/** @return this descriptor as a byte[] */
	public byte[] toBytes()
	{
		int length = UsbUtil.unsignedInt( getLength() );

		if (length < DescriptorConst.DESCRIPTOR_MIN_LENGTH_INTERFACE)
			length = DescriptorConst.DESCRIPTOR_MIN_LENGTH_INTERFACE;

		byte[] b = new byte[length];

		b[0] = getLength();
		b[1] = getType();
		b[2] = getInterfaceNumber();
		b[3] = getAlternateSetting();
		b[4] = getNumEndpoints();
		b[5] = getInterfaceClass();
		b[6] = getInterfaceSubClass();
		b[7] = getInterfaceProtocol();
		b[8] = getInterfaceIndex();

		return b;
	}

    /**
     * Accepts a DescriptorVisitor objects
     * @param visitor the DescriptorVisitor object
     */
    public void accept( DescriptorVisitor visitor ) { visitor.visitInterfaceDescriptor( this ); }

    /**
     * Sets this descriptor's interfaceNumber value
     * @param b the byte argument
     * @exception java.lang.IllegalArgumentException for a bad argument
     */
    public void setInterfaceNumber( byte b )
    {
        //May need to do some pre-condition checks here

        interfaceNumber = b;
    }

    /**
     * Sets this descriptor's alternateSetting value
     * @param b the byte argument
     * @exception java.lang.IllegalArgumentException for a bad argument
     */
    public void setAlternateSetting( byte b )
    {
        //May need to do some pre-condition checks here

        alternateSetting = b;
    }

    /**
     * Sets this descriptor's numEndpoints value
     * @param b the byte argument
     * @exception java.lang.IllegalArgumentException for a bad argument
     */
    public void setNumEndpoints( byte b )
    {
        //May need to do some pre-condition checks here

        numEndpoints = b;
    }

    /**
     * Sets this descriptor's interfaceClass value
     * @param b the byte argument
     * @exception java.lang.IllegalArgumentException for a bad argument
     */
    public void setInterfaceClass( byte b )
    {
        //May need to do some pre-condition checks here

        interfaceClass = b;
    }

    /**
     * Sets this descriptor's interfaceSubClass value
     * @param b the byte argument
     * @exception java.lang.IllegalArgumentException for a bad argument
     */
    public void setInterfaceSubClass( byte b )
    {
        //May need to do some pre-condition checks here

        interfaceSubClass = b;
    }

    /**
     * Sets this descriptor's interfaceProtocol value
     * @param b the byte argument
     * @exception java.lang.IllegalArgumentException for a bad argument
     */
    public void setInterfaceProtocol( byte b )
    {
        //May need to do some pre-condition checks here

        interfaceProtocol = b;
    }

    /**
     * Sets this descriptor's interfaceIndex value
     * @param b the byte argument
     * @exception java.lang.IllegalArgumentException for a bad argument
     */
    public void setInterfaceIndex( byte b )
    {
        //May need to do some pre-condition checks here

        interfaceIndex = b;
    }

    //-------------------------------------------------------------------------
    // Instance variables
    //

    private byte interfaceNumber = 0x00;
    private byte alternateSetting = 0x00;
    private byte numEndpoints = 0x00;
    private byte interfaceClass = 0x00;
    private byte interfaceSubClass = 0x00;
    private byte interfaceProtocol = 0x00;
    private byte interfaceIndex = 0x00;
}
