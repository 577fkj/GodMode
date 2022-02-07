package tiiehenry.android.ui.dialogs.mddialogs.base

import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.button.ButtonCallback

abstract class ButtonTemp {
    var res: Int? = null
    var text: CharSequence? = null
    var click: ButtonCallback? = null

    fun exists(): Boolean {
        return res != null || text != null
    }

    abstract fun apply(builder: MaterialDialog, dialog: IDialog)

}