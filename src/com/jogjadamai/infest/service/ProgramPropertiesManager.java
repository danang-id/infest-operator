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
 * <h1>class <code>ProgramPropertiesManager</code></h1>
 * <p><code>ProgramPropertiesManager</code> is a methods to read Infest Configuration
 * file (<code>infest.conf</code> in the working directory and pass it back to
 * the part of <code>Program</code> which called the method.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Danang Galuh Tegar P for Infest.</i></b></p>
 * 
 * @author Danang Galuh Tegar P
 * @version 2017.03.10.0001
 */
public final class ProgramPropertiesManager {
    
    private final java.io.File propertyFile;
    private final String propertyComment;
    
    private static ProgramPropertiesManager INSTANCE;
    
    private ProgramPropertiesManager() {
        java.net.URL propertyFileURL = getClass().getResource("infest.conf");
        propertyFile = new java.io.File(propertyFileURL.getPath());
        propertyComment = 
                      " \n"
                    + " INFEST CONFIGURATION FILE \n"
                    + " \n"
                    + " Copyright (C) 2017 Infest Developer Team. Licensed under Apache License, Version 2.0. \n"
                    + " \n"
                    + " WARNING: Do not make any modification to this file except if you know what you are doing. \n"
                    + " Infest Developer team, and any its affiliates does not take any responsibilties toward \n"
                    + " program error caused by a modified or miss-configured file. Please take any necessary \n"
                    + " cautions on proceeding. \n";
        if(getProperty("serveraddress") == null) setProperty("serveraddress", "127.0.0.1");
    }
    
    public static ProgramPropertiesManager getInstance() {
        if(INSTANCE == null) INSTANCE = new ProgramPropertiesManager();
        return INSTANCE;
    }
    
    public String getProperty(String propertyKey) throws NullPointerException {
        java.util.Properties property = new java.util.Properties();
	java.io.InputStream inputStream = null;
        String propertyValue;
	try {
            inputStream = new java.io.FileInputStream(propertyFile);
            property.load(inputStream);
            propertyValue = property.getProperty(propertyKey);
            if(propertyValue == null) throw new NullPointerException();
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
    
    public final void setProperty(String propertyKey, String propertyValue) {
        java.util.Properties property = new java.util.Properties();
	java.io.InputStream inputStream = null;
        try {
            inputStream = new java.io.FileInputStream(propertyFile);
            property.load(inputStream);
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
        java.io.OutputStream outputStream = null;
        try {
            outputStream = new java.io.FileOutputStream(propertyFile);
            property.setProperty(propertyKey, propertyValue);
            property.store(outputStream, propertyComment);
	} catch (java.io.IOException ex) {
            System.err.println("[INFEST] " + ex);
            throw new NullPointerException();
	} finally {
            if(outputStream != null) {
                try {
                    outputStream.close();
                } catch (java.io.IOException ex) {
                    System.err.println("[INFEST] " + ex);
                    throw new NullPointerException();
                }
            }
	}
    }
    
}
