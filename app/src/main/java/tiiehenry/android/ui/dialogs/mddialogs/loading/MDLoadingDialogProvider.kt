package tiiehenry.android.ui.dialogs.mddialogs.loading

import android.content.Context
import tiiehenry.android.ui.dialogs.api.strategy.loading.ILoadingDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.loading.ILoadingDialogProvider

class MDLoadingDialogProvider(val context: Context) : ILoadingDialogProvider {
    override fun builder(): ILoadingDialogBuilder {
        return MDLoadingDialogBuilder(context)
    }
}