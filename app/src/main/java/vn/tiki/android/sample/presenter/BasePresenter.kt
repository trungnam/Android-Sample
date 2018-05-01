package vn.tiki.android.sample.presenter

import android.os.Bundle
import vn.tiki.android.sample.ui.view.BaseView

/**
 * Created by trungnam1992 on 5/1/18.
 */
abstract class BasePresenter<in T : BaseView>{

    abstract fun detachView(view: T)
    abstract fun attachView(view: T)

    open fun initialize (extras: Bundle) {}

}