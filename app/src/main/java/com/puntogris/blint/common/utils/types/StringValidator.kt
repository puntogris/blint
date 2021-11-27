package com.puntogris.blint.common.utils.types

import androidx.annotation.StringRes
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.containsInvalidCharacters

sealed class StringValidator {
    class Valid(val value: String) : StringValidator()
    class NotValid(@StringRes val error: Int) : StringValidator()

    companion object {
        fun from(
            text: String,
            minLength: Int = 3,
            maxLength: Int = 20,
            allowSpecialChars: Boolean = false,
            isName: Boolean = false
        ): StringValidator {
            return when {
                text.isBlank() ->
                    NotValid(if (isName) R.string.snack_name_empty else R.string.snack_text_empty)
                !allowSpecialChars && text.containsInvalidCharacters() ->
                    NotValid(
                        if (isName) R.string.snack_name_cant_contain_special_chars
                        else R.string.snack_the_text_cant_contain_special_chars
                    )
                text.length !in (minLength..maxLength) ->
                    NotValid(
                        if (isName) R.string.snack_name_min_max_length
                        else R.string.snack_text_min_max_length
                    )
                else -> Valid(text)
            }
        }
    }
}