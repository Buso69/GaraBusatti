package garabusatti;


public class GaraBusatti {
    
    public static void main(String[] args) {
        // mostra il menu della scelta dela moto da tifare
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuFrame().setVisible(true);
            }
        });
    }
}
