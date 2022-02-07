package tiiehenry.android.ui.dialogs.mddialogs.list.regular

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.list.base.MaterialBaseListDialogBuilder
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.ListCallback
import tiiehenry.android.ui.dialogs.api.strategy.list.regular.IRegularListDialogBuilder

open class MaterialRegularListDialogBuilder(val context: Context) : MaterialBaseListDialogBuilder<IRegularListDialogBuilder>, IRegularListDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()
    override val listTemp: RegularListTemp = RegularListTemp()

    override lateinit var dialog: IDialog

    override fun itemsCallback(callback: ListCallback): IRegularListDialogBuilder {
        listTemp.itemCallback = callback
        return builder()
    }
//
//    override fun itemsLongCallback(callback: ListLongCallback): IRegularListDialogBuilder {
//        builder.itemsLongCallback { _, itemView, position, text ->
//            callback.onLongSelection(dialog, itemView, position, text)
//        }
//        return builder()
//    }

    override fun builder(): IRegularListDialogBuilder {
        return this
    }

}