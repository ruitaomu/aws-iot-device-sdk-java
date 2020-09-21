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

package com.amazonaws.services.iot.client.core;


import java.util.Timer;
import java.util.TimerTask;

import org.joshvm.java.util.*;
import org.joshvm.java.util.concurrent.ConcurrentHashMap;
import org.joshvm.java.util.concurrent.ConcurrentMap;

import com.amazonaws.services.iot.client.AWSIotConfig;
import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.AWSIotTopic;
import com.amazonaws.services.iot.client.logging.Logger;
import com.amazonaws.services.iot.client.shadowme.AbstractAwsIotDevice;


/**
 * The actual implementation of {@code AWSIotMqttClient}.
 */
public abstract class AbstractAwsIotClient implements AwsIotConnectionCallback {

    private static final int DEFAULT_MQTT_PORT = 8883;

    private static final Logger LOGGER = Logger.getLogger(AbstractAwsIotClient.class.getName());

    protected final String clientId;
    public String getClientId() {
		return clientId;
	}

	protected final String clientEndpoint;
    protected final boolean clientEnableMetrics;
    public boolean isClientEnableMetrics() {
		return clientEnableMetrics;
	}

	protected final AwsIotConnectionType connectionType;

    protected int port = DEFAULT_MQTT_PORT;
    public String getClientEndpoint() {
		return clientEndpoint;
	}

	public int getPort() {
		return port;
	}

	protected int numOfClientThreads = AWSIotConfig.NUM_OF_CLIENT_THREADS;
    public int getNumOfClientThreads() {
		return numOfClientThreads;
	}

	public void setNumOfClientThreads(int numOfClientThreads) {
		this.numOfClientThreads = numOfClientThreads;
	}

	protected int connectionTimeout = AWSIotConfig.CONNECTION_TIMEOUT;
    public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	protected int serverAckTimeout = AWSIotConfig.SERVER_ACK_TIMEOUT;
    public int getServerAckTimeout() {
		return serverAckTimeout;
	}

	public void setServerAckTimeout(int serverAckTimeout) {
		this.serverAckTimeout = serverAckTimeout;
	}

	protected int keepAliveInterval = AWSIotConfig.KEEP_ALIVE_INTERVAL;
    public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	protected int maxConnectionRetries = AWSIotConfig.MAX_CONNECTION_RETRIES;
    public void setMaxConnectionRetries(int maxConnectionRetries) {
		this.maxConnectionRetries = maxConnectionRetries;
	}

	public int getMaxConnectionRetries() {
		return maxConnectionRetries;
	}

	protected int baseRetryDelay = AWSIotConfig.CONNECTION_BASE_RETRY_DELAY;
    public void setBaseRetryDelay(int baseRetryDelay) {
		this.baseRetryDelay = baseRetryDelay;
	}

	public int getBaseRetryDelay() {
		return baseRetryDelay;
	}

	protected int maxRetryDelay = AWSIotConfig.CONNECTION_MAX_RETRY_DELAY;
    public void setMaxRetryDelay(int maxRetryDelay) {
		this.maxRetryDelay = maxRetryDelay;
	}

	public int getMaxRetryDelay() {
		return maxRetryDelay;
	}

	protected int maxOfflineQueueSize = AWSIotConfig.MAX_OFFLINE_QUEUE_SIZE;
    public void setMaxOfflineQueueSize(int maxOfflineQueueSize) {
		this.maxOfflineQueueSize = maxOfflineQueueSize;
	}

	public int getMaxOfflineQueueSize() {
		return maxOfflineQueueSize;
	}

	protected boolean cleanSession = AWSIotConfig.CLEAN_SESSION;
    public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	protected AWSIotMessage willMessage;

    public void setWillMessage(AWSIotMessage willMessage) {
		this.willMessage = willMessage;
	}

	public AWSIotMessage getWillMessage() {
		return willMessage;
	}

	private final ConcurrentMap subscriptions = new ConcurrentHashMap();
    private final ConcurrentMap devices = new ConcurrentHashMap();
    private final AwsIotConnection connection;

    public AwsIotConnection getConnection() {
		return connection;
	}

	private ScheduledThreadPool executionService;

    AbstractAwsIotClient(String clientEndpoint, String clientId, AwsIotConnection connection,
                         boolean enableSdkMetrics) {
        this.clientEndpoint = clientEndpoint;
        this.clientId = clientId;
        this.connection = connection;
        this.connectionType = null;
        this.clientEnableMetrics = enableSdkMetrics;
    }

    AbstractAwsIotClient(String clientEndpoint, String clientId, AwsIotConnection connection) {
        // Enable Metrics by default
        this(clientEndpoint, clientId, connection, true);
    }

    protected AbstractAwsIotClient(String clientEndpoint, String clientId, int port, boolean enableSdkMetrics) {
        this.clientEndpoint = clientEndpoint;
        this.clientId = clientId;
        this.connectionType = AwsIotConnectionType.MQTT_OVER_TLS;
        this.port = port;
        this.clientEnableMetrics = enableSdkMetrics;

        try {
            this.connection = new AwsIotTlsConnection(this);
        } catch (AWSIotException e) {
            throw new AwsIotRuntimeException(e);
        }
    }

    protected AbstractAwsIotClient(String clientEndpoint, String clientId, int port) {
        this(clientEndpoint, clientId, port, true);
    }

    public void updateCredentials(String awsAccessKeyId, String awsSecretAccessKey, String sessionToken) {
        this.connection.updateCredentials(awsAccessKeyId, awsSecretAccessKey, sessionToken);
    }

    public void connect() throws AWSIotException {
        try {
            connect(0, true);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AwsIotRuntimeException(e);
        }
    }

    public void connect(long timeout) throws AWSIotException, AWSIotTimeoutException {
        connect(timeout, true);
    }

    public void connect(long timeout, boolean blocking) throws AWSIotException, AWSIotTimeoutException {
        synchronized (this) {
            if (executionService == null) {
                executionService = new ScheduledThreadPool(numOfClientThreads);
            }
        }

        AwsIotCompletion completion = new AwsIotCompletion(timeout, !blocking);
        connection.connect(completion);
        completion.get(this);
    }

    public void disconnect() throws AWSIotException {
        try {
            disconnect(0, true);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AwsIotRuntimeException(e);
        }
    }

    public void disconnect(long timeout) throws AWSIotException, AWSIotTimeoutException {
        disconnect(timeout, true);
    }

    public void disconnect(long timeout, boolean blocking) throws AWSIotException, AWSIotTimeoutException {
        AwsIotCompletion completion = new AwsIotCompletion(timeout, !blocking);
        connection.disconnect(completion);
        completion.get(this);
    }

    public void publish(String topic, String payload) throws AWSIotException {
        publish(topic, AWSIotQos.QOS0, payload);
    }

    public void publish(String topic, String payload, long timeout) throws AWSIotException, AWSIotTimeoutException {
        publish(topic, AWSIotQos.QOS0, payload, timeout);
    }

    public void publish(String topic, AWSIotQos qos, String payload) throws AWSIotException {
        try {
            publish(topic, qos, payload, 0);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AwsIotRuntimeException(e);
        }
    }

    public void publish(String topic, AWSIotQos qos, String payload, long timeout)
            throws AWSIotException, AWSIotTimeoutException {
        AwsIotCompletion completion = new AwsIotCompletion(topic, qos, payload, timeout);
        connection.publish(completion);
        completion.get(this);
    }

    public void publish(String topic, byte[] payload) throws AWSIotException {
        publish(topic, AWSIotQos.QOS0, payload);
    }

    public void publish(String topic, byte[] payload, long timeout) throws AWSIotException, AWSIotTimeoutException {
        publish(topic, AWSIotQos.QOS0, payload, timeout);
    }

    public void publish(String topic, AWSIotQos qos, byte[] payload) throws AWSIotException {
        try {
            publish(topic, qos, payload, 0);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AwsIotRuntimeException(e);
        }
    }

    public void publish(String topic, AWSIotQos qos, byte[] payload, long timeout)
            throws AWSIotException, AWSIotTimeoutException {
        AwsIotCompletion completion = new AwsIotCompletion(topic, qos, payload, timeout);
        connection.publish(completion);
        completion.get(this);
    }

    public void publish(AWSIotMessage message) throws AWSIotException {
        publish(message, 0);
    }

    public void publish(AWSIotMessage message, long timeout) throws AWSIotException {
        AwsIotCompletion completion = new AwsIotCompletion(message, timeout, true);
        connection.publish(completion);
        try {
            completion.get(this);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because it's asynchronous call
            throw new AwsIotRuntimeException(e);
        }
    }

    public void subscribe(AWSIotTopic topic, boolean blocking) throws AWSIotException {
        try {
            _subscribe(topic, 0, !blocking);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AwsIotRuntimeException(e);
        }
    }

    public void subscribe(AWSIotTopic topic, long timeout, boolean blocking)
            throws AWSIotException, AWSIotTimeoutException {
        _subscribe(topic, timeout, !blocking);
    }

    public void subscribe(AWSIotTopic topic) throws AWSIotException {
        subscribe(topic, 0);
    }

    public void subscribe(AWSIotTopic topic, long timeout) throws AWSIotException {
        try {
            _subscribe(topic, timeout, true);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because it's asynchronous call
            throw new AwsIotRuntimeException(e);
        }
    }

    private void _subscribe(AWSIotTopic topic, long timeout, boolean async)
            throws AWSIotException, AWSIotTimeoutException {
        AwsIotCompletion completion = new AwsIotCompletion(topic, timeout, async);
        connection.subscribe(completion);
        completion.get(this);

        subscriptions.put(topic.getTopic(), topic);
    }

    public void unsubscribe(String topic) throws AWSIotException {
        try {
            unsubscribe(topic, 0);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because timeout is 0
            throw new AwsIotRuntimeException(e);
        }
    }

    public void unsubscribe(String topic, long timeout) throws AWSIotException, AWSIotTimeoutException {
        if (subscriptions.remove(topic) == null) {
            return;
        }

        AwsIotCompletion completion = new AwsIotCompletion(topic, AWSIotQos.QOS0, timeout);
        connection.unsubscribe(completion);
        completion.get(this);
    }

    public void unsubscribe(AWSIotTopic topic) throws AWSIotException {
        unsubscribe(topic, 0);
    }

    public void unsubscribe(AWSIotTopic topic, long timeout) throws AWSIotException {
        if (subscriptions.remove(topic.getTopic()) == null) {
            return;
        }

        AwsIotCompletion completion = new AwsIotCompletion(topic, timeout, true);
        connection.unsubscribe(completion);
        try {
            completion.get(this);
        } catch (AWSIotTimeoutException e) {
            // We shouldn't get timeout exception because it's asynchronous call
            throw new AwsIotRuntimeException(e);
        }
    }

    public boolean topicFilterMatch(String topicFilter, String topic) {
        if (topicFilter == null || topic == null) {
            return false;
        }

        StringTokenizer filterTokens = new StringTokenizer(topicFilter, "/");
        StringTokenizer topicTokens = new StringTokenizer(topic, "/");
        int filterTokensLength = 0;
        int topicTokensLength = 0;
        while (filterTokens.hasMoreTokens()) {
        	filterTokens.nextToken();
        	filterTokensLength++;
        }
        
        while (topicTokens.hasMoreTokens()) {
        	topicTokens.nextToken();
        	topicTokensLength++;
        }
        
        if (filterTokensLength > topicTokensLength) {
            return false;
        }
        
        filterTokens = new StringTokenizer(topicFilter, "/");
        topicTokens = new StringTokenizer(topic, "/");
                
        int i = 0;
        while (filterTokens.hasMoreTokens()) {
        	String filterToken = filterTokens.nextToken();
        	String topicToken = topicTokens.nextToken();
            if (filterToken.equals("#")) {
                // '#' must be the last character
                return ((i + 1) == filterTokensLength);
            }

            if (!(filterToken.equals(topicToken) || filterToken.equals("+"))) {
                return false;
            }
            i++;
        }

        return (filterTokensLength == topicTokensLength);
    }

    public void dispatch(final AWSIotMessage message) {
        boolean matches = false;

        for (Iterator it = subscriptions.keySet().iterator(); it.hasNext();) {
        	String topicFilter = (String)it.next();
            if (topicFilterMatch(topicFilter, message.getTopic())) {
                final AWSIotTopic topic = (AWSIotTopic)subscriptions.get(topicFilter);
                scheduleTask(new TimerTask() {
                    
                    public void run() {
                        topic.onMessage(message);
                    }
                });
                matches = true;
            }
        }

        if (!matches) {
            LOGGER.warning("Unexpected message received from topic " + message.getTopic());
        }
    }

    public void attach(AWSIotDevice device) throws AWSIotException {
        if (devices.putIfAbsent(device.getThingName(), device) != null) {
            return;
        }

        device.setClient(this);

        // start the shadow sync task if the connection is already established
        if (getConnectionStatus().equals(AWSIotConnectionStatus.CONNECTED)) {
            device.activate();
        }
    }

    public void detach(AWSIotDevice device) throws AWSIotException {
        if (devices.remove(device.getThingName()) == null) {
            return;
        }

        device.deactivate();
    }

    public AWSIotConnectionStatus getConnectionStatus() {
        if (connection != null) {
            return connection.getConnectionStatus();
        } else {
            return AWSIotConnectionStatus.DISCONNECTED;
        }
    }

    
    public void onConnectionSuccess() {
        LOGGER.info("Client connection active: " + clientId);

        try {
            // resubscribe all the subscriptions
            for (Iterator it = subscriptions.values().iterator(); it.hasNext();) {
                subscribe((AWSIotTopic)it.next(), serverAckTimeout);
            }

            // start device sync
            for (Iterator it = devices.values().iterator(); it.hasNext();) {
                ((AbstractAwsIotDevice)it.next()).activate();
            }
        } catch (AWSIotException e) {
            // connection couldn't be fully recovered, disconnecting
            LOGGER.warning("Failed to complete subscriptions while client is active, will disconnect");
            try {
                connection.disconnect(null);
            } catch (AWSIotException de) {
                // ignore disconnect errors
            }
        }
    }

    
    public void onConnectionFailure() {
        LOGGER.info("Client connection lost: " + clientId);

        // stop device sync
        Iterator it = devices.values().iterator();
        while (it.hasNext()) {
        	AbstractAwsIotDevice device = (AbstractAwsIotDevice)it.next();
            try {
                device.deactivate();
            } catch (AWSIotException e) {
                // ignore errors from deactivate() as the connection is lost
                LOGGER.warning("Failed to deactive all the devices, ignoring the error");
            }
        }
    }

    
    public void onConnectionClosed() {
        LOGGER.info("Client connection closed: " + clientId);

        // stop device sync
        Iterator it = devices.values().iterator();
        while (it.hasNext()) {
        	AbstractAwsIotDevice device = (AbstractAwsIotDevice)it.next();
            try {
                device.deactivate();
            } catch (AWSIotException e) {
                // ignore errors from deactivate() as the connection is lost
                LOGGER.warning("Failed to deactive all the devices, ignoring the error");
            }
        }

        subscriptions.clear();
        devices.clear();

        executionService.shutdown();
    }

    public Timer scheduleTask(TimerTask runnable) {
        return scheduleTimeoutTask(runnable, 0);
    }

    public Timer scheduleTimeoutTask(TimerTask runnable, long timeout) {
        if (executionService == null) {
            throw new AwsIotRuntimeException("Client is not connected");
        }
        return executionService.schedule(runnable, timeout);
    }

    public Timer scheduleRoutineTask(TimerTask runnable, long initialDelay, long period) {
        if (executionService == null) {
            throw new AwsIotRuntimeException("Client is not connected");
        }
        return executionService.scheduleAtFixedRate(runnable, initialDelay, period);
    }

}
