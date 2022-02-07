package tiiehenry.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author TIIEHenry
 */
public class IOExceptionMaker {

    public static void exists(File f) throws IOException {
        exists(f.toString());
    }

    public static void exists(String path) throws IOException {
        throw new IOException("File already exists: " + path);
    }

    public static void mkdir(File f) throws IOException {
        mkdir(f.toString());
    }

    public static void mkdir(String path) throws IOException {
        throw new IOException("Can not create directory: " + path);
    }

    public static void notFound(File f) throws FileNotFoundException {
        notFound(f.toString());
    }

    public static void notFound(String path) throws FileNotFoundException {
        throw new FileNotFoundException("File not found: " + path);
    }

    public static void notFile(File f) throws IOException {
        notFile(f.toString());
    }

    public static void notFile(String path) throws IOException {
        throw new IOException("Is not a file: " + path);
    }

    public static void notDir(File f) throws IOException {
        notDir(f.toString());
    }

    public static void notDir(String path) throws IOException {
        throw new IOException("Is not a directory: " + path);
    }

    public static void notFileOrDir(File f) throws IOException {
        notFileOrDir(f.toString());
    }

    public static void notFileOrDir(String path) throws IOException {
        throw new IOException("Is not a file or directory: " + path);
    }
}
