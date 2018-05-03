package vn.tiki.android.sample.utils

import android.provider.ContactsContract

/**
 * Created by trungnam1992 on 5/1/18.
 */
interface ProfileQuery {
    companion object {

        val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)

    }
}