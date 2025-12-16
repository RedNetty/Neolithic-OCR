package org.codered.neolithic.images.conversion;

import java.awt.image.BufferedImage;

public interface ImageConverter {
    String convertToText(BufferedImage image) throws Exception;
}