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

package com.amazonaws.services.iot.client.sample.pubSub;

import com.amazonaws.services.iot.client.AWSIotMqttClient;

import org.joshvm.crypto.keystore.KeyStore;
import org.joshvm.crypto.keystore.PrivateKeyStore;
import org.joshvm.crypto.keystore.UserKeyStoreParam;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.AWSIotTopic;

/**
 * This is an example that uses {@link AWSIotMqttClient} to subscribe to a topic and
 * publish messages to it. Both blocking and non-blocking publishing are
 * demonstrated in this example.
 */
public class PublishSubscribeSample {

    private static final String TestTopic = "sdk/test/java";
    private static final AWSIotQos TestTopicQos = AWSIotQos.QOS0;

    private static AWSIotMqttClient awsIotClient;

    public static void setClient(AWSIotMqttClient client) {
        awsIotClient = client;
    }

    public static class BlockingPublisher implements Runnable {
        private final AWSIotMqttClient awsIotClient;

        public BlockingPublisher(AWSIotMqttClient awsIotClient) {
            this.awsIotClient = awsIotClient;
        }

        
        public void run() {
            long counter = 1;

            while (true) {
                String payload = "hello from blocking publisher - " + (counter++);
                try {
                    awsIotClient.publish(TestTopic, payload);
                } catch (AWSIotException e) {
                    System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
                }
                System.out.println(System.currentTimeMillis() + ": >>> " + payload);

                try {
                	System.out.println("Free memory:"+Runtime.getRuntime().freeMemory());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(System.currentTimeMillis() + ": BlockingPublisher was interrupted");
                    return;
                }
            }
        }
    }

    public static class NonBlockingPublisher implements Runnable {
        private final AWSIotMqttClient awsIotClient;

        public NonBlockingPublisher(AWSIotMqttClient awsIotClient) {
            this.awsIotClient = awsIotClient;
        }

        
        public void run() {
            long counter = 1;

            while (true) {
                String payload = "hello from non-blocking publisher - " + (counter++);
                AWSIotMessage message = new NonBlockingPublishListener(TestTopic, TestTopicQos, payload);
                try {
                    awsIotClient.publish(message);
                } catch (AWSIotException e) {
                    System.out.println(System.currentTimeMillis() + ": publish failed for " + payload);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(System.currentTimeMillis() + ": NonBlockingPublisher was interrupted");
                    return;
                }
            }
        }
    }

//    private static void initClient(CommandArguments arguments) {
private static void initClient(String clientEndpoint) {
	String clientId = "basicPubSub";
//        String clientEndpoint = arguments.getNotNull("clientEndpoint", SampleUtil.getConfig("clientEndpoint"));
//        String clientId = arguments.getNotNull("clientId", SampleUtil.getConfig("clientId"));
//
//        String certificateFile = arguments.get("certificateFile", SampleUtil.getConfig("certificateFile"));
//        String privateKeyFile = arguments.get("privateKeyFile", SampleUtil.getConfig("privateKeyFile"));
//        if (awsIotClient == null && certificateFile != null && privateKeyFile != null) {
//            String algorithm = arguments.get("keyAlgorithm", SampleUtil.getConfig("keyAlgorithm"));

//            KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);
    	if (awsIotClient == null) {
            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, 8883);
        }

//        if (awsIotClient == null) {
//            String awsAccessKeyId = arguments.get("awsAccessKeyId", SampleUtil.getConfig("awsAccessKeyId"));
//            String awsSecretAccessKey = arguments.get("awsSecretAccessKey", SampleUtil.getConfig("awsSecretAccessKey"));
//            String sessionToken = arguments.get("sessionToken", SampleUtil.getConfig("sessionToken"));
//
//            if (awsAccessKeyId != null && awsSecretAccessKey != null) {
//                awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey,
//                        sessionToken);
//            }
//        }

        if (awsIotClient == null) {
            throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
        }
    }
    
    static final String base64_cert = "";
    static final String base64_privatekey = "";

    /**
     *cldc_vm com.amazonaws.services.iot.client.sample.pubSub.PublishSubscribeSample clientEndpoint
    */
    public static void main(String args[]) throws InterruptedException, AWSIotException, AWSIotTimeoutException {
//        CommandArguments arguments = CommandArguments.parse(args);
//        initClient(arguments);
        
    	System.out.println("Free memory at startup:"+Runtime.getRuntime().freeMemory());
    	KeyStore.initWebPublicKeystoreLocation(PublishSubscribeSample.class, "_main.ks");
    	PrivateKeyStore ks = KeyStore.selectPrivateKeyStore(null);
    	ks.load(((UserKeyStoreParam)UserKeyStoreParam.Build())
    			.addCertificate(base64_cert)
    			.setPrivateKey(base64_privatekey));
    	System.out.println("init");
    	initClient(args[0]);
    	System.out.println("conn");
        awsIotClient.connect();
        System.out.println("sub");

        AWSIotTopic topic = new TestTopicListener("TestTopic", TestTopicQos);
        awsIotClient.subscribe(topic, true);

        Thread blockingPublishThread = new Thread(new BlockingPublisher(awsIotClient));
        //Thread nonBlockingPublishThread = new Thread(new NonBlockingPublisher(awsIotClient));
        System.out.println("go");
        blockingPublishThread.start();
        //nonBlockingPublishThread.start();
        System.out.println("Free memory when started:"+Runtime.getRuntime().freeMemory());
        blockingPublishThread.join();
        //nonBlockingPublishThread.join();
    }

}
