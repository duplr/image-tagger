package photo_renamer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.logging.*;

/**
 * A Singleton object that logs ImageFile rename events.
 * Only one instance can exist.
 */
public class PhotoRenameLogger {

    /** The Logger used by the PhotoRenameLogger */
    private static final Logger logger = Logger.getLogger(PhotoRenameLogger.class.getName());
    /** The pathname of the log the PhotoRenameLogger works in. */
    String logPath = System.getProperty("user.dir") + "/log.txt";

    /**
     * Construct a logger to rename images on the system.
     *
     * @throws SecurityException a SecurityException exception
     * @throws IOException       an IOException exception
     */
    private PhotoRenameLogger() throws IOException {
        File logFile = new File(logPath);
        logFile.createNewFile();
        Handler fileHandler = new FileHandler(logFile.getAbsolutePath(), true);
        logger.setLevel(Level.ALL);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
    }

    /**
     * Return the single instance of PhotoRenameLogger.
     *
     * @return PhotoRenameLogger
     */
    static PhotoRenameLogger getInstance() {
        return PhotoRenameLoggerHolder.INSTANCE;
    }

    /** A nested class to contain the single instance of PhotoRenameLogger. */
    private static class PhotoRenameLoggerHolder {
        /** The PhotoRenameLogger instance. */
        private static final PhotoRenameLogger INSTANCE;

        // Attempt to assign a PhotoRenameLogger instance to INSTANCE.
        static {
            PhotoRenameLogger loggerInstance;
            try {
                loggerInstance = new PhotoRenameLogger();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(new JFrame(), "Failed to create logger instance!");
                e.printStackTrace();
                loggerInstance = null;
            }
            INSTANCE = loggerInstance;
        }
    }

    /**
     * Log rename events in the PhotoRenameLogger logger.
     *
     * @param o       the renamed ImageFile to be logged.
     * @param oldName the old name of the ImageFile.
     */
    void logRename(Object oldName, Observable o) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String formattedDate = dateFormat.format(date);
        logger.log(Level.FINE, "(" + oldName + " >>> " + o + " @ " + formattedDate + ")");
    }
}
