package com.devtrae.parasolarexperience;


import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WeatherDisplay extends AppCompatActivity {
    private TextView cityText_tv;
    private TextView condDescr_tv;
    private TextView temp_tv;
    private TextView pressure_tv;
    private TextView wind_tv;
    private TextView windDeg_tv;

    private TextView hum;
    private ImageView imgView;
    Bitmap bitmap;

    private static final String TAG = "WeatherDisplay";
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private Button mUpdateLocationButton;
    private Button mSecurityModeButton;


    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        cityText_tv = (TextView) findViewById(R.id.cityText);
        condDescr_tv = (TextView) findViewById(R.id.condDescr);
        temp_tv = (TextView) findViewById(R.id.temp);
        hum = (TextView) findViewById(R.id.hum);
        pressure_tv = (TextView) findViewById(R.id.press);
        wind_tv = (TextView) findViewById(R.id.windSpeed);
        //windDeg_tv = (TextView) findViewById(R.id.windDeg);
        imgView = (ImageView) findViewById(R.id.condIcon);

        mLatitudeTextView = (TextView) findViewById(R.id.latitude_textview);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_textview);
        mUpdateLocationButton = (Button) findViewById(R.id.updatelocation_btn);
        mSecurityModeButton = (Button) findViewById(R.id.securitymode_btn);

        requestPermission();

        client = LocationServices.getFusedLocationProviderClient(this);

        mUpdateLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(WeatherDisplay.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                client.getLastLocation().addOnSuccessListener(WeatherDisplay.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        updateLocation(location);
                    }
                });
            }
        });

        mSecurityModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WeatherDisplay.this, DeviceList.class);
                startActivity(intent);
            }
        });
    }

    private void updateLocation(Location location){
        if(location!=null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.d(TAG, "Setting Location TextViews.");
            mLatitudeTextView.setText(String.valueOf(latitude));
            mLongitudeTextView.setText(String.valueOf(longitude));
            Log.d(TAG, "Location TextViews are set.");
            Log.d(TAG, "Entering find_weather method.");
            find_weather(String.valueOf(latitude), String.valueOf(longitude));
            Log.d(TAG, "Exited find_weather method.");
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }


//    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {
//
//        @Override
//        protected Weather doInBackground(String... params) {
//            Weather weather = new Weather();
//            String data = ( (new WeatherHttpClient()).getWeatherData(location.getCity()));
//
//            try {
//                weather = JSONWeatherParser.getWeather(data);
//
//                // Let's retrieve the icon
//                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return weather;
//
//        }

//        @Override
//        protected void onPostExecute(Weather weather) {
//            super.onPostExecute(weather);
//
//            if (weather.iconData != null && weather.iconData.length > 0) {
//                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
//                imgView.setImageBitmap(img);
//            }
//
//            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
//            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
//            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "�C");
//            hum.setText("" + weather.currentCondition.getHumidity() + "%");
//            press.setText("" + weather.currentCondition.getPressure() + " hPa");
//            windSpeed.setText("" + weather.wind.getSpeed() + " mps");
//            windDeg.setText("" + weather.wind.getDeg() + "�");
//
//        }

    public void find_weather(String latitude, String longitude){
        String apiKey = "3230cfeff61e018efbf5e1ab58fc9ccb";
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=Imperial&appid=" + apiKey;
        Log.d(TAG, "URL is: " + url);
        Log.d(TAG, "Inside of find_weather method");
        JsonObjectRequest jor = new JsonObjectRequest (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse (JSONObject response) {
                try{
                    Log.d(TAG, "WeatherDisplay: Made JsonObjectRequest inside of find_weather method.");

                    String city = response.getString("name");
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject wind_object = response.getJSONObject("wind");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp")) + "°F";
                    String humidity = String.valueOf(main_object.getInt("humidity")) + "%";
                    String windSpeed = String.valueOf(wind_object.getDouble("speed")) + "mph";
                    String pressure = String.valueOf(main_object.getInt("pressure")) + "hPa";
                    int windDegree = (wind_object.getInt("deg"));
                    String windDirection = findWindDirection(windDegree);
                    String windSpeedAndDirection = windSpeed + " " + windDirection;
                    String description = object.getString("description");

                    Log.d(TAG, "Strings are built from the JSON response");
                    temp_tv.setText(temp);
                    condDescr_tv.setText(description);
                    cityText_tv.setText(city);
                    hum.setText(humidity);
                    wind_tv.setText(windSpeedAndDirection);
                    pressure_tv.setText(pressure);

                    Log.d(TAG, "WeatherDisplay: Textviews set inside of find_weather method.");
//                    Calendar calendar = Calendar.getInstance();
//                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE=MM-dd");
//                    String formatted_date = sdf.format(calendar.getTime());

                    String iconCode = object.getString("icon");
                    String iconUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";

                    new GetImageFromURL(imgView).execute(iconUrl);




                }catch (JSONException e){
                    Log.d(TAG, "JsonObjectRequest didn't work out.");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }


    private String findWindDirection(int windDegree) {
        String windDirection = "";

        if (windDegree <= 11 || windDegree >= 349){
            windDirection = "N";
        }

        else if (windDegree >= 12 && windDegree <= 33){
            windDirection = "NNE";
        }

        else if (windDegree >= 34 && windDegree <= 56){
            windDirection = "NE";
        }

        else if(windDegree >= 57 && windDegree <=78){
            windDirection = "ENE";
        }

        else if(windDegree >= 79 && windDegree <= 101){
            windDirection = "E";
        }

        else if(windDegree >= 102 && windDegree <= 123){
            windDirection = "ESE";
        }

        else if(windDegree >= 124 && windDegree <= 146){
            windDirection = "SE";
        }

        else if(windDegree >= 147 && windDegree <= 168){
            windDirection = "SSE";
        }

        else if(windDegree >= 169 && windDegree <= 191){
            windDirection = "S";
        }

        else if(windDegree >= 192 && windDegree <= 213){
            windDirection = "SSW";
        }

        else if(windDegree >= 214 && windDegree <= 236){
            windDirection = "SW";
        }

        else if(windDegree >= 237 && windDegree <= 258){
            windDirection = "WSW";
        }

        else if(windDegree >= 259 && windDegree <= 281){
            windDirection = "W";
        }

        else if(windDegree >= 282 && windDegree <= 303){
            windDirection = "WNW";
        }

        else if(windDegree >= 304 && windDegree <= 326){
            windDirection = "NW";
        }

        else if(windDegree >= 327 && windDegree <= 348){
            windDirection = "NNW";
        }

        return windDirection;
    }

    public class GetImageFromURL extends AsyncTask<String, Void, Bitmap>{
        ImageView imgView;

        public GetImageFromURL(ImageView imgV){
            this.imgView = imgV;
        }

        @Override
        protected Bitmap doInBackground(String... url){
            String urldisplay = url[0];
            bitmap = null;
            try{
                InputStream srt = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(srt);
            } catch (Exception e){
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            imgView.setImageBitmap(bitmap);
        }
    }
}




