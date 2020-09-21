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

package com.amazonaws.services.iot.client.shadowme;

import com.amazonaws.services.iot.client.AWSIotConfig;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.core.AbstractAwsIotClient;
import com.amazonaws.services.iot.client.logging.Logger;

/**
 * The actual implementation of {@link AWSIotDevice}.
 */
public abstract class AbstractAwsIotDevice {

	protected long reportInterval = AWSIotConfig.DEVICE_REPORT_INTERVAL;
    
    public long getReportInterval() {
		return reportInterval;
	}
    
    public void setReportInterval(long reportInterval) {
		this.reportInterval = reportInterval;
	}

	protected boolean enableVersioning = AWSIotConfig.DEVICE_ENABLE_VERSIONING;
    
	public void setEnableVersioning(boolean enableVersioning) {
		this.enableVersioning = enableVersioning;
	}

	public boolean isEnableVersioning() {
		return enableVersioning;
	}

	protected AWSIotQos deviceReportQos = AWSIotQos.valueOf(AWSIotConfig.DEVICE_REPORT_QOS);
	protected AWSIotQos shadowUpdateQos = AWSIotQos.valueOf(AWSIotConfig.DEVICE_SHADOW_UPDATE_QOS);
    protected AWSIotQos methodQos = AWSIotQos.valueOf(AWSIotConfig.DEVICE_METHOD_QOS);
    protected AWSIotQos methodAckQos = AWSIotQos.valueOf(AWSIotConfig.DEVICE_METHOD_ACK_QOS);

	protected final String thingName;

    public String getThingName() {
		return thingName;
	}

	public AWSIotQos getDeviceReportQos() {
		return deviceReportQos;
	}

	public void setDeviceReportQos(AWSIotQos deviceReportQos) {
		this.deviceReportQos = deviceReportQos;
	}

	public AWSIotQos getShadowUpdateQos() {
		return shadowUpdateQos;
	}

	public void setShadowUpdateQos(AWSIotQos shadowUpdateQos) {
		this.shadowUpdateQos = shadowUpdateQos;
	}

	public AWSIotQos getMethodQos() {
		return methodQos;
	}

	public void setMethodQos(AWSIotQos methodQos) {
		this.methodQos = methodQos;
	}

	public AWSIotQos getMethodAckQos() {
		return methodAckQos;
	}

	public void setMethodAckQos(AWSIotQos methodAckQos) {
	}
	
	protected AbstractAwsIotDevice(String thingName) {
		this.thingName = thingName;
    }

    protected AbstractAwsIotDevice getDevice() {
        return this;
    }

    protected String get() throws AWSIotException {
    	return null;
    }

    protected String get(long timeout) throws AWSIotException, AWSIotTimeoutException {
    	return null;
    }

    protected void get(AWSIotMessage message, long timeout) throws AWSIotException {
    }

    protected void update(String jsonState) throws AWSIotException {
    }

    protected void update(String jsonState, long timeout) throws AWSIotException, AWSIotTimeoutException {
    }

    protected void update(AWSIotMessage message, long timeout) throws AWSIotException {
    }

    protected void delete() throws AWSIotException {
    }

    protected void delete(long timeout) throws AWSIotException, AWSIotTimeoutException {
    }

    protected void delete(AWSIotMessage message, long timeout) throws AWSIotException {
    }

    protected void onShadowUpdate(String jsonState) {
    }

    protected String onDeviceReport() {
    	return null;
    }

    public void activate() throws AWSIotException {
    }

    public void deactivate() throws AWSIotException {
    }

    public boolean isTopicReady(String topic) {
    	return false;
    }

    
    public void onSubscriptionAck(String topic, boolean success) {
    }

    public void onCommandAck(AWSIotMessage message) {
    }

    protected void startSync() {
    }

    protected void stopSync() {
    }

    protected void startVersionSync() {
    }
    

	public void setClient(AbstractAwsIotClient abstractAwsIotClient) {
		// TODO Auto-generated method stub
		
	}

}

