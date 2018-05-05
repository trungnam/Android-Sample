package vn.tiki.android.sample.utils

/**
 * Created by trungnam1992 on 5/5/18.
 */
sealed class LoginRegisterState{
    object LOGIN_STATE : LoginRegisterState()
    object REGISTER_STATE : LoginRegisterState()
}