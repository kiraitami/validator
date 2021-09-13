package com.l.input_validator

import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @see updateInputError
 */
class InputValidator(
    private val layout: TextInputLayout? = null,
    private val editText: EditText? = null,
    private val rules: List<Rule>,
    val onTextValid: (validated: String) -> Unit
) {

    private var validatedText: String = ""
    private var validateTogetherErrorMessage: String? = null

    constructor(layout: TextInputLayout, rules: List<Rule>, onTextValid: (validated: String) -> Unit) : this(layout, null, rules, onTextValid)

    constructor(editText: EditText, rules: List<Rule>, onTextValid: (validated: String) -> Unit) : this(null, editText, rules, onTextValid)

    init {
        if (layout == null && editText == null) {
            throw NoSuchElementException("There's no Input to validate")
        }
    }

    suspend fun validate(): Boolean {
        val ruleList = rules.toMutableList()

        if (ruleList.isEmpty()) {
            throw NoSuchElementException("There's no Rule added")
        }

        validatedText = getInputtedText()

        updateInputError(null)

        ruleList.forEachAndOnLast(
            onEach = { rule ->
                if (!rule.validate(validatedText)) {
                    updateInputError(rule.errorMessage)
                    return false
                }
                validatedText = rule.textToValidate
            },
            onLast = {
                if (validatedText.isNotBlank()) {
                    onTextValid(validatedText)
                }
            }
        )

        return true
    }

    suspend fun validateTogether(): Boolean {
        val ruleList = rules.toMutableList()

        if (ruleList.isEmpty()) {
            throw NoSuchElementException("There's no Rule added")
        }

        validatedText = getInputtedText()

        updateInputError(null)

        ruleList.forEachAndOnLast(
            onEach = { rule ->
                if (!rule.validate(validatedText)) {
                    validateTogetherErrorMessage = rule.errorMessage
                    return false
                }
                validatedText = rule.textToValidate
            },
            onLast = {
                if (validatedText.isNotBlank()) {
                    onTextValid(validatedText)
                }
            }
        )

        return true
    }

    private fun getInputtedText(): String {
        return if (layout != null) {
            layout.editText?.text.toString()
        } else {
            editText?.text.toString()
        }
    }

    /**
     * @see updateInputError
     */
    suspend fun showValidateTogetherErrorMessage() {
        updateInputError(validateTogetherErrorMessage)
    }

    /**
     * The [errorMessage] is executed on [Dispatchers.Main] thread
     */
    private suspend fun updateInputError(errorMessage: String?) {
        withContext(Dispatchers.Main) {
            layout?.error = errorMessage
            editText?.error = errorMessage
        }
    }

    /**
     * Performs the given [onEach] on each element, and the given [onLast] on the last element.
     *
     * The last element will respectively trigger both [onEach] and [onLast] call
     *
     */
    private inline fun <T> List<T>.forEachAndOnLast(onLast: (T) -> Unit, onEach: (T) -> Unit) {
        for (index in this.indices) {
            onEach(this[index])
            if (index == this.lastIndex) {
                onLast(this[index])
            }
        }
    }
}

fun TextInputLayout.validator(rules: List<Rule>, onTextValid: (validated: String) -> Unit): InputValidator {
    return InputValidator(this, rules, onTextValid)
}

fun TextInputLayout.validator(onTextValid: (validated: String) -> Unit, vararg rules: Rule): InputValidator {
    return InputValidator(this, rules.toList<Rule>(), onTextValid)
}

fun EditText.validator(rules: List<Rule>, onTextValid: (validated: String) -> Unit): InputValidator {
    return InputValidator(this, rules, onTextValid)
}

fun EditText.validator(onTextValid: (validated: String) -> Unit, vararg rules: Rule): InputValidator {
    return InputValidator(this, rules.toList<Rule>(), onTextValid)
}

/**
 * Default [InputValidator] with only [RuleBuilder.NotBlank] rule added
 */
fun TextInputLayout.validator(errorMessage: String, onTextValid: (validated: String) -> Unit): InputValidator {
    return InputValidator(this, listOf(RuleBuilder.NotBlank(errorMessage)), onTextValid)
}

/**
 * Default [InputValidator] with only [RuleBuilder.NotBlank] rule added
 */
fun EditText.validator(errorMessage: String, onTextValid: (validated: String) -> Unit): InputValidator {
    return InputValidator(this, listOf(RuleBuilder.NotBlank(errorMessage)), onTextValid)
}
