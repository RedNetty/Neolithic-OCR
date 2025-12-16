package org.codered.neolithic;

import org.codered.neolithic.screenshot.WindowCaptureTool;
import org.codered.neolithic.utils.ConfigReader;

import javax.swing.*;

/**
 * Main class for the Neolithic AI Tool.
 */
public class Neolithic {
    private static Neolithic instance;
    private final JFrame frame;
    private static ConfigReader configReader;

    /**
     * Constructor for Neolithic. Initializes the main frame.
     *
     * @param frame The main JFrame for the application.
     */
    public Neolithic(JFrame frame) {
        Neolithic.instance = this;
        configReader = new ConfigReader();
        this.frame = frame;
    }

    /**
     * Retrieves the singleton instance of Neolithic.
     *
     * @return The singleton instance of Neolithic.
     */
    public static Neolithic getInstance() {
        return instance;
    }

    /**
     * Entry point of the application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Initialize the Neolithic instance with a new JFrame
        new Neolithic(new JFrame("Neolithic AI Tool"));

        // Attempt to create and use a WindowCaptureTool instance
        try {
            new WindowCaptureTool();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the main frame of the application.
     *
     * @return The JFrame used by the application.
     */
    public JFrame getFrame() {
        return frame;
    }

    public static ConfigReader getConfigReader() {
        return configReader;
    }
}
