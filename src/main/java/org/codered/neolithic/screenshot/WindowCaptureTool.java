package org.codered.neolithic.screenshot;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.codered.neolithic.images.ImageConversionUtility;
import org.codered.neolithic.Neolithic;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

/**
 * Tool for capturing and processing a selected window area.
 */
public class WindowCaptureTool implements NativeKeyListener {

    private BufferedImage screenCapture;
    private boolean captureRequested = false;
    private Point startPoint;
    private Point endPoint;
    private JDialog previewDialog;
    private JFrame frame;

    /**
     * Constructor initializes the frame and key listener.
     */
    public WindowCaptureTool() {
        frame = Neolithic.getInstance().getFrame();
        if (frame == null) {
            frame = new JFrame("Window Capture Tool");
        }
        registerKeyListener();
        setupCaptureFrame();
    }

    /**
     * Entry point of the application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WindowCaptureTool::new);
    }

    /**
     * Registers a global key listener to listen for capture commands.
     */
    private void registerKeyListener() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Error registering native hook: " + e.getMessage());
            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        boolean ctrlPressed = (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0;
        if (ctrlPressed && e.getKeyCode() == NativeKeyEvent.VC_SHIFT) {
            SwingUtilities.invokeLater(this::toggleCapture);
        }
    }

    /**
     * Sets up the main capture frame with necessary event listeners.
     */
    private void setupCaptureFrame() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setOpacity(0.0f);
        frame.setAlwaysOnTop(true);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancelCapture();
                }
            }
        });

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (captureRequested) {
                    startPoint = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (captureRequested) {
                    captureArea(e.getPoint());
                }
            }
        });

        frame.setVisible(true);
    }

    /**
     * Toggles the screen capture process.
     */
    private void toggleCapture() {
        if (!captureRequested) {
            startCapture();
        } else {
            completeCapture();
        }
    }

    /**
     * Starts the screen capture process.
     */
    private void startCapture() {
        try {
            Robot robot = new Robot();
            screenCapture = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            frame.setOpacity(.05f);
            captureRequested = true;
            showPreviewDialog();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a preview dialog for the captured area.
     */
    private void showPreviewDialog() {
        previewDialog = new JDialog(frame, "Preview");
        previewDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        previewDialog.setSize(400, 400);
        previewDialog.setLayout(new BorderLayout());
        previewDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cancelCapture();
            }
        });

        JPanel previewPanel = new JPanel(new BorderLayout());
        previewDialog.add(previewPanel, BorderLayout.CENTER);

        JButton acceptButton = new JButton("Accept");
        acceptButton.addActionListener(e -> completeCapture());
        previewDialog.add(acceptButton, BorderLayout.SOUTH);

        previewDialog.setLocationRelativeTo(frame);
        previewDialog.setVisible(true);
    }

    /**
     * Captures the selected area of the screen.
     *
     * @param endPoint The end point of the capture area.
     */
    private void captureArea(Point endPoint) {
        this.endPoint = endPoint;
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int width = Math.abs(endPoint.x - startPoint.x);
        int height = Math.abs(endPoint.y - startPoint.y);

        updatePreview(x, y, width, height); // Update the preview
    }

    /**
     * Updates the preview dialog with the captured image.
     *
     * @param x      The x coordinate of the capture area.
     * @param y      The y coordinate of the capture area.
     * @param width  The width of the capture area.
     * @param height The height of the capture area.
     */
    private void updatePreview(int x, int y, int width, int height) {
        if (previewDialog != null) {
            try {
                BufferedImage previewImage = screenCapture.getSubimage(x, y, width, height);
                JLabel previewLabel = new JLabel(new ImageIcon(previewImage));
                JPanel previewPanel = (JPanel) previewDialog.getContentPane().getComponent(0);
                previewDialog.setSize(width + 20, height + 20);
                previewPanel.removeAll();
                previewPanel.add(previewLabel);
                previewDialog.revalidate();
            } catch (RasterFormatException e) {
                cancelCapture();
            }
        }
    }

    /**
     * Cancels the current capture process.
     */
    private void cancelCapture() {
        captureRequested = false;
        closePreviewDialog();
        frame.setOpacity(0.0F);
    }

    /**
     * Completes the capture and processes the selected area.
     */
    private void completeCapture() {
        captureWindow(startPoint.x, startPoint.y, Math.abs(endPoint.x - startPoint.x), Math.abs(endPoint.y - startPoint.y));
        System.out.println("Capture accepted!");
    }

    /**
     * Captures the selected window area and starts text conversion.
     *
     * @param x      The x coordinate of the window.
     * @param y      The y coordinate of the window.
     * @param width  The width of the window.
     * @param height The height of the window.
     */
    private void captureWindow(int x, int y, int width, int height) {
        try {
            BufferedImage windowCapture = screenCapture.getSubimage(x, y, width, height);

            new ImageConversionUtility().convertImageToText(windowCapture);
            cancelCapture(); // Reset capture states
            frame.setOpacity(0.0F); // Reset frame opacity
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Closes the preview dialog.
     */
    private void closePreviewDialog() {
        if (previewDialog != null) {
            previewDialog.dispose();
        }
    }
}
