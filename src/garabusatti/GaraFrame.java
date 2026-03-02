package garabusatti;

public class GaraFrame extends javax.swing.JFrame {
    
    private MotoThread[] threads;
    private boolean garaIniziata;
    private int motoScelta;
    private String[] nomiMoto;
    private javax.swing.ImageIcon[] icone;
    
    public GaraFrame(int motoScelta) {
        this.threads = new MotoThread[5];
        this.garaIniziata = false;
        this.motoScelta = motoScelta;
        this.nomiMoto = new String[]{"Honda", "Kawasaki", "KTM", "Suzuki", "Yamaha"};
        this.icone = new javax.swing.ImageIcon[5];
        
        initComponents();
        caricaImmagini();
        impostaLabels();
    }
    
   
    // Aggiunge una riga alla cronaca (chiamabile anche dai thread)
    public void aggiungiCronaca(String messaggio) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            txtCronaca.append(messaggio + "\n");
            txtCronaca.setCaretPosition(txtCronaca.getDocument().getLength());
        });
    }
    
    private void btnAvviaActionPerformed(java.awt.event.ActionEvent evt) {
        if (garaIniziata) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gara già in corso!");
            return;
        }
        
        garaIniziata = true;
        btnAvvia.setEnabled(false);
        txtCronaca.setText("");
        aggiungiCronaca("🚦 GARA INIZIATA!");
        
        threads[0] = new MotoThread(this, 0, barMoto1, lblMoto1, nomiMoto[0]);
        threads[1] = new MotoThread(this, 1, barMoto2, lblMoto2, nomiMoto[1]);
        threads[2] = new MotoThread(this, 2, barMoto3, lblMoto3, nomiMoto[2]);
        threads[3] = new MotoThread(this, 3, barMoto4, lblMoto4, nomiMoto[3]);
        threads[4] = new MotoThread(this, 4, barMoto5, lblMoto5, nomiMoto[4]);
        
        for (int i = 0; i < 5; i++) {
            threads[i].start();
        }
    }

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {
        if (threads[0] != null) {
            for (int i = 0; i < 5; i++) {
                threads[i].interrupt();
            }
        }
        
        barMoto1.setValue(0);
        barMoto2.setValue(0);
        barMoto3.setValue(0);
        barMoto4.setValue(0);
        barMoto5.setValue(0);
        
        impostaLabels();
        txtCronaca.setText("🔄 Gara resettata. Premi AVVIA per una nuova gara.\n");
        
        garaIniziata = false;
        btnAvvia.setEnabled(true);
    }

    private void caricaImmagini() {
        for (int i = 0; i < 5; i++) {
            java.net.URL imgURL = getClass().getResource("/immagini/" + nomiMoto[i] + ".png");
            if (imgURL != null) {
                java.awt.Image img = new javax.swing.ImageIcon(imgURL).getImage();
                java.awt.Image scaledImg = img.getScaledInstance(40, 25, java.awt.Image.SCALE_SMOOTH);
                icone[i] = new javax.swing.ImageIcon(scaledImg);
            }
        }
    }
    
    private void impostaLabels() {
        javax.swing.JLabel[] labels = {lblMoto1, lblMoto2, lblMoto3, lblMoto4, lblMoto5};
        
        for (int i = 0; i < 5; i++) {
            String testo = nomiMoto[i];
            if (i == motoScelta) {
                testo = "▼ TU ▼  " + testo;
            }
            labels[i].setText(testo);
            if (icone[i] != null) {
                labels[i].setIcon(icone[i]);
            }
        }
    }
    
    public void controllaVincitore(String nomeMoto, int indiceMoto) {
        if (garaIniziata) {
            garaIniziata = false;
            
            String msg = (indiceMoto == motoScelta) ?
                "🏆🏆🏆 HAI VINTO! 🏆🏆🏆\n\nLa tua " + nomeMoto + " è arrivata prima!" : 
                "🏁 Ha vinto: " + nomeMoto + " 🏁\n\nLa tua moto non ce l'ha fatta...";
            
            aggiungiCronaca("🏆 VINCITORE: " + nomeMoto + "!");
            
            javax.swing.JOptionPane.showMessageDialog(this, msg);
        } else {
            aggiungiCronaca("🏁 " + nomeMoto + " ha tagliato il traguardo.");
        }
    }
    
    @SuppressWarnings("unchecked")
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
        
        // --- PANNELLO CRONACA ---
        jPanelCronaca = new javax.swing.JPanel();
        txtCronaca = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gara Moto - Thread");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24));
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

        // --- SETUP CRONACA ---
        jPanelCronaca.setBorder(javax.swing.BorderFactory.createTitledBorder("📋 Cronaca di Gara"));
        txtCronaca.setEditable(false);
        txtCronaca.setFont(new java.awt.Font("Monospaced", 0, 12));
        txtCronaca.setRows(6);
        txtCronaca.setText("In attesa della gara...\n");
        jScrollPane1.setViewportView(txtCronaca);
        
        javax.swing.GroupLayout jPanelCronacaLayout = new javax.swing.GroupLayout(jPanelCronaca);
        jPanelCronaca.setLayout(jPanelCronacaLayout);
        jPanelCronacaLayout.setHorizontalGroup(
            jPanelCronacaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCronacaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelCronacaLayout.setVerticalGroup(
            jPanelCronacaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCronacaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnAvvia.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnAvvia.setText("▶ AVVIA GARA");
        btnAvvia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAvviaActionPerformed(evt);
            }
        });

        btnReset.setFont(new java.awt.Font("Segoe UI", 1, 14));
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
                    .addComponent(jPanelCronaca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelCronaca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAvvia, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private javax.swing.JProgressBar barMoto1;
    private javax.swing.JProgressBar barMoto2;
    private javax.swing.JProgressBar barMoto3;
    private javax.swing.JProgressBar barMoto4;
    private javax.swing.JProgressBar barMoto5;
    private javax.swing.JButton btnAvvia;
    private javax.swing.JButton btnReset;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelCronaca;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtCronaca;
    private javax.swing.JLabel lblMoto1;
    private javax.swing.JLabel lblMoto2;
    private javax.swing.JLabel lblMoto3;
    private javax.swing.JLabel lblMoto4;
    private javax.swing.JLabel lblMoto5;
}
