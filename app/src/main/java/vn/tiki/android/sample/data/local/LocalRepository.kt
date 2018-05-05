package vn.tiki.android.sample.data.local

import io.reactivex.Single
import io.reactivex.Single.create
import io.reactivex.SingleEmitter
import io.realm.Realm
import io.realm.exceptions.RealmException
import vn.tiki.android.sample.model.UserLogin
import javax.inject.Inject

/**
 * Created by trungnam1992 on 5/5/18.
 */
class LocalRepository @Inject constructor(realm: Realm) : LocalResource {

    private val mRealm: Realm = realm

    override fun registerUser(userLogin: UserLogin): Single<UserLogin> {


        return create {

            singleEmitter ->
            val arr = ArrayList<UserLogin>()

            mRealm.where(UserLogin::class.java)
                    .equalTo("phone", userLogin.phone)
                    .equalTo("contry", userLogin.contry)
                    .or()
                    .equalTo("email", userLogin.email)
                    .findAll().apply {
                        this.forEach { st: UserLogin -> arr.add(st) }
                    }
            when {
                arr.size > 0 -> {
                    singleEmitter.onError(throw Exception("This Phone number or Email has been registered !"))
                }
                else -> {
                    mRealm.executeTransaction({ realm ->
                        try {
                            val user = mRealm.createObject<UserLogin>(UserLogin::class.java)
                            user.password = userLogin.password
                            user.email = userLogin.email
                            user.phone = userLogin.phone
                            user.contry = userLogin.contry

                            realm.copyToRealm(user)
                            singleEmitter.onSuccess(user)

                        } catch (e: RealmException) {
                            singleEmitter.onError(throw Exception("Can not register User"))
                        }
                    })
                }
            }
        }
    }

    override fun requestLoginUser(userLogin: UserLogin): Single<UserLogin> {
        return create {

            singleEmitter: SingleEmitter<UserLogin> ->
            val arr = ArrayList<UserLogin>()
            mRealm.where(UserLogin::class.java)
                    .equalTo("phone", userLogin.phone)
                    .equalTo("password", userLogin.password)
                    .equalTo("contry", userLogin.contry)
                    .findAll().apply {
                        this.forEach { st: UserLogin -> arr.add(st) }
                    }
            when {
                arr.size == 1 -> singleEmitter.onSuccess(arr[0])
                else -> singleEmitter.onError(throw Exception("Wrong Phone number or Password"))
            }

        }

    }


}
