package garabusatti;

import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class MotoThread extends Thread {
    
    private GaraFrame frame;
    private final JProgressBar barra;      
    private final JLabel lblNome;         
    private final String nomeMoto;      
    private boolean arrivato;
    
    // ========== COSTRUTTORE ==========

    public MotoThread(GaraFrame frame,JProgressBar barra, JLabel lblNome, String nomeMoto) {
        this.frame = frame;
        this.barra = barra;
        this.lblNome = lblNome;
        this.nomeMoto = nomeMoto;
        this.arrivato = false;
        this.setName("Thread-" + nomeMoto);
    }
    
    // ========== METODI GET/SET ==========
    
    public boolean isArrivato() {
        return arrivato;
    }
    
    // ========== METODI PUBBLICI ==========
    
    @Override
    public void run() {
        // Velocità casuale tra 5 e 15 millisecondi
        int velocita = 5 + (int)(Math.random() * 10);
        
        // Fai correre la moto da 0 a 100%
        for (int posizione = 0; posizione <= 100; posizione++) {
            
            // Aggiorna la barra
            barra.setValue(posizione);
            
            // Se arriva al 100 ha vinto 
            if (posizione == 100) {
                arrivato = true;
                lblNome.setText(nomeMoto + " - ARRIVATO! 🏆");
            }
            
            try {
                // Aspetta un po', varia leggermente la velocità
                int pausa = velocita + (int)(Math.random() * 5) - 2;
                Thread.sleep(pausa);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
