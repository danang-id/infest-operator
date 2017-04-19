/*
 * Copyright 2017 Danang Galuh Tegar P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jogjadamai.infest.service;

/**
 * <h1>class <code>ConfigurationReader</code></h1>
 * <p><code>ConfigurationReader</code> is a methods to read Infest Configuration
 * file (<code>infest.conf</code> in the working directory and pass it back to
 * the part of <code>Program</code> which called the method.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Danang Galuh Tegar P for Infest.</i></b></p>
 * 
 * @author Danang Galuh Tegar P
 * @version 2017.03.10.0001
 */
public final class ConfigurationReader {
    
    public static String getConfiguration(String propertyKey) {
        java.util.Properties property = new java.util.Properties();
	java.io.InputStream inputStream = null;
        String propertyValue;
	try {
            inputStream = new java.io.FileInputStream("infest.conf");
            property.load(inputStream);
            propertyValue = property.getProperty(propertyKey);
	} catch (java.io.IOException ex) {
            System.err.println("[INFEST] " + ex);
            throw new NullPointerException();
	} finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (java.io.IOException ex) {
                    System.err.println("[INFEST] " + ex);
                    throw new NullPointerException();
                }
            }
	}
        return propertyValue;
    }
    
}
