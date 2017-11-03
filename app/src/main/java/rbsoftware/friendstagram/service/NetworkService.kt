package rbsoftware.friendstagram.service

import java.io.IOException

import okhttp3.ResponseBody
import rbsoftware.friendstagram.Constants
import rbsoftware.friendstagram.model.ErrorResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by silver_android on 1/5/2017.
 */

class NetworkService {
    companion object {
        fun parseError(error: ResponseBody?): String? {
            val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.Application.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val converter = retrofit.responseBodyConverter<ErrorResponse>(ErrorResponse::class.java, arrayOfNulls(0))

            return try {
                if (error != null) {
                    val errors = converter.convert(error).errors
                    if (errors.isNotEmpty()) {
                        errors[0].title
                    } else {
                        null
                    }
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
        }
    }
}
