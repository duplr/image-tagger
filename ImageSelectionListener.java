package photo_renamer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/** A listener that opens ImageFiles selected from a list. */
class ImageSelectionListener extends MouseAdapter {

    /** The PhotoRenamer application. */
    private PhotoRenamer mainApp;
    /** The JList being listened to. */
    private JList<String> imageList;
    /** The ListModel associated with the JList. */
    private DefaultListModel<ImageFile> listModel;
    /** The ArrayList of ImageWindows opened by this listener. */
    private ArrayList<ImageWindow> openedWindows;

    /**
     * Construct a listener that opens selected ImageFiles.
     *
     * @param mainApp   the PhotoRenamer application this listener is attached to
     * @param imageList the JList of ImageFiles
     * @param listModel the model used by the list of ImageFiles
     */
    ImageSelectionListener(PhotoRenamer mainApp, JList<String> imageList, DefaultListModel<ImageFile> listModel) {
        this.mainApp = mainApp;
        this.imageList = imageList;
        this.listModel = listModel;
        this.openedWindows = new ArrayList<>();
    }

    /** Handle mouse clicks on a selected ImageFile from the imageList. */
    @Override
    public void mouseClicked(MouseEvent e) {
        int selection = imageList.getSelectedIndex();
        ImageFile selectedImage = listModel.getElementAt(selection);
        // Prevent an ImageWindow for the same ImageFile from being opened twice.
        for (ImageWindow window : openedWindows) {
            if (window.selectedImage == selectedImage) {
                window.imageFrame.dispose();
                openedWindows.remove(window);
                break;
            }
        }
        openedWindows.add(new ImageWindow(mainApp, selectedImage));
    }
}