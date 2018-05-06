package vn.tiki.android.sample.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import vn.tiki.android.sample.App
import vn.tiki.android.sample.R
import vn.tiki.android.sample.model.UserLogin
import vn.tiki.android.sample.presenter.KotlinLoginPresenter
import vn.tiki.android.sample.ui.contact.KotlinLoginContact
import vn.tiki.android.sample.ui.view.BaseActivity
import vn.tiki.android.sample.utils.LoginRegisterState
import vn.tiki.android.sample.utils.LoginRegisterState.LOGIN_STATE
import vn.tiki.android.sample.utils.LoginRegisterState.REGISTER_STATE
import javax.inject.Inject


@SuppressLint("Registered")
/**
 * Created by trungnam1992 on 5/1/18.
 */
class KotlinLoginActivity : BaseActivity(), KotlinLoginContact.KotlinLoginView {


    @Inject
    lateinit var mPresenter: KotlinLoginPresenter

    var screenState: LoginRegisterState = REGISTER_STATE

    companion object {
        const val REQUEST_READ_CONTACTS = 0
    }

    override val layoutId: Int
        get() = R.layout.activity_login


    override fun initializeDagger() {
        App.appComponent.inject(this@KotlinLoginActivity)
    }

    override fun initializePresenter() {
        mPresenter.attachView(this@KotlinLoginActivity)
        mPresenter.context = this
        mPresenter.populateAutoComplete()

        switchLoginRegisterText.setOnClickListener({
            mPresenter.switchState()
        })

        //Login or register click
        btnSignIn.setOnClickListener({
            //handle login
            val user = UserLogin().apply {
                email = emailAutoCompleteText.text.toString()
                password = passwordEditText.text.toString()
                phone = phoneAutoCompleteText.text.toString()
                contry = selectContryNumberButton.text.toString()
            }
            mPresenter.requestLogin(user)
        })
        //Log out Click
        btnSignOut.setOnClickListener(View.OnClickListener {
            mPresenter.requestLogOut()
        })

        selectContryNumberButton.setOnClickListener { _ ->
            mPresenter.doGetContryCode()
        }

        phoneAutoCompleteText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter.validatePhone(s.toString(), phoneAutoCompleteText)
            }
        })
        emailAutoCompleteText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter.validateEmail(s.toString(), emailAutoCompleteText)
            }
        })
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter.validatePassword(s.toString(), passwordEditText)
            }
        })
        mPresenter.combineLastestValidate()

    }

    override fun screenState(): LoginRegisterState {
        return screenState
    }

    override fun setCodePhoneText(code: String) {
        selectContryNumberButton.text = code
    }

    override fun showErrorLoginOrRegister(strErr: String, isError: Boolean) {
        when {
            isError -> {
                errorTextView.visibility = View.VISIBLE
                errorTextView.text = strErr
            }
            else -> {
                errorTextView.text = ""
                errorTextView.visibility = View.GONE
            }
        }
    }

    //Login - sign in mode
    override fun intiViewLoginState() {
        screenState = LOGIN_STATE
        emailAutoCompleteText.text.clear()
        emailAutoCompleteText.visibility = View.GONE
        emailAutoCompleteText.error = null
        phoneAutoCompleteText.text.clear()
        phoneAutoCompleteText.error = null
        passwordEditText.text.clear()
        passwordEditText.error = null
        btnSignIn.text = getString(R.string.action_sign_in_short)
        switchLoginRegisterText.text = getString(R.string.action_register_new_account)
        screenStateTextView.text = getString(R.string.state_sign_in_account)
    }

    //Login - Register  mode
    override fun intiViewRegisterState() {
        screenState = REGISTER_STATE
        emailAutoCompleteText.visibility = View.VISIBLE
        emailAutoCompleteText.text.clear()
        emailAutoCompleteText.error = null
        phoneAutoCompleteText.text.clear()
        phoneAutoCompleteText.error = null
        passwordEditText.text.clear()
        passwordEditText.error = null
        btnSignIn.text = getString(R.string.action_register_in)
        switchLoginRegisterText.text = getString(R.string.action_sign_in_if_have_account)
        screenStateTextView.text = getString(R.string.state_register_new_account)
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            loginFromView.visibility = if (show) View.GONE else View.VISIBLE
            loginFromView
                    .animate()
                    .setDuration(shortAnimTime.toLong())
                    .alpha(
                            when {
                                show -> 0
                                else -> 1
                            }.toFloat()
                    )
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            loginFromView.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            progressView.visibility = if (show) View.VISIBLE else View.GONE
            progressView.animate()
                    .setDuration(shortAnimTime.toLong())
                    .alpha(
                            when {
                                show -> 1
                                else -> 0
                            }.toFloat()
                    )
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            progressView.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
            layoutLoginSuccessful.visibility = if (show) View.VISIBLE else View.GONE
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.visibility = if (show) View.VISIBLE else View.GONE
            loginFromView.visibility = if (show) View.GONE else View.VISIBLE
            layoutLoginSuccessful.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    override fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        val emailAdapter = ArrayAdapter(this@KotlinLoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)
        emailAutoCompleteText.setAdapter(emailAdapter)
    }

    override fun requestFocusError(view: View, strErr: String?) {
        when (view) {
            is TextView -> {
                view.error = strErr
                view.requestFocus()
            }
        }
    }

    override fun enableRegLoginButton(isEnable: Boolean) {
        btnSignIn.isEnabled = isEnable
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.populateAutoComplete()
            }
        }
    }

}