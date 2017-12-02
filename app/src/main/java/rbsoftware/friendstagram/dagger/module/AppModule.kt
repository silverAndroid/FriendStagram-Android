package rbsoftware.friendstagram.dagger.module

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Rushil on 8/21/2017.
 */
@Module
open class AppModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context = context
}