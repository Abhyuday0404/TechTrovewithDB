package main.java.main;

import main.java.ui.MainFrame; // Import the MainFrame class
import javax.swing.SwingUtilities;

public class TechTroveCLI {

    public static void main(String[] args) {
        // Launch the Swing application
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
