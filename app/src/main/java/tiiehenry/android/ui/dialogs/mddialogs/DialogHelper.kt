package tiiehenry.android.ui.dialogs.mddialogs

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.api.strategy.input.IInputDialog
import tiiehenry.android.ui.dialogs.api.strategy.input.IInputDialogBuilder
import java.io.File


fun MaterialDialog.applyTheme(): MaterialDialog {
//    titleColorAttr(R.attr.main_content_foreground_color_hint)
//    contentColorAttr(R.attr.main_content_foreground_color)

    /*backgroundColorAttr(R.attr.main_content_background_color)
    */
//    itemsColorAttr(R.attr.main_content_foreground_color)
//
//    positiveColorAttr(R.attr.main_content_foreground_color)
//    negativeColorAttr(R.attr.main_content_foreground_color)
//    neutralColorAttr(R.attr.main_content_foreground_color)
//    buttonRippleColorAttr(R.attr.colorAccent)
//    btnSelector(R.drawable.main_btn_selector)
    return this
}


fun IInputDialogBuilder.inputTypeFile(): IInputDialogBuilder {
    inputType(InputType.TYPE_TEXT_VARIATION_URI)
    return this
}

fun IInputDialogBuilder.inputRangeFile(): IInputDialogBuilder {
    inputRange(1, 255)
    return this
}

fun Any.runOnUIThread(action: ()->Unit){
    Handler(Looper.getMainLooper()).post { action() }
}

fun MaterialDialog.showInAnim(): MaterialDialog {
    context.runOnUIThread {
        window?.setWindowAnimations(R.style.MDDialogs_Animation_MaterialDialog)
        show()
    }

//    show()
    return this
}

fun isValidFileName(fileName: CharSequence): Boolean {
    if (fileName.length > 255)
        return false
    if (fileName.isEmpty())
        return false
//    return fileName.matches("[^\\s\\\\/:*?\"<>|](\\x20|[^\\s\\\\/:*?\"<>|])*[^\\s\\\\/:*?\"<>|.]$".toRegex())
    return fileName.matches("[^\\s\\\\/:*?\"<>|]?(\\x20|[^\\s\\\\/:*?\"<>|])*[^\\s\\\\/:*?\"<>|.]?$".toRegex())
}

fun getNameError(parent: File?, name: String): String? {
    if (!isValidFileName(name)) {
        return "非法文件名！"
    }
    if (parent == null) {
        return null
    }
    val newFile = File(parent, name)
    if (newFile.exists()) {
        return "文件已存在！"
    }
    return null
}

fun IInputDialog.addFileNameChecker(parent: File): IInputDialog {
    inputField.apply {
        addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable) {
                getNameError(parent, s.toString())?.let {
                    error = it
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
    return this
}
