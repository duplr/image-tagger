package photo_renamer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/** A window for displaying and modifying a selected ImageFile. */
class ImageWindow {

    /** The main PhotoRenamer JFrame. */
    private JFrame mainFrame;
    /** The TagManager managing the database of Tags. */
    private TagManager tagManager;
    /** The ImageFileManager managing the selected ImageFile. */
    private ImageFileManager imageFileManager;
    /** The ArrayList of selected Tags in the window. */
    private ArrayList<Tag> selectedTags;
    /** The ArrayList of unselected Tags in the window. */
    private ArrayList<Tag> unselectedTags;
    /** The JScrollPane containing the selectable list of Tags. */
    private JScrollPane tagScroller;
    /** The file name to be potentially reverted to. */
    private String revertName;
    /** The selected ImageFile associated with this ImageWindow. */
    ImageFile selectedImage;
    /** The JFrame of this ImageWindow. */
    JFrame imageFrame;


    /**
     * Construct a window for viewing and modifying ImageFiles.
     *
     * @param mainApp       the PhotoRenamer application this window was launched from
     * @param selectedImage the selected ImageFile that this ImageWindow is for
     */
    ImageWindow(PhotoRenamer mainApp, ImageFile selectedImage) {
        this.mainFrame = mainApp.mainFrame;
        this.tagManager = mainApp.tagManager;
        this.selectedImage = selectedImage;

        this.imageFrame = new JFrame(selectedImage.name);
        this.selectedTags = new ArrayList<>();
        this.unselectedTags = new ArrayList<>();
        this.tagScroller = new JScrollPane();
        try {
            this.imageFileManager = new ImageFileManager(this.selectedImage, mainApp.photoRenameLogger,
                    mainApp.historyManager);
            this.renderImageWindow();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Failed to render image window!");
            e.printStackTrace();
        }
    }

    /** Render a window for the selected ImageFile. */
    private void renderImageWindow() throws IOException, ClassNotFoundException {
        // Begin constructing a window for the observed selected ImageFile.
        selectedImage.addObserver(imageFileManager);
        imageFrame.add(createDisplayPanel(), BorderLayout.NORTH);

        // Generate a scrollable JPanel of Tag checkboxes for the user to select from for tagging.
        JPanel tagListPanel = new JPanel();
        tagListPanel.setLayout(new BoxLayout(tagListPanel, BoxLayout.Y_AXIS));
        for (Tag tag : tagManager.tags) {
            createCheckbox(tag, tagListPanel);
        }
        tagScroller.setViewportView(tagListPanel);

        // Button for retagging the selected ImageFile with the appropriate Tags.
        JButton retagButton = new JButton("Apply Selected/Delete Unselected Tag(s)");
        retagButton.addActionListener(press -> {
            try {
                imageFileManager.applyTags(selectedTags);
                imageFileManager.deleteTags(unselectedTags);
                refreshWindows();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(new JFrame(), "Failed to retag image!");
                e1.printStackTrace();
            }
        });

        // Field to allow the user to add new Tags to the Tag database and apply it the selected ImageFile.
        JTextField newTagField = new JTextField("Enter a new tag for immediate retagging...", 10);
        newTagField.addActionListener(press -> {
            Tag newTag = new Tag(newTagField.getText());
            try {
                tagManager.addTag(newTag);
                imageFileManager.applyTags(new ArrayList<>(Collections.singletonList(newTag)));
                createCheckbox(newTag, tagListPanel);
                refreshWindows();
                newTagField.setText("");
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(new JFrame(), "Failed to add new tag!");
                e1.printStackTrace();
            }
        });

        // Button to open the containing directory of the selected image.
        JButton openLocationButton = new JButton("Open File Location");
        openLocationButton.addActionListener(press -> {
            try {
                Desktop.getDesktop().open(selectedImage.imgFile.getParentFile());
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(new JFrame(), "Failed to open file location!");
                e1.printStackTrace();
            }
        });

        // Collect the UI components the user interacts with to execute application functions.
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.add(newTagField);
        actionPanel.add(retagButton);
        createHistoryDropdown(actionPanel);
        actionPanel.add(openLocationButton);
        retagButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newTagField.setAlignmentX(Component.CENTER_ALIGNMENT);
        openLocationButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Compose the whole ImageWindow.
        imageFrame.add(tagScroller, BorderLayout.CENTER);
        imageFrame.add(actionPanel, BorderLayout.SOUTH);
        imageFrame.setSize(415, 500);
        imageFrame.setResizable(false);
        imageFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                selectedImage.deleteObservers();
            }
        });
        imageFrame.setVisible(true);
    }

    /** Prepare and return a JPanel for displaying the selected image. */
    private JPanel createDisplayPanel() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(selectedImage.imgFilePath));
        } catch (IOException eio) {
            JOptionPane.showMessageDialog(new JFrame(), "Failed to access image!");
            eio.printStackTrace();
        }
        assert img != null;
        ImageIcon display = new ImageIcon(img.getScaledInstance(400, 200, Image.SCALE_FAST));
        JLabel imageLabel = new JLabel(null, display, JLabel.CENTER);
        JPanel displayer = new JPanel();
        displayer.add(imageLabel);
        return displayer;
    }

    /**
     * Create a JCheckBox for a Tag.
     *
     * @param tag      the Tag the checkbox is being created for
     * @param tagPanel the JPanel the checkbox will be added to
     */
    private void createCheckbox(Tag tag, JPanel tagPanel) {
        JCheckBox box = new JCheckBox(tag.name);
        box.addItemListener(tick -> {
            int tagIndex = tagManager.tags.indexOf(tag);
            if (tagIndex != -1) {
                if (tick.getStateChange() == 1) {
                    selectedTags.add(tagManager.tags.get(tagIndex));
                    unselectedTags.remove(tag);
                } else {
                    selectedTags.remove(tag);
                    unselectedTags.add(tag);
                }
            }
        });
        if (selectedImage.tags.contains(tag)) {
            box.setSelected(true);
        }
        if (!box.isSelected()) {
            unselectedTags.add(tag);
        }
        tagPanel.add(box);
    }

    /**
     * Create the UI components necessary for allowing the user to revert names.
     *
     * @param actionPanel the JPanel to add the reversion UI elements to
     */
    private void createHistoryDropdown(JPanel actionPanel) {
        JLabel pastNameLabel = new JLabel("Filename History");
        JComboBox<Object> historyDropdown = new JComboBox<>(selectedImage.nameHistoryList.toArray());
        revertName = (String) historyDropdown.getSelectedItem();
        historyDropdown.addActionListener(select -> revertName = (String) historyDropdown.getSelectedItem());
        JButton revertButton = new JButton("Revert File");
        revertButton.addActionListener(press -> {
            try {
                ArrayList<Tag> revertAddTags = new ArrayList<>();
                ArrayList<Tag> revertDeleteTags = new ArrayList<>();
                int extBeginning = revertName.lastIndexOf(".");
                ArrayList<String> revertTagNames = new ArrayList<>(Arrays.asList
                        (revertName.substring(0, extBeginning).split(" @")));
                revertTagNames.remove(0);
                for (String tagName : revertTagNames) {
                    Tag revertTag = new Tag(tagName);
                    revertAddTags.add(revertTag);
                }
                for (Tag currentTag : selectedImage.tags) {
                    if (!revertAddTags.contains(currentTag)) {
                        revertDeleteTags.add(currentTag);
                    }
                }
                imageFileManager.applyTags(revertAddTags);
                imageFileManager.deleteTags(revertDeleteTags);
                refreshWindows();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(new JFrame(), "Failed to revert name!");
                e1.printStackTrace();
            }
        });
        actionPanel.add(pastNameLabel);
        actionPanel.add(historyDropdown);
        actionPanel.add(revertButton);
        revertButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        pastNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        historyDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

    }

    /** Refresh this ImageWindow and the main PhotoRenamer window. */
    private void refreshWindows() {
        tagScroller.revalidate();
        imageFrame.setTitle(selectedImage.name);
        imageFrame.revalidate();
        imageFrame.repaint();
        mainFrame.invalidate();
        mainFrame.validate();
        mainFrame.repaint();
    }
}
