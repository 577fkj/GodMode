package tiiehenry.android.ui.dialogs.mddialogs.input

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import tiiehenry.android.ui.dialogs.mddialogs.base.*
import tiiehenry.android.ui.dialogs.api.IDialog
import tiiehenry.android.ui.dialogs.api.callback.InputCallback
import tiiehenry.android.ui.dialogs.api.strategy.input.IInputDialog
import tiiehenry.android.ui.dialogs.api.strategy.input.IInputDialogBuilder

class MDInputDialogBuilder(val context: Context) : MaterialBaseDialogBuilder<IInputDialogBuilder>, IInputDialogBuilder {
    override val builder: MaterialDialog = MaterialDialog(context)
    override lateinit var dialog: IDialog

    override val positiveTemp: PositiveButtonTemp = PositiveButtonTemp()
    override val negativeTemp: NegativeButtonTemp = NegativeButtonTemp()
    override val neutralTemp: NeutralButtonTemp = NeutralButtonTemp()
    private val inputTemp: InputTemp = InputTemp()

    override fun alwaysCallInputCallback(isCall: Boolean): IInputDialogBuilder {
        inputTemp.alwaysCallInputCallback = isCall
        return builder()
    }

    override fun allowEmptyInput(allow: Boolean): IInputDialogBuilder {
//        builder.allowEmptyInput(allow)
        inputTemp.allowEmptyInput = allow
        return builder()
    }


    override fun inputType(type: Int): IInputDialogBuilder {
        inputTemp.inputType = type
        return builder()
    }

    override fun input(hint: CharSequence?, preFill: CharSequence?, callback: InputCallback): IInputDialogBuilder {
        inputTemp.hint = hint
        inputTemp.preFill = preFill
        inputTemp.callback = callback
//
//        builder.input(hint, preFill, _inputAllowEmpty) { _, input ->
//            if (_inputAllowEmpty || input.isNotEmpty()) {
//                callback.onInput(inputDialog, input)
//            }
//        }
        return builder()
    }

    override fun input(hint: Int, preFill: Int, callback: InputCallback): IInputDialogBuilder {
        return input(context.getString(hint), context.getString(preFill), callback)
    }

    override fun inputRange(minLength: Int, maxLength: Int): IInputDialogBuilder {
        inputTemp.inputRange = Pair(minLength, maxLength)
        return builder()
    }
//
//    override fun inputRange(minLength: Int, maxLength: Int, errorColor: Int): IInputDialogBuilder {
//        builder.inputRange(minLength, maxLength, errorColor)
//        return builder()
//    }

//    override fun inputRangeRes(minLength: Int, maxLength: Int, errorColor: Int): IInputDialogBuilder {
//        builder.inputRangeRes(minLength, maxLength, errorColor)
//        return builder()
//    }

    override fun builder(): IInputDialogBuilder {
        return this
    }

    override fun build(): IInputDialog {
        val inputDialog = MaterialDialogInputWrapper(builder)
        dialog = inputDialog
        positiveTemp.apply(builder, dialog)
        negativeTemp.apply(builder, dialog)
        neutralTemp.apply(builder, dialog)
        builder.input(
                hint = inputTemp.hint.toString(),
                prefill = inputTemp.preFill,
                inputType = inputTemp.inputType,
                maxLength = inputTemp.inputRange?.second,
                waitForPositiveButton = !inputTemp.alwaysCallInputCallback,
                allowEmpty = inputTemp.inputRange?.first ?: 0 == 0
        ) { materialDialog, charSequence ->
            inputTemp.callback?.onInput(inputDialog, charSequence)
        }
        return inputDialog
    }

    override fun show(): IInputDialog {
        return super<IInputDialogBuilder>.show()
    }
}