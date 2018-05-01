package vn.tiki.android.sample.di

import dagger.Component
import vn.tiki.android.sample.KotlinLoginActivity
import javax.inject.Singleton

/**
 * Created by trungnam1992 on 5/1/18.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent{

    fun inject(activity: KotlinLoginActivity)
}
