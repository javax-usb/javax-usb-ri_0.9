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

/*
 * DeviceDescriptor implementation.
 * @author E. Michael Maximilien
 * @author Dan Streetman
 */
public class DeviceDescriptorImp extends AbstractDescriptor implements DeviceDescriptor
{
	/**
	 * Constructor.
	 * @param len Descriptor length.
	 * @param type Descriptor type.
	 * @param dClass Device class.
	 * @param dSubClass Device SubClass.
	 * @param dProto Device Protocol.
	 * @param mSize Max packet size.
	 * @param mInd Manufacturer index.
	 * @param pInd Product index.
	 * @param snInd Serialnumber index.
	 * @param nConfigs Number of configs.
	 * @param vID Vendor ID.
	 * @param pID Product ID.
	 * @param bcdDev Device Binary Coded Decimal number.
	 * @param bcdUsb USB Binary Coded Decimal number.
	 */
	public DeviceDescriptorImp( byte len, byte type, byte dClass, byte dSubClass,
								byte dProto, byte mSize, byte mInd, byte pInd, byte snInd,
								byte nConfigs, short vID, short pID, short bcdDev, short bcdUsb )
	{
		setLength(len);
		setType(type);
		setDeviceClass(dClass);
		setDeviceSubClass(dSubClass);
		setDeviceProtocol(dProto);
		setMaxPacketSize(mSize);
		setManufacturerIndex(mInd);
		setProductIndex(pInd);
		setSerialNumberIndex(snInd);
		setNumConfigs(nConfigs);
		setVendorId(vID);
		setProductId(pID);
		setBcdDevice(bcdDev);
		setBcdUsb(bcdUsb);
	}

    /** @return a binary coded decimal for the level of USB supported by this spec */
    public short getBcdUsb() { return bcdUsb; }

    /** @return the USB device class for this descriptor */
    public byte getDeviceClass() { return deviceClass; }

    /** @return the USB device subclass for this descriptor */
    public byte getDeviceSubClass() { return deviceSubClass; }

    /** @return the device protocol for this descriptor */
    public byte getDeviceProtocol() { return deviceProtocol; }

    /** @return the maximum packet size for this descriptor */
    public byte getMaxPacketSize() { return maxPacketSize; }

    /** @return the vendor ID for this descriptor */
    public short getVendorId() { return vendorId; }

    /** @return the product ID for this descriptor */
    public short getProductId() { return productId; }

    /** @return a binary coded decimal of the device release number */
    public short getBcdDevice() { return bcdDevice; }

    /** @return the index of StringDescriptor describing the manufacturer */
    public byte getManufacturerIndex() { return manufacturerIndex; }

    /** @return the index of StringDescriptor describing the product */
    public byte getProductIndex() { return productIndex; }

    /** @return the index of StringDescriptor describing the serial number */
    public byte getSerialNumberIndex() { return serialNumberIndex; }

    /** @return the number of configuration that the device that will get this descriptor supports */
    public byte getNumConfigs() { return numConfigs; }

	/** @return this descriptor as a byte[] */
	public byte[] toBytes()
	{
		int length = UsbUtil.unsignedInt( getLength() );

		if (length < DescriptorConst.DESCRIPTOR_MIN_LENGTH_DEVICE)
			length = DescriptorConst.DESCRIPTOR_MIN_LENGTH_DEVICE;

		byte[] b = new byte[length];

		b[0] = getLength();
		b[1] = getType();
		b[2] = (byte)getBcdUsb();
		b[3] = (byte)(getBcdUsb()>>8);
		b[4] = getDeviceClass();
		b[5] = getDeviceSubClass();
		b[6] = getDeviceProtocol();
		b[7] = getMaxPacketSize();
		b[8] = (byte)getVendorId();
		b[9] = (byte)(getVendorId()>>8);
		b[10] = (byte)getProductId();
		b[11] = (byte)(getProductId()>>8);
		b[12] = (byte)getBcdDevice();
		b[13] = (byte)(getBcdDevice()>>8);
		b[14] = getManufacturerIndex();
		b[15] = getProductIndex();
		b[16] = getSerialNumberIndex();
		b[17] = getNumConfigs();

		return b;
	}

    /**
     * Accepts a DescriptorVisitor objects
     * @param visitor the DescriptorVisitor object
     */
    public void accept( DescriptorVisitor visitor ) { visitor.visitDeviceDescriptor( this ); }

    /**
     * Sets the BcdUsb value
     * @param s the short value
     */
    public void setBcdUsb( short s )
    {
        //May need to do some pre-condition checks

        bcdUsb = s;
    }

    /**
     * Sets the device class for this descriptor
     * @param b the byte device class code
     */
    public void setDeviceClass( byte b )
    {
        //May need to do some pre-condition checks

        deviceClass = b;
    }

    /**
     * Sets the device sub-class for this descriptor
     * @param b the byte code for the device sub-class
     */
    public void setDeviceSubClass( byte b )
    {
        //May need to do some pre-condition checks

        deviceSubClass = b;
    }

    /** 
     * Sets the protocol for this descriptor
     * @param b the byte code for this descriptor protocol
     */
    public void setDeviceProtocol( byte  b )
    {
        //May need to do some pre-condition checks

        deviceProtocol = b;
    }

    /**
     * Sets the max packet size for this descriptor
     * @param b the byte code for the max packet size
     */
    public void setMaxPacketSize( byte b )
    {
        //May need to do some pre-condition checks

        maxPacketSize = b;
    }

    /**
     * Sets the vendorId for the device that will accept this descriptor
     * @param w the word vendor ID code
     */
    public void setVendorId( short w )
    {
        //May need to do some pre-condition checks

        vendorId = w;
    }

    /**
     * Sets the product Id of the device that will accept this descritor
     * @param w the product ID word
     */
    public void setProductId( short w )
    {
        //May need to do some pre-condition checks

        productId = w;
    }

    /**
     * Sets the bcdDevice for this descritor
     * @param s the short value
     * NOTE: may need to make this a byte[] instead (TBD)
     */
    public void setBcdDevice( short s )
    {
        //May need to do some pre-condition checks

        bcdDevice = s;
    }

    /**
     * Sets the manufacturerIndex for this descriptor
     * @param b the manufacturerIndex code
     */
    public void setManufacturerIndex( byte b )
    {
        //May need to do some pre-condition checks

        manufacturerIndex = b;
    }

    /**
     * Sets the productIndex for this descritptor
     * @param b the productIndex value
     */
    public void setProductIndex( byte b )
    {
        //May need to do some pre-condition checks

        productIndex = b;
    }

    /**
     * Set the serialNumberIndex for this descriptor
     * @param b the serialNumberIndex value
     */
    public void setSerialNumberIndex( byte b )
    {
        //May need to do some pre-condition checks
        
        serialNumberIndex = b;
    }

    /**
     * Sets the number of config for the device that will accept this descriptor
     * @param b the number of config
     */
    public void setNumConfigs( byte b )
    {
        //May need to do some pre-condition checks

        numConfigs = b;
    }

	/** Compare this to another Object */
	public boolean equals(Object object)
	{
		DeviceDescriptorImp desc = null;

		try { desc = (DeviceDescriptorImp)object; }
		catch ( ClassCastException ccE ) { return false; }

		return
			getBcdUsb() == desc.getBcdUsb() &&
			getDeviceClass() == desc.getDeviceClass() &&
			getDeviceSubClass() == desc.getDeviceSubClass() &&
			getDeviceProtocol() == desc.getDeviceProtocol() &&
			getMaxPacketSize() == desc.getMaxPacketSize() &&
			getVendorId() == desc.getVendorId() &&
			getProductId() == desc.getProductId() &&
			getBcdDevice() == desc.getBcdDevice() &&
			getManufacturerIndex() == desc.getManufacturerIndex() &&
			getProductIndex() == desc.getProductIndex() &&
			getSerialNumberIndex() == desc.getSerialNumberIndex() &&
			getNumConfigs() == desc.getNumConfigs();
	}

    //-------------------------------------------------------------------------
    // Instance variables
    //

    private short bcdUsb = 0x0000;
    private byte deviceClass = 0x00;
    private byte deviceSubClass = 0x00;
    private byte deviceProtocol = 0x00;
    private byte maxPacketSize = 0x00;
    private short vendorId = 0x0000;
    private short productId = 0x0000;
    private short bcdDevice = 0x0000;
    private byte manufacturerIndex = 0x00;
    private byte productIndex = 0x00;
    private byte serialNumberIndex = 0x00;
    private byte numConfigs = 0x00;
}
