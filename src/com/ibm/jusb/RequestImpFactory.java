package com.ibm.jusb;

/*
 * Copyright (c) 1999 - 2001, International Business Machines Corporation.
 * All Rights Reserved.
 *
 * This software is provided and licensed under the terms and conditions
 * of the Common Public License:
 * http://oss.software.ibm.com/developerworks/opensource/license-cpl.html
 */

import java.util.*;

import javax.usb.*;
import javax.usb.util.DefaultRequestBundle;

/**
 * RequestFactory implementation.
 * @author Dan Streetman
 * @author E. Michael Maximilien
 */
public class RequestImpFactory implements RequestFactory
{
	//**************************************************************************
	// Public methods

	/**
	 * Indicates to the RequestFactory object that the Request object can be recycled
	 * That is can be reused again.
	 * @param request the Request object to recycle
	 */
	public void recycle( Request request )
	{
		request.clean();
		freeRequestList.add( request );
	}

	/**
	 * Indicates to the RequestFactory object that the RequestBundle object can be recycled
	 * That is can be reused again.
	 * @param requestBundle the RequestBundle object to recycle
	 */
	public void recycle( RequestBundle requestBundle )
	{
		requestBundle.clean();
		freeBundleList.add( requestBundle );
	}

	/**
	 * @return a RequestBundle object that is used to aggregate and submit 
	 * vendor or class specific requests (standard request cannot be bundled)
	 */
	public RequestBundle createRequestBundle()
	{
		if( freeBundleList.isEmpty() == false )
			return (RequestBundle)freeBundleList.remove( 0 );

		return new MyRequestBundle( this );
	}

	/** @return a Vendor Request */
	public Request createVendorRequest( byte bmRequestType, byte requestType, short wValue, short wIndex, byte[] data ) throws RequestException
	{ return createRequest( bmRequestType, requestType, wValue, wIndex, data ); }
	
	/** @return a Class Request (this also includes hub class requests) */
	public Request createClassRequest( byte bmRequestType, byte requestType, short wValue, short wIndex, byte[] data ) throws RequestException
	{ return createRequest( bmRequestType, requestType, wValue, wIndex, data ); }

	/** @return A ClearFeatureRequest */
	public Request createClearFeatureRequest( byte bmRequestType, short wValue, short wIndex ) throws RequestException
	{ return createRequest( bmRequestType, RequestConst.REQUEST_CLEAR_FEATURE, wValue, wIndex, new byte[0] ); }

	/** @return A GetConfigurationRequest */
	public Request createGetConfigurationRequest( byte[] data ) throws RequestException
	{ return createRequest( REQUESTTYPE_GET_CONFIGURATION, RequestConst.REQUEST_GET_CONFIGURATION, (short)0x0000, (short)0x0000, data ); }

	/** @return A GetDescriptorRequest */
	public Request createGetDescriptorRequest( short wValue, short wIndex, byte[] data ) throws RequestException
	{ return createRequest( REQUESTTYPE_GET_DESCRIPTOR, RequestConst.REQUEST_GET_DESCRIPTOR, wValue, wIndex, data ); }

	/** @return A GetInterfaceRequest */
	public Request createGetInterfaceRequest( short wIndex, byte[] data ) throws RequestException
	{ return createRequest( REQUESTTYPE_GET_INTERFACE, RequestConst.REQUEST_GET_INTERFACE, (short)0x0000, wIndex, data ); }

	/** @return A GetStatusRequest */
	public Request createGetStatusRequest( byte bmRequestType, short wIndex, byte[] data ) throws RequestException
	{ return createRequest( bmRequestType, RequestConst.REQUEST_GET_STATUS, (short)0x0000, wIndex, data ); }

	/** @return A SetAddressRequest */
	public Request createSetAddressRequest( short wValue ) throws RequestException
	{ return createRequest( REQUESTTYPE_SET_ADDRESS, RequestConst.REQUEST_SET_ADDRESS, wValue, (short)0x0000, new byte[0] ); }

	/** @return A SetConfigurationRequest */
	public Request createSetConfigurationRequest( short wValue ) throws RequestException
	{ return createRequest( REQUESTTYPE_SET_CONFIGURATION, RequestConst.REQUEST_SET_CONFIGURATION, wValue, (short)0x0000, new byte[0] ); }

	/** @return A SetDescriptorRequest */
	public Request createSetDescriptorRequest( short wValue, short wIndex, byte[] data ) throws RequestException
	{ return createRequest( REQUESTTYPE_SET_DESCRIPTOR, RequestConst.REQUEST_SET_DESCRIPTOR, wValue, wIndex, data ); }

	/** @return A SetFeatureRequest */
	public Request createSetFeatureRequest( byte bmRequestType, short wValue, short wIndex ) throws RequestException
	{ return createRequest( bmRequestType, RequestConst.REQUEST_SET_FEATURE, wValue, wIndex, new byte[0] ); }

	/** @return A SetInterfaceRequest */
	public Request createSetInterfaceRequest( short wValue, short wIndex ) throws RequestException
	{ return createRequest( REQUESTTYPE_SET_INTERFACE, RequestConst.REQUEST_SET_INTERFACE, wValue, wIndex, new byte[0] ); }

	/** @return A SynchFrameRequest */
	public Request createSynchFrameRequest( short wIndex, byte[] data ) throws RequestException
	{ return createRequest( REQUESTTYPE_SYNCH_FRAME, RequestConst.REQUEST_SYNCH_FRAME, (short)0x0000, wIndex, data ); }

	/** @return A GetStateRequest */
	public Request createGetStateRequest( short wIndex, byte[] data ) throws RequestException
	{ return createRequest( REQUESTTYPE_GET_STATE, RequestConst.REQUEST_GET_STATE, (short)0x0000, wIndex, data ); }

	/** @return A Request */
	public Request createRequest( byte bmRequestType, byte requestType, short wValue, short wIndex, byte[] data ) throws RequestException
	{ return createRequestImp( bmRequestType, requestType, wValue, wIndex, data ); }

	/** @return A RequestImp */
	public RequestImp createRequestImp( Request request ) throws RequestException
	{
		RequestImp requestImp = createRequestImp( request.getRequestType(), request.getRequestCode(), request.getValue(), request.getIndex(), request.getData() );

		requestImp.setRequest( request );

		return requestImp;
	}

	/** @return A RequestImp */
	public RequestImp createRequestImp( byte bmRequestType, byte requestType, short wValue, short wIndex, byte[] data ) throws RequestException
	{
		RequestImp request = new RequestImp( this );

		request.setRequestType( bmRequestType );
		request.setRequestCode( requestType );
		request.setValue( wValue );
		request.setIndex( wIndex );
		request.setData( data );

		return request;
	}

	//**************************************************************************
	// Instance variables

	private List freeRequestList = new Vector();
	private List freeBundleList = new Vector();

	//**************************************************************************
	// Class constants

	private static final byte REQUESTTYPE_GET_CONFIGURATION = 
		RequestConst.REQUESTTYPE_DIRECTION_IN |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_DEVICE;
	private static final byte REQUESTTYPE_GET_DESCRIPTOR =
		RequestConst.REQUESTTYPE_DIRECTION_IN |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_DEVICE;
	private static final byte REQUESTTYPE_GET_INTERFACE =
		RequestConst.REQUESTTYPE_DIRECTION_IN |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_INTERFACE;
	private static final byte REQUESTTYPE_SET_ADDRESS =
		RequestConst.REQUESTTYPE_DIRECTION_OUT |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_DEVICE;
	private static final byte REQUESTTYPE_SET_CONFIGURATION =
		RequestConst.REQUESTTYPE_DIRECTION_OUT |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_DEVICE;
	private static final byte REQUESTTYPE_SET_DESCRIPTOR =
		RequestConst.REQUESTTYPE_DIRECTION_OUT |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_DEVICE;
	private static final byte REQUESTTYPE_SET_INTERFACE =
		RequestConst.REQUESTTYPE_DIRECTION_OUT |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_INTERFACE;
	private static final byte REQUESTTYPE_SYNCH_FRAME =
		RequestConst.REQUESTTYPE_DIRECTION_IN |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_ENDPOINT;
	private static final byte REQUESTTYPE_GET_STATE =
		RequestConst.REQUESTTYPE_DIRECTION_IN |
		RequestConst.REQUESTTYPE_TYPE_STANDARD |
		RequestConst.REQUESTTYPE_RECIPIENT_ENDPOINT;

	//**************************************************************************
	// Inner classes

	/**
	 * Simple inner class overridding DefaultRequestBundle to allow clients
	 * to set the in submission flag
	 * @author E. Michael Maximilien
	 * @version 1.0.0
	 */
	public static class MyRequestBundle extends DefaultRequestBundle
	{
		//---------------------------------------------------------------------
		// Ctor
		//

		/**
		 * Takes the RequestFactory that created it
		 * @param factory the RequestFactory that created this bundle
		 */
		public MyRequestBundle( RequestFactory factory )
		{									  
			requestFactory = factory;
		}

		//---------------------------------------------------------------------
		// Public methods
		//

		/** 
		 * Recycles this bundle so it may be returned when clients call ask the
		 * RequestFactory to create a new bundle.  Should be called once client is
		 * done using this bundle.
		 * @see javax.usb.RequestFactory
		 */
		public synchronized void recycle() { requestFactory.recycle( this ); }

		/**
		 * Marks this RequestBundle as beeing in submission
		 * @param b the boolean parameter
		 */
		public synchronized void setInSubmission( boolean b ) { this.inSubmission = b; }

		//---------------------------------------------------------------------
		// Instance variables
		//

		private RequestFactory requestFactory = null;
	}
}
