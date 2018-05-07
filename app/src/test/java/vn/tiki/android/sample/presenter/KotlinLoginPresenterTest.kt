package vn.tiki.android.sample.presenter

import android.view.View
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import vn.tiki.android.sample.data.DataReponsitory
import vn.tiki.android.sample.model.UserLogin
import vn.tiki.android.sample.ui.contact.KotlinLoginContact
import vn.tiki.android.sample.utils.LoginRegisterState


/**
 * Created by trungnam1992 on 5/6/18.
 */
@PrepareForTest(CompositeDisposable::class, DataReponsitory::class, PublishSubject::class, View::class, Observable::class,
        Single::class)
@RunWith(PowerMockRunner::class)
class KotlinLoginPresenterTest {
    @Mock
    lateinit var mKotlinLoginPresenterTest: KotlinLoginPresenter
    @Mock
    lateinit var mDataRepository: DataReponsitory
    @Mock
    lateinit var compositeDisposable: CompositeDisposable
    @Mock
    lateinit var view: KotlinLoginContact.KotlinLoginView
    @Mock
    lateinit var mockView: View

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        //for rx testing
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> Schedulers.trampoline() }
        //presenter
        mKotlinLoginPresenterTest = KotlinLoginPresenter(mDataRepository, compositeDisposable)
        mKotlinLoginPresenterTest.attachView(view)
        mockView = mock(View::class.java)

    }

    @Test
    fun `validate single field input test wrapper phone-mail-pass`() {
        mKotlinLoginPresenterTest.subPhone.subscribe { t: String ->
            view.requestFocusError(mockView, "Error expect")
        }
        mKotlinLoginPresenterTest.validatePhone("", mockView)
        verify(view, times(1)).requestFocusError(mockView, "Error expect")
    }

    @Test
    fun `enable button login register`() {
        `when`(view.screenState()).thenReturn(
                LoginRegisterState.LOGIN_STATE
        )
        mKotlinLoginPresenterTest.combineLastestValidate()
        mKotlinLoginPresenterTest.observableCombine = Observable.just(true)
        mKotlinLoginPresenterTest.observableCombine?.subscribe { t: Boolean ->
            view.enableRegLoginButton(true)
        }
        verify(view, times(1)).enableRegLoginButton(true)
    }

    @Test
    fun `request Log Out`() {
        mKotlinLoginPresenterTest.requestLogOut()
        verify(view, times(1)).intiViewLoginState()
        verify(view, times(1)).showProgress(false)
    }

    @Test
    fun `switchState form LOG IN to REGISTER`() {
        `when`(view.screenState()).thenReturn(
                LoginRegisterState.LOGIN_STATE
        )
        mKotlinLoginPresenterTest.switchState()
        verify(view, times(1)).intiViewRegisterState()
        verify(view, times(1)).showErrorLoginOrRegister("", false)
    }

    @Test
    fun `switchState REGISTER to LOG IN`() {
        `when`(view.screenState()).thenReturn(
                LoginRegisterState.REGISTER_STATE
        )
        mKotlinLoginPresenterTest.switchState()
        verify(view, times(1)).intiViewLoginState()
        verify(view, times(1)).showErrorLoginOrRegister("", false)
    }

    @Test
    fun `request Login from Register - state successful`() {
        `when`(view.screenState()).thenReturn(
                LoginRegisterState.REGISTER_STATE
        )
        val stubUser = UserLogin().apply {
            phone = "1234556"
            password = "123456"
            contry = "+84"
        }

        `when`(mDataRepository.registerUser(stubUser)).thenReturn(
                Single.just(stubUser)
        )
        mKotlinLoginPresenterTest.requestLogin(stubUser)
        verify(view, times(2)).showProgress(true)
        verify(view, times(1)).showErrorLoginOrRegister("", false)
        verify(view, times(1)).enableRegLoginButton(true)

    }

    @Test
    fun `request Login from Register - state Error`() {
        `when`(view.screenState()).thenReturn(
                LoginRegisterState.REGISTER_STATE
        )
        val stubUser = UserLogin().apply {
            phone = "1234556"
            password = "123456"
            contry = "+84"
        }

        `when`(mDataRepository.registerUser(stubUser)).thenReturn(
                Single.error(Exception("err msg"))
        )
        mKotlinLoginPresenterTest.requestLogin(stubUser)
        verify(view, times(1)).showProgress(false)
        verify(view, times(1)).showErrorLoginOrRegister("err msg", true)
        verify(view, times(1)).enableRegLoginButton(true)

    }

    @Test
    fun `request Login from Sign in`() {
        `when`(view.screenState()).thenReturn(
                LoginRegisterState.LOGIN_STATE
        )
        val stubUser = UserLogin().apply {
            phone = "1234556"
            password = "123456"
            contry = "+84"
        }

        `when`(mDataRepository.requestLoginUser(stubUser)).thenReturn(
                Single.just(stubUser))

        mKotlinLoginPresenterTest.requestLogin(stubUser)
        verify(view, times(2)).showProgress(true)
        verify(view, times(1)).showErrorLoginOrRegister("", false)
        verify(view, times(1)).enableRegLoginButton(true)

    }

    @Test
    fun `request Login from Sign in state error`() {
        `when`(view.screenState()).thenReturn(
                LoginRegisterState.LOGIN_STATE
        )
        val stubUser = UserLogin().apply {
            phone = "1234556"
            password = "123456"
            contry = "+84"
        }

        `when`(mDataRepository.requestLoginUser(stubUser)).thenReturn(
                Single.error(Exception("err msg")))

        mKotlinLoginPresenterTest.requestLogin(stubUser)
        verify(view, times(1)).showProgress(false)
        verify(view, times(1)).showErrorLoginOrRegister("err msg", true)
        verify(view, times(1)).enableRegLoginButton(true)

    }
}