package tiiehenry.io;

import android.util.ArrayMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * File的扩展类
 * Directory 全部简写为Dir
 * absolute 和 parent 的 get 省略
 *
 * @author TIIEHenry
 */
public class Filej extends File {
    public static String DEF_ENCODING = "UTF-8";
    public static int DEF_BYTESIZE = 4096;

    public Filej(String pathname) {
        super(pathname);
    }

    public Filej(String parent, String child) {
        super(parent, child);
    }

    public Filej(File parent, String child) {
        super(parent, child);
    }

    public Filej(URI uri) {
        super(uri);
    }

    public Filej(File f) {
        this(f.getPath());
    }

    public static void deleteDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
                // 删除所有文件
            } else if (file.isDirectory()) {
                deleteDir(file);
                // 递规的方式删除文件夹
            }
        }
        dir.delete();
        // 删除目录本身
    }

    public static void clearDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
                // 删除所有文件
            } else if (file.isDirectory()) {
                deleteDir(file);
                // 递规的方式删除文件夹
            }
        }
        dir.delete();
        // 删除目录本身
    }

    public static void copyStream(InputStream i, OutputStream o) throws IOException {
        copyStream(i, o, DEF_BYTESIZE);
    }

    public static void copyStream(InputStream i, OutputStream o, int byteSize) throws IOException {
        byte[] buffer = new byte[byteSize];
        copyStream(i, o, buffer);
    }

    public static void copyStream(InputStream i, OutputStream o, byte[] buffer) throws IOException {
        int s;
        while ((s = i.read(buffer)) != -1) {
            o.write(buffer, 0, s);
        }
    }

    /**
     * 默认编码UTF8
     */
    public static String readStreamString(InputStream i) throws IOException {
        return readStreamString(i, DEF_ENCODING);
    }

    /**
     * @param encoding 编码格式默认UTF-8
     */
    public static String readStreamString(InputStream i, String encoding) throws IOException {
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(i, encoding));
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                sb.append(lineTxt);
                sb.append("\n");
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return sb.toString();
    }

    public static byte[] readStreamBytes(FileInputStream in) throws IOException {
        byte[] buffer;
        try {
            int length = in.available();
            buffer = new byte[length];
            in.read(buffer);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return buffer;
    }

    public static String getFormatedSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String sizeizeString = "0K";
        if (size < 1000) {
            sizeizeString = df.format((double) size) + "B";
        } else if (size < 1024000) {
            sizeizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1048576000) {
            sizeizeString = df.format((double) size / 1048576) + "M";
        } else {
            sizeizeString = df.format((double) size / 1073741824) + "G";
        }
        return sizeizeString;
    }

    public Filej parent(int level) {
        File f = this;
        for (int i = 0; i < level; i++) {
            f = this.getParentFile();
        }
        return new Filej(f);
    }

    public Filej parent() {
        return parent(1);
    }

    public Filej child(String name) {
        return new Filej(this, name);
    }

    public Filej absolute() {
        return new Filej(getAbsolutePath());
    }

    public String absolutePath() {
        return getAbsolutePath();
    }

    public String path() {
        return getPath();
    }

    public boolean deleteAll() {
        if (isDirectory()) {
            deleteDir(this);
        } else if (isFile()) {
            return delete();
        }
        return !exists();
    }

    public boolean copyTo(String path) throws IOException {
        return copyTo(new File(path), true);
    }

    public boolean copyTo(String path, boolean recover) throws IOException {
        return copyTo(new File(path), recover);
    }

    public boolean copyTo(File path) throws IOException {
        return copyTo(path, true);
    }

    public boolean copyTo(File path, boolean recover) throws IOException {
        if (isFile()) {
            if (path.exists() && !recover) {
                IOExceptionMaker.exists(path);
            } else {
                File parent = path.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    IOExceptionMaker.mkdir(parent);
                }
                InputStream in = getInputStream();
                OutputStream out = new FileOutputStream(path);
                copyStream(in, out);
                in.close();
                out.close();
            }
        } else if (isDirectory()) {
            path.mkdirs();
            ArrayMap<String, File> map = getDirMap();
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                File sourceFile = (File) entry.getValue();
                new Filej(sourceFile).copyTo(path.getPath() + separator + name, recover);
            }
        } else {
            IOExceptionMaker.notFileOrDir(path);
        }
        return true;
    }

    public String readString() throws IOException {
        return readString(DEF_ENCODING);
    }

    public String readString(String encoding) throws IOException {
        return readStreamString(getInputStream(), encoding);
    }

    public boolean writeString(String s) throws IOException {
        return writeString(s, false);
    }

    public boolean writeString(String s, boolean append) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this, append)));
            bw.write(s);
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
        return true;
    }

    public FileInputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(this);
    }

    public FileOutputStream getOutputStream() throws FileNotFoundException {
        return new FileOutputStream(this);
    }

    /**
     * 获取文件树
     *
     * @return (" music / ", File) ("music/a.mp3",File)
     */
    public ArrayMap<String, File> getDirMap() {
        if (!isDirectory()) {
            return null;
        }
        ArrayMap<String, File> size = new ArrayMap<>();
        addFileToMap(size, "", this);
        return size;
    }

    private void addFileToMap(ArrayMap<String, File> size, String namePerfix, File dir) {
        for (File f : dir.listFiles()) {
            String fname = f.getName();
            if (f.isFile()) {
                size.put(namePerfix + fname, f);
            } else if (f.isDirectory()) {
                size.put(namePerfix + fname + "/", f);
                addFileToMap(size, namePerfix + fname + "/", f);
            }
        }
    }

    public long getSize() {
        long size = 0;
        if (isDirectory()) {
            for (File f : listFiles()) {
                if (f.isDirectory()) {
                    size += new Filej(f).getSize();
                } else if (f.isFile()) {
                    size += f.length();
                }
            }
            return size;
        } else if (isFile()) {
            return length();
        }
        return 0;
    }

    /**
     * @return 2.1M
     */
    public String getFormatedSize() {
        return getFormatedSize(length());
    }

}
