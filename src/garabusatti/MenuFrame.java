package garabusatti;

/**
 * Menu iniziale - scegli quale moto tifare
 */
public class MenuFrame extends javax.swing.JFrame {

    public MenuFrame() {
        initComponents();
    }

    // Bottone Honda
    private void btnHondaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHondaActionPerformed
        new GaraFrame(0).setVisible(true); // 0 = Honda
        this.dispose();
    }//GEN-LAST:event_btnHondaActionPerformed

    // Bottone Kawasaki
    private void btnKawasakiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKawasakiActionPerformed
        new GaraFrame(1).setVisible(true); // 1 = Kawasaki
        this.dispose();
    }//GEN-LAST:event_btnKawasakiActionPerformed

    // Bottone KTM
    private void btnKTMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKTMActionPerformed
        new GaraFrame(2).setVisible(true); // 2 = KTM
        this.dispose();
    }//GEN-LAST:event_btnKTMActionPerformed

    // Bottone Suzuki
    private void btnSuzukiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuzukiActionPerformed
        new GaraFrame(3).setVisible(true); // 3 = Suzuki
        this.dispose();
    }//GEN-LAST:event_btnSuzukiActionPerformed

    // Bottone Yamaha
    private void btnYamahaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnYamahaActionPerformed
        new GaraFrame(4).setVisible(true); // 4 = Yamaha
        this.dispose();
    }//GEN-LAST:event_btnYamahaActionPerformed

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnHonda = new javax.swing.JButton();
        btnKawasaki = new javax.swing.JButton();
        btnKTM = new javax.swing.JButton();
        btnSuzuki = new javax.swing.JButton();
        btnYamaha = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Scegli la tua Moto");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("🏍 GARA MOTO 🏍");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Scegli quale moto tifare (tutte corrono con i Thread!)");

        btnHonda.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnHonda.setText("Honda");
        btnHonda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHondaActionPerformed(evt);
            }
        });

        btnKawasaki.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnKawasaki.setText("Kawasaki");
        btnKawasaki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKawasakiActionPerformed(evt);
            }
        });

        btnKTM.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnKTM.setText("KTM");
        btnKTM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKTMActionPerformed(evt);
            }
        });

        btnSuzuki.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnSuzuki.setText("Suzuki");
        btnSuzuki.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuzukiActionPerformed(evt);
            }
        });

        btnYamaha.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnYamaha.setText("Yamaha");
        btnYamaha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnYamahaActionPerformed(evt);
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
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnHonda, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnKawasaki, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnKTM, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 14, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSuzuki, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnYamaha, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnKTM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKawasaki, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHonda, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSuzuki, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(btnYamaha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHonda;
    private javax.swing.JButton btnKTM;
    private javax.swing.JButton btnKawasaki;
    private javax.swing.JButton btnSuzuki;
    private javax.swing.JButton btnYamaha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
