package com.example.SportsTracker.football.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/football/figure")
@RequiredArgsConstructor
public class FigureColorController {

    // Region pixel indices (pre-computed — flattened y*w+x)
    // Load from classpath resources
    @GetMapping(value = "/colored", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getColoredFigure(
            @RequestParam String primary,
            @RequestParam String secondary) throws Exception {

        // Load base image from resources
        ClassPathResource resource = new ClassPathResource("static/fan-figure-base.png");
        BufferedImage img = ImageIO.read(resource.getInputStream());
        int w = img.getWidth(), h = img.getHeight();

        // Parse hex colors
        int pr = hex(primary, 0), pg = hex(primary, 1), pb = hex(primary, 2);
        int sr = hex(secondary, 0), sg = hex(secondary, 1), sb = hex(secondary, 2);

        // Apply region masks (precomputed)
        applyMask(img, w, h, JERSEY_MASK, pr, pg, pb);
        applyMask(img, w, h, SOCK_MASK,   pr, pg, pb);
        applyMask(img, w, h, SHORTS_MASK, sr, sg, sb);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header("Cache-Control", "public, max-age=3600")
                .body(baos.toByteArray());
    }

    private void applyMask(BufferedImage img, int w, int h, boolean[][] mask, int r, int g, int b) {
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (mask[y][x]) {
                    int argb = img.getRGB(x, y);
                    int a = (argb >> 24) & 0xff;
                    img.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
                }
            }
        }
    }

    private static int hex(String color, int channel) {
        String c = color.replace("#", "");
        return Integer.parseInt(c.substring(channel*2, channel*2+2), 16);
    }

    // These would be loaded from files - see note below
    private static final boolean[][] JERSEY_MASK = loadMask("jersey");
    private static final boolean[][] SHORTS_MASK = loadMask("shorts");
    private static final boolean[][] SOCK_MASK   = loadMask("socks");

    private static boolean[][] loadMask(String name) { /* see below */ return null; }
}