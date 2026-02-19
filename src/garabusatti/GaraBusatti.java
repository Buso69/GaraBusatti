package garabusatti;

/**
 * Classe principale - avvia il programma
 */
public class GaraBusatti {
    
    public static void main(String[] args) {
        // Crea e mostra il MENU DI SELEZIONE
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuFrame().setVisible(true);
            }
        });
    }
}
