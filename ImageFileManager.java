package photo_renamer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * A manager for an ImageFile.
 * Implements Observer in order to keep track of name changes to an Observable ImageFile.
 */
class ImageFileManager implements Observer {

    /** A list of permissible ImageFile file types. */
    private static final String[] EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp"};
    /** The ImageFile object being renamed. */
    private ImageFile managedImage;
    /** The PhotoRenameLogger used to log the renaming events. */
    private PhotoRenameLogger photoRenameLogger;
    /** The ImageHistoryManager used to manage the history of ImageFiles. */
    private ImageHistoryManager historyManager;

    /**
     * Construct a manager for observing an ImageFile.
     *
     * @param managedImage      the ImageFile object being managed
     * @param photoRenameLogger the PhotoRenameLogger used for logging rename events
     * @param historyManager    the ImageHistoryManager managing the history database
     */
    @SuppressWarnings("unchecked")
    ImageFileManager(ImageFile managedImage, PhotoRenameLogger photoRenameLogger, ImageHistoryManager historyManager)
            throws IOException, ClassNotFoundException {
        this.managedImage = managedImage;
        this.photoRenameLogger = photoRenameLogger;
        this.historyManager = historyManager;
    }

    /**
     * Rename and modify an ImageFile to include the argued Tags.
     *
     * @param toApply the list of tags to apply to the managed image
     */
    void applyTags(ArrayList<Tag> toApply) throws IOException {
        // Record the old name for logging purposes.
        String oldName = managedImage.name;
        int extBeginning = oldName.lastIndexOf(".");
        String oldExt = oldName.substring(extBeginning);
        String newName = oldName.substring(0, extBeginning);
        boolean shouldRename = false;
        // Add each tag to the ImageFile Tag list, as well as the new name to be used.
        for (Tag tag : toApply) {
            if (!managedImage.tags.contains(tag) && !oldName.contains("@" + tag)) {
                managedImage.tags.add(tag);
                newName += " @" + tag.name;
                shouldRename = true;
            }
        }
        if (shouldRename) {
            newName += oldExt;
            managedImage.renameImage(newName);
        }
    }

    /**
     * Modify and rename the managed image to remove the argued Tags.
     *
     * @param toDelete the list of tags to delete from the managed image
     */
    void deleteTags(ArrayList<Tag> toDelete) throws IOException {
        // Record the old name and file extension.
        String oldName = managedImage.name;
        int extBeginning = oldName.lastIndexOf(".");
        String oldExt = oldName.substring(extBeginning);
        String newName = oldName.substring(0, extBeginning);
        boolean shouldRename = false;
        // Remove each tag from the ImageFile object and/or its old name.
        for (Tag tag : toDelete) {
            String removeTag = " @" + tag.name;
            if (managedImage.tags.contains(tag)) {
                managedImage.tags.remove(tag);
                shouldRename = true;
            }
            if (oldName.contains(removeTag)) {
                newName = newName.replace(removeTag, "");
                shouldRename = true;
            }
        }
        if (shouldRename) {
            newName += oldExt;
            managedImage.renameImage(newName);
        }
    }

    /**
     * Log rename events for the observed ImageFile and update the history database.
     *
     * @param o       the ImageFile this ImageManager observes
     * @param oldName the oldName of this ImageFile
     */
    @Override
    public void update(Observable o, Object oldName) {
        try {
            historyManager.toFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Failed to access history database!");
            e.printStackTrace();
        }
        photoRenameLogger.logRename(oldName, o);
    }

    /**
     * Return an ArrayList of ImageFiles located anywhere under a given directory.
     *
     * @param dir            the root directory
     * @param imageFiles     the ArrayList of managedImages found
     * @param historyManager the ImageHistoryManager being used
     * @return the updated ArrayList of managedImages found
     */
    static ArrayList<ImageFile> getImages(File dir, ArrayList<ImageFile> imageFiles, ImageHistoryManager historyManager)
            throws IOException, ClassNotFoundException {
        File[] files = dir.listFiles();
        if (files != null) {
            // Recursively search this directory for any images under it.
            for (File sub : files) {
                if (sub.isDirectory()) {
                    getImages(sub, imageFiles, historyManager);
                } else {
                    String fileName = sub.getName().toLowerCase();
                    // Check for image file extensions and append the file if compliant.
                    for (String ext : EXTENSIONS) {
                        if (fileName.endsWith(ext)) {
                            ImageFile image = new ImageFile(sub.getName(), new ArrayList<>(), sub.getAbsolutePath());
                            image = historyManager.historicizeImage(image);
                            imageFiles.add(image);
                            break;
                        }
                    }
                }
            }
        }
        return imageFiles;
    }
}
