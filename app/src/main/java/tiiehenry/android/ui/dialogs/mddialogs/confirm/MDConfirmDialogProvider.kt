package tiiehenry.android.ui.dialogs.mddialogs.confirm

import android.content.Context
import tiiehenry.android.ui.dialogs.api.strategy.confirm.IConfirmDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.confirm.IConfirmDialogProvider

class MDConfirmDialogProvider(val context: Context) : IConfirmDialogProvider {
    override fun builder(): IConfirmDialogBuilder {
        return MDConfirmDialogBuilder(context)
    }
}