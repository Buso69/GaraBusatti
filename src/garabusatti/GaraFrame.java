package garabusatti;

/**
 * Finestra gara - SEMPLICISSIMA
 * 5 moto = 5 Thread
 * Ogni Thread fa avanzare una barra fino al 100%
 */
public class GaraFrame extends javax.swing.JFrame {
    
    // Array dei 5 thread (uno per ogni moto)
    private MotoThread[] threads = new MotoThread[5];
    private boolean garaIniziata = false;
    private int motoScelta = 0; // quale moto hai scelto (0=Honda, 1=Kawasaki, ecc)
    
    // Nomi delle moto
    private String[] nomiMoto = {"Honda", "Kawasaki", "KTM", "Suzuki", "Yamaha"};
    
    // Immagini delle moto (piccole, per le label)
    private javax.swing.ImageIcon[] icone = new javax.swing.ImageIcon[5];

    // Costruttore con scelta moto
    public GaraFrame(int motoScelta) {
        this.motoScelta = motoScelta;
        initComponents();
        caricaImmagini();
        impostaLabels();
    }
    
    // Costruttore senza scelta (per compatibilità)
    public GaraFrame() {
        this(0); // default Honda
    }
    
    /**
     * Carica le immagini delle moto (se esistono)
     */
    private void caricaImmagini() {
        try {
            for (int i = 0; i < 5; i++) {
                java.net.URL imgURL = getClass().getResource("/immagini/" + nomiMoto[i] + ".png");
                if (imgURL != null) {
                    // Ridimensiona l'immagine a 40x25 per le label
                    java.awt.Image img = new javax.swing.ImageIcon(imgURL).getImage();
                    java.awt.Image scaledImg = img.getScaledInstance(40, 25, java.awt.Image.SCALE_SMOOTH);
                    icone[i] = new javax.swing.ImageIcon(scaledImg);
                }
            }
        } catch (Exception ex) {
            // Se le immagini non ci sono, usa solo il testo
        }
    }
    
    /**
     * Imposta le label con immagini + freccia TU sulla moto scelta
     */
    private void impostaLabels() {
        javax.swing.JLabel[] labels = {lblMoto1, lblMoto2, lblMoto3, lblMoto4, lblMoto5};
        
        for (int i = 0; i < 5; i++) {
            String testo = nomiMoto[i];
            
            // Aggiungi freccia "TU" sulla moto scelta
            if (i == motoScelta) {
                testo = "▼ TU ▼  " + testo;
            }
            
            // Imposta testo + immagine
            labels[i].setText(testo);
            if (icone[i] != null) {
                labels[i].setIcon(icone[i]);
            }
        }
    }

    /**
     * Bottone AVVIA GARA - crea e avvia i 5 thread
     */
    private void btnAvviaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvviaActionPerformed
        
        if (garaIniziata) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gara già in corso!");
            return;
        }
        
        garaIniziata = true;
        btnAvvia.setEnabled(false);
        
        // Crea i 5 thread (uno per ogni moto)
        threads[0] = new MotoThread(barMoto1, lblMoto1, nomiMoto[0]);
        threads[1] = new MotoThread(barMoto2, lblMoto2, nomiMoto[1]);
        threads[2] = new MotoThread(barMoto3, lblMoto3, nomiMoto[2]);
        threads[3] = new MotoThread(barMoto4, lblMoto4, nomiMoto[3]);
        threads[4] = new MotoThread(barMoto5, lblMoto5, nomiMoto[4]);
        
        // Avvia tutti i 5 thread contemporaneamente!
        for (int i = 0; i < 5; i++) {
            threads[i].start();
        }
        
        // Thread separato che controlla chi vince
        new Thread(new Runnable() {
            public void run() {
                controllaVincitore();
            }
        }).start();
        
    }//GEN-LAST:event_btnAvviaActionPerformed

    /**
     * Bottone RESET - resetta tutto
     */
    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        
        // Interrompi i thread
        if (threads[0] != null) {
            for (int i = 0; i < 5; i++) {
                threads[i].interrupt();
            }
        }
        
        // Resetta le barre
        barMoto1.setValue(0);
        barMoto2.setValue(0);
        barMoto3.setValue(0);
        barMoto4.setValue(0);
        barMoto5.setValue(0);
        
        // Resetta le etichette (con freccia TU sulla moto scelta)
        impostaLabels();
        
        garaIniziata = false;
        btnAvvia.setEnabled(true);
        
    }//GEN-LAST:event_btnResetActionPerformed

    /**
     * Controlla continuamente quale moto arriva prima
     */
    private void controllaVincitore() {
        while (true) {
            // Controlla ogni thread
            for (int i = 0; i < 5; i++) {
                if (threads[i].isArrivato()) {
                    // Qualcuno ha vinto!
                    String vincitore = threads[i].getName().replace("Thread-", "");
                    
                    // Messaggio speciale se hai vinto TU
                    String msg;
                    if (i == motoScelta) {
                        msg = "🏆🏆🏆 HAI VINTO! 🏆🏆🏆\n\nLa tua " + vincitore + " è arrivata prima!";
                    } else {
                        msg = "🏁 Ha vinto: " + vincitore + " 🏁\n\nLa tua " + nomiMoto[motoScelta] + " non ce l'ha fatta...";
                    }
                    
                    javax.swing.JOptionPane.showMessageDialog(null, msg);
                    return; // esce dal loop
                }
            }
            
            // Aspetta un po' prima di ricontrollare
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblMoto1 = new javax.swing.JLabel();
        barMoto1 = new javax.swing.JProgressBar();
        lblMoto2 = new javax.swing.JLabel();
        barMoto2 = new javax.swing.JProgressBar();
        lblMoto3 = new javax.swing.JLabel();
        barMoto3 = new javax.swing.JProgressBar();
        lblMoto4 = new javax.swing.JLabel();
        barMoto4 = new javax.swing.JProgressBar();
        lblMoto5 = new javax.swing.JLabel();
        barMoto5 = new javax.swing.JProgressBar();
        btnAvvia = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gara Moto - Thread");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("🏍 GARA MOTO CON THREAD 🏍");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Gara in Corso"));

        lblMoto1.setText("🏍 Honda");

        barMoto1.setStringPainted(true);

        lblMoto2.setText("🏍 Kawasaki");

        barMoto2.setStringPainted(true);

        lblMoto3.setText("🏍 KTM");

        barMoto3.setStringPainted(true);

        lblMoto4.setText("🏍 Suzuki");

        barMoto4.setStringPainted(true);

        lblMoto5.setText("🏍 Yamaha");

        barMoto5.setStringPainted(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblMoto1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barMoto1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblMoto2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barMoto2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblMoto3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barMoto3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblMoto4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barMoto4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblMoto5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barMoto5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(barMoto1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMoto1, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(barMoto2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMoto2, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(barMoto3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMoto3, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(barMoto4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMoto4, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(barMoto5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMoto5, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAvvia.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAvvia.setText("▶ AVVIA GARA");
        btnAvvia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvviaActionPerformed(evt);
            }
        });

        btnReset.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReset.setText("🔄 RESET");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAvvia, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAvvia, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GaraFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GaraFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GaraFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GaraFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GaraFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barMoto1;
    private javax.swing.JProgressBar barMoto2;
    private javax.swing.JProgressBar barMoto3;
    private javax.swing.JProgressBar barMoto4;
    private javax.swing.JProgressBar barMoto5;
    private javax.swing.JButton btnAvvia;
    private javax.swing.JButton btnReset;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblMoto1;
    private javax.swing.JLabel lblMoto2;
    private javax.swing.JLabel lblMoto3;
    private javax.swing.JLabel lblMoto4;
    private javax.swing.JLabel lblMoto5;
    // End of variables declaration//GEN-END:variables
}
