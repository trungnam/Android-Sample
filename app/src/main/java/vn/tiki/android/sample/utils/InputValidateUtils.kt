package vn.tiki.android.sample.utils

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Created by trungnam1992 on 5/4/18.
 */
class InputValidateUtils {

    companion object {

        private val EMAIL_PATTERN: Pattern = Patterns.EMAIL_ADDRESS;

        @JvmStatic
        fun isPasswordValid(password: String): Boolean {
            //TODO: Replace this with your own logic
            return password.length > 4
        }

        @JvmStatic
        fun isEmailValid(email: String): Boolean {
            //TODO: Replace this with your own logic

            return EMAIL_PATTERN.matcher(email).matches()
        }
    }

}