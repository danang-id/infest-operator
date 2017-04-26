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
 * <h1>class <code>Operator</code></h1>
 * <p><code>Operator</code> is a controller class containing all the business
 * login of Infest Operator application.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Adam Afandi for Infest.</i></b></p>
 * 
 * @author Adam Afandi
 * @version 2017.03.10.0001
 */
public final class Operator {
    
    private java.rmi.registry.Registry registry;
    private com.jogjadamai.infest.communication.IProtocolClient protocolClient;
    private com.jogjadamai.infest.communication.IProtocolServer protocolServer;
    private com.jogjadamai.infest.operator.SignInGUI signInFrame;
    private com.jogjadamai.infest.operator.MainGUI mainFrame;
    private ViewFrame activeFrame;
    private java.util.List<com.jogjadamai.infest.entity.Menus> loadedMenu;
    private java.util.List<com.jogjadamai.infest.entity.Tables> loadedTable;
    private java.util.List<com.jogjadamai.infest.entity.FinanceReport> loadedFinancialStatement;
    private SaveMethod currentSaveMethod;
    private byte[] currentImageData;
    
    private final com.jogjadamai.infest.service.ProgramPropertiesManager programPropertiesManager;
    
    private enum SaveMethod {
        INSERT, UPDATE
    }
    
    private enum ViewFrame {
        SIGN_IN, MAIN
    }
    
    private static Operator INSTANCE;
    
    private Operator() {
        this.programPropertiesManager = com.jogjadamai.infest.service.ProgramPropertiesManager.getInstance();
        this.initialiseConnection();
        this.activeFrame = ViewFrame.SIGN_IN;
    }
    
    protected static Operator getInstance() {
        if(INSTANCE == null) INSTANCE = new Operator();
        return INSTANCE;
    }
    
    private void initialiseConnection() {
        String serverAddress = null;
        try {
            serverAddress = programPropertiesManager.getProperty("serveraddress");
        } catch (java.lang.NullPointerException ex) {
            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
            javax.swing.JOptionPane.showMessageDialog(mainFrame, "Infest Configuration File is miss-configured!\n\n"
                    + "Please verify that the Infest Configuration File (infest.conf) is exist in the current\n"
                    + "working directory and is properly configured. Any wrong setting or modification of\n"
                    + "Infest Configuration File would cause this error.", "INFEST: Program Configuration Manager", javax.swing.JOptionPane.ERROR_MESSAGE);
            fatalExit(-1);
        }
        try {
            this.registry = java.rmi.registry.LocateRegistry.getRegistry(serverAddress, 42700);
            this.protocolClient = new com.jogjadamai.infest.communication.OperatorClient();
            this.protocolServer = (com.jogjadamai.infest.communication.IProtocolServer) this.registry.lookup("InfestAPIServer");
            this.protocolServer.authenticate(this.protocolClient);
        } catch (java.rmi.NotBoundException | java.rmi.RemoteException ex) {
            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to initialise remote connection to Infest API Server (" + serverAddress  +")!\n\n"
                + "Please verify that Infest API Server is currently turned on and the configuration file\n"
                + "of this program is properly configured to the Infest API Server address.", "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            fatalExit(-1);
        }
    }
    
    private void fatalExit(int code) {
        System.err.println("[INFEST] " +  getNowTime() + ": System exited with code " + code + ".");
        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame,
                "Fatal error occured! Please contact an Infest Adminisrator.\n\n"
                + "CODE [" + code + "]\n"
                + "Infest Program is now exiting.", "INFEST: System Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        System.exit(code);
    }
    
    private Boolean isMaintenanceModeActive() {
        Boolean featureValue = false;
        try {
            featureValue = (protocolServer.readFeature(protocolClient, 1).getStatus() == 1);
        } catch (java.rmi.RemoteException ex) {
            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                    + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                    + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                    + "Infest Program will now switched to MAINTENANCE MODE.",
                    "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            featureValue = true;
        } catch (java.lang.NullPointerException ex) {
            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                    + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                    + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                    + "Infest Program will now switched to MAINTENANCE MODE.",
                    "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
            featureValue = true;
        }
        return featureValue;
    }
    
    private Boolean isStatementGeneratorActive() {
        Boolean featureValue = false;
        try {
            featureValue = (protocolServer.readFeature(protocolClient, 3).getStatus() == 1);
        } catch (java.rmi.RemoteException ex) {
            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                    + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                    + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                    + "Infest Program will now switched to MAINTENANCE MODE.",
                    "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            signOut();
        } catch (java.lang.NullPointerException ex) {
            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                    + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                    + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                    + "Infest Program will now switched to MAINTENANCE MODE.",
                    "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
            signOut();
        }
        return featureValue;
    }
    
    protected void setSignInFrame(com.jogjadamai.infest.operator.SignInGUI signInFrame) {
        this.signInFrame = signInFrame;
    }
    
    protected void setMainFrame(com.jogjadamai.infest.operator.MainGUI mainFrame) {
        this.mainFrame = mainFrame;
    }
    
    private String getNowTime() {
        return java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(java.time.LocalDateTime.now());
    }
    
    protected void signIn() {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } else {
            com.jogjadamai.infest.communication.Credentials inputCred = new com.jogjadamai.infest.communication.Credentials(signInFrame.usernameField.getText(), signInFrame.passwordField.getPassword());
            try {
                String salt = null;
                salt = this.programPropertiesManager.getProperty("salt");
                try {
                    inputCred.encrpyt(salt);
                } catch (java.security.NoSuchAlgorithmException 
                        | java.security.spec.InvalidKeySpecException 
                        | javax.crypto.NoSuchPaddingException 
                        | java.security.InvalidKeyException 
                        | java.security.spec.InvalidParameterSpecException 
                        | java.io.UnsupportedEncodingException 
                        | javax.crypto.IllegalBlockSizeException 
                        | javax.crypto.BadPaddingException ex) {
                System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to encrypt credentials!\n\n"
                        + "Please contact an Infest Administrator for furhter help.", 
                        "INFEST: Encryption Service", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (NullPointerException ex) {
                System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest Configuration File is miss-configured!\n\n"
                        + "Please verify that the Infest Configuration File (infest.conf) is exist in the current\n"
                        + "working directory and is properly configured. Any wrong setting or modification of\n"
                        + "Infest Configuration File would cause this error.", 
                        "INFEST: Program Configuration Manager", javax.swing.JOptionPane.ERROR_MESSAGE);
                fatalExit(-1);
            }
            com.jogjadamai.infest.communication.Credentials savedCred = null;
            try {    
                savedCred = this.protocolServer.getCredentials(protocolClient);
            } catch (java.rmi.RemoteException ex) {
                savedCred = new com.jogjadamai.infest.communication.Credentials("", new char[0]);System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server is unable to run!\n\n"
                    + "Program error detected.", "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                fatalExit(-1);
            }
            if(savedCred.equals(inputCred)) {
                mainFrame.setVisible(false);
            } else {
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, 
                        "Sign In Failed!\n\n"
                        + "Either username or password is wrong, or your\n"
                        + "Infest Configuration File is miss-configured.", 
                        "INFEST: Authentication System", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    protected void signOut() {
        mainFrame.setVisible(false);
        signInFrame.setVisible(true);
        activeFrame = ViewFrame.SIGN_IN;
    }
    
    protected void shutdown(int code) {
        System.out.println("[INFEST] " +  getNowTime() + ": System exited with code " + code + ".");
        signInFrame.setVisible(false);
        mainFrame.setVisible(false);
        System.exit(code);
    }
    
    protected void switchCard(com.jogjadamai.infest.operator.MainGUI.CardList card) {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            currentSaveMethod = SaveMethod.UPDATE;
            switch(card) {
                case WELCOME:
                    mainFrame.titleLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/OperatorPanelLabel.png")));
                    mainFrame.setTitle("INFEST: Operator Panel");
                    mainFrame.getCardLayout().show(mainFrame.mainPanel, "welcomeCard");
                    break;
                case MENUS:
                    mainFrame.titleLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/MenuManagementLabel.png")));
                    mainFrame.setTitle("INFEST: Operator Panel >> Menu Management");
                    mainFrame.getCardLayout().show(mainFrame.mainPanel, "manageMenusCard");
                    this.readAll(card);
                    break;
                case TABLES:
                    mainFrame.titleLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/TableManagementLabel.png")));
                    mainFrame.setTitle("INFEST: Operator Panel >> Table Management");
                    mainFrame.getCardLayout().show(mainFrame.mainPanel, "manageTablesCard");
                    this.readAll(card);
                    break;
                case FINANCIAL_STATEMENT:
                    mainFrame.titleLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/FinancialStatementLabel.png")));
                    mainFrame.setTitle("INFEST: Operator Panel >> Financial Statement");
                    mainFrame.getCardLayout().show(mainFrame.mainPanel, "financialStatementCard");
                    if(!isStatementGeneratorActive()) {
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, 
                        "The feature [FINANCIAL_STATEMENT_GENERATOR] is turned OFF by an Infest Administrator.\n"
                        + "Please contact the Infest Administrator for further information.\n\n"
                        + "NOTE: If this features is turned OFF, then any chances to generate Financial Statement\n"
                        + "would be PROHIBITED.", 
                        "INFEST: Financial Statement Generator", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
            }
        }
    }
    
    private com.jogjadamai.infest.entity.Features getCurrency() {
        com.jogjadamai.infest.entity.Features currency = new com.jogjadamai.infest.entity.Features();
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            try {
                currency = protocolServer.readFeature(protocolClient, 2);
            } catch (java.rmi.RemoteException ex) {
                System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                        + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                        + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                        + "Infest Program will now switched to MAINTENANCE MODE.",
                        "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                signOut();
            } catch (java.lang.NullPointerException ex) {
                System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                        + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                        + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                        + "Infest Program will now switched to MAINTENANCE MODE.",
                        "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                signOut();
            }
            if(currency.getStatus() == 0) currency.setDescription("");
        }
        return currency;
    }
    
    private void reloadTable() {
        currentSaveMethod = SaveMethod.UPDATE;
        if(loadedMenu != null) mainFrame.menusTable.setModel(new com.jogjadamai.infest.tablemodel.MenusTableModel(loadedMenu, getCurrency()));
        if(loadedTable != null) mainFrame.tablesTable.setModel(new com.jogjadamai.infest.tablemodel.TablesTableModel(loadedTable));
        if(loadedFinancialStatement != null) {
            mainFrame.financialStatementTable.setModel(new com.jogjadamai.infest.tablemodel.FinancialStatementTableModel(loadedFinancialStatement, getCurrency()));
            Integer totalIncome = 0;
            for(com.jogjadamai.infest.entity.FinanceReport statement : loadedFinancialStatement) {
                totalIncome = totalIncome + statement.getIncome();
            }
            mainFrame.totalIncomeValueLabel.setText(String.valueOf(totalIncome) + " " + getCurrency().getDescription());
        }
    }
    
    protected void search(com.jogjadamai.infest.operator.MainGUI.CardList card) {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            currentSaveMethod = SaveMethod.UPDATE;
            switch(card) {
                case WELCOME:
                    break;
                case MENUS:
                    mainFrame.currencyLabel.setText(getCurrency().getDescription());
                    try {
                        java.util.List<com.jogjadamai.infest.entity.Menus> allMenus = protocolServer.readAllMenu(protocolClient);
                        java.util.List<com.jogjadamai.infest.entity.Menus> requestedMenus = new java.util.ArrayList<>();
                        for(com.jogjadamai.infest.entity.Menus menu : allMenus) {
                            if( String.valueOf(menu.getId()).toLowerCase().contains(mainFrame.searchMenusField.getText().toLowerCase()) | 
                                    menu.getName().toLowerCase().contains(mainFrame.searchMenusField.getText().toLowerCase()) |
                                    String.valueOf(menu.getPrice()).contains(mainFrame.searchMenusField.getText().toLowerCase()) | 
                                    menu.getDescription().toLowerCase().contains(mainFrame.searchMenusField.getText().toLowerCase()) ) {
                                requestedMenus.add(menu);
                            }
                        }
                        loadedMenu = requestedMenus;
                    } catch (java.rmi.RemoteException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } catch (java.lang.NullPointerException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    }
                    break;
                case TABLES:
                    try {
                        java.util.List<com.jogjadamai.infest.entity.Tables> allTables = protocolServer.readAllTable(protocolClient);
                        java.util.List<com.jogjadamai.infest.entity.Tables> requestedTables = new java.util.ArrayList<>();
                        for(com.jogjadamai.infest.entity.Tables table : allTables) {
                            if( String.valueOf(table.getId()).toLowerCase().contains(mainFrame.searchTablesField.getText().toLowerCase()) | 
                                    table.getName().toLowerCase().contains(mainFrame.searchTablesField.getText().toLowerCase()) |
                                    table.getDescription().toLowerCase().contains(mainFrame.searchTablesField.getText().toLowerCase()) ) {
                                requestedTables.add(table);
                            }
                        }
                        loadedTable = requestedTables;
                    } catch (java.rmi.RemoteException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } catch (java.lang.NullPointerException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    }
                    break;
                case FINANCIAL_STATEMENT:
                    break;
                default:
                    break;
            }
            reloadTable();
        }
        
    }
    
    protected void readAll(com.jogjadamai.infest.operator.MainGUI.CardList card) {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            currentSaveMethod = SaveMethod.UPDATE;
            switch(card) {
                case WELCOME:
                    break;
                case MENUS:
                    mainFrame.currencyLabel.setText(this.getCurrency().getDescription());
                    try {
                        loadedMenu = protocolServer.readAllMenu(protocolClient);
                        reloadTable();
                    } catch (java.rmi.RemoteException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } catch (java.lang.NullPointerException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    }
                    break;
                case TABLES:
                    try {
                        loadedTable = new java.util.ArrayList<>();
                        for(com.jogjadamai.infest.entity.Tables table : protocolServer.readAllTable(protocolClient)) {
                            if(table.getStatus() != 0) loadedTable.add(table);
                        }
                        reloadTable();
                    } catch (java.rmi.RemoteException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } catch (java.lang.NullPointerException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    }
                    break;
                case FINANCIAL_STATEMENT:
                    break;
                default:
                    break;
            }
        }
    }

    protected void generateFinancialStatement() {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            if(!isStatementGeneratorActive()) {
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, 
                        "Failed to generate Financial Statement!\n\n"
                        + "The feature [FINANCIAL_STATEMENT_GENERATOR] is turned off by an Infest Administrator.\n"
                        + "Please contact Infest Administrator for further information, or try again later.", 
                        "INFEST: Financial Statement Generator", javax.swing.JOptionPane.ERROR_MESSAGE);
                mainFrame.findByDateCheckBox.setSelected(false);
                loadedFinancialStatement = new java.util.ArrayList<>();
                reloadTable();
            } else {
                try {
                    java.util.Date date = mainFrame.findByDateCheckBox.isSelected() ? 
                            mainFrame.financialStatementDateChooser.getDate() : 
                            new java.util.Date(0);
                    java.time.LocalDate localDate = java.time.LocalDateTime.ofInstant(date.toInstant(), java.time.ZoneId.of("GMT+7")).toLocalDate();
                    try {
                        loadedFinancialStatement = mainFrame.findByDateCheckBox.isSelected() ? 
                                protocolServer.readFinanceReport(protocolClient, localDate) :
                                protocolServer.readFinanceReport(protocolClient);
                    } catch (java.rmi.RemoteException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } catch (java.lang.NullPointerException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    }
                } catch (java.lang.NullPointerException ex) {
                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to generate Financial Statement!\n\n"
                            + "Please verify that you have set an exact date before generating a Financial Statement\n"
                            + "by Date.",
                            "INFEST: Financial Statement", javax.swing.JOptionPane.ERROR_MESSAGE);
                    loadedFinancialStatement = new java.util.ArrayList<>();
                } finally {
                    reloadTable();
                }
            }
        }
    }
    
    private void displayImage(byte[] imageData) {
        currentImageData = imageData;
        java.awt.image.BufferedImage menuBufferedImage = null;
        try {
            if (imageData != null) menuBufferedImage = javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(imageData));
        } catch (java.io.IOException ex) {
            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Error loading menu image!\n\n"
                    + "Please verify that menu's image has been properly set before.\n"
                    + "If problem persist, please renew the menu image or contact an Infest Administrator.",
                    "INFEST: Menu Image", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        if (menuBufferedImage != null) {
            mainFrame.menuImageIconLabel.setText("");
            Integer imageSize = (menuBufferedImage.getHeight() >= menuBufferedImage.getWidth()) ? menuBufferedImage.getHeight() : menuBufferedImage.getWidth();
            Double scalingFactor = mainFrame.menuImagePanel.getHeight() / imageSize.doubleValue();
            Integer newHeight = (int) (menuBufferedImage.getHeight() * scalingFactor);
            Integer newWidth = (int) (menuBufferedImage.getWidth() * scalingFactor);
            mainFrame.menuImageIconLabel.setIcon(new javax.swing.ImageIcon(menuBufferedImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
            mainFrame.saveImageToFileMenuItem.setEnabled(true);
        } else {
            mainFrame.menuImageIconLabel.setText("No image. Right-click to change image.");
            mainFrame.menuImageIconLabel.setIcon(null);
            mainFrame.saveImageToFileMenuItem.setEnabled(false);
        }
    }
    
    protected void loadFields(com.jogjadamai.infest.operator.MainGUI.CardList card) {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            currentSaveMethod = SaveMethod.UPDATE;
            Integer row;
            switch(card) {
                case WELCOME:
                    break;
                case MENUS:
                    row = mainFrame.menusTable.getSelectedRow();
                    if(row >= 0 && row < loadedMenu.size()) {
                        mainFrame.menuNameField.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.menuTypeComboBox.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.menuPriceField.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.menuStockField.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.menuDurationField.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.menuDescriptionArea.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.saveImageToFileMenuItem.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.changeImageMenuItem.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.deleteMenuButton.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.saveChangesMenuButton.setEnabled(!(loadedMenu.get(row).getStatus() == 0));
                        mainFrame.menuIDField.setText(Integer.toString(loadedMenu.get(row).getId()));
                        mainFrame.menuNameField.setText(loadedMenu.get(row).getName());
                        mainFrame.menuTypeComboBox.setSelectedIndex(loadedMenu.get(row).getType());
                        mainFrame.menuPriceField.setValue((Integer) loadedMenu.get(row).getPrice());
                        mainFrame.currencyLabel.setText(this.getCurrency().getDescription());
                        mainFrame.menuStockField.setValue((Integer) loadedMenu.get(row).getStock());
                        java.time.LocalTime menuDurationLocalTime = java.time.LocalDateTime.ofInstant(loadedMenu.get(row).getDuration().toInstant(),java.time.ZoneId.systemDefault()).toLocalTime();
                        Integer menuDurationInteger = 0;
                        menuDurationInteger = menuDurationInteger + menuDurationLocalTime.getMinute();
                        menuDurationInteger = menuDurationInteger + (menuDurationLocalTime.getHour() * 60);
                        mainFrame.menuDurationField.setValue(menuDurationInteger);
                        mainFrame.menuDescriptionArea.setText(loadedMenu.get(row).getDescription());
                        displayImage(loadedMenu.get(row).getImage());
                    } else {
                        System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.operator.OutOfBoundsException");
                        mainFrame.menuNameField.setEnabled(false);
                        mainFrame.menuTypeComboBox.setEnabled(false);
                        mainFrame.menuPriceField.setEnabled(false);
                        mainFrame.menuStockField.setEnabled(false);
                        mainFrame.menuDurationField.setEnabled(false);
                        mainFrame.menuDescriptionArea.setEnabled(false);
                        mainFrame.saveImageToFileMenuItem.setEnabled(false);
                        mainFrame.changeImageMenuItem.setEnabled(false);
                        mainFrame.deleteMenuButton.setEnabled(false);
                        mainFrame.saveChangesMenuButton.setEnabled(false);
                        mainFrame.menuIDField.setText("");
                        mainFrame.menuNameField.setText("");
                        mainFrame.menuTypeComboBox.setSelectedIndex(0);
                        mainFrame.menuPriceField.setValue(0);
                        mainFrame.currencyLabel.setText(this.getCurrency().getDescription());
                        mainFrame.menuStockField.setValue(0);
                        mainFrame.menuDurationField.setValue(0);
                        mainFrame.menuDescriptionArea.setText("");
                    }
                    break;
                case TABLES:
                    row = mainFrame.tablesTable.getSelectedRow();
                    if(row >= 0 && row < loadedTable.size()) {
                        mainFrame.tableNameField.setEnabled(!(loadedTable.get(row).getStatus() == 0));
                        mainFrame.tableDescriptionArea.setEnabled(!(loadedTable.get(row).getStatus() == 0));
                        mainFrame.deleteTableButton.setEnabled(!(loadedTable.get(row).getStatus() == 0));
                        mainFrame.saveChangesTableButton.setEnabled(!(loadedTable.get(row).getStatus() == 0));
                        mainFrame.tableIDField.setText(Integer.toString(loadedTable.get(row).getId()));
                        mainFrame.tableNameField.setText(loadedTable.get(row).getName());
                        mainFrame.tableDescriptionArea.setText(loadedTable.get(row).getDescription());
                    } else {
                        System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.operator.OutOfBoundsException");
                        mainFrame.tableNameField.setEnabled(false);
                        mainFrame.tableDescriptionArea.setEnabled(false);
                        mainFrame.deleteTableButton.setEnabled(false);
                        mainFrame.saveChangesTableButton.setEnabled(false);
                        mainFrame.tableIDField.setText("");
                        mainFrame.tableNameField.setText("");
                        mainFrame.tableDescriptionArea.setText("");
                    }
                    break;
                case FINANCIAL_STATEMENT:
                    break;
                default:
                    break;
            }
        }
        
    }
    
    protected void newEntity(com.jogjadamai.infest.operator.MainGUI.CardList card) {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            currentSaveMethod = SaveMethod.INSERT;
            switch(card) {
                case WELCOME:
                    break;
                case MENUS:
                    mainFrame.menuNameField.setEnabled(true);
                    mainFrame.menuTypeComboBox.setEnabled(true);
                    mainFrame.menuPriceField.setEnabled(true);
                    mainFrame.currencyLabel.setEnabled(true);
                    mainFrame.menuStockField.setEnabled(true);
                    mainFrame.menuDurationField.setEnabled(true);
                    mainFrame.menuDescriptionArea.setEnabled(true);
                    mainFrame.saveImageToFileMenuItem.setEnabled(true);
                    mainFrame.changeImageMenuItem.setEnabled(true);
                    mainFrame.deleteMenuButton.setEnabled(false);
                    mainFrame.saveChangesMenuButton.setEnabled(true);
                    mainFrame.menuIDField.setText("to be created");
                    mainFrame.menuNameField.setText("");
                    mainFrame.menuTypeComboBox.setSelectedIndex(0);
                    mainFrame.menuPriceField.setValue(0);
                    mainFrame.currencyLabel.setText(this.getCurrency().getDescription());
                    mainFrame.menuStockField.setValue(0);
                    mainFrame.menuDurationField.setValue(0);
                    mainFrame.menuDescriptionArea.setText("");
                    displayImage(null);
                    break;
                case TABLES:
                    mainFrame.tableNameField.setEnabled(true);
                    mainFrame.tableDescriptionArea.setEnabled(true);
                    mainFrame.deleteTableButton.setEnabled(false);
                    mainFrame.saveChangesTableButton.setEnabled(true);
                    mainFrame.tableIDField.setText("to be created");
                    mainFrame.tableNameField.setText("");
                    mainFrame.tableDescriptionArea.setText("");
                    break;
                case FINANCIAL_STATEMENT:
                    break;
                default:
                    break;
            }
        }
    }
       
    private Boolean areFieldsNotEmpty(com.jogjadamai.infest.operator.MainGUI.CardList card){
        switch(card) {
            case WELCOME:
                return false;
            case MENUS:
                return !(mainFrame.menuNameField.getText().trim().isEmpty()
                | mainFrame.menuTypeComboBox.getSelectedItem().equals("")
                | String.valueOf(mainFrame.menuPriceField.getValue()).trim().isEmpty()
                | String.valueOf(mainFrame.menuStockField.getValue()).trim().isEmpty()
                | String.valueOf(mainFrame.menuDurationField.getValue()).trim().isEmpty()
                | mainFrame.menuDescriptionArea.getText().trim().isEmpty());
            case TABLES:
                return !(mainFrame.tableNameField.getText().trim().isEmpty()
                | mainFrame.tableDescriptionArea.getText().trim().isEmpty());
            case FINANCIAL_STATEMENT:
                return false;
            default:
                return false;
        }
    }
    
    protected void saveChanges(com.jogjadamai.infest.operator.MainGUI.CardList card) {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            if(areFieldsNotEmpty(card)) {
                Boolean isSuccess = false;
                com.jogjadamai.infest.entity.Menus menu = new com.jogjadamai.infest.entity.Menus();
                com.jogjadamai.infest.entity.Tables table = new com.jogjadamai.infest.entity.Tables();
                switch(currentSaveMethod) {
                    case INSERT:
                        switch(card) {
                            case MENUS:
                                menu.setStatusDate(new java.util.Date());
                                break;
                        }
                        break;
                    case UPDATE:
                        try {
                            switch(card) {
                                case MENUS:
                                    menu = protocolServer.readMenu(protocolClient, Integer.parseInt(mainFrame.menuIDField.getText()));
                                    break;
                                case TABLES:
                                    table = protocolServer.readTable(protocolClient, Integer.parseInt(mainFrame.menuIDField.getText()));
                                    break;
                            }
                        } catch (java.rmi.RemoteException ex) {
                            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                    + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                    + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                    + "Infest Program will now switched to MAINTENANCE MODE.",
                                    "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                            signOut();
                        } catch (java.lang.NullPointerException ex) {
                            System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                    + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                    + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                    + "Infest Program will now switched to MAINTENANCE MODE.",
                                    "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                            signOut();
                        }
                        break;
                }
                switch(card) {
                    case WELCOME:
                        break;
                    case MENUS:
                        menu.setName(mainFrame.menuNameField.getText());
                        menu.setType(mainFrame.menuTypeComboBox.getSelectedIndex());
                        menu.setPrice(Integer.parseInt(String.valueOf(mainFrame.menuPriceField.getValue())));
                        menu.setStock(Integer.parseInt(String.valueOf(mainFrame.menuStockField.getValue())));
                        menu.setStatus(1);
                        Integer menuDurationInteger = Integer.parseInt(String.valueOf(mainFrame.menuDurationField.getValue()));
                        Integer menuDurationHours = menuDurationInteger / 60;
                        Integer menuDurationMinutes = menuDurationInteger % 60;
                        java.time.LocalTime menuDurationLocalTime = java.time.LocalTime.of(menuDurationHours, menuDurationMinutes);
                        java.time.Instant menuDurationInstant = menuDurationLocalTime.atDate(java.time.LocalDate.of(1970, 1, 1)).atZone(java.time.ZoneId.systemDefault()).toInstant();
                        java.util.Date menuDurationDate = java.util.Date.from(menuDurationInstant);
                        menu.setDuration(menuDurationDate);
                        menu.setDescription(mainFrame.menuDescriptionArea.getText());
                        menu.setImage(currentImageData);
                        switch(currentSaveMethod) {
                            case INSERT:
                                try {
                                    menu = protocolServer.createMenu(protocolClient, menu);
                                    isSuccess = (menu.getId() != null);
                                } catch (java.rmi.RemoteException ex) {
                                    menu.setId(-1);
                                    isSuccess = false;
                                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                            + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                            + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                            + "Infest Program will now switched to MAINTENANCE MODE.",
                                            "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                                    signOut();
                                } catch (java.lang.NullPointerException ex) {
                                    menu.setId(-1);
                                    isSuccess = false;
                                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                            + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                            + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                            + "Infest Program will now switched to MAINTENANCE MODE.",
                                            "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                                    signOut();
                                }
                                mainFrame.menuIDField.setText(String.valueOf(menu.getId()));
                                readAll(card);
                                break;
                            case UPDATE:
                                try {
                                    isSuccess = protocolServer.updateMenu(protocolClient, menu);
                                } catch (java.rmi.RemoteException ex) {
                                    isSuccess = false;
                                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                            + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                            + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                            + "Infest Program will now switched to MAINTENANCE MODE.",
                                            "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                                    signOut();
                                }
                                readAll(card);
                                break;
                            default:
                                break;
                        }
                        break;
                    case TABLES:
                        table.setName(mainFrame.tableNameField.getText());
                        table.setDescription(mainFrame.tableDescriptionArea.getText());
                        table.setStatus(1);
                        switch(currentSaveMethod) {
                            case INSERT:
                                try {
                                    table = protocolServer.createTable(protocolClient, table);
                                    isSuccess = (table.getId() != null);
                                } catch (java.rmi.RemoteException ex) {
                                    table.setId(-1);
                                    isSuccess = false;
                                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                            + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                            + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                            + "Infest Program will now switched to MAINTENANCE MODE.",
                                            "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                                    signOut();
                                } catch (java.lang.NullPointerException ex) {
                                    table.setId(-1);
                                    isSuccess = false;
                                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                            + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                            + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                            + "Infest Program will now switched to MAINTENANCE MODE.",
                                            "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                                    signOut();
                                }
                                mainFrame.tableIDField.setText(String.valueOf(table.getId()));
                                readAll(card);
                                break;
                            case UPDATE:
                                try {
                                    isSuccess = protocolServer.updateTable(protocolClient, table);
                                } catch (java.rmi.RemoteException ex) {
                                    isSuccess = false;
                                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                            + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                            + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                            + "Infest Program will now switched to MAINTENANCE MODE.",
                                            "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                                    signOut();
                                }
                                readAll(card);
                                break;
                            default:
                                break;
                        }
                        break;
                    case FINANCIAL_STATEMENT:
                        break;
                    default:
                        break;
                }
                if(isSuccess) {
                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, 
                            "Changes saved successfully!",
                            "INFEST: Save Changes", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, 
                            "Failed to save any changes made!",
                            "INFEST: Save Changes", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                readAll(card);
                loadFields(card);
            } else {
                System.err.println("[INFEST] " +  getNowTime() + ": com.jogjadamai.infest.Operator.FieldsNotCompletedOnChangesException");
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to save changes!\n\nPlease verify all the field have been filled and try again.", "INFEST: Save Changes", javax.swing.JOptionPane.ERROR_MESSAGE);    
            }
        }
    }
    
    protected void delete(com.jogjadamai.infest.operator.MainGUI.CardList card) {
        if(isMaintenanceModeActive()) {
            System.err.println("[INFEST] " +  getNowTime() + ": com.jogjdamai.infest.MaintenanceModeException");
            javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "MAINTENANCE MODE is ACTIVE.\n\n"
                    + "NOTE: During maintenance mode, Infest Program will be PROHIBITED to execute any functional activities\n"
                    + "within the program, yet is still connected to Infest API Server. Please contact Infest Administrator\n"
                    + "for further information about the maintenance. If Infest Administrator did not activate maintenance\n"
                    + "mode, then network problem was occured and Infest Program is automatically switched to Maintenance\n"
                    + "mode. If that is the case, please verify that Infest API Server is currently turned on and your\n"
                    + "network connection is working.",
                    "INFEST: Maintenance Mode", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            signOut();
        } else {
            currentSaveMethod = SaveMethod.UPDATE;
            switch(card) {
                case WELCOME:
                    break;
                case MENUS:
                    try {
                        com.jogjadamai.infest.entity.Menus menuToDelete = protocolServer.readMenu(protocolClient, Integer.parseInt(mainFrame.menuIDField.getText()));
                        menuToDelete.setStatus(0);
                        menuToDelete.setStatusDate(new java.util.Date());
                        protocolServer.updateMenu(protocolClient, menuToDelete);
                    } catch (java.rmi.RemoteException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } catch (java.lang.NullPointerException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } 
                    break;
                case TABLES:
                    try {
                        com.jogjadamai.infest.entity.Tables tableToDelete = protocolServer.readTable(protocolClient, Integer.parseInt(mainFrame.tableIDField.getText()));
                        tableToDelete.setStatus(0);
                        protocolServer.updateTable(protocolClient, tableToDelete);
                    } catch (java.rmi.RemoteException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Failed to communicate with Infest API Server!\n\n"
                                + "Please verify that Infest API Server is currently turned on and your network connection is working.\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Remote Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } catch (java.lang.NullPointerException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Infest API Server give blank response!\n\n"
                                + "Please verify that Infest API Server is currently ON & LISTENING (not in IDLE mode).\n"
                                + "If Infest API Server is OFF, try to restart this program if problem after turning ON Infest API Server.\n"
                                + "Infest Program will now switched to MAINTENANCE MODE.",
                                "INFEST: Blank Response from Server", javax.swing.JOptionPane.ERROR_MESSAGE);
                        signOut();
                    } 
                    break;
                case FINANCIAL_STATEMENT:
                    break;
                default:
                    break;
            }
            readAll(card);
            loadFields(card);
        }
    }
    
    protected void openDocumentation() {
        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, 
            "Documentation of this program is not yet available. Please check on further release.\n\nThank you!",
            "INFEST: Documentation", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected void displayPopupMenu(java.awt.event.MouseEvent evt) {
        if(evt.isPopupTrigger()) {
            mainFrame.imagePanelPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
    
    protected void saveImageToFile() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        java.io.File file = null;
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG image", "jpg"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG image", "png"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("GIF image", "gif"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG image", "jpg"));
        fileChooser.setDialogTitle("Save Menu Image");
        fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(mainFrame) == javax.swing.JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            java.awt.image.BufferedImage bufferedImage = null;
            java.io.ByteArrayInputStream byteArrayInputStream = new java.io.ByteArrayInputStream(currentImageData);
            try {
                bufferedImage = javax.imageio.ImageIO.read(byteArrayInputStream);
                try {
                    String type = "";
                    if(org.apache.commons.io.FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jpg")) type = "jpg";
                    if(org.apache.commons.io.FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("jpeg")) type = "jpg";
                    if(org.apache.commons.io.FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("png")) type = "png";
                    if(org.apache.commons.io.FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("gif")) type = "gif";
                    if(type.equals("")) {
                        type = "jpg";
                        file = new java.io.File(file.getParentFile(), org.apache.commons.io.FilenameUtils.getBaseName(file.getName())+".jpg");
                    }
                    javax.imageio.ImageIO.write(bufferedImage, type, file);
                } catch (java.io.IOException ex) {
                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Error parsing image file!\n\n"
                            + "Please verify that the menu has an image.",
                            "INFEST: Menu Image", javax.swing.JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        byteArrayInputStream.close();
                    } catch (java.io.IOException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame,
                                "Failed to closed file stream. Please contact Infest Developer if you are seeing this problem.",
                                "INFEST: Menu Image", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (java.io.IOException ex) {
                System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Error parsing image data!\n\n"
                        + "Please verify that the menu has an image.",
                        "INFEST: Menu Image", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
        if (file != null) javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame,
                "Menu Image succesfuly saved to " + file.getAbsolutePath() + ".",
                "INFEST: Menu Image", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected void browseFile() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("All image files", "jpg", "jpeg", "png", "gif"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPEG image", "jpg", "jpeg"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG image", "png"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("GIF image", "gif"));
        fileChooser.setDialogTitle("Please pick a Menu Image");
        fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        byte[] imageData;
        if (fileChooser.showOpenDialog(mainFrame) == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.awt.image.BufferedImage bufferedImage = null;
            java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
            try {
                bufferedImage = javax.imageio.ImageIO.read(fileChooser.getSelectedFile());
                try {
                    byteArrayOutputStream = new java.io.ByteArrayOutputStream();
                    javax.imageio.ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
                    imageData = byteArrayOutputStream.toByteArray();
                } catch (java.io.IOException ex) {
                    imageData = null;
                    System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                    javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Error parsing image file!\n\n"
                            + "Please verify that the file that you choose is an image file.",
                            "INFEST: Menu Image", javax.swing.JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        byteArrayOutputStream.close();
                    } catch (java.io.IOException ex) {
                        System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                        javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame,
                                "Failed to closed file stream. Please contact Infest Developer if you are seeing this problem.",
                                "INFEST: Menu Image", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (java.io.IOException ex) {
                imageData = null;
                System.err.println("[INFEST] " +  getNowTime() + ": " + ex);
                javax.swing.JOptionPane.showMessageDialog((activeFrame == ViewFrame.MAIN) ? mainFrame : signInFrame, "Error parsing image file!\n\n"
                        + "Please verify that the file that you choose is an image file.",
                        "INFEST: Menu Image", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } else {
            imageData = null;
        }
        displayImage(imageData);
    }
    
}
