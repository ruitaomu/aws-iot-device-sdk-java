/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.services.iot.client;

/**
 * These error codes are used by the server in acknowledgement message for the
 * shadow methods, namely Get, Update, and Delete.
 */
public class AWSIotDeviceErrorCode {

    /** The bad request. */
	public final static AWSIotDeviceErrorCode BAD_REQUEST = new AWSIotDeviceErrorCode(400);
    /** The Unauthorized. */
	public final static AWSIotDeviceErrorCode UNAUTHORIZED = new AWSIotDeviceErrorCode(401);
    /** The Forbidden. */
	public final static AWSIotDeviceErrorCode FORBIDDEN = new AWSIotDeviceErrorCode(403);
    /** The Not found. */
	public final static AWSIotDeviceErrorCode NOT_FOUND = new AWSIotDeviceErrorCode(404);
    /** The Conflict. */
	public final static AWSIotDeviceErrorCode CONFLICT = new AWSIotDeviceErrorCode(409);
    /** The Payload too large. */
	public final static AWSIotDeviceErrorCode PAYLOAD_TOO_LARGE = new AWSIotDeviceErrorCode(413);
    /** The Unsupported media type. */
	public final static AWSIotDeviceErrorCode UNSUPPORTED_MEDIA_TYPE = new AWSIotDeviceErrorCode(415);
    /** The Too many requests. */
	public final static AWSIotDeviceErrorCode TOO_MANY_REQUESTS = new AWSIotDeviceErrorCode(429);
    /** The Internal service failure. */
	public final static AWSIotDeviceErrorCode INTERNAL_SERVICE_FAILURE = new AWSIotDeviceErrorCode(429);

    /** The error code. */
    private final long errorCode;

    /**
     * Instantiates a new device error code object.
     *
     * @param errorCode
     *            the error code
     */
    private AWSIotDeviceErrorCode(final long errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code value.
     *
     * @return the error code value
     */
    public long getValue() {
        return this.errorCode;
    }

    /**
     * Returns the Enum representation of the error code value
     *
     * @param code
     *            the error code value
     * @return the Enum representation of the error code, or null if the error
     *         code is unknown
     */
    public static AWSIotDeviceErrorCode valueOf(long code) {
    	switch ((int)code) {
		 /** The bad request. */
		case 400: return BAD_REQUEST;
	    /** The Unauthorized. */
		case 401: return UNAUTHORIZED;
	    /** The Forbidden. */
		case 403: return FORBIDDEN;
	    /** The Not found. */
		case 404: return NOT_FOUND;
	    /** The Conflict. */
		case 409: return CONFLICT;
	    /** The Payload too large. */
		case 413: return PAYLOAD_TOO_LARGE;
	    /** The Unsupported media type. */
		case 415: return UNSUPPORTED_MEDIA_TYPE;
	    /** The Too many requests. */
		/** The Internal service failure. */
		case 429: return INTERNAL_SERVICE_FAILURE;
		}

        return null;
    }

}
