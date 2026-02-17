package garabusatti;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Utility per caricare immagini senza mai lanciare NullPointerException.
 * Usa getResourceAsStream (NON getResource) → zero NPE.
 * Cache interna per non ricaricare lo stesso file più volte.
 */
public class SpriteLoader {

    private static final Logger LOG = Logger.getLogger(SpriteLoader.class.getName());
    private static final Map<String, Image> CACHE = new HashMap<>();

    /** Carica un'immagine dal classpath o dal disco. Ritorna null se non trovata. */
    public static Image loadImage(String classpathPath) {
        if (CACHE.containsKey(classpathPath)) return CACHE.get(classpathPath);

        // 1. Classpath (es. src/immagini/Honda.png in NetBeans)
        String[] cpPaths = {
            classpathPath,
            "/immagini" + classpathPath,
            "/garabusatti" + classpathPath
        };
        for (String p : cpPaths) {
            try (InputStream is = SpriteLoader.class.getResourceAsStream(p)) {
                if (is != null) {
                    Image img = ImageIO.read(is);
                    CACHE.put(classpathPath, img);
                    return img;
                }
            } catch (IOException ignored) {}
        }

        // 2. Disco 
        String fileName = classpathPath.replaceAll(".*/", "");
        String[] diskPaths = {
            fileName,
            "immagini/" + fileName,
            "src/immagini/" + fileName
        };
        for (String p : diskPaths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    Image img = ImageIO.read(f);
                    CACHE.put(classpathPath, img);
                    return img;
                } catch (IOException ignored) {}
            }
        }

     
        CACHE.put(classpathPath, null);
        return null;
    }

    /** Carica e scala immediatamente a w×h */
    public static Image loadScaled(String classpathPath, int w, int h) {
        Image img = loadImage(classpathPath);
        if (img == null) return null;
        return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }
}
