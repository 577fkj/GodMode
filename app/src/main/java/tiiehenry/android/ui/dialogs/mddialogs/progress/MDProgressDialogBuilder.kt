package tiiehenry.android.ui.dialogs.mddialogs.progress

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.mddialogs.base.MaterialBaseDialogBuilder
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.strategy.progress.IProgressDialogBuilder
import java.text.NumberFormat

class MDProgressDialogBuilder(val context: Context) : MaterialBaseDialogBuilder<IProgressDialogBuilder>, IProgressDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()

    override lateinit var dialog: IDialog
    override fun progressPercentFormat(format: NumberFormat): IProgressDialogBuilder {
//        builder.progressPercentFormat(format)
        return builder()
    }

    override fun progressIndeterminateStyle(horizontal: Boolean): IProgressDialogBuilder {
//        builder.progressIndeterminateStyle(horizontal)
        return builder()
    }

    override fun progressNumberFormat(format: String): IProgressDialogBuilder {
//        builder.progressNumberFormat(format)
        return builder()
    }

    override fun progress(indeterminate: Boolean, max: Int): IProgressDialogBuilder {
//        builder.progress(indeterminate, max)
        return builder()
    }

    override fun progress(indeterminate: Boolean, max: Int, showMinMax: Boolean): IProgressDialogBuilder {
//        builder.progress(indeterminate, max, showMinMax)
        return builder()
    }


    override fun builder(): IProgressDialogBuilder {
        return this
    }

}