package photo_renamer;

import java.io.*;
import java.util.ArrayList;

/** An abstract class for managing database files. */
abstract class AbstractDatabaseManager {
    /** The path of the managed database file. */
    private String path;
    /** The ArrayList of managed objects. */
    ArrayList managedObjects;

    /**
     * Construct an AbstractManager for managing database files.
     *
     * @param path the path of the managed database file
     * @throws IOException            an IOException exception
     * @throws ClassNotFoundException a ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    AbstractDatabaseManager(String path) throws IOException, ClassNotFoundException {
        this.path = path;
        File databaseFile = new File(this.path);
        if (databaseFile.exists()) {
            InputStream file = new FileInputStream(this.path);
            InputStream buffer = new BufferedInputStream(file);
            ObjectInput input = new ObjectInputStream(buffer);
            this.managedObjects = (ArrayList<Object>) input.readObject();
            input.close();
        } else {
            this.managedObjects = new ArrayList<>();
        }
    }

    /**
     * Save the contents of the managed objects ArrayList to the history database file.
     *
     * @throws IOException an IOException exception
     */
    void toFile() throws IOException {
        System.out.println("Making new file!");
        OutputStream file = new FileOutputStream(path);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutput output = new ObjectOutputStream(buffer);
        output.writeObject(managedObjects);
        output.close();
    }
}
