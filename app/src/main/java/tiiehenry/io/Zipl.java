package tiiehenry.io;

import android.util.ArrayMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Checksum;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * @author TIIEHenry
 */
public class Zipl {

    private File zipFile;
    //4M
    private byte[] buffer = new byte[4096 * 1024];
    private int level = Deflater.DEFAULT_COMPRESSION;
    private int method = ZipOutputStream.DEFLATED;
    private String comment = null;
    private Checksum checksum = new Adler32();

    private Zipl.OnUpdateListener onUpdateListener;

    public Zipl(File zipFile) {
        this.zipFile = zipFile;
    }

    public Zipl setByteSize(int byteSize) {
        buffer = new byte[byteSize];
        return this;
    }

    public Zipl setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Zipl setMethod(int method) {
        this.method = method;
        return this;
    }

    public Zipl setLevel(int level) {
        this.level = level;
        return this;
    }

    public Zipl setChecksum(Checksum checksum) {
        this.checksum = checksum;
        return this;
    }

    public Zipl setOnUpdateListener(OnUpdateListener listener) {
        onUpdateListener = listener;
        return this;
    }

    public void zipDir(File dir, File zipFile) throws IOException {
        zip(new Filej(dir).getDirMap(), zipFile);
    }

    public void zipDir(File dir) throws IOException {
        zip(new Filej(dir).getDirMap(), zipFile);
    }

    public void zip(ArrayMap<String, File> files, File zipFile) throws IOException {
        if (zipFile.exists()) {
            IOExceptionMaker.exists(zipFile);
        }
        if (!zipFile.getParentFile().mkdirs()) {
            IOExceptionMaker.mkdir(zipFile.getParent());
        }

        FileOutputStream dest = new FileOutputStream(zipFile);
        CheckedOutputStream cos = new CheckedOutputStream(dest, checksum);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(cos));
        out.setMethod(method);
        out.setLevel(level);
        if (comment != null) {
            out.setComment(comment);
        }
        for (Object o : files.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String name = (String) entry.getKey();
            File file = (File) entry.getValue();
            addEntry(out, name, file);
        }
        out.closeEntry();
        out.close();
    }

    private void addEntry(ZipOutputStream out, String name, File file) throws IOException {
        FileInputStream fi = new FileInputStream(file);
        BufferedInputStream origin = new BufferedInputStream(fi, buffer.length);
        if (file.isFile()) {
            out.putNextEntry(new ZipEntry(name));
            if (onUpdateListener != null) {
                onUpdateListener.onUpdate(name, file);
            }
            int count;
            while ((count = origin.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, count);
            }
        } else if (file.isDirectory()) {
            name = name.endsWith("/") ? name : name + "/";
            out.putNextEntry(new ZipEntry(name));
        }
        origin.close();
    }


    /**
     * 获取zip中单个文件的流
     *
     * @param name 文件名（"a/b.txt"）
     */
    public InputStream getInputStream(String name) throws IOException {
        ZipFile zf = new ZipFile(zipFile);
        FileInputStream fis = new FileInputStream(zipFile);
        CheckedInputStream cos = new CheckedInputStream(fis, checksum);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(cos));

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (entryName.equals(name)) {
                return zf.getInputStream(entry);
            }
        }
        return null;
    }

    public ArrayList<String> getFileNames() throws IOException {
        FileInputStream fis = new FileInputStream(zipFile);
        CheckedInputStream cos = new CheckedInputStream(fis, checksum);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(cos));

        ArrayList<String> files = new ArrayList<>();
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            files.add(entry.getName());
        }
        zis.close();
        return files;
    }

    public void unZip(String name, File outFile) throws IOException {
        ArrayMap<String, File> files = new ArrayMap<>();
        files.put(name, outFile);
        unZip(files);
    }

    /**
     * 解压zip中文件夹内所有文件
     *
     * @param name    文件夹名
     * @param outPath 输出文件夹
     * @throws IOException 。
     */
    public void unZipDir(String name, File outPath) throws IOException {
        FileInputStream fis = new FileInputStream(zipFile);
        CheckedInputStream cos = new CheckedInputStream(fis, checksum);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(cos));

        ArrayMap<String, File> files = new ArrayMap<>();
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            if (entryName.startsWith(name + "/")) {
                files.put(entryName, new File(outPath, entryName.substring(name.length())));
            }
        }
        zis.close();
        unZip(files);
    }

    public void unZipAll(File outPath) throws IOException {
        FileInputStream fis = new FileInputStream(zipFile);
        CheckedInputStream cos = new CheckedInputStream(fis, checksum);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(cos));

        ArrayMap<String, File> files = new ArrayMap<>();
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            files.put(entryName, new File(outPath, entryName));
        }
        zis.close();
        unZip(files);
    }

    public void unZip(ArrayMap<String, File> files) throws IOException {
        if (!zipFile.exists()) {
            IOExceptionMaker.notFound(zipFile);
        }

        FileInputStream fis = new FileInputStream(zipFile);
        CheckedInputStream cos = new CheckedInputStream(fis, checksum);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(cos));

        unZip(zis, files);
        zis.close();
    }

    private void unZip(ZipInputStream zis, ArrayMap<String, File> files) throws IOException {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String entryName = entry.getName();
            File outFile = files.get(entryName);
            if (outFile != null) {
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    if (outFile.exists()) {
                        IOExceptionMaker.exists(outFile);
                    }
                    if (!outFile.getParentFile().exists() && !outFile.getParentFile().mkdirs()) {
                        IOExceptionMaker.mkdir(outFile.getParent());
                    }
                    if (onUpdateListener != null) {
                        onUpdateListener.onUpdate(entryName, outFile);
                    }

                    BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(outFile), buffer.length);
                    int count;
                    while ((count = zis.read(buffer, 0, buffer.length)) != -1) {
                        dest.write(buffer, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }
        }
    }

    public void append(String appendFilePath) throws Exception {
        ZipFile war = new ZipFile(zipFile);
        ZipOutputStream append = new ZipOutputStream(new FileOutputStream(zipFile));
        Enumeration<? extends ZipEntry> entries = war.entries();
        while (entries.hasMoreElements()) {
            ZipEntry e = entries.nextElement();
            append.putNextEntry(e);
            if (!e.isDirectory()) {
                Filej.copyStream(war.getInputStream(e), append, buffer);
            }
            append.closeEntry();
        }
        // now append some extra content
        ZipEntry e = new ZipEntry(appendFilePath);
        append.putNextEntry(e);
        Filej.copyStream(new FileInputStream(new File(appendFilePath)), append, buffer);
        append.closeEntry();
        // close
        war.close();
        append.close();
    }

    public interface OnUpdateListener {
        void onUpdate(String name, File out);
    }
}
