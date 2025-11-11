package com.nhom.restaurant.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static final Map<String, ImageIcon> cache = new HashMap<>();
    public static ImageIcon getImage(String path, int width, int height) {
        String cacheKey = path + "_" + width + "_" + height;

        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        try {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                BufferedImage img = ImageIO.read(imgFile);
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaledImg);

                cache.put(cacheKey, icon);
                return icon;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}