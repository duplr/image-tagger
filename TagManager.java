package photo_renamer;

import java.io.*;
import java.util.ArrayList;

/** A manager for Tag database. */
class TagManager extends AbstractDatabaseManager {

    /** The ArrayList containing the managed Tags. */
    ArrayList<Tag> tags;

    /**
     * Construct a TagManager for the database of Tags.
     *
     * @param tagsPath the pathname to save the tag list
     * @throws IOException            an IOException exception
     * @throws ClassNotFoundException a ClassNotFoundException exception
     */
    @SuppressWarnings("unchecked")
    TagManager(String tagsPath) throws IOException, ClassNotFoundException {
        super(tagsPath);
        this.tags = super.managedObjects;
    }

    /**
     * Add a new Tag to the managed database of Tags.
     *
     * @param newTag the Tag to be added
     * @throws IOException an IOException exception
     */
    void addTag(Tag newTag) throws IOException {
        if (!tags.contains(newTag)) {
            tags.add(newTag);
            toFile();
        }
    }

    /**
     * Remove a Tag from the managed database of Tags.
     *
     * @param oldTag the Tag to be removed
     * @throws IOException an IOException exception
     */
    void removeTag(Tag oldTag) throws IOException {
        if (tags.contains(oldTag)) {
            tags.remove(oldTag);
            toFile();
        }
    }
}
