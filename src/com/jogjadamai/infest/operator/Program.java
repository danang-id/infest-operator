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
    
    private static SignInGUI SignInGUI;
    private static MainGUI MainGUI;
    private static Thread SignInThread, MainThread;
    private static Operator Controller;
    
    public static void main(String[] args) {
        Program.SignInGUI = new SignInGUI();
        Program.MainGUI = new MainGUI();
        Program.Controller = Operator.getInstance(Program.SignInGUI, Program.MainGUI);
        Program.SignInThread = new Thread(Program.SignInGUI);
        Program.MainThread = new Thread(Program.MainGUI);
        java.awt.EventQueue.invokeLater(Program.SignInThread);
        java.awt.EventQueue.invokeLater(Program.MainThread);
    }
    
}