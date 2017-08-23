package rbsoftware.friendstagram.dagger.component

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.Component
import rbsoftware.friendstagram.viewmodel.UserViewModel
import rbsoftware.friendstagram.dagger.module.AppModule
import rbsoftware.friendstagram.dagger.module.ServicesModule
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.viewmodel.PostViewModel
import javax.inject.Singleton

/**
 * Created by Rushil on 8/19/2017.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, ServicesModule::class))
interface ServicesComponent {
    fun inject(activity: AppCompatActivity)
    fun inject(fragment: Fragment)
    fun userViewModel(): UserViewModel
    fun postViewModel(): PostViewModel
    fun authService(): AuthenticationService
}