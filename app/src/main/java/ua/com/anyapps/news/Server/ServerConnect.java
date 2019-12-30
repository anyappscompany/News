package ua.com.anyapps.news.Server;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerConnect {
    private static ServerConnect mInstance;
    private static final String BASE_URL = "http://anyapps.cf/news/api/";
    private Retrofit mRetrofit;

    private ServerConnect() {

        // logs
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor);

        //

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
    }

    public static ServerConnect getInstance() {
        if (mInstance == null) {
            mInstance = new ServerConnect();
        }
        return mInstance;
    }

    public ServerApi getJSONApi() {
        return mRetrofit.create(ServerApi.class);
    }
}
