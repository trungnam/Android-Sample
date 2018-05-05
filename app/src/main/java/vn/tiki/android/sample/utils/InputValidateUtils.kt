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
            return password.length > 4
        }

        @JvmStatic
        fun isEmailValid(email: String): Boolean {
            return EMAIL_PATTERN.matcher(email).matches()
        }

        @JvmStatic
        fun isPhoneValid(phone: String): Boolean {
            return phone.length > 4
        }
    }

}