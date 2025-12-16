package org.codered.neolithic.images;

import org.codered.neolithic.Neolithic;
import org.codered.neolithic.images.conversion.ImageConverter;
import org.codered.neolithic.images.conversion.TesseractConverter;
import org.codered.neolithic.images.ui.ConversionDialog;

import java.awt.image.BufferedImage;

public class ImageConversionUtility {

    private final ImageConverter imageConverter;
    private final ConversionDialog conversionDialog;

    public ImageConversionUtility() {
        imageConverter = new TesseractConverter(".\\tessdata");
        conversionDialog = new ConversionDialog(Neolithic.getInstance().getFrame());
    }

    public void convertImageToText(BufferedImage bufferedImage) {
        try {
            String converted = imageConverter.convertToText(bufferedImage);
            conversionDialog.displayConvertedText(converted, bufferedImage);
        } catch (Exception e) {
            conversionDialog.showErrorDialog("Error: " + e.getMessage());
        }
    }
}