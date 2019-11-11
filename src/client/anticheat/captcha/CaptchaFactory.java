/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.anticheat.captcha;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Weber
 */
public class CaptchaFactory {

    private final static int IMAGE_WIDTH = 194;

    private final static int IMAGE_HEIGHT = 43;

    private final static int NUMBER_OF_CHARS = 6;

    private final static int SPACING = IMAGE_WIDTH / NUMBER_OF_CHARS;

    private final char[] CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();

    private final int BACKGROUND_POINTS_PER_COLOR = 80;

    private final int BACKGOUND_COLORS = 4;

    private final Random random = new Random();

    private final static CaptchaFactory INSTANCE = new CaptchaFactory();

    public static CaptchaFactory getInstance() {
        return INSTANCE;
    }

    public Captcha getCaptcha() {

        String answer = this.randomString(NUMBER_OF_CHARS);
        BufferedImage biImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2dImage = (Graphics2D) biImage.getGraphics();

        drawBackground(g2dImage);
        drawTexts(g2dImage, answer);

        ByteArrayOutputStream osImage = new ByteArrayOutputStream();
        try {
            ImageIO.write(biImage, "jpeg", osImage);
        } catch (IOException ex) {
            Logger.getLogger(CaptchaFactory.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return new Captcha(answer, osImage.toByteArray());
    }

    private void drawBackground(Graphics2D g2dImage) {
        g2dImage.setColor(Color.WHITE);
        g2dImage.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        for (int i = 0; i < BACKGOUND_COLORS; i++) {
            for (int j = 0; j < BACKGROUND_POINTS_PER_COLOR; j++) {
                g2dImage.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                int x = random.nextInt(IMAGE_WIDTH);
                int y = random.nextInt(IMAGE_HEIGHT);
                g2dImage.drawLine(x, y, x, y);
            }
        }
    }

    private void drawTexts(Graphics2D g2dImage, String text) {
        for (int i = 0; i < text.length(); i++) {
            String chr = text.substring(i, i + 1);
            int fontSize = IMAGE_HEIGHT * (6 + random.nextInt(1)) / 10;
            Font fntStyle1 = new Font("Arialbd", Font.BOLD, fontSize);
            Rectangle2D rect = fntStyle1.getStringBounds(chr, g2dImage.getFontRenderContext());
            g2dImage.setFont(fntStyle1);
            int x = SPACING / 3 + i * SPACING;
            int y = IMAGE_HEIGHT - ((Double) (rect.getMaxX() * 1)).intValue();
            g2dImage.setColor(new Color(random.nextInt(128), random.nextInt(128), random.nextInt(128)));
            AffineTransform orig = g2dImage.getTransform();
            double radius = (-1 * Math.PI / 4) + random.nextDouble() * (Math.PI / 2);
            g2dImage.rotate(radius, x, y);
            g2dImage.drawString(chr, x, y);
            if ((random.nextFloat() * 1.5 > 1.0) || chr.equals("6") || chr.equals("9")) {
                Stroke oriStorke = g2dImage.getStroke();
                g2dImage.setStroke(new BasicStroke(3));
                g2dImage.drawLine(
                        x - random.nextInt(5),
                        y + 3,
                        x + random.nextInt(5) + ((Double) rect.getWidth()).intValue(),
                        y + 3
                );
                g2dImage.setStroke(oriStorke);
            }
            g2dImage.setTransform(orig);
        }
    }

    private String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(CHARS[random.nextInt(CHARS.length - 1)]);
        }
        return builder.toString();
    }

}
