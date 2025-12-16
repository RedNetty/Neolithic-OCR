package org.codered.neolithic.images.ui;

import org.codered.neolithic.images.conversion.TesseractConverter;
import org.codered.neolithic.openai.AIRequest;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ConversionDialog extends JDialog {
    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 700;

    private final JFrame parentFrame;
    private JTextArea instructionsArea;
    private JTextArea textArea;
    private BufferedImage bufferedImage;

    public ConversionDialog(JFrame parentFrame) {
        super(parentFrame, "Converted Text", true);
        this.parentFrame = parentFrame;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parentFrame);
    }

    public void displayConvertedText(String text, BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        instructionsArea = createInstructionsArea();
        textArea = createTextArea(text);
        JButton acceptButton = createAcceptButton();
        JButton refreshButton = createRefreshButton();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(acceptButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(instructionsArea), BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private JTextArea createInstructionsArea() {
        JTextArea instructionsArea = new JTextArea();
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setEditable(true);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("Enter AI Instructions (example: Solve this math problem)");
        titledBorder.setTitleColor(Color.WHITE);
        instructionsArea.setBorder(titledBorder);
        return instructionsArea;
    }

    private JTextArea createTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Proofread Your Converted Text");
        titledBorder.setTitleColor(Color.WHITE);
        textArea.setBorder(titledBorder);
        return textArea;
    }

    private JButton createAcceptButton() {
        JButton acceptButton = new JButton("Send to AI");
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String editedText = textArea.getText();
                String userInstructions = instructionsArea.getText();
                new AIRequest(userInstructions, editedText).sendRequest();
                dispose();
            }
        });
        return acceptButton;
    }

    private JButton createRefreshButton() {
        JButton refreshButton = new JButton("Refresh Text");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String converted = new TesseractConverter(".\\tessdata")
                            .convertToText(bufferedImage);
                    textArea.setText(converted);
                } catch (Exception ex) {
                    showErrorDialog("Error: " + ex.getMessage());
                }
            }
        });
        return refreshButton;
    }

    public void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(parentFrame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}