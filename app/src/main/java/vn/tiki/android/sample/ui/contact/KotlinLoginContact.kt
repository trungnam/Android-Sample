package vn.tiki.android.sample.ui.contact

import android.content.Context
import android.view.View
import vn.tiki.android.sample.ui.view.BaseView

/**
 * Created by trungnam1992 on 5/1/18.
 */
class KotlinLoginContact {

    interface KotlinLoginView : BaseView {

        fun showProgress(show: Boolean)
        fun addEmailsToAutoComplete(emailAddressCollection: List<String>)
        fun requestFocusError(view: View, strErr: String)

    }

    interface Presenter {

        var context: Context?
        fun isEmailValid(email: String)
        fun isPasswordValid(password: String)
        fun attemptLogin()
        fun loadUserEmail()
        fun populateAutoComplete()
        fun reQuestLogin(email: String, password: String, phone : String)
        fun mayRequestContacts(): Boolean

    }

}