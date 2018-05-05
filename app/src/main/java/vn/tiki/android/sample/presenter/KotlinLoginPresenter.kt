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
import android.text.TextUtils
import android.util.Log
import android.view.View
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import vn.tiki.android.sample.R
import vn.tiki.android.sample.model.UserLogin
import vn.tiki.android.sample.ui.KotlinLoginActivity
import vn.tiki.android.sample.ui.KotlinLoginActivity.Companion.REQUEST_READ_CONTACTS
import vn.tiki.android.sample.ui.contact.KotlinLoginContact
import vn.tiki.android.sample.utils.InputValidateUtils
import vn.tiki.android.sample.utils.ProfileQuery.Companion.PROJECTION
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by trungnam1992 on 5/1/18.
 */
class KotlinLoginPresenter @Inject constructor() : BasePresenter<KotlinLoginContact.KotlinLoginView>(), KotlinLoginContact.Presenter, LoaderCallbacks<Cursor> {

    override var context: Context? = null

    lateinit var mView: KotlinLoginContact.KotlinLoginView
    val isvailidEmail = PublishSubject.create<Boolean>()
    val isvailidPassword = PublishSubject.create<Boolean>()
    val isvailidPhone = PublishSubject.create<Boolean>()
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {
        const val LONG_TIME_BUFFER = 500L
    }

    private val mAccountArr = ArrayList<String>()

    override fun detachView(view: KotlinLoginContact.KotlinLoginView) {
        compositeDisposable.clear()
    }

    override fun attachView(view: KotlinLoginContact.KotlinLoginView) {
        mView = view

        //disable login for first time load
        Observable.just(false)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    isvailidPassword.onNext(false)
                    isvailidPhone.onNext(false)
                    isvailidEmail.onNext(false)
                })
    }

    override fun attemptLogin() = Unit

    override fun loadUserEmail() {

        try {
            val account = AccountManager.get(context).accounts
            account?.let {
                for (accountUser in it) {
                    if (InputValidateUtils.isEmailValid(accountUser.name)) {
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

    override fun validateEmail(strEmail: String, view: View) {
        val sub = PublishSubject.create<String> { e ->
            e.onNext(strEmail)
        }
        sub.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)
                .map {
                    when {
                        TextUtils.isEmpty(it) -> {
                            throw Throwable(context?.getString(R.string.error_field_required))
                        }
                        else -> return@map it
                    }
                }
                .map {
                    when {
                        !InputValidateUtils.isEmailValid(it) -> {
                            throw Throwable(context?.getString(R.string.error_invalid_email))
                        }
                        else -> return@map it
                    }
                }

                .doOnError {
                    isvailidEmail.onNext(false)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    mView.requestFocusError(view, null)
                    isvailidEmail.onNext(true)

                }, { t: Throwable ->
                    mView.requestFocusError(view, t.message)

                })
//        compositeDisposable.add()
    }

    override fun validatePassword(strPassword: String, view: View) {
        val sub = PublishSubject.create<String> { e ->
            e.onNext(strPassword)
        }

        sub.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)
                .map {
                    when {
                        TextUtils.isEmpty(it) -> {
                            throw Throwable(context?.getString(R.string.error_field_required))
                        }
                        else -> return@map it
                    }
                }
                .map {
                    when {
                        !InputValidateUtils.isPasswordValid(it) -> {
                            throw Throwable(context?.getString(R.string.error_invalid_password))
                        }
                        else -> return@map it
                    }
                }
                .doOnError {
                    isvailidPassword.onNext(false)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    mView.requestFocusError(view, null)
                    isvailidPassword.onNext(true)

                }, { t: Throwable ->
                    mView.requestFocusError(view, t.message)

                })
    }

    override fun validatePhone(strPhone: String, view: View) {
        val sub = PublishSubject.create<String> { e ->
            e.onNext(strPhone)
        }
        sub.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)
                .map {
                    when {
                        TextUtils.isEmpty(it) -> {
                            throw Throwable(context?.getString(R.string.error_field_required))
                        }
                        else -> return@map it
                    }
                }
                .map {
                    when {
                        !InputValidateUtils.isPasswordValid(it) -> {
                            throw Throwable(context?.getString(R.string.error_invalid_phone))
                        }
                        else -> return@map it
                    }
                }
                .doOnError {
                    isvailidPhone.onNext(false)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    mView.requestFocusError(view, null)
                    isvailidPhone.onNext(true)
                }, { t: Throwable ->
                    mView.requestFocusError(view, t.message)

                })
    }

    //validate 3 field for enable button login
    override fun combineLastestVailate() {

        val observableCombine: Observable<Boolean> = Observable
                .combineLatest(isvailidEmail, isvailidPhone, isvailidPassword,
                        Function3 { t1, t2, t3 ->
                            return@Function3 (t1 && t2 && t3)
                        }
                )
        observableCombine.let {
            val subscribe = it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ t: Boolean ->
                        mView.enableLoginButton(t)

                    }, { t: Throwable ->
                        Log.e("nnam ", " " + t.message)
                    })
            compositeDisposable.add(subscribe)
        }
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

    override fun requestLogin(user: UserLogin) {

        Single.just(user)
                .doOnError({ t ->
                    //
                    mView.showProgress(false)
                })
                .doAfterSuccess({ _ ->
                    //
                    mView.showProgress(false)
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { t ->
                            // handle logic login here
                        },
                        { t: Throwable ->
                            // throw Ex msg

                        }
                )

    }
}