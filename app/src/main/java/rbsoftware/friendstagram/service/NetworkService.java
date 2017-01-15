package rbsoftware.friendstagram.service;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import rbsoftware.friendstagram.model.Error;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by silver_android on 1/5/2017.
 */

public class NetworkService {
    private static final String baseURL = "https://8deb3cb7.ngrok.io";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    static Retrofit getRetrofit() {
        return retrofit;
    }

    public static Error parseError(Response<?> response) {
        Converter<ResponseBody, Error> converter = getRetrofit()
                .responseBodyConverter(Error.class, new Annotation[0]);

        Error error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new Error();
        }

        return error;
    }
}
