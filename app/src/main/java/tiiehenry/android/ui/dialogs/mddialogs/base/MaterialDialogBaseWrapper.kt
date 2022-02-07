package tiiehenry.android.ui.dialogs.mddialogs.base

import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.mddialogs.runOnUIThread
import tiiehenry.android.ui.dialogs.mddialogs.showInAnim

open class MaterialDialogBaseWrapper(val dialog: MaterialDialog) : IDialog {
    override fun dismiss() {
        runOnUIThread {
            dialog.hide()
        }
    }

    override fun show() {
        dialog.showInAnim()
    }

}