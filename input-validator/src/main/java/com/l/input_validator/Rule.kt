package com.l.input_validator

import android.util.Log
import androidx.annotation.CallSuper

/**
 * Classes that inherit from this class can be used as rule for [InputValidator]
 *
 * @param errorMessage text to be displayed as a error in case [validate] returns `false`
 * @property [textToValidate] text that rule validation should be applied to. Given by [validate] from [InputValidator]
 */
abstract class Rule(val errorMessage: String) {
    var textToValidate: String = ""

    /**
     * Override this function when [textToValidate] needs to be transformed i.e. by a mask, regex...
     */
    @CallSuper
    protected open suspend fun updateValidText(text: String?) {
        this.textToValidate = text ?: ""
        Log.d("VALIDATOR", "updateValidText $textToValidate")
    }

    /**
     * This function will be called by [InputValidator] and should contain the validation logic
     *
     * The validation must be on [textToValidate]
     *
     * @param text given inputted text by [InputValidator]
     */
    @CallSuper
    open suspend fun validate(text: String?): Boolean {
        updateValidText(text)
        return false
    }
}
