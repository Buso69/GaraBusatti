package garabusatti;

import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class MotoThread extends Thread {
    
    private final JProgressBar barra;      
    private final JLabel lblNome;         
    private final String nomeMoto;      
    private boolean arrivato = false;      
    
    public MotoThread(JProgressBar barra, JLabel lblNome, String nomeMoto) {
        this.barra = barra;
        this.lblNome = lblNome;
        this.nomeMoto = nomeMoto;
        this.setName("Thread-" + nomeMoto); // nome del thread
    }
    
    public boolean isArrivato() {
        return arrivato;
    }
    
    @Override
    public void run() {
        // Velocità casuale tra 5 e 15
        int velocita = 5 + (int)(Math.random() * 10);
        
        // Fai correre la moto fino al 100%
        for (int posizione = 0; posizione <= 100; posizione++) {
            
            // Aggiorna la barra che scorre
            barra.setValue(posizione);
            
            // Se arriva al 100 ogicamnete hai vinto
            if (posizione == 100) {
                arrivato = true;
                lblNome.setText(nomeMoto + " - ARRIVATO! 🏆");
            }
            
            try {
                // Aspetta un po varia la velocita
                int pausa = velocita + (int)(Math.random() * 5) - 2;
                Thread.sleep(pausa);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
