package garabusatti;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Schermata di selezione moto.
 * Mostra le 5 moto reali come PNG. Il giocatore sceglie la sua.
 */
public class SelezioneFrame extends JFrame {

    /** {nomeFile, nomeVisualizzato, colore sfondo card} */
    public static final Object[][] MOTO_DEFS = {
        {"Honda",    "Honda CRF",      new Color(200, 30,  30)},
        {"Kawasaki", "Kawasaki KX",    new Color(30,  160, 40)},
        {"KTM",      "KTM EXC",        new Color(210, 100, 20)},
        {"Suzuki",   "Suzuki RM",      new Color(220, 195, 20)},
        {"Yamaha",   "Yamaha YZF",     new Color(20,  60,  200)},
    };

    private int selected = 0;
    private final JPanel[] cards = new JPanel[MOTO_DEFS.length];
    // Immagini precaricate
    private static final Image[] SPRITES = new Image[MOTO_DEFS.length];

    static {
        for (int i = 0; i < MOTO_DEFS.length; i++) {
            String nome = (String) MOTO_DEFS[i][0];
            SPRITES[i] = SpriteLoader.loadScaled("/immagini/" + nome + ".png", 220, 130);
        }
    }

    public SelezioneFrame() {
        setTitle("Scegli la tua Moto");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(null);
        root.setBackground(new Color(12, 12, 28));
        setContentPane(root);

        // Titolo
        JLabel title = new JLabel("SCEGLI LA TUA MOTO", SwingConstants.CENTER);
        title.setBounds(0, 18, 1024, 50);
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(Color.YELLOW);
        root.add(title);

        JLabel sub = new JLabel("La tua moto sarà indicata con la freccia  ▼ TU  durante la gara", SwingConstants.CENTER);
        sub.setBounds(0, 66, 1024, 24);
        sub.setFont(new Font("Arial", Font.ITALIC, 14));
        sub.setForeground(new Color(180, 180, 180));
        root.add(sub);

        // Cards — griglia 1×5 orizzontale
        int cardW = 180, cardH = 310;
        int startX = (1024 - (MOTO_DEFS.length * cardW + (MOTO_DEFS.length-1)*14)) / 2;
        int cardY  = 105;

        for (int i = 0; i < MOTO_DEFS.length; i++) {
            final int idx = i;
            final Color accent = (Color) MOTO_DEFS[i][2];

            cards[i] = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean sel = (idx == selected);

                    // Sfondo card
                    g2.setColor(sel ? accent.darker().darker() : new Color(22, 22, 48));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                    // Bordo
                    g2.setColor(sel ? Color.YELLOW : new Color(55, 55, 95));
                    g2.setStroke(new BasicStroke(sel ? 3 : 1));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 14, 14);

                    // Immagine moto PNG
                    Image spr = SPRITES[idx];
                    if (spr != null) {
                        int iw = 170, ih = 105;
                        int ix = (getWidth() - iw) / 2;
                        int iy = 38;
                        g2.drawImage(spr, ix, iy, iw, ih, this);
                    } else {
                        // Fallback: rettangolo colorato con nome
                        g2.setColor(accent.darker());
                        g2.fillRoundRect(15, 45, getWidth()-30, 110, 8, 8);
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Arial", Font.BOLD, 14));
                        g2.drawString("🏍 Immagine", 30, 105);
                        g2.drawString("non trovata", 30, 122);
                    }

                    // Striscia colorata brand
                    g2.setColor(accent);
                    g2.fillRoundRect(12, 158, getWidth()-24, 6, 4, 4);

                    // Nome brand
                    g2.setFont(new Font("Arial", Font.BOLD, 16));
                    g2.setColor(sel ? Color.YELLOW : Color.WHITE);
                    String nome = (String) MOTO_DEFS[idx][1];
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(nome, (getWidth()-fm.stringWidth(nome))/2, 188);

                    // Badge selezionato
                    if (sel) {
                        g2.setColor(new Color(255, 220, 0, 220));
                        g2.fillRoundRect(20, 206, getWidth()-40, 28, 8, 8);
                        g2.setColor(new Color(20, 20, 20));
                        g2.setFont(new Font("Arial", Font.BOLD, 13));
                        String badge = "✓  SELEZIONATO";
                        fm = g2.getFontMetrics();
                        g2.drawString(badge, (getWidth()-fm.stringWidth(badge))/2, 225);
                    } else {
                        g2.setColor(new Color(55, 55, 80));
                        g2.fillRoundRect(20, 206, getWidth()-40, 28, 8, 8);
                        g2.setColor(new Color(140, 140, 140));
                        g2.setFont(new Font("Arial", Font.BOLD, 12));
                        String lbl = "Clicca per scegliere";
                        fm = g2.getFontMetrics();
                        g2.drawString(lbl, (getWidth()-fm.stringWidth(lbl))/2, 225);
                    }

                    // Freccia TU se selezionata
                    if (sel) {
                        int cx = getWidth() / 2;
                        g2.setColor(Color.YELLOW);
                        g2.setFont(new Font("Arial", Font.BOLD, 13));
                        g2.drawString("▼ TU", cx-18, 26);
                    }
                }
            };
            cards[i].setOpaque(false);
            cards[i].setBounds(startX + i * (cardW + 14), cardY, cardW, cardH);
            cards[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cards[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    selected = idx;
                    for (JPanel c : cards) c.repaint();
                }
            });
            root.add(cards[i]);
        }

        // Pulsanti
        JButton back = new JButton("◀  Indietro");
        back.setBounds(312, 438, 160, 44);
        back.setFont(new Font("Arial", Font.BOLD, 15));
        back.addActionListener(e -> { new JFrameAvvia().setVisible(true); dispose(); });
        root.add(back);

        JButton avvia = new JButton("AVVIA GARA  ▶");
        avvia.setBounds(552, 438, 200, 44);
        avvia.setFont(new Font("Arial", Font.BOLD, 18));
        avvia.setBackground(new Color(210, 50, 30));
        avvia.setForeground(Color.WHITE);
        avvia.setFocusPainted(false);
        avvia.setBorderPainted(false);
        avvia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avvia.addActionListener(e -> { new GaraFrame(selected).setVisible(true); dispose(); });
        root.add(avvia);

        setSize(1024, 530);
        setLocationRelativeTo(null);
    }
}
