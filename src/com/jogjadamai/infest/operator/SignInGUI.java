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
 * <h1>class <code>SignInGUI</code></h1>
 * <p><code>SignInGUI</code> is <code>javax.swing.JFrame</code> class defining
 * the Login Graphical User Interface of the application.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Adam Afandi for Infest.</i></b></p>
 * 
 * @author Adam Afandi
 * @version 2017.03.10.0001
 */
public final class SignInGUI extends javax.swing.JFrame implements Runnable {

    public SignInGUI() {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        initialiseComponents();
    }
    
    private void initialiseComponents() {

        mouseDownCompCoords = null;
        layeredPane = new javax.swing.JLayeredPane();
        passwordFieldBackground = new javax.swing.JLabel();
        usernameFieldBackground = new javax.swing.JLabel();
        usernameIcon = new javax.swing.JLabel();
        passwordIcon = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        signInButton = new javax.swing.JButton();
        infestLogo = new javax.swing.JLabel();
        clientLabel = new javax.swing.JLabel();
        exitButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("INFEST: Operator Authentication");
        setAlwaysOnTop(true);
        setMaximumSize(new java.awt.Dimension(302, 450));
        setMinimumSize(new java.awt.Dimension(302, 450));
        setName("authenticationFrame"); // NOI18N
        setUndecorated(true);
        setOpacity(1.0F);
        setPreferredSize(new java.awt.Dimension(302, 450));
        setResizable(false);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 302, 450, 50, 50));
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/InfestIcon.png")).getImage());
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });

        layeredPane.setDoubleBuffered(true);
        layeredPane.setMaximumSize(new java.awt.Dimension(302, 238));
        layeredPane.setMinimumSize(new java.awt.Dimension(302, 238));
        layeredPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        passwordFieldBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/TextFieldBackground.png"))); // NOI18N
        passwordFieldBackground.setToolTipText("Password");
        passwordFieldBackground.setDoubleBuffered(true);
        layeredPane.add(passwordFieldBackground, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, -1, -1));

        usernameFieldBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/TextFieldBackground.png"))); // NOI18N
        usernameFieldBackground.setToolTipText("Username");
        usernameFieldBackground.setDoubleBuffered(true);
        layeredPane.add(usernameFieldBackground, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        usernameIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/UsernameIcon.png"))); // NOI18N
        usernameIcon.setToolTipText("Username");
        usernameIcon.setDoubleBuffered(true);
        layeredPane.setLayer(usernameIcon, javax.swing.JLayeredPane.PALETTE_LAYER);
        layeredPane.add(usernameIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 20, 40));

        passwordIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/PasswordIcon.png"))); // NOI18N
        passwordIcon.setToolTipText("Password");
        passwordIcon.setDoubleBuffered(true);
        layeredPane.setLayer(passwordIcon, javax.swing.JLayeredPane.PALETTE_LAYER);
        layeredPane.add(passwordIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        usernameField.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        usernameField.setForeground(new java.awt.Color(0x651112));
        usernameField.setToolTipText("Type your username.");
        usernameField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        usernameField.setDoubleBuffered(true);
        usernameField.setMaximumSize(new java.awt.Dimension(250, 25));
        usernameField.setMinimumSize(new java.awt.Dimension(250, 25));
        usernameField.setName(""); // NOI18N
        usernameField.setOpaque(false);
        usernameField.setPreferredSize(new java.awt.Dimension(250, 25));
        usernameField.addActionListener(this::usernameFieldActionPerformed);
        layeredPane.setLayer(usernameField, javax.swing.JLayeredPane.PALETTE_LAYER);
        layeredPane.add(usernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 7, 250, 25));

        passwordField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        passwordField.setForeground(new java.awt.Color(0x651112));
        passwordField.setToolTipText("Type your password.");
        passwordField.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        passwordField.setDoubleBuffered(true);
        passwordField.setOpaque(false);
        passwordField.addActionListener(this::passwordFieldActionPerformed);
        layeredPane.setLayer(passwordField, javax.swing.JLayeredPane.PALETTE_LAYER);
        layeredPane.add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 57, 250, 25));

        signInButton.setBackground(new java.awt.Color(0x651112));
        signInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/SignInButton.png"))); // NOI18N
        signInButton.setBorder(null);
        signInButton.setDoubleBuffered(true);
        signInButton.setMaximumSize(new java.awt.Dimension(120, 40));
        signInButton.setMinimumSize(new java.awt.Dimension(120, 40));
        signInButton.setPreferredSize(new java.awt.Dimension(120, 40));
        signInButton.addActionListener(this::signInButtonActionPerformed);

        infestLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/LoginViewLogo.png"))); // NOI18N
        infestLogo.setToolTipText("INFEST");
        infestLogo.setDoubleBuffered(true);

        clientLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/OperatorLabel.png"))); // NOI18N
        clientLabel.setToolTipText("Operator Panel");
        clientLabel.setDoubleBuffered(true);
        clientLabel.setMaximumSize(new java.awt.Dimension(302, 20));
        clientLabel.setMinimumSize(new java.awt.Dimension(302, 20));
        clientLabel.setPreferredSize(new java.awt.Dimension(302, 20));

        exitButton.setToolTipText("Exit Infest.");
        exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/jogjadamai/infest/assets/ExitButton.png"))); // NOI18N
        exitButton.setBorder(null);
        exitButton.setDoubleBuffered(true);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setMaximumSize(new java.awt.Dimension(20, 20));
        exitButton.setMinimumSize(new java.awt.Dimension(20, 20));
        exitButton.setOpaque(false);
        exitButton.setPreferredSize(new java.awt.Dimension(20, 20));
        exitButton.setRequestFocusEnabled(false);
        exitButton.addActionListener(this::exitButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(signInButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(91, 91, 91))
            .addGroup(layout.createSequentialGroup()
                .addGap(111, 111, 111)
                .addComponent(infestLogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(clientLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(layeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infestLogo)
                    .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(clientLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(signInButton)
                .addGap(120, 120, 120))
        );

        pack();
        
        usernameField.requestFocusInWindow();
    }                       

    private void formMousePressed(java.awt.event.MouseEvent evt) {
        this.mouseDownCompCoords = evt.getPoint();
    }                                 

    private void formMouseDragged(java.awt.event.MouseEvent evt) {
        java.awt.Point currCoords = evt.getLocationOnScreen();
        this.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
    }                                 

    private void formMouseReleased(java.awt.event.MouseEvent evt) {
        mouseDownCompCoords = null;
    }                                  

    private void signInButtonActionPerformed(java.awt.event.ActionEvent evt) {
        operator.signIn();
    }  
    
    private void usernameFieldActionPerformed(java.awt.event.ActionEvent evt) {
        operator.signIn();
    }  
    
    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {
        operator.signIn();
    }  
    
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) { 
        operator.shutdown(0);
    }
    
    // Variables declaration                  
    private java.awt.Point mouseDownCompCoords;   
    private javax.swing.JLabel clientLabel;   
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel infestLogo;
    private javax.swing.JLayeredPane layeredPane;
    private javax.swing.JButton signInButton;
    protected javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordFieldBackground;
    private javax.swing.JLabel passwordIcon;
    protected javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameFieldBackground;
    private javax.swing.JLabel usernameIcon;
    private com.jogjadamai.infest.operator.Operator operator;
    // End of variables declaration  

    @Override
    public void run() {
        operator = com.jogjadamai.infest.operator.Operator.getInstance();
        getContentPane().setBackground(new java.awt.Color(0x651112));
        setLocationRelativeTo(null);
    }
    
}

