package garabusatti;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class JFrameAvvia extends JFrame {

    public JFrameAvvia() {
        setTitle("Gara Moto - Menu");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel bg = new JPanel() {
            private final Image img = SpriteLoader.loadImage("/immagini/sfondo.jpg");
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                if (img != null) {
                    g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(10,10,25), 0, getHeight(), new Color(60,15,5)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(255,255,255,15));
                    for (int x = 0; x < getWidth(); x += 60) g2.drawLine(x,0,x,getHeight());
                    for (int y = 0; y < getHeight(); y += 60) g2.drawLine(0,y,getWidth(),y);
                }
            }
        };
        bg.setLayout(null);
        setContentPane(bg);

        JButton btn = new JButton("▶  SCEGLI MOTO E GAREGGIA");
        btn.setBounds(312, 370, 400, 60);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(new Color(210,50,30));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { new SelezioneFrame().setVisible(true); dispose(); });
        bg.add(btn);

        setSize(1024, 640);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JFrameAvvia().setVisible(true));
    }
}
