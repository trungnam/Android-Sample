package vn.tiki.android.sample.ui.contact

import android.content.Context
import android.view.View
import vn.tiki.android.sample.model.UserLogin
import vn.tiki.android.sample.ui.view.BaseView
import vn.tiki.android.sample.utils.LoginRegisterState

/**
 * Created by trungnam1992 on 5/1/18.
 */
class KotlinLoginContact {

    interface KotlinLoginView : BaseView {

        fun showProgress(show: Boolean)
        fun addEmailsToAutoComplete(emailAddressCollection: List<String>)
        fun requestFocusError(view: View, strErr: String?)
        fun enableRegLoginButton(isEnable: Boolean)
        fun setCodePhoneText(code: String)
        fun screenState(): LoginRegisterState
        fun intiViewLoginState()
        fun intiViewRegisterState()
    }

    interface Presenter {

        var context: Context?
        fun attemptLogin()
        fun loadUserEmail()
        fun populateAutoComplete()
        fun requestLogin(user: UserLogin)
        fun mayRequestContacts(): Boolean
        fun doGetContryCode()
        fun validateEmail(strEmail: String, view: View)
        fun validatePassword(strPassword: String, view: View)
        fun validatePhone(strPhone: String, view: View)
        fun combineLastestValidate()
        fun switchState()

    }

}