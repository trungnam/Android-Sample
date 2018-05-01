package vn.tiki.android.sample

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import vn.tiki.android.sample.presenter.KotlinLoginPresenter
import vn.tiki.android.sample.ui.contact.KotlinLoginContact
import vn.tiki.android.sample.ui.view.BaseActivity
import javax.inject.Inject

@SuppressLint("Registered")
/**
 * Created by trungnam1992 on 5/1/18.
 */
class KotlinLoginActivity : BaseActivity(), KotlinLoginContact.KotlinLoginView {

    @Inject
    lateinit var mPresenter: KotlinLoginPresenter

    companion object {
        const val REQUEST_READ_CONTACTS = 0
    }

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun initializeDagger() {
        App.appComponent.inject(this@KotlinLoginActivity)
    }

    override fun initializePresenter() {
        mPresenter.attachView(this@KotlinLoginActivity)
        mPresenter.context = this
        populateAutoComplete()
    }

    override fun showProgress(show: Boolean) {

    }

    override fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {

    }

    override fun requestFocusError(view: View, strErr: String) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader<Cursor>(0, null, mPresenter)
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               populateAutoComplete()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun mayRequestContacts(): Boolean {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> return true
            else -> {
                when {
                    checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED -> return true
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> Snackbar.make(email, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok) { requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS) }
                    else -> requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
                }
                return false
            }
        }

    }
}