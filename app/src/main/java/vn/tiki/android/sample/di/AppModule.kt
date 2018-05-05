package vn.tiki.android.sample.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import javax.inject.Singleton

/**
 * Created by trungnam1992 on 5/1/18.
 */
@Module
class AppModule {

    lateinit var mApplication: Application
    fun AppModule(mApplication: Application) {
        this.mApplication = mApplication
    }

    @Provides
    @Singleton
    internal fun provideApplication(appModule: AppModule): Application {
        return appModule.mApplication
    }

    @Provides
    @Singleton
    fun providerCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

    @Provides
    @Singleton
    fun providerRealm(): Realm {
        return Realm.getDefaultInstance()
    }

}