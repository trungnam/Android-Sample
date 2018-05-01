package vn.tiki.android.sample.presenter

import android.app.LoaderManager.LoaderCallbacks
import android.content.Context
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import vn.tiki.android.sample.ui.contact.KotlinLoginContact
import vn.tiki.android.sample.utils.ProfileQuery
import java.util.*
import javax.inject.Inject

/**
 * Created by trungnam1992 on 5/1/18.
 */
class KotlinLoginPresenter @Inject constructor() : BasePresenter<KotlinLoginContact.KotlinLoginView>(), KotlinLoginContact.Presenter, LoaderCallbacks<Cursor> {
    override var context: Context? = null

    lateinit var mView: KotlinLoginContact.KotlinLoginView

    companion object {
        val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }

    override fun detachView(view: KotlinLoginContact.KotlinLoginView) {

    }

    override fun attachView(view: KotlinLoginContact.KotlinLoginView) {
        mView = view
    }

    ///logic impl

    override fun isEmailValid(email: String) {
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isEmpty(it)) {
//            var ex = Exception("not vailid")
//            throw ex
//        }
    }

    override fun isPasswordValid(password: String) {

    }

    override fun attemptLogin() {

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(context,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }


    override fun onLoadFinished(loader: Loader<Cursor>?, cursor: Cursor?) {
        val emails = ArrayList<String>()
        cursor?.apply {
            moveToFirst()
            while (!isAfterLast) {
                emails.add(getString(ProfileQuery.ADDRESS))
                moveToNext()
            }
        }
        mView.addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        // nothing to do
    }
}