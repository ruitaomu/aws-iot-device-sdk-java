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

package com.amazonaws.services.iot.client.sample.sampleUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is a helper class to facilitate reading of the configurations and
 * certificate from the resource files.
 */
public class SampleUtil {
    private static final String PropertyFile = "aws-iot-sdk-samples.properties";

    public static String getConfig(String name) {
        Properties prop = new Properties();
        URL resource = SampleUtil.class.getResource(PropertyFile);
        if (resource == null) {
            return null;
        }
        try {
        	InputStream stream = resource.openStream();
            prop.load(stream);
        } catch (IOException e) {
            return null;
        }
        String value = prop.getProperty(name);
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return value;
        }
    }

}
