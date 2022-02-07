package tiiehenry.viewcontroller.backup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tiiehenry.io.Filej;
import tiiehenry.io.Zipl;
import tiiehenry.viewcontroller.GodModeApplication;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.util.ViewBitmapUtils;
import tiiehenry.viewcontroller.rule.ActRules;
import tiiehenry.viewcontroller.rule.ViewRule;

public class RuleImporter {


    //导入单个应用规则或者全部应用规则
    public static boolean importRulesFromDir(File dirFile) throws IOException {
        File manifestFile = new File(dirFile, RuleExporter.MANIFEST);
        Gson gson = new Gson();

        if (manifestFile.exists()) {
            String json = new Filej(manifestFile).readString();
            try {
                ActRules actRules = new ActRules();
                ViewRule[] list = gson.fromJson(json, ViewRule[].class);
                if (list.length == 0) {
                    return false;
                }
                for (ViewRule viewRule : list) {
                    List<ViewRule> viewRules = actRules.get(viewRule.activityClass);
                    if (viewRules == null) {
                        actRules.put(viewRule.activityClass, viewRules = new ArrayList<>());
                    }
                    viewRules.add(viewRule);
                    String imagePath = viewRule.imagePath;
                    if (!viewRule.imagePath.startsWith("/")) {
                        imagePath = new File(dirFile, viewRule.imagePath).getAbsolutePath();
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    viewRule.imagePath = new File(imagePath).getName();
                    GodModeManager.getDefault().writeBitmap(viewRule.packageName, viewRule.imagePath, bitmap);
                    ViewBitmapUtils.recycleNullableBitmap(bitmap);
                }

                GodModeManager.getDefault().writeAllRule(list[0].packageName, actRules);
                return true;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            File[] files = dirFile.listFiles();
            if (files == null)
                return false;
            boolean result = true;
            for (File file : files) {
                result = result && importRulesFromDir(file);
            }
            return result;
        }
    }

    //导入单个应用规则或者全部应用规则
    public static boolean importRules(File rulesFile) {
        File cacheDir = GodModeApplication.getApplication().getExternalFilesDir("import");
        Filej.clearDir(cacheDir);
        try {
            new Zipl(rulesFile).unZipAll(cacheDir);
            return importRulesFromDir(cacheDir);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Filej.clearDir(cacheDir);
        }
        return false;
    }
}
