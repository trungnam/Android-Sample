package vn.tiki.android.sample.presenter

import android.Manifest
import android.Manifest.permission.READ_CONTACTS
import android.accounts.AccountManager
import android.app.LoaderManager.LoaderCallbacks
import android.content.Context
import android.content.CursorLoader
import android.content.Loader
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.support.design.widget.Snackbar
import android.util.Patterns
import vn.tiki.android.sample.R
import vn.tiki.android.sample.ui.KotlinLoginActivity
import vn.tiki.android.sample.ui.KotlinLoginActivity.Companion.REQUEST_READ_CONTACTS
import vn.tiki.android.sample.ui.contact.KotlinLoginContact
import vn.tiki.android.sample.utils.ProfileQuery.Companion.PROJECTION
import java.util.*
import javax.inject.Inject


/**
 * Created by trungnam1992 on 5/1/18.
 */
class KotlinLoginPresenter @Inject constructor() : BasePresenter<KotlinLoginContact.KotlinLoginView>(), KotlinLoginContact.Presenter, LoaderCallbacks<Cursor> {
    override var context: Context? = null

    lateinit var mView: KotlinLoginContact.KotlinLoginView

    companion object {
//        val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }

    val mAccountArr = ArrayList<String>()

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

    override fun loadUserEmail() {
        try {
            val account = AccountManager.get(context).accounts
            account?.let {
                for (accountUser in it) {
                    val pattern = Patterns.EMAIL_ADDRESS
                    if (pattern.matcher(accountUser.name).matches()) {
                        mAccountArr.add(accountUser.name)
                        mView.addEmailsToAutoComplete(mAccountArr)
                    }
                }
            }

        } catch (e: SecurityException) {
            //load email err do nothing
        }
    }

    override fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }
        //load current mail
        loadUserEmail()

        //list contact mail
        (context as KotlinLoginActivity).loaderManager.initLoader<Cursor>(0, null, this)
    }

    override fun mayRequestContacts(): Boolean {
        val activity = (context as KotlinLoginActivity)
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> return true
            else -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when {
                        activity.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED -> {
                            return true
                        }
                        activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                            Snackbar.make(activity.findViewById(android.R.id.content), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(android.R.string.ok) {
                                        activity.requestPermissions(arrayOf<String>(READ_CONTACTS), REQUEST_READ_CONTACTS)
                                    }
                        }
                        else -> {
                            activity.requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), KotlinLoginActivity.REQUEST_READ_CONTACTS)
                        }
                    }
                }

                return false
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(context,
                Email.CONTENT_URI,
                PROJECTION, // the selection args (none)
                null // the sort order (default)
                , null
                , null
        )
    }


    override fun onLoadFinished(loader: Loader<Cursor>?, cursor: Cursor?) {
        cursor?.apply {
            moveToFirst()
            val contactEmailColumnIndex = getColumnIndex(Email.DATA)
            while (!isAfterLast) {
                mAccountArr.add(getString(contactEmailColumnIndex))
                moveToNext()
            }
        }
        mView.addEmailsToAutoComplete(mAccountArr)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        // nothing to do
    }

    override fun reQuestLogin(email: String, password: String, phone: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}