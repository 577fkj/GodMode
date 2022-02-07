package tiiehenry.android.ui.dialogs.mddialogs.custom

import android.content.Context
import tiiehenry.android.ui.dialogs.api.strategy.custom.ICustomDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.custom.ICustomDialogProvider


class MDCustomDialogProvider(val context: Context) : ICustomDialogProvider {
    override fun builder(): ICustomDialogBuilder {
        return MDCustomDialogBuilder(context)
    }
}