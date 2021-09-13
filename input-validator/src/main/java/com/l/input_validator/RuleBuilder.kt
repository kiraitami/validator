package com.l.input_validator

class RuleBuilder {

    private val rules: MutableList<Rule> = mutableListOf<Rule>()

    fun build() = rules.toList()

    class NotBlank(errorMessage: String) : Rule(errorMessage) {
        override suspend fun validate(text: String?): Boolean {
            super.validate(text)
            return textToValidate.isNotBlank()
        }
    }

    class MatchRegex(private val regex: Regex, errorMessage: String) : Rule(errorMessage) {
        override suspend fun validate(text: String?): Boolean {
            super.validate(text)
            return textToValidate.matches(regex)
        }
    }

    class MinLength(private val minLength: Int, errorMessage: String) : Rule(errorMessage) {
        override suspend fun validate(text: String?): Boolean {
            super.validate(text)
            return textToValidate.length >= minLength
        }
    }

    class MaxLength(private val maxLength: Int, errorMessage: String) : Rule(errorMessage) {
        override suspend fun validate(text: String?): Boolean {
            super.validate(text)
            return textToValidate.length <= maxLength
        }
    }

    class ReplaceString(
        private val replace: String,
        private val replacement: String = "",
        private val ignoreCase: Boolean = true,
        errorMessage: String = ""
    ) : Rule(errorMessage) {

        override suspend fun updateValidText(text: String?) {
            super.updateValidText(text?.replace(replace, replacement, ignoreCase))
        }

        override suspend fun validate(text: String?): Boolean {
            super.validate(text)
            return true
        }
    }

    class ReplaceRegex(
        private val replace: Regex,
        private val replacement: String = "",
        errorMessage: String = ""
    ) : Rule(errorMessage) {

        override suspend fun updateValidText(text: String?) {
            super.updateValidText(text?.replace(replace, replacement))
        }

        override suspend fun validate(text: String?): Boolean {
            super.validate(text)
            return true
        }
    }

    fun notBlank(errorMessage: String): RuleBuilder {
        rules.add(NotBlank(errorMessage))
        return this
    }

    fun minLength(minLength: Int, errorMessage: String): RuleBuilder {
        rules.add(MinLength(minLength, errorMessage))
        return this
    }

    fun maxLength(maxLength: Int, errorMessage: String): RuleBuilder {
        rules.add(MaxLength(maxLength, errorMessage))
        return this
    }

    fun matchRegex(regex: Regex, errorMessage: String): RuleBuilder {
        rules.add(MatchRegex(regex, errorMessage))
        return this
    }

    fun replace(replace: String, replacement: String = "", errorMessage: String = "", ignoreCase: Boolean = true): RuleBuilder {
        rules.add(ReplaceString(replace, replacement, ignoreCase, errorMessage))
        return this
    }

    fun replace(replace: Regex, replacement: String, errorMessage: String = ""): RuleBuilder {
        rules.add(ReplaceRegex(replace, replacement, errorMessage))
        return this
    }

    fun removeEmoji(replacement: String = ""): RuleBuilder {
        rules.add(ReplaceRegex("[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]".toRegex(), replacement))
        return this
    }
}
