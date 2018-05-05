package vn.tiki.android.sample.data

import io.reactivex.Single
import vn.tiki.android.sample.data.local.LocalRepository
import vn.tiki.android.sample.model.UserLogin
import javax.inject.Inject

/**
 * Created by trungnam1992 on 5/5/18.
 */
class DataReponsitory @Inject constructor(
        val localRepository: LocalRepository
) : DataSource {
    override fun registerUser(userLogin: UserLogin): Single<UserLogin> {
       return localRepository.registerUser(userLogin)
    }

    override fun requestLoginUser(userLogin: UserLogin): Single<UserLogin> {
       return localRepository.requestLoginUser(userLogin)
    }

}