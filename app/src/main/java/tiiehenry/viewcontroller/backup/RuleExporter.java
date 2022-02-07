package tiiehenry.viewcontroller.backup;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;

import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tiiehenry.io.Filej;
import tiiehenry.io.Zipl;
import tiiehenry.viewcontroller.GodModeApplication;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.rule.ActRules;
import tiiehenry.viewcontroller.rule.AppRules;
import tiiehenry.viewcontroller.rule.ViewRule;

public class RuleExporter {

   public static final String MANIFEST = "manifest.json";
   private static final String PACK_SUFFIX = ".zip";
   private static final String EXPORT_DIR = "God_Mode_Plus";

   public static File exportAllRules(String saveDir, AppRules appRules) throws IOException {
      File tmpDir = GodModeApplication.getApplication().getCacheDir();
      Filej.clearDir(tmpDir);

      for (ActRules a : appRules.values()) {
         for (List<ViewRule> viewRules : a.values()) {
            String outputNamePrefix = getAppExportPrefix(viewRules.get(0));
            File dir = new File(tmpDir, outputNamePrefix);
            exportToDir(dir, viewRules);
         }
      }

      try {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
         String filename = String.format("GodModBackup-%s%s", sdf.format(new Date()), PACK_SUFFIX);
         File zipFile = new File(saveDir, filename);
         if (zipFile.exists()) {
            zipFile.renameTo(new File(saveDir, System.currentTimeMillis() + "-" + filename));
         }
         new Zipl(new File(saveDir, filename)).zipDir(tmpDir);
//            ZipUtils.compress(new File(savePath, filename).getAbsolutePath(), filePathList.toArray(new String[0]));
         return zipFile;
      } finally {
         Filej.clearDir(tmpDir);
      }
   }

   private static String getAppExportPrefix(ViewRule viewRule) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
      return String.format("gmr-%s(%s)-%s", viewRule.label, viewRule.matchVersionName, sdf.format(new Date()));

   }

   private static void exportToDir(File outDir, List<ViewRule> viewRules) {
      ArrayList<ViewRule> viewRuleList = new ArrayList<>(viewRules.size());
      Filej.clearDir(outDir);
      outDir.mkdirs();
      // Save rule preview image
      for (ViewRule r : viewRules) {
         ViewRule viewRuleCopy = r.clone();
         ParcelFileDescriptor parcelFileDescriptor = GodModeManager.getDefault().openImageFileDescriptor(r.imagePath);
         if (parcelFileDescriptor != null) {
            try {
               try (FileChannel inChannel = new FileInputStream(parcelFileDescriptor.getFileDescriptor()).getChannel()) {
                  File file = new File(outDir, System.currentTimeMillis() + ".webp");
                  try (FileChannel outChannel = new FileOutputStream(file).getChannel()) {
                     inChannel.transferTo(0, inChannel.size(), outChannel);
                     viewRuleCopy.imagePath = file.getName();
                  }
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
         viewRuleList.add(viewRuleCopy);
      }
      // Write manifest config
      try {
         File manifestFile = new File(outDir, MANIFEST);
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
         new Filej(manifestFile).writeString(gson.toJson(viewRuleList));
      } catch (IOException e) {
         Logger.e(TAG, "Write manifest file fail", e);
      }
   }

   private static File exportToCacheDir(List<ViewRule> viewRules) {
      String outputNamePrefix = getAppExportPrefix(viewRules.get(0));
      File outDir = new File(GodModeApplication.getApplication().getCacheDir(), outputNamePrefix);
      exportToDir(outDir, viewRules);
      return outDir;
   }

   public static File exportRules(String savePath, List<ViewRule> viewRules) throws IOException {
      File file = exportToCacheDir(viewRules);

      String outputNamePrefix = getAppExportPrefix(viewRules.get(0));
      String filename = outputNamePrefix + PACK_SUFFIX;
      File zipFile = new File(savePath, filename);
      try {
         new Zipl(zipFile).zipDir(file);
      } finally {
         Filej.deleteDir(file);
      }
      return zipFile;
   }

   public static File getExportDir(Context context) {
      File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), EXPORT_DIR);
      file.mkdirs();
      if (!file.exists()) {
         file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
         if (!file.exists()) {
            file = context.getExternalFilesDir(EXPORT_DIR);
         }
      }
      return file;
   }
}
