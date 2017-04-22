package rbsoftware.friendstagram.service;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import rbsoftware.friendstagram.Constants;
import rbsoftware.friendstagram.model.Error;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by silver_android on 1/5/2017.
 */

public class NetworkService {
    final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(Constants.Application.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit getRetrofit() {
        return retrofitBuilder.build();
    }

    public static Error parseError(Response<?> response) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.Application.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        Converter<ResponseBody, Error> converter = retrofit.responseBodyConverter(Error.class, new Annotation[0]);

        Error error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new Error();
        }

        return error;
    }
}
