package tiiehenry.android.ui.dialogs.mddialogs.base

import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.api.IDialog

class NeutralButtonTemp : ButtonTemp() {
    override fun apply(builder: MaterialDialog, dialog: IDialog) {
        if (exists())
            builder.neutralButton(res, text) { click?.onClick(dialog) }
    }
}