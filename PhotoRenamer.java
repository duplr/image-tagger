package photo_renamer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/** A GUI driven application for exploring, viewing, and tagging images on system. */
public class PhotoRenamer {

    /** The directory PhotoRenamers are launched from. */
    private static final String root = System.getProperty("user.dir") + "/";
    /** The main JFrame of the application. */
    JFrame mainFrame;
    /** The PhotoRenameLogger used for logging. */
    PhotoRenameLogger photoRenameLogger;
    /** The TagManager used for managing the database of Tags. */
    TagManager tagManager;
    /** The ImageHistoryManager used for managing the history database. */
    ImageHistoryManager historyManager;
    /** The JLabel used display the working directory. */
    JLabel dirLabel;
    /** The JPanel displaying the directory contents. */
    JPanel dirPanel;

    /**
     * Construct a PhotoRenamer application instance.
     *
     * @throws IOException            an IOException exception
     * @throws ClassNotFoundException a ClassNotFoundException exception
     */
    private PhotoRenamer() throws IOException, ClassNotFoundException {
        // Instantiate the central components for the application.
        this.mainFrame = new JFrame("Photo Renamer Pro 2017");
        this.photoRenameLogger = PhotoRenameLogger.getInstance();
        this.tagManager = new TagManager(root + "tags.txt");
        this.historyManager = new ImageHistoryManager(root + "history.txt");
        this.dirPanel = new JPanel();
        this.dirLabel = new JLabel("Open a directory of images...");
        dirLabel.setBorder(new EmptyBorder(6, 12, 6, 0));

        // Create buttons for executing top level application functions and add them to a JPanel.
        JButton modifyTagsButton = new ModifyTagsButton("Modify Tags", tagManager);
        JButton openDirButton = new OpenDirButton("Open Directory", this);
        JButton exit = new JButton("Exit");
        exit.addActionListener(press -> System.exit(0));
        JButton viewLogButton = new JButton("View Log");
        viewLogButton.addActionListener(press -> {
            try {
                Desktop.getDesktop().open(new File(photoRenameLogger.logPath));
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(new JFrame(), "Failed to open log file!");
                e1.printStackTrace();
            }
        });
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(openDirButton);
        buttonsPanel.add(modifyTagsButton);
        buttonsPanel.add(viewLogButton);
        buttonsPanel.add(exit);

        // Collect all central components in the JFrame of this PhotoRenamer.
        mainFrame.add(dirPanel, BorderLayout.CENTER);
        mainFrame.add(buttonsPanel, BorderLayout.SOUTH);
        mainFrame.add(dirLabel, BorderLayout.NORTH);
        mainFrame.pack();
    }

    /**
     * Execute the main program.
     *
     * @param args a String array of arguments
     */
    public static void main(String[] args) {
        try {
            new PhotoRenamer().mainFrame.setVisible(true);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Failed to launch application!");
            e.printStackTrace();
        }
    }
}
