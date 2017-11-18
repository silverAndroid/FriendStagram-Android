package rbsoftware.friendstagram.dagger.module

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import rbsoftware.friendstagram.Constants
import rbsoftware.friendstagram.service.AuthenticationService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Rushil on 8/19/2017.
 */
@Module
class ServicesModule {
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Provides
    fun provideRetrofit(authService: AuthenticationService): Retrofit {
        val builder = Retrofit.Builder()
                .baseUrl(Constants.Application.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
        if (authService.hasToken()) {
            val clientBuilder = OkHttpClient.Builder()
            clientBuilder.addInterceptor {
                val originalRequest = it.request()
                val requestBuilder = originalRequest
                        .newBuilder()
                        .addHeader("token", authService.token)
                val request = requestBuilder.build()
                it.proceed(request)
            }
            val client = clientBuilder.build()
            builder.client(client)
        }
        return builder.build()
    }
}