package com.jaipur.tour.touristan.Remote;
import com.jaipur.tour.touristan.Model.MyPlaces;
import com.jaipur.tour.touristan.Model.PlaceDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Rrohi on 14-03-2018.
 */

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetailPlace(@Url String url);
}
