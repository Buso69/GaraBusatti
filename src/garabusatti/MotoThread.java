package garabusatti;

import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class MotoThread extends Thread {
    
    private GaraFrame frame;
    private final int indice;
    private final JProgressBar barra;      
    private final JLabel lblNome;         
    private final String nomeMoto;      
    private boolean arrivato;
    
    // ========== COSTRUTTORE ==========

    public MotoThread(GaraFrame frame, int indice, JProgressBar barra, JLabel lblNome, String nomeMoto) {
        this.frame = frame;
        this.indice = indice;
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
        int velocita = 5 + (int)(Math.random() * 10);
        
        for (int posizione = 0; posizione <= 100; posizione++) {
            
            barra.setValue(posizione);
            
            if (posizione == 100) {
                arrivato = true;
                lblNome.setText(nomeMoto + " - ARRIVATO! 🏆");
                frame.controllaVincitore(nomeMoto, indice);
            }
            
            try {
                int pausa = velocita + (int)(Math.random() * 5) - 2;
                Thread.sleep(pausa);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
