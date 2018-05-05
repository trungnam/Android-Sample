package vn.tiki.android.sample.model

import io.realm.RealmObject

/**
 * Created by trungnam1992 on 5/1/18.
 */
open class UserLogin : RealmObject(){
    var email: String? = null
    var password: String? = null
    var phone : String? = null
    var contry : String? = null
}