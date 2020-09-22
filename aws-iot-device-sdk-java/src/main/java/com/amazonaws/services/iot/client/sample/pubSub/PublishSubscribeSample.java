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
private static void initClient() {
	String clientEndpoint = "a36s517adg7uyq-ats.iot.ap-northeast-1.amazonaws.com";
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

static final String  cert = 
        "MIIDWTCCAkGgAwIBAgIUVkXIjHqJ6Rg7wKVvasP4R7A1heMwDQYJKoZIhvcNAQEL"
    +   "BQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcyBPPUFtYXpvbi5jb20g"
    +   "SW5jLiBMPVNlYXR0bGUgU1Q9V2FzaGluZ3RvbiBDPVVTMB4XDTIwMDgzMTA2MDUy"
    +   "NFoXDTQ5MTIzMTIzNTk1OVowHjEcMBoGA1UEAwwTQVdTIElvVCBDZXJ0aWZpY2F0"
    +   "ZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK/R08Y0XlmcgYmlDJDY"
    +   "C4qirD5A2HVHsUbKn/TdOiB0IoDuWCJh0prImiNlYeDEw8LVxPoOGwlj4WW72Xlc"
    +   "ME2hwztdIzKKoIVL1TJLX0fZyCTTsF2zvHhcRtJ3htjWE7oPiISw+UOWuvbWCw+r"
    +   "ZunoNM7MV1gtB5r4znrFMRDf+6AIn9Jkv6OnsjbrjyedB/c4/m14BtBAFu9afU/t"
    +   "racz+33GGmEp2CmQSOKQk0ffo1a4uyMfPkNI8uV3SIY2/tZVF/KfT7P3Vttu8PhN"
    +   "6hOahpNdIy5vMzG9Y2ZK2nntU0elxje4mtZjFILzkADTtFllC2+oIhYiIc0vVtet"
    +   "OCcCAwEAAaNgMF4wHwYDVR0jBBgwFoAUIXb1hHvT0z7i67HU2HH79mlYAqkwHQYD"
    +   "VR0OBBYEFFJvSuoGaFwF5Q9bdt1JNA2PzGZ+MAwGA1UdEwEB/wQCMAAwDgYDVR0P"
    +   "AQH/BAQDAgeAMA0GCSqGSIb3DQEBCwUAA4IBAQAZxEqjvwIWqmxMRYFpuIcWC3zD"
    +   "aNgIuoacZTV1hz3rJyOrnkQmeXveVOhFWy7xvT+NB6aQ0BPcPnRcrDvyi9WPzc3P"
    +   "E+ef+IhFm4pfddajKVXzy1ejKhj+uObrr3o3L5EpfQ0uKw4piM1t3DWiypSORc0B"
    +   "/FFpIwM/qrFy0Kn7NIKgEpo+H56i0vgaDtbJeWEeXKfp28NY+8kZbbKgJKYTSZZs"
    +   "0mt64g4BeMYIPy0TEqOtR4vuqos6gejGAhqTqZjDO2j1Xb8BacXAD+lxsf4/jUXy"
    +   "iYK6rxCYCM9cnBAQZ0UU3CMaxxKJWGH4odaolR9vs6UEAO1BGVrLrUzcVttD";

static final String  key = 
        "MIIEpAIBAAKCAQEAr9HTxjReWZyBiaUMkNgLiqKsPkDYdUexRsqf9N06IHQigO5Y"
    +   "ImHSmsiaI2Vh4MTDwtXE+g4bCWPhZbvZeVwwTaHDO10jMoqghUvVMktfR9nIJNOw"
    +   "XbO8eFxG0neG2NYTug+IhLD5Q5a69tYLD6tm6eg0zsxXWC0HmvjOesUxEN/7oAif"
    +   "0mS/o6eyNuuPJ50H9zj+bXgG0EAW71p9T+2tpzP7fcYaYSnYKZBI4pCTR9+jVri7"
    +   "Ix8+Q0jy5XdIhjb+1lUX8p9Ps/dW227w+E3qE5qGk10jLm8zMb1jZkraee1TR6XG"
    +   "N7ia1mMUgvOQANO0WWULb6giFiIhzS9W1604JwIDAQABAoIBAQCWXDzfQcP6oPTL"
    +   "MUKY0Jq2Oj5RkwVK7z/1ia3YLCXcwVUMrEjlRoFk6++eG8LkBYJhKo4lR1Dp6+hE"
    +   "J61ps2R/z7p2F0XVoGZ8+IhgVcMrYF1g2UT4LZEd4dSTg7Ln19TRBx26VNsnaU2F"
    +   "cTHOecgue+5FeM2t0/ywFN4IcoFuqqNE0/rI6Rulu/gA3LYkgw3WT5kpmw18HjLR"
    +   "++b2+PC2gMDrO28B8DKv0zgurqjvy5M+JInznOs3hXulyVGQYEKCypOxvA+Q7Mca"
    +   "VKuQFk3DTWvUF5jLVB1kJZfua3I1Txmxcuyuyrsv4H/liN+riNM86I+l8Kw4phD+"
    +   "ZBgHpcaZAoGBAOBthBMwspysv9yWaFrZtQUrcJOGgOpJiTuLP4+xSeIO9GIQypq3"
    +   "r3ZRBqv/1TwfyMVwrXiWr57zA+DhdPJGLpmtvDFELfXhQZSjKd+ayZabE4fbekoe"
    +   "2Vbz7jhDCS03OX3de9w42+qTYkw8oeR0jZu2SLatLU48BmJhQOOmVXUFAoGBAMiN"
    +   "v/GMbyZ2xvVH/UAcS/dxooShawgx8PBWUkrRRaUXqfexGhdh00PG+uqj6hUX6Jeh"
    +   "y8Ubp0+rw8GknXSVvW1/r+yTpn9/JhplkCevBYxvYdUa7GBczjnI2yYhrv6n/fc4"
    +   "tk2UXd+4iITBn08kxRS7qr8ElvaT9BDsdpJO8kA7AoGACjYxekYO+n4JxNm7Kdca"
    +   "G4AcgTnvh7mM2v69B6bDe9u+Vu/4qA2PmEybhxv+8dtoYUN9QS1qFEtuc78OeS+k"
    +   "Uy3KkFtP1A5J05+Q+jGq1oV74ASmjdTVgtVak4jJVBtdLFL1ndGggA3wvRNNMn3z"
    +   "K5Chwl4i35pvl+5g63rYDJkCgYEAmTdR8KOtNuF/VlxNKfF0R/JUiqcXMgeVXsdT"
    +   "zzzZzCqr+0Md/8cIs7sMbr4TU1IUq+M73bYmuEMtJn8wm6TaNxFZwY97n+PvpDL7"
    +   "UpXv0z40q64J8AR2uMauRg9ttbd52qlE/TfgD87W3TVfFAOvXQuSgqRrAK4/w74j"
    +   "R9twT4kCgYBYXgPppDln1VXCyMSZPvPrDXayDmjBgdT69FWXhLXn9huNefj/MgLQ"
    +   "HFApLcQBuKwgXI9kEnDCLI4SLcm5EBcWTzi22ORLjhLz50Tp0RVPJOEJJXlUMqZX"
    +   "z4HNs6hl61aeT1Xg9uT0B2Y+NyE7nXZm8HTUuN1owcPcz6/sVjh5Ug==";


    public static void main(String args[]) throws InterruptedException, AWSIotException, AWSIotTimeoutException {
//        CommandArguments arguments = CommandArguments.parse(args);
//        initClient(arguments);
    	
    	System.out.println("Free memory at startup:"+Runtime.getRuntime().freeMemory());
    	KeyStore.initWebPublicKeystoreLocation(PublishSubscribeSample.class, "_main.ks");
    	PrivateKeyStore ks = KeyStore.selectPrivateKeyStore(null);
    	ks.load(((UserKeyStoreParam)UserKeyStoreParam.Build())
    			.addCertificate(cert)
    			.setPrivateKey(key));
    	System.out.println("init");
    	initClient();
    	System.out.println("conn");
        awsIotClient.connect();
        System.out.println("sub");

        AWSIotTopic topic = new TestTopicListener(TestTopic, TestTopicQos);
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
