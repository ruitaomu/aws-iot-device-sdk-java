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

package com.amazonaws.services.iot.client.shadow;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.amazonaws.services.iot.client.AWSIotConfig;
import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotDeviceProperty;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.iot.client.core.AbstractAwsIotClient;
import com.amazonaws.services.iot.client.logging.Level;
import com.amazonaws.services.iot.client.logging.Logger;
import com.amazonaws.services.iot.client.shadow.AwsIotDeviceCommandManager.Command;
import com.amazonaws.services.iot.client.shadow.AwsIotDeviceCommandManager.CommandAck;
import com.joshvm.java.util.*;

/**
 * The actual implementation of {@link AWSIotDevice}.
 */
public abstract class AbstractAwsIotDevice {

    private static final Logger LOGGER = Logger.getLogger(AbstractAwsIotDevice.class.getName());

    protected final String thingName;

    public String getThingName() {
		return thingName;
	}

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
		this.methodAckQos = methodAckQos;
	}

    private final Map reportedProperties;
    private final Map updatableProperties;
    private final AwsIotDeviceCommandManager commandManager;
    private final ConcurrentMap deviceSubscriptions;
    private final ObjectMapper jsonObjectMapper;

    private AbstractAwsIotClient client;
    public void setClient(AbstractAwsIotClient client) {
		this.client = client;
	}

	public AbstractAwsIotClient getClient() {
		return client;
	}

	private Timer syncTask;
    private AtomicLong localVersion;

    public AtomicLong getLocalVersion() {
		return localVersion;
	}

	protected AbstractAwsIotDevice(String thingName) {
        this.thingName = thingName;

        reportedProperties = getDeviceProperties(true, false);
        updatableProperties = getDeviceProperties(false, true);
        commandManager = new AwsIotDeviceCommandManager(this);

        deviceSubscriptions = new ConcurrentHashMap();
        Iterator it;
        for (it = ((List)getDeviceTopics()).iterator(); it.hasNext(); ) {
        	String topic = (String)it.next();
            deviceSubscriptions.put(topic, Boolean.FALSE);
        }

        jsonObjectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(AbstractAwsIotDevice.class, new AwsIotJsonSerializer());
        jsonObjectMapper.registerModule(module);

        localVersion = new AtomicLong(-1);
    }

    protected AbstractAwsIotDevice getDevice() {
        return this;
    }

    protected String get() throws AWSIotException {
        AWSIotMessage message = new AWSIotMessage(null, methodQos);
        return commandManager.runCommandSync(Command.GET, message);
    }

    protected String get(long timeout) throws AWSIotException, AWSIotTimeoutException {
        AWSIotMessage message = new AWSIotMessage(null, methodQos);
        return commandManager.runCommandSync(Command.GET, message, timeout);
    }

    protected void get(AWSIotMessage message, long timeout) throws AWSIotException {
        commandManager.runCommand(Command.GET, message, timeout);
    }

    protected void update(String jsonState) throws AWSIotException {
        AWSIotMessage message = new AWSIotMessage(null, methodQos, jsonState);
        commandManager.runCommandSync(Command.UPDATE, message);
    }

    protected void update(String jsonState, long timeout) throws AWSIotException, AWSIotTimeoutException {
        AWSIotMessage message = new AWSIotMessage(null, methodQos, jsonState);
        commandManager.runCommandSync(Command.UPDATE, message, timeout);
    }

    protected void update(AWSIotMessage message, long timeout) throws AWSIotException {
        commandManager.runCommand(Command.UPDATE, message, timeout);
    }

    protected void delete() throws AWSIotException {
        AWSIotMessage message = new AWSIotMessage(null, methodQos);
        commandManager.runCommandSync(Command.DELETE, message);
    }

    protected void delete(long timeout) throws AWSIotException, AWSIotTimeoutException {
        AWSIotMessage message = new AWSIotMessage(null, methodQos);
        commandManager.runCommandSync(Command.DELETE, message, timeout);
    }

    protected void delete(AWSIotMessage message, long timeout) throws AWSIotException {
        commandManager.runCommand(Command.DELETE, message, timeout);
    }

    protected void onShadowUpdate(String jsonState) {
        // synchronized block to serialize device accesses
        synchronized (this) {
            try {
                AwsIotJsonDeserializer.deserialize(this, jsonState);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to update device", e);
            }
        }
    }

    protected String onDeviceReport() {
        // synchronized block to serialize device accesses
        synchronized (this) {
            try {
                return jsonObjectMapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.WARNING, "Failed to generate device report", e);
                return null;
            }
        }
    }

    public void activate() throws AWSIotException {
        stopSync();

        for (Iterator it = getDeviceTopics().iterator(); it.hasNext();) {
        	String topic = (String)it.next();
            AWSIotTopic awsIotTopic;

            if (commandManager.isDeltaTopic(topic)) {
                awsIotTopic = new AwsIotDeviceDeltaListener(topic, shadowUpdateQos, this);
            } else {
                awsIotTopic = new AwsIotDeviceCommandAckListener(topic, methodAckQos, this);
            }

            client.subscribe(awsIotTopic, client.getServerAckTimeout());
        }

        startSync();
    }

    public void deactivate() throws AWSIotException {
        stopSync();

        commandManager.onDeactivate();

        for (Iterator it = getDeviceTopics().iterator(); it.hasNext();) {
        	String topic = (String)it.next();
            deviceSubscriptions.put(topic, Boolean.FALSE);

            AWSIotTopic awsIotTopic = new AWSIotTopic(topic);
            client.unsubscribe(awsIotTopic, client.getServerAckTimeout());
        }
    }

    public boolean isTopicReady(String topic) {
        Boolean status = (Boolean)deviceSubscriptions.get(topic);

        return Boolean.TRUE.equals(status);
    }

    public boolean isCommandReady(Command command) {
        Boolean accepted = (Boolean)deviceSubscriptions.get(commandManager.getTopic(command, CommandAck.ACCEPTED));
        Boolean rejected = (Boolean)deviceSubscriptions.get(commandManager.getTopic(command, CommandAck.REJECTED));

        return (Boolean.TRUE.equals(accepted) && Boolean.TRUE.equals(rejected));
    }

    public void onSubscriptionAck(String topic, boolean success) {
        deviceSubscriptions.put(topic, success?Boolean.TRUE:Boolean.FALSE);
        commandManager.onSubscriptionAck(topic, success);
    }

    public void onCommandAck(AWSIotMessage message) {
        commandManager.onCommandAck(message);
    }

    protected void startSync() {
        // don't start the publish task if no properties are to be published
        if (reportedProperties.isEmpty() || reportInterval <= 0) {
            return;
        }

        syncTask = client.scheduleRoutineTask(new TimerTask() {
            public void run() {
                if (!isCommandReady(Command.UPDATE)) {
                    LOGGER.fine("Device not ready for reporting");
                    return;
                }
                
                long reportVersion = localVersion.get();
                if (enableVersioning && reportVersion < 0) {
                    // if versioning is enabled, synchronize the version first
                    LOGGER.fine("Starting version sync");
                    startVersionSync();
                    return;
                }

                String jsonState = onDeviceReport();
                if (jsonState != null) {
                    LOGGER.fine("Sending device report");
                    sendDeviceReport(reportVersion, jsonState);
                }
            }
        }, 0l, reportInterval);
    }

    protected void stopSync() {
        if (syncTask != null) {
            syncTask.cancel();
            syncTask = null;
        }

        localVersion.set(-1);
    }

    protected void startVersionSync() {
        localVersion.set(-1);

        AwsIotDeviceSyncMessage message = new AwsIotDeviceSyncMessage(null, shadowUpdateQos, this);
        try {
            commandManager.runCommand(Command.GET, message, client.getServerAckTimeout(), true);
        } catch (AWSIotTimeoutException e) {
            // async command, shouldn't receive timeout exception
        } catch (AWSIotException e) {
            LOGGER.log(Level.WARNING, "Failed to publish version update message", e);
        }
    }

    private void sendDeviceReport(long reportVersion, String jsonState) {
        StringBuilder payload = new StringBuilder("{");

        if (enableVersioning) {
            payload.append("\"version\":").append(reportVersion).append(",");
        }
        payload.append("\"state\":{\"reported\":").append(jsonState).append("}}");

        AwsIotDeviceReportMessage message = new AwsIotDeviceReportMessage(null, shadowUpdateQos, reportVersion,
                payload.toString(), this);
        if (enableVersioning && reportVersion != localVersion.get()) {
            LOGGER.warning("Local version number has changed, skip reporting for this round");
            return;
        }

        try {
            commandManager.runCommand(Command.UPDATE, message, client.getServerAckTimeout(), true);
        } catch (AWSIotTimeoutException e) {
            // async command, shouldn't receive timeout exception
        } catch (AWSIotException e) {
            LOGGER.log(Level.WARNING, "Failed to publish device report message", e);
        }
    }

    private Map getDeviceProperties(boolean enableReport, boolean allowUpdate) {
        Map properties = new HashMap();

        for (Field field : this.getClass().getDeclaredFields()) {
            AWSIotDeviceProperty annotation = field.getAnnotation(AWSIotDeviceProperty.class);
            if (annotation == null) {
                continue;
            }

            String propertyName = annotation.name().length() > 0 ? annotation.name() : field.getName();
            if ((enableReport && annotation.enableReport()) || (allowUpdate && annotation.allowUpdate())) {
                properties.put(propertyName, field);
            }
        }

        return properties;
    }

    private List getDeviceTopics() {
        List topics = (List) new ArrayList();

        topics.add(commandManager.getTopic(Command.DELTA, null));

        topics.add(commandManager.getTopic(Command.GET, CommandAck.ACCEPTED));
        topics.add(commandManager.getTopic(Command.GET, CommandAck.REJECTED));
        topics.add(commandManager.getTopic(Command.UPDATE, CommandAck.ACCEPTED));
        topics.add(commandManager.getTopic(Command.UPDATE, CommandAck.REJECTED));
        topics.add(commandManager.getTopic(Command.DELETE, CommandAck.ACCEPTED));
        topics.add(commandManager.getTopic(Command.DELETE, CommandAck.REJECTED));

        return topics;
    }

}

