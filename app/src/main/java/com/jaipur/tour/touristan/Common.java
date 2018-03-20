package com.jaipur.tour.touristan;

import com.jaipur.tour.touristan.Model.MyPlaces;
import com.jaipur.tour.touristan.Model.Results;
import com.jaipur.tour.touristan.Remote.IGoogleAPIService;
import com.jaipur.tour.touristan.Remote.RetrofitClient;

/**
 * Created by Rrohi on 14-03-2018.
 */

public class Common {
    public static Results currentResult;
    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";
    public static IGoogleAPIService getGoogleAPIService()
    {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
