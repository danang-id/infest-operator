/*
 * Copyright 2017 Adam Afandi.
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
package com.jogjadamai.infest.operator;

/**
 * <h1>class <code>Program</code></h1>
 * <p><code>Program</code> is class defining <code>main()</code> to run the
 * application.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Adam Afandi for Infest.</i></b></p>
 * 
 * @author Adam Afandi
 * @version 2017.03.10.0001
 */


public final class Program {
    
    private static Runnable MainGUI, SignInGUI;
    private static Thread MainGUIThread, SignInGUIThread;
    
    private static final Integer[] SECURITY_NUMBER = {
        390895594, 179331562
    };
    
    public static void main(String[] args) {
        Program.MainGUI = new MainGUI();
        Program.SignInGUI = new SignInGUI();
        Program.MainGUIThread = new Thread(Program.MainGUI);
        Program.SignInGUIThread = new Thread(Program.SignInGUI);
        Program.showSignInGUI();
    }
    
    protected static void showSignInGUI() {
        java.awt.EventQueue.invokeLater(Program.SignInGUIThread);
    }
    
    protected static Boolean authenticate(Integer[] securityNumber){
        if (java.util.Arrays.equals(securityNumber, Program.SECURITY_NUMBER)) {
            java.awt.EventQueue.invokeLater(Program.MainGUIThread);
            return true;
        } else {
            return false;
        }
    }
    
}