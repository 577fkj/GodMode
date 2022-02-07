package tiiehenry.android.ui.dialogs.mddialogs.progress

import android.content.Context
import tiiehenry.android.ui.dialogs.api.strategy.progress.IProgressDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.progress.IProgressDialogProvider

class MDProgressDialogProvider(val context: Context) : IProgressDialogProvider {
    override fun builder(): IProgressDialogBuilder {
        return MDProgressDialogBuilder(context)
    }
}