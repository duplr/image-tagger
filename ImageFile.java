package photo_renamer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;

/**
 * An object representing an image file on the system.
 * Extends Observable to allow ImageFileManager Observers to know of renaming events.
 */
class ImageFile extends Observable implements Serializable {

    /** The serialVersionUID for this class. */
    private static final long serialVersionUID = 3962603919026990230L;
    /** The name of this imageFile. */
    String name;
    /** The ArrayList of tags for this image. */
    ArrayList<Tag> tags;
    /** The path of the image in the system. */
    String imgFilePath;
    /** The File object of the image in the system. */
    File imgFile;
    /** The ArrayList of past names of the image. */
    ArrayList<String> nameHistoryList;

    /**
     * Construct an observable ImageFile object.
     *
     * @param name        the name of this ImageFile
     * @param tags        the Tags for this image
     * @param imgFilePath the system path for the image
     * @throws IOException            an IOException exception
     * @throws ClassNotFoundException a ClassNotFoundException exception
     */
    ImageFile(String name, ArrayList<Tag> tags, String imgFilePath) throws IOException, ClassNotFoundException {
        this.name = name;
        this.tags = tags;
        this.imgFilePath = imgFilePath;
        this.imgFile = new File(imgFilePath);
        this.nameHistoryList = new ArrayList<>();
        this.nameHistoryList.add(this.name);
    }

    /**
     * Change the name of the ImageFile.
     *
     * @param newName the name to change the ImageFile to
     * @throws IOException an IOException exception
     */
    void renameImage(String newName) throws IOException {
        String oldName = name;
        if (!Objects.equals(oldName, newName)) {
            name = newName;
            // Record, rename, and point to the new pathname of this ImageFile.
            imgFilePath = imgFilePath.substring(0, imgFilePath.length() - oldName.length());
            imgFilePath += name;
            File newFile = new File(imgFilePath);
            // Attempt to rename the image file.
            if (imgFile.renameTo(newFile)) {
                imgFile = newFile;
                if (!nameHistoryList.contains(oldName)) {
                    nameHistoryList.add(oldName);
                }
                // Mark this ImageFile as changed and notify any Observers of this rename event.
                setChanged();
                notifyObservers(oldName);
            }
        }
    }

    /**
     * Return a string representation of this ImageFile object.
     *
     * @return a string representing this ImageFile
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Return whether this ImageFile is equal to ImageFile comp.
     *
     * @return a boolean of whether this ImageFile is equal to ImageFile comp
     */
    @Override
    public boolean equals(Object comp) {
        boolean isEqual = false;
        if (comp != null && comp instanceof ImageFile) {
            isEqual = (this.name.equals(((ImageFile) comp).name) &&
                    this.imgFilePath.equals(((ImageFile) comp).imgFilePath));
        }
        return isEqual;
    }
}

