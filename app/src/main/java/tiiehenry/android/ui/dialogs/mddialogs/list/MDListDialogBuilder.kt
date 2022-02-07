package tiiehenry.android.ui.dialogs.mddialogs.list

import android.content.Context
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.list.custom.MaterialCustomListDialogBuilder
import tiiehenry.android.ui.dialogs.mddialogs.list.multi.MaterialMultiListDialogBuilder
import tiiehenry.android.ui.dialogs.mddialogs.list.regular.MaterialRegularListDialogBuilder
import tiiehenry.android.ui.dialogs.mddialogs.list.single.MaterialSingleListDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.list.IListDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.list.custom.ICustomListDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.list.multi.IMultiListDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.list.regular.IRegularListDialogBuilder
import tiiehenry.android.ui.dialogs.api.strategy.list.single.ISingleListDialogBuilder

class MDListDialogBuilder(context: Context) : MaterialRegularListDialogBuilder(context), IListDialogBuilder {

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()

    override fun typeRegular(): IRegularListDialogBuilder {
        return this

    }

    override fun typeMultiChoice(): IMultiListDialogBuilder {
        return MaterialMultiListDialogBuilder(context)
    }

    override fun typeSingleChoice(): ISingleListDialogBuilder {
        return MaterialSingleListDialogBuilder(context)
    }

    override fun typeCustom(): ICustomListDialogBuilder {
        return MaterialCustomListDialogBuilder(context)
    }
}