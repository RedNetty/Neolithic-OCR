package org.codered.neolithic.images.conversion;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.codered.neolithic.images.processing.ConversionRefiner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TesseractConverter implements ImageConverter {
    private final ITesseract tesseract;

    public TesseractConverter(String dataPath) {
        tesseract = new Tesseract();
        tesseract.setDatapath(dataPath);
    }

    @Override
    public String convertToText(BufferedImage image) throws TesseractException, IOException {
        File tempFile = File.createTempFile("image", ".png");
        ImageIO.write(image, "png", tempFile);

        try {
            String result = tesseract.doOCR(tempFile);
            return new ConversionRefiner().refineConversion(result);
        } finally {
            tempFile.delete();
        }
    }
}