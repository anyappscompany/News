package ua.com.anyapps.news.Server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ua.com.anyapps.news.Server.SearchTrendsForDay.FullNews;
import ua.com.anyapps.news.Server.SearchTrendsForDay.SearchTrendsForDay;

public interface ServerApi {
    @GET("getnews/{country}")
    Call<List<SearchTrendsForDay>> getnews(@Path("country") String country);

    @GET("getfullnews/{uniquestr}")
    Call<FullNews> getfullnews(@Path("uniquestr") String country);
}
