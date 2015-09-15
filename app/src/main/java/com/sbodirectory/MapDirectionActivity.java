package com.sbodirectory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sbodirectory.controller.GRouteLoader;
import com.sbodirectory.model.Company;
import com.sbodirectory.model.Location;
import com.sbodirectory.model.maps.GRoute;
import com.sbodirectory.model.maps.GRoutes;
import com.sbodirectory.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

/**
 * Created by AnhNDT on 3/28/15.
 */
public class MapDirectionActivity extends ActionBarActivity implements View.OnClickListener ,OnMapReadyCallback {
    public static final String EXTRA_COMPANY = "extra_company";
    public static final String EXTRA_MY_LOCATION = "extra_my_location";
    private TextView txtCompanyName, txtCompanyPhone;
    private TextView txtTime, txtDistance;
    private ImageButton btnDirectory;
    private ImageButton btnGmapsDirectory;
    private GoogleMap mMap;
    private MapFragment mapFragment;
    private GRoutes mRoutes;
    private Company mCompany;
    private Location mMyLocation;
    private Polyline mLastPolyline;
    private Marker mStartPoint, mDestinationPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_map_direction);

        txtCompanyName = (TextView)findViewById(R.id.companyName);
        txtCompanyPhone = (TextView)findViewById(R.id.companyPhone);

        txtTime = (TextView)findViewById(R.id.time);
        txtDistance = (TextView)findViewById(R.id.distance);

        btnDirectory = (ImageButton)findViewById(R.id.btnDirectory);
        btnDirectory.setOnClickListener(this);
        btnGmapsDirectory = (ImageButton) findViewById(R.id.btnGmapsDirectory);
        btnGmapsDirectory.setOnClickListener(this);
        mapFragment=(MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMap = mapFragment.getMap();
        mMap.setMyLocationEnabled(true);

        if (getIntent().hasExtra(EXTRA_COMPANY)) {
            mCompany = (Company)getIntent().getParcelableExtra(EXTRA_COMPANY);
            updateUIWithCompany();
        }
        if (getIntent().hasExtra(EXTRA_MY_LOCATION)) {
            mMyLocation = (Location)getIntent().getParcelableExtra(EXTRA_MY_LOCATION);
        }
        if (mCompany != null && mMyLocation != null) {
            loadRoutes(true);
        } else if (mMyLocation == null) {
            //move camera of map to location of company
            if (mCompany != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCompany.location.latitude, mCompany.location.longitude), 14));
            }

            TrackerSettings settings = new TrackerSettings();
            settings.setUseGPS(true);
            settings.setUseNetwork(true);
            settings.setUsePassive(false);
            settings.setTimeout(5 * 1000);

            // You can pass an ui Context but it is not mandatory getApplicationContext() would also works
            new LocationTracker(this) {
                @Override
                public void onTimeout(LocationTracker tracker) {
                    if (tracker != null) {
                        tracker.stopListen();
                    }
                    Toast.makeText(MapDirectionActivity.this, R.string.msg_error_get_my_location, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLocationFound(LocationTracker tracker, android.location.Location location) {
                    if (tracker != null) {
                        tracker.stopListen();
                    }
                    if (mMyLocation != null) return;
                    // Do some stuff
                    if (location != null && (location.getLongitude() != 0 || location.getLongitude() != 0)) {
                        mMyLocation = new Location(location.getLatitude(), location.getLongitude());
                        loadRoutes(true);
                    }
                }
            };
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnDirectory) {

            LatLng startLocation = new LatLng(mMyLocation.latitude , mMyLocation.longitude);
            LatLng endLocation =new LatLng(mCompany.location.latitude ,mCompany.location.longitude);
            String url = getDirectionsUrl(startLocation, endLocation);

            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            // Start downloading json data from Google Directions API
        } else if(v == btnGmapsDirectory){
            final String startLocation = mMyLocation.latitude + "," + mMyLocation.longitude;
            final String endLocation = mCompany.location.latitude + "," + mCompany.location.longitude;
            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+startLocation+"&daddr="+endLocation;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(Intent.createChooser(intent, "Select an application"));
        }
    }






    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {
        private DownloadTask() {

        }
        String data = "";
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service


            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Downloading data in non-ui thread

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(),"result\n"+data,Toast.LENGTH_LONG).show();
            //            ParserTask parserTask = new ParserTask();
//
//            // Invokes the thread for parsing the JSON data
//            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                com.sbodirectory.DirectionsJSONParser parser = new com.sbodirectory.DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }







    /**
     * Make sure mCompany value is not null
     */
    private void updateUIWithCompany() {
        if (mCompany == null) return;
        txtCompanyName.setText(mCompany.getName());
        txtCompanyPhone.setText(mCompany.getPhone());
    }
    private void onLoadedRoutes() {
        if (mRoutes == null || mRoutes.routes == null || mRoutes.routes.size() == 0) {
            //show toast before
            return;
        }
        //draw route in map
        GRoute route = mRoutes.routes.get(0);
        updateRoute(route);
        drawRoute(route);
    }

    private void updateRoute(GRoute route) {
        if (route == null) return;
//        int totalDuration = route.getTotalDuration();
        txtTime.setText(route.getTotalDurationString());
        txtDistance.setText((route.getTotalEstimatedDistance()/1000f)+" mi");
    }

    private void drawRoute(GRoute route) {
        if (route == null) return;
        if (mLastPolyline != null)
            mLastPolyline.remove();

        PolylineOptions opts = route.getOverviewPolylineOptions();
        opts.color(0xff0054a6);
        mLastPolyline = mMap.addPolyline(opts);
        mLastPolyline.setWidth(Utils.convertDpToPx(this, 3));

        fitRouteToScreen();
    }
    private void fitRouteToScreen() {
        try {
            LatLngBounds currentBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
            if (mStartPoint == null || mDestinationPoint == null ||
                    !currentBounds.contains(mStartPoint.getPosition())
                            || !currentBounds.contains(mDestinationPoint.getPosition())
                    ) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                final List<LatLng> points = mLastPolyline.getPoints();
                for (LatLng p : points) {
                    builder.include(p);
                }
                LatLngBounds bounds = builder.build();
                int lp = 0;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, lp);
                mMap.animateCamera(cu);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDetachedFromWindow() {
        dismissLoadingDialog();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
    }

    private ProgressDialog mLoadingDialog;
    private void showLoadingDialog() {
        dismissLoadingDialog();
        mLoadingDialog = ProgressDialog.show(this, null, getString(R.string.loading));
    }
    private void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
    private final int TASK_ID = 2;
    private void loadRoutes(boolean showLoadingDialog) {
        if (mMyLocation == null || !mMyLocation.isValid()
                || mCompany == null || mCompany.location == null || !mCompany.location.isValid()) return;
        if (showLoadingDialog) {
            showLoadingDialog();
        }
        final String startLocation = mMyLocation.latitude + "," + mMyLocation.longitude;
        final String endLocation = mCompany.location.latitude + "," + mCompany.location.longitude;
        getSupportLoaderManager().initLoader(TASK_ID, null, new LoaderManager.LoaderCallbacks<GRoutes>() {
            @Override
            public Loader<GRoutes> onCreateLoader(int id, Bundle args) {
                return new GRouteLoader(MapDirectionActivity.this, startLocation, endLocation);
            }

            @Override
            public void onLoadFinished(Loader<GRoutes> loader, GRoutes data) {
                mRoutes = data;
                onLoadedRoutes();
                dismissLoadingDialog();
            }

            @Override
            public void onLoaderReset(Loader<GRoutes> loader) {

            }
        });
    }
}
