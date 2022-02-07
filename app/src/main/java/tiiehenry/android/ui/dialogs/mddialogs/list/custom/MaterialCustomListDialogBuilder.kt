package tiiehenry.android.ui.dialogs.mddialogs.list.custom


import android.content.Context
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import tiiehenry.android.ui.dialogs.mddialogs.base.MaterialBaseDialogBuilder
import tiiehenry.android.ui.dialogs.mddialogs.base.NegativeButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.NeutralButtonTemp
import tiiehenry.android.ui.dialogs.mddialogs.base.PositiveButtonTemp
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.strategy.list.custom.ICustomListDialogBuilder

class MaterialCustomListDialogBuilder(val context: Context) : MaterialBaseDialogBuilder<ICustomListDialogBuilder>, ICustomListDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()

    override lateinit var dialog: IDialog

    override fun adapter(adapter: RecyclerView.Adapter<*>, layoutManager: RecyclerView.LayoutManager?): ICustomListDialogBuilder {
//        builder.customListAdapter(adapter, layoutManager)
//        return builder()
        val rv = RecyclerView(context)
        rv.let {
            it.adapter = adapter
            it.layoutManager = layoutManager ?: LinearLayoutManager(context)
        }
//        val marginHorizontal=context.dp2px(24f)
        rv.layoutParams = FrameLayout.LayoutParams(-1, -2)
//        rv.margin(leftMargin = marginHorizontal,rightMargin = marginHorizontal)
        builder.customView(view = rv, scrollable = true, horizontalPadding = true, dialogWrapContent = true)
        return builder()
    }

    override fun adapter(adapter: RecyclerView.Adapter<*>): ICustomListDialogBuilder {
        return adapter(adapter, LinearLayoutManager(context, RecyclerView.VERTICAL, false))
    }

    override fun builder(): ICustomListDialogBuilder {
        return this
    }
}