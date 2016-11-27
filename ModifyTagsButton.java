package photo_renamer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/** A button used to view and edit a list of currently existing Tags. */
class ModifyTagsButton extends JButton implements ActionListener {

    /** The serialVersionUID for this class. */
    private static final long serialVersionUID = -9021117525598345699L;
    /** the TagManager being modified. */
    private final TagManager tagManager;

    /**
     * A button that can open a new window for modifying Tags.
     *
     * @param label      the label of this button
     * @param tagManager the TagManager managing the Tags
     */
    ModifyTagsButton(String label, TagManager tagManager) {
        super(label);
        this.tagManager = tagManager;
        this.addActionListener(this);
    }

    /**
     * Handle ModifyTagsButton clicks.
     *
     * @param click the ActionEvent
     */
    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent click) {
        // Create the window to display the tag modifier in.
        JFrame modifierWindow = new JFrame("Modify Tags Database");
        // Create a list model to populate the list of Tags.
        DefaultListModel<Tag> tagModel = new DefaultListModel<>();
        tagManager.tags.forEach(tagModel::addElement);
        JList<String> tagList = new JList(tagModel);
        JScrollPane tagsPane = new JScrollPane(tagList);
        // Create a text field for user entry of new Tags.
        JTextField newTagField = new JTextField("Enter new tag...", 20);
        newTagField.addActionListener(enter -> {
            Tag newTag = new Tag(newTagField.getText());
            if (!tagModel.contains(newTag)) {
                tagModel.addElement(newTag);
                newTagField.setText("");
                try {
                    tagManager.addTag(newTag);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(new JFrame(), "Failed to write new tag!");
                    e1.printStackTrace();
                }
            }
        });
        // Create a button to remove tags from the list of Tags.
        JButton removeTagButton = new JButton("Remove Tag");
        removeTagButton.addActionListener(e13 -> {
            int tagIndex = tagList.getSelectedIndex();
            if (tagIndex >= 0) {
                Tag toDelete = tagModel.getElementAt(tagIndex);
                tagModel.removeElement(toDelete);
                try {
                    tagManager.removeTag(toDelete);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(new JFrame(), "Failed to remove tag!");
                    e1.printStackTrace();
                }
            }
        });
        // Put all components of the modifier window together.
        JPanel bottomRow = new JPanel();
        bottomRow.add(newTagField);
        bottomRow.add(removeTagButton);
        modifierWindow.add(tagsPane, BorderLayout.NORTH);
        modifierWindow.add(bottomRow, BorderLayout.SOUTH);
        modifierWindow.pack();
        modifierWindow.setVisible(true);
    }
}
