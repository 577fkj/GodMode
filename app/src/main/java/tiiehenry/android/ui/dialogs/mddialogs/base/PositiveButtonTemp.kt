package tiiehenry.android.ui.dialogs.mddialogs.base

import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.api.IDialog

class PositiveButtonTemp : ButtonTemp() {
    override fun apply(builder: MaterialDialog, dialog: IDialog) {
        if (exists())
            builder.positiveButton(res, text) { click?.onClick(dialog) }
    }
}