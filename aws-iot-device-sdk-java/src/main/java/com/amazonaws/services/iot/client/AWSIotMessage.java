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

import java.io.UnsupportedEncodingException;

import com.amazonaws.services.iot.client.core.AwsIotMessageCallback;
import com.amazonaws.services.iot.client.core.AwsIotRuntimeException;

/**
 * A common data structure that is used in a lot of non-blocking APIs in this
 * library.
 * <p>
 * It provides common data elements, such as {@link #topic}, {@link #qos}, and
 * {@link #payload}, used by the APIs.
 * </p>
 * <p>
 * It also contains callback functions that can be overridden to provide
 * customized handlers. The callback functions are invoked when a non-blocking
 * API call has completed successfully, unsuccessfully, or timed out.
 * Applications wish to have customized callback functions must extend this
 * class or its child classes, such as {@link AWSIotTopic}.
 */
public class AWSIotMessage implements AwsIotMessageCallback {

    /**
     * The topic the message is received from or published to.
     *
     * @param topic the new topic of the message
     * @return the current topic of the message
     */
    protected String topic;

    public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	/**
     * The MQTT QoS level for the message.
     *
     * @param qos the new QoS level
     * @return the current QoS level
     */
    protected AWSIotQos qos;

    public AWSIotQos getQos() {
		return qos;
	}

	/**
     * The payload of the message.
     */
    protected byte[] payload;

    /**
     * Error code for shadow methods. It's only applicable to messages returned
     * by those shadow method APIs.
     *
     * @param errorCode the new error code for the shadow method
     * @return the current error code of the shadow method
     */
    protected AWSIotDeviceErrorCode errorCode;

    public void setErrorCode(AWSIotDeviceErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public AWSIotDeviceErrorCode getErrorCode() {
		return errorCode;
	}

	/**
     * Error message for shadow methods. It's only applicable to messages
     * returned by those shadow method APIs.
     *
     * @param errorMessage the new error message for the shadow method
     * @return the current error message of the shadow method
     */
    protected String errorMessage;

    public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
     * Instantiates a new message object.
     *
     * @param topic
     *            the topic of the message
     * @param qos
     *            the QoS level of the message
     */
    public AWSIotMessage(String topic, AWSIotQos qos) {
        this.topic = topic;
        this.qos = qos;
    }

    /**
     * Instantiates a new message object.
     *
     * @param topic
     *            the topic of the message
     * @param qos
     *            the QoS level of the message
     * @param payload
     *            the payload of the message
     */
    public AWSIotMessage(String topic, AWSIotQos qos, byte[] payload) {
        this.topic = topic;
        this.qos = qos;
        setPayload(payload);
    }

    /**
     * Instantiates a new message object.
     *
     * @param topic
     *            the topic of the message
     * @param qos
     *            the QoS level of the message
     * @param payload
     *            the payload of the message
     */
    public AWSIotMessage(String topic, AWSIotQos qos, String payload) {
        this.topic = topic;
        this.qos = qos;
        setStringPayload(payload);
    }

    /**
     * Gets the byte array payload.
     *
     * @return the byte array payload
     */
    public byte[] getPayload() {
        if (payload == null) {
            return null;
        }
        
        byte[] tmp = new byte[payload.length];
        System.arraycopy(payload, 0, tmp, 0, tmp.length);

        return tmp;
    }

    /**
     * Sets the byte array payload.
     *
     * @param payload
     *            the new byte array payload
     */
    public void setPayload(byte[] payload) {
        if (payload == null) {
            this.payload = null;
            return;
        }

        byte[] tmp = new byte[payload.length];
        System.arraycopy(payload, 0, tmp, 0, tmp.length);

        this.payload = tmp;
    }

    /**
     * Gets the string payload.
     *
     * @return the string payload
     */
    public String getStringPayload() {
        if (payload == null) {
            return null;
        }

        String str;
        try {
            str = new String(payload, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AwsIotRuntimeException(e);
        }
        return str;
    }

    /**
     * Sets the string payload.
     *
     * @param payload
     *            the new string payload
     */
    public void setStringPayload(String payload) {
        if (payload == null) {
            this.payload = null;
            return;
        }

        try {
            this.payload = payload.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AwsIotRuntimeException(e);
        }
    }

    /**
     * Callback function to be invoked a non-block API has completed
     * successfully.
     */
    public void onSuccess() {
        // Default callback implementation is no-op
    }

    /**
     * Callback function to be invoked a non-block API has completed
     * unsuccessfully.
     */
    public void onFailure() {
        // Default callback implementation is no-op
    }

    /**
     * Callback function to be invoked a non-block API has timed out.
     */
    public void onTimeout() {
        // Default callback implementation is no-op
    }

}
