package tiiehenry.android.ui.dialogs.mddialogs.input

import android.content.Context
import tiiehenry.android.ui.dialogs.api.strategy.input.IInputDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.input.IInputDialogProvider

class MDInputDialogProvider(val context: Context) : IInputDialogProvider {
    override fun builder(): IInputDialogBuilder {
        return MDInputDialogBuilder(context)
    }
}