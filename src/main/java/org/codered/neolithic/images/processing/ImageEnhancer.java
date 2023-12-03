package org.codered.neolithic.images.processing;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;

public class ImageEnhancer {

    public static BufferedImage enhanceImage(BufferedImage image) {
        return sharpenImage(enhanceContrast(image));
    }

    private static BufferedImage enhanceContrast(BufferedImage originalImage) {
        RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null); // You can adjust these values
        BufferedImage contrastEnhancedImage = rescaleOp.filter(originalImage, null);
        return contrastEnhancedImage;
    }

    private static BufferedImage sharpenImage(BufferedImage originalImage) {
        float[] sharpenMatrix = {
                0.0f, -1.0f, 0.0f,
                -1.0f, 5.0f, -1.0f,
                0.0f, -1.0f, 0.0f
        };
        BufferedImage sharpenedImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                originalImage.getType());
        ConvolveOp convolveOp = new ConvolveOp(new Kernel(3, 3, sharpenMatrix));
        convolveOp.filter(originalImage, sharpenedImage);
        return sharpenedImage;
    }
}