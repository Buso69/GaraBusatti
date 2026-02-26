package garabusatti;


public class GaraBusatti {
    
    public static void main(String[] args) {
        // Mostra il menu della scelta della moto da tifare
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuFrame().setVisible(true);
            }
        });
    }
}
