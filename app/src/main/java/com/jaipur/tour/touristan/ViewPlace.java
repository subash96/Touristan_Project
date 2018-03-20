package com.jaipur.tour.touristan;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jaipur.tour.touristan.Model.Photos;
import com.jaipur.tour.touristan.Model.PlaceDetail;
import com.jaipur.tour.touristan.Remote.IGoogleAPIService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlace extends AppCompatActivity {
    ImageView photo;
    RatingBar ratingBar;
    TextView opening_hours,place_address,place_name;
    Button btnViewOnMap;
    IGoogleAPIService mService;
    PlaceDetail mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);
        mService=Common.getGoogleAPIService();
        photo=(ImageView)findViewById(R.id.photo);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        place_address = (TextView)findViewById(R.id.place_address);
        place_name = (TextView)findViewById(R.id.place_name);
        opening_hours=(TextView)findViewById(R.id.place_open_hour);
        btnViewOnMap = (Button)findViewById(R.id.btn_show_map);
        place_name.setText("");
        place_address.setText("");
        opening_hours.setText("");

        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cab = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlace.getResult().getUrl()));
                startActivity(cab);

            }
        });
        if(Common.currentResult.getPhotos()!=null && Common.currentResult.getPhotos().length>0)
        {
            Picasso.with(this).load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(),1000)).placeholder(R.drawable.ic_image).error(R.drawable.ic_error).into(photo);
        }
        if(Common.currentResult.getRating()!=null && !TextUtils.isEmpty(Common.currentResult.getRating()))
        {
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        }
        else
        {
            ratingBar.setVisibility(View.GONE);
        }
        if(Common.currentResult.getOpening_hours()!=null)
        {
            opening_hours.setText("Opened: "+Common.currentResult.getOpening_hours().getOpen_now());
        }
        else
        {
            opening_hours.setVisibility(View.GONE);
        }
        mService.getDetailPlace(getPlaceDetailUrl(Common.currentResult.getPlace_id())).enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                mPlace=response.body();
                place_address.setText(mPlace.getResult().getFormatted_address());
                place_name.setText(mPlace.getResult().getName());
            }

            @Override
            public void onFailure(Call<PlaceDetail> call, Throwable t) {

            }
        });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid="+place_id);
        url.append("&key="+getResources().getString(R.string.browser_key));
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference,int maxWidth)
    {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+maxWidth);
        url.append("&photoreference="+photo_reference);
        url.append("&key="+getResources().getString(R.string.browser_key));
        return url.toString();
    }
}
