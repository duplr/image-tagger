package photo_renamer;

import java.io.*;
import java.util.ArrayList;

/** A manager for the history database of ImageFiles. */
class ImageHistoryManager extends AbstractDatabaseManager {

    /** The ArrayList containing managed ImageFiles. */
    private ArrayList<ImageFile> images;

    /**
     * Construct a manager for the history database of ImageFiles.
     *
     * @param historyPath the pathname of the history database file
     */
    @SuppressWarnings("unchecked")
    ImageHistoryManager(String historyPath) throws IOException, ClassNotFoundException {
        super(historyPath);
        this.images = super.managedObjects;
    }

    /**
     * Add an ImageFile to the historical database if not yet recorded and return the relevant ImageFile.
     *
     * @param newImage The ImageFile potentially being added.
     * @return an ImageFile if found, otherwise return the argued ImageFile
     * @throws IOException an IOException exception
     */
    ImageFile historicizeImage(ImageFile newImage) throws IOException {
        if (images.contains(newImage)) {
            int index = images.indexOf(newImage);
            return images.get(index);
        } else {
            images.add(newImage);
            toFile();
            return newImage;
        }
    }
}
