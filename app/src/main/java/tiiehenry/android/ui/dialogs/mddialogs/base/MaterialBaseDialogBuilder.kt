package tiiehenry.android.ui.dialogs.mddialogs.base

import android.graphics.drawable.Drawable
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.builder.IBaseDialogBuilder
import tiiehenry.android.ui.dialogs.api.callback.*
import tiiehenry.android.ui.dialogs.api.callback.button.ButtonCallback
import tiiehenry.android.ui.dialogs.mddialogs.applyTheme

interface MaterialBaseDialogBuilder<T> : IBaseDialogBuilder<T> {
    val builder: MaterialDialog
    var dialog: IDialog
    val positiveTemp: PositiveButtonTemp
    val negativeTemp: NegativeButtonTemp
    val neutralTemp: NeutralButtonTemp
//
//    override fun onAny(callback: OnAnyCallback): T {
//        builder.onAny { _, which ->
//            when (which) {
//                DialogAction.POSITIVE -> callback.onClick(dialog)
//                DialogAction.NEGATIVE -> callback.onNegative(dialog)
//                DialogAction.NEUTRAL -> callback.onNeutral(dialog)
//            }
//        }
//        return builder()
//    }

    override fun cancelable(cancelable: Boolean): T {
        builder.cancelable(cancelable)
        return builder()
    }

//    override fun iconAttr(iconAttr: Int): T {
//        builder.icon(iconAttr)
//        return builder()
//    }

    override fun checkBoxPrompt(prompt: CharSequence, initiallyChecked: Boolean, checkListener: OnCheckedChangeListener?): T {
        builder.checkBoxPrompt(0, prompt.toString(), initiallyChecked, { checked -> checkListener?.onCheckedChanged(checked) })
        return builder()
    }


    override fun checkBoxPromptRes(prompt: Int, initiallyChecked: Boolean, checkListener: OnCheckedChangeListener?): T {
        builder.checkBoxPrompt(prompt, null, initiallyChecked, { checked -> checkListener?.onCheckedChanged(checked) })
        return builder()
    }

    override fun build(): IDialog {
        dialog = MaterialDialogBaseWrapper(builder.applyTheme())
        positiveTemp.apply(builder, dialog)
        negativeTemp.apply(builder, dialog)
        neutralTemp.apply(builder, dialog)
        return dialog
    }

//    override fun tag(tag: Any?): T {
//        builder.tag(tag)
//        return builder()
//    }

    override fun icon(icon: Drawable): T {
        builder.icon(drawable = icon)
        return builder()
    }

    override fun iconRes(iconRes: Int): T {
        builder.icon(res = iconRes)
        return builder()
    }

    override fun keyListener(listener: OnKeyListener): T {
        builder.setOnKeyListener { _, keyCode, event -> listener.onKey(dialog, keyCode, event) }
        return builder()
    }

    override fun limitIconToDefaultSize(): T {
//        builder.limitIconToDefaultSize()
        return builder()
    }


    override fun cancelListener(listener: OnCancelListener): T {
        builder.setOnCancelListener { listener.onCancel(dialog) }
        return builder()
    }


    override fun title(textRes: Int): T {
        builder.title(textRes)
        return builder()
    }

    override fun title(title: CharSequence): T {
        builder.title(text = title.toString())
        return builder()
    }
//
//    override fun titleGravity(gravity: GravityEnum): T {
//        builder.titleGravity(translateGravityEnum(gravity.gravityInt))
//        return builder()
//    }


    override fun positiveText(textRes: Int): T {
        positiveTemp.res = textRes
        return builder()
    }

    override fun positiveText(text: CharSequence): T {
        positiveTemp.text = text
        return builder()
    }

    override fun onPositive(callback: ButtonCallback): T {
        positiveTemp.click = callback
        return builder()
    }
//
//    override fun positiveFocus(isFocusedDefault: Boolean): T {
//        builder.positiveFocus(isFocusedDefault)
//        return builder()
//    }

    override fun negativeText(textRes: Int): T {
        negativeTemp.res = textRes
        return builder()
    }

    override fun negativeText(text: CharSequence): T {
        negativeTemp.text = text
        return builder()
    }

    override fun onNegative(callback: ButtonCallback): T {
        negativeTemp.click = callback
        return builder()
    }
//
//    override fun negativeFocus(isFocusedDefault: Boolean): T {
//        builder.negativeFocus(isFocusedDefault)
//        return builder()
//    }


    override fun neutralText(textRes: Int): T {
        neutralTemp.res = textRes
        return builder()
    }

    override fun neutralText(text: CharSequence): T {
        neutralTemp.text = text
        return builder()
    }

    override fun onNeutral(callback: ButtonCallback): T {
        neutralTemp.click = callback
        return builder()
    }
//
//    override fun neutralFocus(isFocusedDefault: Boolean): T {
//        builder.neutralFocus(isFocusedDefault)
//        return builder()
//    }

    override fun dismissListener(listener: OnDismissListener): T {
        builder.setOnDismissListener { listener.onDismiss(dialog) }
        return builder()
    }


    override fun showListener(listener: OnShowListener): T {
        builder.setOnShowListener { listener.onShow(dialog) }
        return builder()
    }

    override fun autoDismiss(enable: Boolean): T {
        if (!enable) {
            builder.noAutoDismiss()
        }
        return builder()
    }

    override fun cancelOnTouchOutside(cancelable: Boolean): T {
        builder.cancelOnTouchOutside(cancelable)
        return builder()
    }

}