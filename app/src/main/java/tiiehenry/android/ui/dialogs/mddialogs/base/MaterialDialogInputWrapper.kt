package tiiehenry.android.ui.dialogs.mddialogs.base

import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import tiiehenry.android.ui.dialogs.api.strategy.input.IInputDialog

class MaterialDialogInputWrapper(dialog: MaterialDialog) : MaterialDialogBaseWrapper(dialog), IInputDialog {
    override fun setInputError(error: String?) {
        inputField.error = error
    }

    override fun setInputError(error: Int) {
        setInputError(dialog.context.getString(error))
    }

    override fun getInputField(): EditText {
        return dialog.getInputField()
    }
}