package garabusatti;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Finestra gara.
 *
 * FIX VELOCITÀ: player e AI hanno la stessa distribuzione di velocità base
 * (MIN_SPEED..MAX_SPEED), generata randomicamente per tutti.
 * Il player NON è più svantaggiato.
 *
 * FIX THREAD: ogni moto ha il suo Thread daemon che aggiorna worldX.
 * Gli update alla GUI avvengono SEMPRE via SwingUtilities.invokeLater.
 *
 * SPRITE PNG: le immagini Honda/Kawasaki/KTM/Suzuki/Yamaha vengono caricate
 * tramite SpriteLoader (nessun NPE). Se mancano → fallback colori.
 */
public class GaraFrame extends JFrame {

    // ──────────────────────────────────────────────────────────────────────────
    //  DATI MOTO (condivisi tra thread e panel via volatile)
    // ──────────────────────────────────────────────────────────────────────────
    static class MotoData {
        final String  brand;       // es. "Honda"
        final String  label;       // nome visualizzato
        final boolean isPlayer;
        volatile double worldX    = 0;
        volatile double speed;
        volatile double jumpY     = 0;
        volatile double jumpVY    = 0;
        volatile boolean jumping  = false;
        volatile boolean finished = false;
        int lastRampKey = -99;
        Image sprite;              // immagine PNG scalata per il gioco
        Image spriteTilted;        // non pre-ruotiamo, usiamo AffineTransform inline

        MotoData(String brand, String label, boolean isPlayer, double speed) {
            this.brand = brand; this.label = label;
            this.isPlayer = isPlayer; this.speed = speed;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  PANEL DI GIOCO
    // ──────────────────────────────────────────────────────────────────────────
    static class GaraPanel extends JPanel {

        // --- Dimensioni / costanti ---
        static final int W           = 1024;
        static final int H           = 600;
        static final int GROUND_Y    = 490;
        static final int TRACK_LEN   = 12000;
        static final int LAP_LEN     = 3000;
        static final int PLAYER_SCR  = 200;   // x schermo fissa per il player
        static final int N_MOTOS     = 5;

        // Sprite in-game width/height
        static final int SPR_W = 120;
        static final int SPR_H =  75;

        // Velocità: TUTTI (player incluso) partono in questo range
        static final double MIN_SPEED = 4.0;
        static final double MAX_SPEED = 7.5;

        // Rampe: {lapX, width, height, id}
        static final int[][] RAMPS = {
            {380,  80, 45, 0},
            {820,  95, 68, 1},
            {1300, 82, 52, 2},
            {1850, 108,80, 3},
            {2450, 85, 55, 4},
        };

        // --- Stato ---
        final MotoData[] motos;
        final int playerIdx;
        final int playerColorIdx;
        final Random rng = new Random();

        volatile boolean raceOver = false;
        volatile String  winMsg   = "";

        final List<int[]> dust = Collections.synchronizedList(new ArrayList<>());
        final double[] cloudX = {60, 200, 410, 640, 830, 980};
        final double[] cloudY = {45, 100, 65, 125, 55, 88};

        Thread[] raceThreads;
        volatile boolean running = true;
        JButton btnNew, btnMenu;

        // ── Costruttore ───────────────────────────────────────────────────────
        GaraPanel(int playerColorIdx) {
            this.playerColorIdx = playerColorIdx;
            setPreferredSize(new Dimension(W, H));
            setBackground(new Color(120, 195, 255));
            setLayout(null);

            // Costruisci le 5 moto
            // Player = brand scelto, = gli altri 4 brand
            String playerBrand = (String) SelezioneFrame.MOTO_DEFS[playerColorIdx][0];
            motos = new MotoData[N_MOTOS];

            int aiSlot = 0;
            for (int i = 0; i < SelezioneFrame.MOTO_DEFS.length; i++) {
                String brand = (String) SelezioneFrame.MOTO_DEFS[i][0];
                String lbl   = (String) SelezioneFrame.MOTO_DEFS[i][1];
                boolean isP  = brand.equals(playerBrand);

                // Velocità: STESSA distribuzione per tutti → player non svantaggiato
                double spd = MIN_SPEED + rng.nextDouble() * (MAX_SPEED - MIN_SPEED);

                int slot = isP ? N_MOTOS - 1 : aiSlot++;
                motos[slot] = new MotoData(brand, lbl, isP, spd);
                motos[slot].worldX = -(slot * 100); // posizioni di partenza sfalsate
            }
            // playerIdx è sempre N_MOTOS-1 = 4
            playerIdx = N_MOTOS - 1;

            // Carica sprite PNG per ogni moto
            for (MotoData m : motos) {
                m.sprite = SpriteLoader.loadScaled("/immagini/" + m.brand + ".png", SPR_W, SPR_H);
            }

            // Bottoni risultato (nascosti finché la gara non finisce)
            btnNew = new JButton("🔄  Nuova Gara");
            btnNew.setBounds(W/2-165, H/2+50, 150, 42);
            btnNew.setFont(new Font("Arial", Font.BOLD, 14));
            btnNew.setVisible(false);
            btnNew.addActionListener(e -> restart());
            add(btnNew);

            btnMenu = new JButton("🏠  Menu");
            btnMenu.setBounds(W/2+15, H/2+50, 150, 42);
            btnMenu.setFont(new Font("Arial", Font.BOLD, 14));
            btnMenu.setVisible(false);
            btnMenu.addActionListener(e -> goMenu());
            add(btnMenu);

            startRaceThreads();
        }

        // ── Thread gara ───────────────────────────────────────────────────────
        void startRaceThreads() {
            raceThreads = new Thread[N_MOTOS];
            for (int i = 0; i < N_MOTOS; i++) {
                final int idx = i;
                raceThreads[i] = new Thread(() -> raceLoop(idx),
                                             "MotoThread-" + motos[idx].brand);
                raceThreads[i].setDaemon(true);
                raceThreads[i].start();
            }
        }

        void raceLoop(int idx) {
            MotoData m = motos[idx];
            try {
                // Piccolo delay di partenza in base alla posizione
                Thread.sleep(idx * 80L);

                while (running && !raceOver && !m.finished) {

                    // ── Aggiornamento velocità ────────────────────────────────
                    // Tutti (AI e player) hanno la stessa logica di variazione.
                    // Nessuno è svantaggiato.
                    double variation = (rng.nextDouble() - 0.50) * 0.4;
                    m.speed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, m.speed + variation));

                    // ── Avanzamento ────────────────────────────────────────────
                    m.worldX += m.speed;

                    // ── Logica salti ───────────────────────────────────────────
                    if (!m.jumping) {
                        int lapX   = (int)(m.worldX % LAP_LEN);
                        int lapNum = (int)(m.worldX / LAP_LEN);
                        for (int[] r : RAMPS) {
                            int launchX = r[0] + r[1] / 2;
                            int rampKey = r[3] + lapNum * 10;
                            if (Math.abs(lapX - launchX) <= (int)(m.speed + 1)
                                    && m.lastRampKey != rampKey) {
                                m.lastRampKey = rampKey;
                                m.jumping     = true;
                                m.jumpY       = 0;
                                m.jumpVY      = -(6.5 + r[2] * 0.10);
                                if (m.isPlayer) spawnDust(PLAYER_SCR + 20, GROUND_Y, 12);
                                break;
                            }
                        }
                    }
                    if (m.jumping) {
                        m.jumpVY += 0.38;   // gravità
                        m.jumpY  += m.jumpVY;
                        if (m.jumpY >= 0) {
                            if (m.isPlayer) spawnDust(PLAYER_SCR + 15, GROUND_Y - 4, 16);
                            m.jumpY  = 0;
                            m.jumpVY = 0;
                            m.jumping = false;
                        }
                    }

                    // ── Controllo arrivo ───────────────────────────────────────
                    if (m.worldX >= TRACK_LEN && !raceOver) {
                        m.finished = true;
                        raceOver   = true;
                        running    = false;
                        winMsg = m.isPlayer ? "🏆  HAI VINTO!  🏆"
                                            : "🏁  Ha vinto: " + m.label;
                        SwingUtilities.invokeLater(() -> {
                            btnNew.setVisible(true);
                            btnMenu.setVisible(true);
                            repaint();
                        });
                    }

                    // Richiedi repaint dal EDT
                    SwingUtilities.invokeLater(this::repaint);

                    // ~50 FPS
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        void spawnDust(int x, int y, int n) {
            for (int i = 0; i < n; i++) {
                dust.add(new int[]{
                    x + rng.nextInt(36)-18, y,
                    18 + rng.nextInt(12),
                    rng.nextInt(5)-2, -(1+rng.nextInt(3))
                });
            }
        }

        void restart() {
            running = false;
            SwingUtilities.getWindowAncestor(this).dispose();
            new GaraFrame(playerColorIdx).setVisible(true);
        }

        void goMenu() {
            running = false;
            SwingUtilities.getWindowAncestor(this).dispose();
            new JFrameAvvia().setVisible(true);
        }

        // ══════════════════════════════════════════════════════════════════════
        //  RENDERING
        // ══════════════════════════════════════════════════════════════════════
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            double camX = motos[playerIdx].worldX - PLAYER_SCR;

            drawSky(g2);
            drawClouds(g2);
            drawMountains(g2, camX);
            drawGround(g2, camX);
            drawDust(g2);
            drawAllMotos(g2, camX);
            drawFinishLine(g2, camX);
            drawHUD(g2);
            if (raceOver) drawResult(g2);
        }

        // ── Cielo ─────────────────────────────────────────────────────────────
        void drawSky(Graphics2D g2) {
            g2.setPaint(new GradientPaint(0, 0, new Color(100,185,255),
                                          0, GROUND_Y-60, new Color(210,238,255)));
            g2.fillRect(0, 0, W, GROUND_Y);
        }

        // ── Nuvole ────────────────────────────────────────────────────────────
        void drawClouds(Graphics2D g2) {
            for (int i = 0; i < cloudX.length; i++) {
                cloudX[i] = (cloudX[i] - 0.12 + W + 120) % (W + 120) - 60;
                int cx = (int) cloudX[i], cy = (int) cloudY[i];
                g2.setColor(new Color(255,255,255,220));
                g2.fillOval(cx,    cy,    90, 38);
                g2.fillOval(cx+20, cy-20, 72, 40);
                g2.fillOval(cx+58, cy-10, 68, 34);
            }
        }

        // ── Montagne con parallasse ────────────────────────────────────────────
        void drawMountains(Graphics2D g2, double camX) {
            int[] mx = {0,160,320,480,640,800,960,1120};
            int[] mh = {90,130,65,115,155,75,120,95};
            int[] mw = {150,190,115,170,210,125,180,155};
            g2.setColor(new Color(165,135,195,110));
            for (int i = 0; i < mx.length; i++)
                drawTiledMountain(g2, mx[i], 360, mw[i], mh[i], camX*0.05, 1400);

            int[] mx2 = {50,220,400,580,760,940,1120};
            int[] mh2 = {50,75,40,65,85,42,70};
            int[] mw2 = {180,230,150,200,250,155,210};
            g2.setColor(new Color(70,140,60,190));
            for (int i = 0; i < mx2.length; i++)
                drawTiledMountain(g2, mx2[i], 430, mw2[i], mh2[i], camX*0.18, 1400);
        }

        void drawTiledMountain(Graphics2D g2, int bx, int by,
                                int w, int h, double off, int period) {
            double o = off % period;
            for (int k = -1; k <= 2; k++) {
                int cx = (int)(bx - o + k * period);
                if (cx+w/2 >= -20 && cx-w/2 <= W+20)
                    g2.fillPolygon(new int[]{cx-w/2, cx, cx+w/2},
                                   new int[]{by, by-h, by}, 3);
            }
        }

        // ── Suolo + rampe ──────────────────────────────────────────────────────
        void drawGround(Graphics2D g2, double camX) {
            // Terreno
            g2.setColor(new Color(155,90,40));
            g2.fillRect(0, GROUND_Y, W, H-GROUND_Y);
            // Pista
            g2.setPaint(new GradientPaint(0,GROUND_Y-5,new Color(210,148,75),
                                          0,GROUND_Y+10,new Color(185,120,55)));
            g2.fillRect(0, GROUND_Y-5, W, 16);
            // Segni pneumatici
            g2.setColor(new Color(160,100,45,130));
            g2.setStroke(new BasicStroke(2));
            double lo = camX % 80;
            for (int x = (int)(-lo%80)-80; x < W+80; x += 80)
                g2.drawLine(x, GROUND_Y+3, x+38, GROUND_Y+5);
            g2.setStroke(new BasicStroke(1));

            // Rampe
            int ls = (int)((camX-200)/LAP_LEN)-1;
            int le = (int)((camX+W+200)/LAP_LEN)+2;
            for (int lap = ls; lap <= le; lap++) {
                for (int[] r : RAMPS) {
                    int rwx = r[0] + lap*LAP_LEN;
                    int rw = r[1], rh = r[2];
                    int sx = (int)(rwx - camX);
                    if (sx > W+160 || sx+rw < -160) continue;

                    int[] px = {sx, sx+rw/2, sx+rw, sx+rw, sx};
                    int[] py = {GROUND_Y, GROUND_Y-rh, GROUND_Y, GROUND_Y+10, GROUND_Y+10};
                    g2.setColor(new Color(200,138,65));
                    g2.fillPolygon(px, py, 5);
                    int[] sx2 = {sx+4, sx+rw/2, sx+rw-4};
                    int[] sy2 = {GROUND_Y-2, GROUND_Y-rh+4, GROUND_Y-2};
                    g2.setColor(new Color(225,165,85));
                    g2.fillPolygon(sx2, sy2, 3);
                    g2.setColor(new Color(140,88,32));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(sx, GROUND_Y, sx+rw/2, GROUND_Y-rh);
                    g2.drawLine(sx+rw/2, GROUND_Y-rh, sx+rw, GROUND_Y);
                    g2.setStroke(new BasicStroke(1));
                    g2.setColor(new Color(255,230,100,210));
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.drawString("↑ JUMP", sx+rw/2-18, GROUND_Y-rh-6);
                    g2.setColor(new Color(255,60,60,170));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(sx+rw/2-6, GROUND_Y-rh+8, sx+rw/2+6, GROUND_Y-rh+8);
                    g2.setStroke(new BasicStroke(1));
                }
            }

            // Start line
            int slx = (int)(0 - camX);
            if (slx > -30 && slx < W+30) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(slx, GROUND_Y-10, slx, GROUND_Y+14);
                g2.setFont(new Font("Arial", Font.BOLD, 9));
                g2.drawString("START", slx-14, GROUND_Y-14);
                g2.setStroke(new BasicStroke(1));
            }
        }

        // ── Polvere ────────────────────────────────────────────────────────────
        void drawDust(Graphics2D g2) {
            synchronized (dust) {
                dust.removeIf(p -> p[2] <= 0);
                for (int[] p : dust) {
                    p[0] += p[3]; p[1] += p[4]; p[2]--;
                    g2.setColor(new Color(210,175,120, Math.min(220, p[2]*10)));
                    int sz = 2+p[2]/5;
                    g2.fillOval(p[0], p[1], sz, sz);
                }
            }
        }

        // ── Tutte le moto ──────────────────────────────────────────────────────
        void drawAllMotos(Graphics2D g2, double camX) {
            Integer[] order = {0,1,2,3,4};
            Arrays.sort(order, (a,b) -> Double.compare(motos[a].worldX, motos[b].worldX));
            for (int idx : order) {
                MotoData m = motos[idx];
                int sx = m.isPlayer
                    ? PLAYER_SCR
                    : (int)(m.worldX - camX);
                if (sx < -SPR_W-20 || sx > W+SPR_W+20) continue;

                int gY  = groundYAt(m.worldX);
                int motoY = gY + (int) m.jumpY - SPR_H + 5; // bottom della sprite al suolo

                drawMotoSprite(g2, m, sx, motoY);

                if (m.isPlayer) {
                    drawTuMarker(g2, sx + SPR_W/2, motoY - 10);
                } else {
                    // Etichetta AI piccola
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    g2.setColor(new Color(0,0,0,160));
                    g2.fillRoundRect(sx+SPR_W/2-22, motoY-16, 44, 14, 5, 5);
                    g2.setColor(Color.WHITE);
                    g2.drawString(m.brand, sx+SPR_W/2-20, motoY-5);
                }
            }
        }

        int groundYAt(double wx) {
            if (wx < 0) return GROUND_Y;
            int lapX = (int)(wx % LAP_LEN);
            for (int[] r : RAMPS) {
                int half = r[1]/2;
                if (lapX >= r[0] && lapX < r[0]+half) {
                    double t = (lapX-r[0]) / (double)half;
                    return GROUND_Y - (int)(t*r[2]);
                } else if (lapX >= r[0]+half && lapX < r[0]+r[1]) {
                    double t = (lapX-r[0]-half) / (double)half;
                    return GROUND_Y - (int)((1-t)*r[2]);
                }
            }
            return GROUND_Y;
        }

        // ── Disegna sprite PNG con tilt durante salto ──────────────────────────
        void drawMotoSprite(Graphics2D g2, MotoData m, int sx, int sy) {
            AffineTransform saved = g2.getTransform();

            // Tilt: -15° in salita, +12° in discesa
            if (m.jumping) {
                double deg = m.jumpVY < 0 ? -15 : (m.jumpVY > 2.5 ? 12 : 0);
                g2.rotate(Math.toRadians(deg), sx + SPR_W/2, sy + SPR_H/2);
            }

            if (m.sprite != null) {
                // Ombra sotto la moto
                if (!m.jumping) {
                    g2.setColor(new Color(0,0,0,40));
                    g2.fillOval(sx+8, sy+SPR_H-4, SPR_W-16, 10);
                }
                g2.drawImage(m.sprite, sx, sy, SPR_W, SPR_H, this);
            } else {
                // Fallback grafico se PNG mancante
                drawFallbackMoto(g2, sx, sy + SPR_H - 5, m);
            }

            g2.setTransform(saved);
        }

        /** Moto disegnata con Graphics2D se il PNG non c'è */
        void drawFallbackMoto(Graphics2D g2, int x, int groundY,  MotoData m) {
            Color body   = getColorForBrand(m.brand);
            Color accent = Color.WHITE;

            g2.setColor(new Color(0,0,0,40));
            if (!m.jumping) g2.fillOval(x+3, groundY-2, 62, 7);

            drawWheel(g2, x+2,  groundY-22);
            drawWheel(g2, x+46, groundY-22);

            int[] fkx = {x+11,x+22,x+38,x+48,x+50,x+38,x+18};
            int[] fky = {groundY-15,groundY-36,groundY-35,groundY-25,groundY-14,groundY-14,groundY-14};
            g2.setColor(body);
            g2.fillPolygon(fkx, fky, 7);

            g2.setColor(body.darker());
            g2.fillRoundRect(x+23, groundY-59, 19, 25, 6, 6);
            g2.setColor(body);
            g2.fillOval(x+22, groundY-73, 21, 18);

            g2.setFont(new Font("Arial", Font.BOLD, 8));
            g2.setColor(Color.WHITE);
            String lbl = m.brand.substring(0,Math.min(3,m.brand.length()));
            g2.drawString(lbl, x+23, groundY-58);
        }

        void drawWheel(Graphics2D g2, int cx, int cy) {
            g2.setColor(new Color(30,30,30));
            g2.fillOval(cx,cy,23,23);
            g2.setColor(new Color(165,165,165));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(cx+2,cy+2,19,19);
            g2.setColor(new Color(205,205,205));
            g2.fillOval(cx+7,cy+7,9,9);
            g2.setColor(new Color(140,140,140));
            g2.setStroke(new BasicStroke(1));
            int hx=cx+11, hy=cy+11;
            for (int a=0;a<6;a++){
                double rad=Math.toRadians(a*60);
                g2.drawLine(hx,hy,hx+(int)(9*Math.cos(rad)),hy+(int)(9*Math.sin(rad)));
            }
        }

        Color getColorForBrand(String brand) {
            switch(brand){
                case "Honda":    return new Color(200,30,30);
                case "Kawasaki": return new Color(30,160,40);
                case "KTM":      return new Color(210,100,20);
                case "Suzuki":   return new Color(220,195,20);
                case "Yamaha":   return new Color(20,60,200);
                default:         return Color.GRAY;
            }
        }

        void drawTuMarker(Graphics2D g2, int cx, int ty) {
            // Alone giallo
            g2.setColor(new Color(255,220,0,65));
            g2.fillOval(cx-20, ty-8, 40, 36);
            // Freccia
            g2.setColor(new Color(255,220,0));
            g2.fillRect(cx-4, ty, 8, 14);
            g2.fillPolygon(new int[]{cx-13,cx,cx+13},
                           new int[]{ty+14, ty+27, ty+14}, 3);
            // Testo TU
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            g2.setColor(Color.BLACK);
            g2.drawString("TU", cx-8, ty-1);
            g2.setColor(new Color(255,220,0));
            g2.drawString("TU", cx-9, ty-2);
        }

        // ── Traguardo ─────────────────────────────────────────────────────────
        void drawFinishLine(Graphics2D g2, double camX) {
            int fx = (int)(TRACK_LEN - camX);
            if (fx < -40 || fx > W+40) return;
            int th = H/12;
            for (int row=0; row<H/th+1; row++)
                for (int col=0; col<3; col++) {
                    g2.setColor((row+col)%2==0 ? Color.WHITE : Color.BLACK);
                    g2.fillRect(fx+col*12, row*th, 12, th);
                }
            g2.setColor(new Color(185,185,185));
            g2.setStroke(new BasicStroke(5));
            g2.drawLine(fx+18, 0, fx+18, GROUND_Y+14);
            g2.setColor(new Color(220,40,40));
            g2.fillRoundRect(fx-28, 28, 110, 32, 8, 8);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 17));
            g2.drawString("FINISH 🏁", fx-22, 50);
            g2.setStroke(new BasicStroke(1));
        }

        // ── HUD ───────────────────────────────────────────────────────────────
        void drawHUD(Graphics2D g2) {
            MotoData player = motos[playerIdx];
            double progress = Math.min(Math.max(player.worldX,0) / TRACK_LEN * 100, 100);
            int laps   = TRACK_LEN / LAP_LEN;
            int curLap = Math.min((int)(Math.max(player.worldX,0) / LAP_LEN)+1, laps);

            // Pannello HUD
            g2.setColor(new Color(0,0,0,160));
            g2.fillRoundRect(8, 8, 345, 72, 12, 12);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(String.format("Percorso: %.0f%%", progress), 18, 28);
            g2.drawString("Giro " + curLap + "/" + laps, 260, 28);

            // Barra progresso
            g2.setColor(new Color(45,45,45));
            g2.fillRoundRect(18, 36, 320, 18, 9, 9);
            int bw = (int)(320 * progress/100);
            if (bw > 0) {
                g2.setPaint(new GradientPaint(18,36,new Color(40,180,50), 18+bw,36,new Color(110,245,80)));
                g2.fillRoundRect(18, 36, bw, 18, 9, 9);
            }
            g2.setColor(new Color(255,255,255,90));
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(18, 36, 320, 18, 9, 9);

            // Puntini moto sulla barra
            for (MotoData m : motos) {
                if (m.worldX < 0) continue;
                int dotX = 18 + (int)(Math.min(m.worldX, TRACK_LEN)/TRACK_LEN*320);
                g2.setColor(m.isPlayer ? Color.YELLOW : getColorForBrand(m.brand));
                g2.fillOval(dotX-5, 32, 11, 11);
                if (m.isPlayer) {
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 7));
                    g2.drawString("TU", dotX-5, 41);
                }
            }

            // Posizione
            long ahead = Arrays.stream(motos)
                .filter(m -> !m.isPlayer && m.worldX > player.worldX).count();
            int pos = (int) ahead + 1;
            g2.setColor(new Color(0,0,0,160));
            g2.fillRoundRect(8, 88, 72, 44, 10, 10);
            g2.setColor(pos==1 ? Color.YELLOW : Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.drawString(pos+"°", 20, 121);

            // Velocità
            g2.setColor(new Color(0,0,0,160));
            g2.fillRoundRect(88, 88, 105, 44, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.drawString((int)(player.speed*18)+" km/h", 95, 104);
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.drawString("velocità", 100, 122);

            // Nome moto player
            g2.setColor(new Color(0,0,0,160));
            g2.fillRoundRect(200, 88, 150, 44, 10, 10);
            g2.setColor(getColorForBrand(player.brand));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(player.label, 210, 108);
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString("▼  TU", 216, 124);

            // Indicatore JUMP
            if (player.jumping) {
                g2.setColor(new Color(0,0,0,150));
                g2.fillRoundRect(W-130, 8, 122, 36, 10, 10);
                g2.setColor(Color.YELLOW);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.drawString("🏍 JUMP!", W-125, 32);
            }
        }

        // ── Schermata risultato ───────────────────────────────────────────────
        void drawResult(Graphics2D g2) {
            boolean won = motos[playerIdx].finished;
            g2.setColor(new Color(0,0,0,175));
            g2.fillRoundRect(W/2-240, H/2-85, 480, 130, 20, 20);
            g2.setColor(new Color(255,220,0,55));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(W/2-240, H/2-85, 480, 130, 20, 20);
            g2.setStroke(new BasicStroke(1));
            g2.setColor(won ? Color.YELLOW : new Color(255,130,130));
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(winMsg, (W-fm.stringWidth(winMsg))/2, H/2-32);
            String sub = won ? "Ottima gara! Vuoi ancora?" : "Ritenta!";
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 17));
            fm = g2.getFontMetrics();
            g2.drawString(sub, (W-fm.stringWidth(sub))/2, H/2+2);
        }

    } // fine GaraPanel

    // ──────────────────────────────────────────────────────────────────────────
    //  GaraFrame costruttore
    // ──────────────────────────────────────────────────────────────────────────
    public GaraFrame(int playerColorIdx) {
        setTitle("Gara Moto - In pista!");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new GaraPanel(playerColorIdx));
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GaraFrame(0).setVisible(true));
    }
}
