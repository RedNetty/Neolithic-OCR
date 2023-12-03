/**
 * The ImageConversion class handles the conversion of images to text using the Google Vision API.
 * It provides a graphical user interface for users to input AI instructions, review and edit the converted text,
 * and send the refined text to an AI service for further processing.
 */
package org.codered.neolithic.images;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;
import org.codered.neolithic.Neolithic;
import org.codered.neolithic.images.processing.ConversionRefiner;
import org.codered.neolithic.openai.AIRequest;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The ImageConversion class provides methods to convert images to text, display the converted text,
 * and facilitate user interaction for sending the text to an AI service.
 */
public class ImageConversion {
    private JFrame frame;

    /**
     * Constructor for the ImageConversion class.
     * Initializes the frame and triggers the image conversion process.
     *
     * @param bufferedImage The image to be converted.
     */
    public ImageConversion(BufferedImage bufferedImage) {
        frame = Neolithic.getInstance().getFrame();
        try {
            String converted = detectText(bufferedImage);
            displayConvertedText(converted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the converted text in a dialog with user interaction options.
     *
     * @param text The converted text to be displayed.
     */
    private void displayConvertedText(String text) {
        JDialog previewDialog = new JDialog(frame, "Converted Text");
        configureDialog(previewDialog);
        JTextArea instructionsArea = createInstructionsArea();
        JTextArea textArea = createTextArea(text);
        JButton acceptButton = createAcceptButton(previewDialog, textArea, instructionsArea);

        // Styling for JTextAreas and JButton
        instructionsArea.setBackground(Color.DARK_GRAY);
        instructionsArea.setForeground(Color.WHITE);
        textArea.setBackground(Color.DARK_GRAY);
        textArea.setForeground(Color.PINK);
        acceptButton.setBackground(Color.BLACK);
        acceptButton.setFont(new Font("Arial", Font.PLAIN, 14));
        acceptButton.setFocusPainted(false);
        acceptButton.setBorder(BorderFactory.createEmptyBorder());
        acceptButton.setContentAreaFilled(false);

        // Add components to the dialog
        previewDialog.add(new JScrollPane(instructionsArea), BorderLayout.NORTH);
        previewDialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
        previewDialog.add(acceptButton, BorderLayout.SOUTH);
        previewDialog.setVisible(true);
    }

    /**
     * Detects text in a given BufferedImage using the Google Vision API.
     *
     * @param image The image to be processed.
     * @return The detected text or an error message.
     * @throws Exception If an error occurs during text detection.
     */
    public String detectText(BufferedImage image) throws Exception {
        GoogleCredentials credentials = loadGoogleCredentials();
        List<AnnotateImageRequest> requests = buildImageRequestList(image, credentials);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(buildClientSettings(credentials))) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            return processResponses(response.getResponsesList());
        }
    }

    /**
     * Configures the main dialog window for displaying text.
     *
     * @param dialog The JDialog to be configured.
     */
    private void configureDialog(JDialog dialog) {
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 500);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(frame);
    }

    /**
     * Creates a JTextArea for user instructions.
     *
     * @return JTextArea for instructions.
     */
    private JTextArea createInstructionsArea() {
        JTextArea instructionsArea = new JTextArea();
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setEditable(true);

        TitledBorder titledBorder =  BorderFactory.createTitledBorder("Enter AI Instructions (example: Solve this math problem)");
        titledBorder.setTitleColor(Color.WHITE);
        instructionsArea.setBorder(titledBorder);
        return instructionsArea;
    }

    /**
     * Creates a JTextArea for displaying and editing the text.
     *
     * @param text The initial text to display.
     * @return JTextArea for text display and editing.
     */
    private JTextArea createTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Proofread Your Converted Text ");
        titledBorder.setTitleColor(Color.WHITE);
        textArea.setBorder(titledBorder);
        return textArea;
    }

    /**
     * Creates an accept button that captures user-edited text.
     *
     * @param dialog           The parent dialog to be closed on button press.
     * @param textArea         The JTextArea from which to capture text.
     * @param instructionsArea The JTextArea containing user instructions.
     * @return JButton configured with an action listener.
     */
    private JButton createAcceptButton(JDialog dialog, JTextArea textArea, JTextArea instructionsArea) {
        JButton acceptButton = new JButton("Send to AI");
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String editedText = textArea.getText();
                String userInstructions = instructionsArea.getText();
                new AIRequest(userInstructions, editedText).sendRequest();
                dialog.dispose();
            }
        });
        return acceptButton;
    }

    /**
     * Loads Google Credentials from a file.
     *
     * @return GoogleCredentials loaded from a file.
     * @throws FileNotFoundException If the credentials file is not found.
     */
    private GoogleCredentials loadGoogleCredentials() throws FileNotFoundException {
        try (FileInputStream serviceAccountStream = new FileInputStream("src/main/resources/service_account.json")) {
            return GoogleCredentials.fromStream(serviceAccountStream);
        } catch (IOException e) {
            throw new FileNotFoundException("Service account file not found: " + e.getMessage());
        }
    }

    /**
     * Builds a list of requests for the Google Vision API.
     *
     * @param image       The image to be processed.
     * @param credentials GoogleCredentials for accessing the API.
     * @return List of AnnotateImageRequest.
     * @throws Exception If an error occurs during image processing.
     */
    private List<AnnotateImageRequest> buildImageRequestList(BufferedImage image, GoogleCredentials credentials) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        ByteString byteString = ByteString.copyFrom(imageBytes);
        Image img = Image.newBuilder().setContent(byteString).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);
        return requests;
    }

    /**
     * Builds settings for the ImageAnnotatorClient.
     *
     * @param credentials GoogleCredentials for the client.
     * @return ImageAnnotatorSettings.
     * @throws IOException If an error occurs while creating client settings.
     */
    private ImageAnnotatorSettings buildClientSettings(GoogleCredentials credentials) throws IOException {
        return ImageAnnotatorSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
    }

    /**
     * Processes the responses from the Google Vision API.
     *
     * @param responses List of AnnotateImageResponse from the API.
     * @return The refined text or an error message.
     */
    private String processResponses(List<AnnotateImageResponse> responses) {
        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                return "Error: " + res.getError().getMessage();
            }
            return new ConversionRefiner().refineConversion(res.getFullTextAnnotation().getText());
        }
        return "";
    }
}
