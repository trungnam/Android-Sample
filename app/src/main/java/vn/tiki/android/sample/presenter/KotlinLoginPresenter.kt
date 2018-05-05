package vn.tiki.android.sample.presenter

import android.Manifest.permission.READ_CONTACTS
import android.accounts.AccountManager
import android.app.LoaderManager.LoaderCallbacks
import android.content.Context
import android.content.CursorLoader
import android.content.Loader
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mukesh.countrypicker.Country
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.OnCountryPickerListener
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import vn.tiki.android.sample.R
import vn.tiki.android.sample.data.DataReponsitory
import vn.tiki.android.sample.model.UserLogin
import vn.tiki.android.sample.ui.KotlinLoginActivity
import vn.tiki.android.sample.ui.KotlinLoginActivity.Companion.REQUEST_READ_CONTACTS
import vn.tiki.android.sample.ui.contact.KotlinLoginContact
import vn.tiki.android.sample.utils.InputValidateUtils
import vn.tiki.android.sample.utils.LoginRegisterState.LOGIN_STATE
import vn.tiki.android.sample.utils.LoginRegisterState.REGISTER_STATE
import vn.tiki.android.sample.utils.ProfileQuery.Companion.PROJECTION
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by trungnam1992 on 5/1/18.
 */
class KotlinLoginPresenter @Inject constructor(

        val dataReponsitory: DataReponsitory

) : BasePresenter<KotlinLoginContact.KotlinLoginView>(), KotlinLoginContact.Presenter, LoaderCallbacks<Cursor>, OnCountryPickerListener {

    override var context: Context? = null

    private lateinit var mView: KotlinLoginContact.KotlinLoginView

    private val isvailidEmail = PublishSubject.create<Boolean>()
    private val isvailidPassword = PublishSubject.create<Boolean>()
    private val isvailidPhone = PublishSubject.create<Boolean>()

    private val subEmail = PublishSubject.create<String>()
    private val subPassword = PublishSubject.create<String>()
    private val subPhone = PublishSubject.create<String>()

    var observableCombine: Observable<Boolean>? = null
    lateinit var subscribeRegisterCombine: Disposable

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {
        const val LONG_TIME_BUFFER = 300L
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
        subEmail.onNext(strEmail)
        val disposableMail = subEmail.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)
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
                .doOnDispose {
                    mView.requestFocusError(view, null)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    mView.requestFocusError(view, null)
                    isvailidEmail.onNext(true)

                }, { t: Throwable ->
                    mView.requestFocusError(view, t.message)

                })
        compositeDisposable.add(disposableMail)
    }

    override fun validatePassword(strPassword: String, view: View) {
        subPassword.onNext(strPassword)
        val disposablePass = subPassword.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)
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
                .doOnDispose {
                    mView.requestFocusError(view, null)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    mView.requestFocusError(view, null)
                    isvailidPassword.onNext(true)

                }, { t: Throwable ->
                    mView.requestFocusError(view, t.message)

                })

        compositeDisposable.add(disposablePass)
    }

    override fun validatePhone(strPhone: String, view: View) {
        subPhone.onNext(strPhone)
        val disposablePhone = subPhone.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)
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
                        !InputValidateUtils.isPhoneValid(it) -> {
                            throw Throwable(context?.getString(R.string.error_invalid_phone))
                        }
                        else -> return@map it
                    }
                }
                .doOnError {
                    isvailidPhone.onNext(false)
                }
                .doOnDispose {
                    mView.requestFocusError(view, null)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    mView.requestFocusError(view, null)
                    isvailidPhone.onNext(true)
                }, { t: Throwable ->
                    mView.requestFocusError(view, t.message)
                })
        compositeDisposable.add(disposablePhone)
    }

    //validate field for enable button login
    override fun combineLastestValidate() {
        when (mView.screenState()) {
            REGISTER_STATE -> observableCombine = Observable
                    .combineLatest(isvailidEmail, isvailidPhone, isvailidPassword,
                            Function3 { t1, t2, t3 ->
                                return@Function3 (t1 && t2 && t3)
                            }
                    )
            LOGIN_STATE -> observableCombine = Observable
                    .combineLatest(isvailidPhone, isvailidPassword,
                            BiFunction { t1, t2 ->
                                return@BiFunction (t1 && t2)
                            }
                    )
        }

        observableCombine?.let {
            subscribeRegisterCombine = it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ t: Boolean ->
                        mView.enableRegLoginButton(t)

                    }, { t: Throwable ->
                        Log.e("nnam ", " " + t.message)
                    })
        }
    }

    //get phone code
    override fun doGetContryCode() {
        context?.let {
            val countryPicker = CountryPicker.Builder().with(it)
                    .listener(this)
                    .build()
            countryPicker.showDialog((context as KotlinLoginActivity).supportFragmentManager);
        }

    }

    override fun onSelectCountry(contry: Country?) {
        contry?.apply {
            mView.setCodePhoneText(dialCode)
        }
    }

    override fun switchState() {


        isvailidPassword.onNext(false)
        isvailidPhone.onNext(false)
        isvailidEmail.onNext(false)
        compositeDisposable.clear()

        subPhone.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)
        subPassword.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)
        subEmail.debounce(LONG_TIME_BUFFER, TimeUnit.MILLISECONDS)

        when (REGISTER_STATE) {
            mView.screenState() -> {
                mView.intiViewLoginState()
                combineLastestValidate()

            }
            else -> {
                mView.intiViewRegisterState()
                combineLastestValidate()
            }
        }

    }

    override fun mayRequestContacts(): Boolean {
        val activity = (context as KotlinLoginActivity)
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> return true
            else -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    when {
                        activity.checkSelfPermission(READ_CONTACTS) == PERMISSION_GRANTED -> {
                            return true
                        }
                        activity.shouldShowRequestPermissionRationale(READ_CONTACTS) -> {
                            Snackbar.make(activity.findViewById(android.R.id.content)
                                    , R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(android.R.string.ok) {
                                        activity.requestPermissions(arrayOf<String>(READ_CONTACTS), REQUEST_READ_CONTACTS)
                                    }
                        }
                        else -> {
                            activity.requestPermissions(arrayOf(READ_CONTACTS), KotlinLoginActivity.REQUEST_READ_CONTACTS)
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
        mView.showProgress(true)

        val observer = object : SingleObserver<UserLogin> {
            override fun onSuccess(t: UserLogin) {
                Toast.makeText(context, "Login success " + t.phone, Toast.LENGTH_LONG).show()
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {

                Toast.makeText(context, "Error " + e.message, Toast.LENGTH_LONG).show()

            }

        }

        when (mView.screenState()) {
            is LOGIN_STATE -> {
                dataReponsitory.requestLoginUser(user)
                        .doOnError({ t ->
                            mView.showProgress(false)
                        })
                        .doOnSuccess {
                            mView.showProgress(false)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(observer)
            }
            is REGISTER_STATE -> {
                dataReponsitory.registerUser(user)
                        .doOnError({ t ->
                            mView.showProgress(false)
                        })
                        .doOnSuccess {
                            mView.showProgress(false)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(observer)
            }
        }
    }
}