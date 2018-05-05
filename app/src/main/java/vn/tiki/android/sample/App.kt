package vn.tiki.android.sample

import android.support.multidex.MultiDexApplication
import io.realm.Realm
import io.realm.RealmConfiguration
import vn.tiki.android.sample.di.AppComponent
import vn.tiki.android.sample.di.DaggerAppComponent

/**
 * Created by trungnam1992 on 5/1/18.
 */

class App : MultiDexApplication() {

    companion object {
        @JvmStatic
        lateinit var appComponent: AppComponent
        lateinit var realmConfiguration : RealmConfiguration

    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
        //set up Realm
        Realm.init(this)
        realmConfiguration = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }


}
