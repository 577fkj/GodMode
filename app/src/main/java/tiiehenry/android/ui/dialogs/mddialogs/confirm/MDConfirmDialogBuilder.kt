package tiiehenry.android.ui.dialogs.mddialogs.confirm

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.mddialogs.base.MaterialBaseDialogBuilder
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.strategy.confirm.IConfirmDialogBuilder

class MDConfirmDialogBuilder(val context: Context) : MaterialBaseDialogBuilder<IConfirmDialogBuilder>, IConfirmDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()

    override lateinit var dialog: IDialog

    override fun content(contentRes: Int): IConfirmDialogBuilder {
        builder.message(contentRes)
        return builder()
    }
//
//    override fun content(contentRes: Int, html: Boolean): IConfirmDialogBuilder {
//        builder.message(contentRes, html)
//        return builder()
//    }

    override fun content(content: CharSequence): IConfirmDialogBuilder {
        builder.message(text = content)
        return builder()
    }

//    override fun content(contentRes: Int, vararg formatArgs: Any?): IConfirmDialogBuilder {
//        builder.content(contentRes, formatArgs)
//        return builder()
//    }

    override fun builder(): IConfirmDialogBuilder {
        return this
    }

}