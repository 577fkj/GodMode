package tiiehenry.android.ui.dialogs.mddialogs.custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import tiiehenry.android.ui.dialogs.mddialogs.base.MaterialBaseDialogBuilder
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.OnViewCreatedCallback
import tiiehenry.android.ui.dialogs.api.strategy.custom.ICustomDialogBuilder

class MDCustomDialogBuilder(val context: Context) : MaterialBaseDialogBuilder<ICustomDialogBuilder>, ICustomDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()
    private val customViewTemp: CustomViewTemp = CustomViewTemp()

    override lateinit var dialog: IDialog

    override fun customView(layoutRes: Int, dialogWrapContent: Boolean, onViewCreatedCallback: OnViewCreatedCallback?): ICustomDialogBuilder {
        val layout = LayoutInflater.from(context).inflate(layoutRes, null)
        onViewCreatedCallback?.onViewCreated(layout)
        return customView(layout, dialogWrapContent)
    }

    override fun customView(view: View, dialogWrapContent: Boolean): ICustomDialogBuilder {
//        val layoutContainer = LinearLayout(context)
//        layoutContainer.setPadding(context.dp2px(16f),0,context.dp2px(16f),0)
//        layoutContainer.addView(view, -1, -1)
        customViewTemp.view = view
        customViewTemp.scrollable = dialogWrapContent
        customViewTemp.dialogWrapContent = dialogWrapContent
        return builder()
    }

    override fun builder(): ICustomDialogBuilder {
        return this
    }

    override fun build(): IDialog {
        val dialog = super.build()

        val margin = positiveTemp.exists() || negativeTemp.exists() || neutralTemp.exists()
//        if (positiveTemp.exists()||negativeTemp.exists()||neutralTemp.exists()) {
//            val marginHorizontal = context.dp2px(24f)
//            customViewTemp.view?.layoutParams = FrameLayout.LayoutParams(-1, -2)
//            customViewTemp.view?.margin(leftMargin = marginHorizontal, rightMargin = marginHorizontal)
//        }
        customViewTemp.apply(builder, dialog, margin)
        return dialog
    }
}