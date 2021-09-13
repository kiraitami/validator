package com.l.input_validator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Validator(vararg validations: InputValidator) {
    private val validationList = mutableListOf<InputValidator>()

    init {
        if (!validations.isNullOrEmpty()) {
            validationList.addAll(validations)
        }
    }

    fun add(vararg inputValidator: InputValidator): Validator {
        validationList.addAll(inputValidator)
        return this
    }

    fun add(inputValidators: List<InputValidator>): Validator {
        validationList.addAll(inputValidators)
        return this
    }

    private fun verifyList() {
        if (validationList.isEmpty()) {
            throw NoSuchElementException("There's no InputValidator added")
        }
    }

    /**
     * Performs [InputValidator.validate] on each [InputValidator] from [validationList],
     * then, when complete, returns `true` if all of given [InputValidator] returned `true`,
     * otherwise returns `false`.
     *
     * The [isValid] function is launched on a [Dispatchers.Default] Coroutine Context.
     *
     * @throws NoSuchElementException case [validationList] is `null` or empty
     * @see [CoroutineScope.coroutineContext]
     * @see [CoroutineScope]
     * @return [withContext] (Dispatchers.Default) `Boolean`
     */
    suspend fun isValid(): Boolean = withContext(Dispatchers.Default) {
        verifyList()

        var isValid = true

        validationList.forEach { inputValidator ->
            if (!inputValidator.validate()) {
                isValid = false
            }
        }

        isValid
    }

    /**
     * Performs [InputValidator.validateTogether] on each [InputValidator] from [validationList],
     * then, when complete, returns `true` if any of given [InputValidator] returned `true`,
     * otherwise returns `false`.
     *
     * The [isAnyValid] function is launched on a [Dispatchers.Default] Coroutine Context.
     *
     * @throws NoSuchElementException case [validationList] is `null` or empty
     * @see [CoroutineScope.coroutineContext]
     * @see [CoroutineScope]
     * @return [withContext] (Dispatchers.Default) `Boolean`
     */
    suspend fun isAnyValid() : Boolean = withContext(Dispatchers.Default) {
        verifyList()

        var isValid = false

        validationList.forEach { inputValidator ->
            if (inputValidator.validateTogether()) {
                isValid = true
            }
        }

        if (!isValid) {
            validationList.forEach { inputValidator ->
                inputValidator.showValidateTogetherErrorMessage()
            }
        }

        isValid
    }
}

