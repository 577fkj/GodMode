package tiiehenry.ktx.app

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager



fun Activity.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}