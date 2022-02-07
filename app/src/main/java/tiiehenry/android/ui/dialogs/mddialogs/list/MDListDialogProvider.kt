package tiiehenry.android.ui.dialogs.mddialogs.list

import android.content.Context
import tiiehenry.android.ui.dialogs.api.strategy.list.IListDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.list.IListDialogProvider

class MDListDialogProvider(val context: Context) : IListDialogProvider {
    override fun builder(): IListDialogBuilder {
        return MDListDialogBuilder(context)
    }
}