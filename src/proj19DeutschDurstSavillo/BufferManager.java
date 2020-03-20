/*
 * File: BufferManager.java
 * S19 CS461 Project 13
 * Names: Evan Savillo, Rob Durst, Martin Deutsch
 * Date: 3/8/19
 */

package proj19DeutschDurstSavillo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;


/**
 * A utility class which manages the buffer files and their related original files
 * which are elsewhere on the filesystem.
 */
public class BufferManager
{
    /** A mapping of buffers to original files */
    private HashMap<File, File> buffer2file;

    /** The folder which holds all the buffers */
    private File bufferHome;

    /** Manages the count for new "Untitled_.btm" files */
    private int untitledCounter;

    public BufferManager()
    {
        this.buffer2file = new HashMap<>();
        this.untitledCounter = 0;

        try {
            // Build a temporary directory in which to store all the buffer files
            this.bufferHome = Files.createTempDirectory("btm-IDE-buffers").toFile();
            this.bufferHome.deleteOnExit(); // delete on application close
        } catch (IOException e) {
            // being unable to save is too critical a task for the program to be able to continue
            System.out.println("[!] Fatal Error: could not create #buffer-home#");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Creates a new buffer from a given file, and records the pair in the map
     *
     * @param file - the file whose contents will be placed in the buffer
     * @return the created buffer file
     */
    public File createBuffer(File file)
    {
        String bufferName = "#" + file.getName();
        File   buffer     = new File(this.getHome() + bufferName);

        this.buffer2file.put(buffer, file);

        return buffer;
    }

    /**
     * Convenience method for acquiring the place where new buffer files will be written
     */
    private String getHome()
    {
        return this.bufferHome.getAbsolutePath() + "/";
    }

    /**
     * Gets the original file associated with a given buffer
     *
     * @param buffer - the given buffer
     * @return the original file
     */
    public File getOriginalFile(File buffer)
    {
        return this.buffer2file.get(buffer);
    }

    /**
     * Creates a new buffer file which is not associate with any
     * file currently existing on the filesystem.
     *
     * @return the created buffer
     */
    public File createLoneBuffer()
    {
        String bufferName = "#" + "Untitled" + (++this.untitledCounter) + ".btm";
        File   buffer     = new File(this.getHome() + bufferName);

        this.buffer2file.put(buffer, null);

        return buffer;
    }

    /**
     * Deletes the buffer from the mapping of buffers to original files,
     * and then deletes the actual buffer file from the system.
     *
     * @param buffer - the buffer to be expunged from the map and filesystem
     */
    public void deleteBuffer(File buffer)
    {
        this.buffer2file.remove(buffer);

        // If we are deleting an Untitled buffer, we can decrement
        if (buffer.getName().startsWith("#Untitled")) {
            this.untitledCounter--;
        }

        /* We must delete the buffers when we are done with them so that their
         * parent directory is empty, and able to be deleted upon the exiting
         * of the application. */
        System.out.println("Buffer was " + (buffer.delete() ? "" : "NOT ") + "deleted.");
        // (and we may as well use the return value of buffer.delete() for something)
    }
}
