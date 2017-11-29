package rbsoftware.friendstagram.service

import android.util.Log
import okhttp3.ResponseBody
import rbsoftware.friendstagram.ApplicationConfig
import rbsoftware.friendstagram.model.ErrorResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Created by silver_android on 1/5/2017.
 */

class NetworkService {
    companion object {
        private val TAG = "NetworkService"

        fun parseError(errorBody: ResponseBody?): String? {
            val retrofit = Retrofit.Builder()
                    .baseUrl(ApplicationConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val converter = retrofit.responseBodyConverter<ErrorResponse>(ErrorResponse::class.java, arrayOfNulls(0))

            return try {
                if (errorBody != null) {
                    val error = converter.convert(errorBody).error
                    error.getMessage()
                } else {
                    null
                }
            } catch (e: IOException) {
                Log.e(TAG, "An error occurred", e)
                null
            }
        }
    }
}
