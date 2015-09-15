package com.sbodirectory.controller;

import android.content.Context;
import android.location.Location;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.sbodirectory.R;
import com.sbodirectory.model.Company;
import com.sbodirectory.model.maps.GRoute;
import com.sbodirectory.model.maps.GRoutes;
import com.sbodirectory.util.Config;
import com.sbodirectory.util.ServiceConnection;
import com.sbodirectory.util.Utils;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class GRouteLoader extends AsyncTaskLoader<GRoutes> {
    private static final String TAG = "ADP_AppListLoader";
    private static final boolean DEBUG = true;
    private String mStartLocation, mEndLocation;
    private GRoutes _datas;
    public GRouteLoader(Context context, String startLocation, String endLocation) {
        super(context);
        this.mStartLocation = startLocation;
        this.mEndLocation = endLocation;
    }

    @Override
    public GRoutes loadInBackground() {
        ServiceConnection serviceConnection = new ServiceConnection();
        final String serverPage = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin="+mStartLocation+
                "&destination="+mEndLocation+
                "&alternatives=true" +
                "&sensor=true" +
                "&mode=driving" +
                "&key=" + getContext().getString(R.string.direction_api_key);
        URI uri = null;
        try {
            uri = new URI(serverPage);
        } catch (Exception e){}
        if (uri == null) return null;

        final String result = serviceConnection.get(uri);;
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jso = new JSONObject(result);
                return new GRoutes(jso);
            } catch (Exception e) {}
        }
        return null;
    }

    /*******************************************/
    /** (2) Deliver the results to the client **/
    /*******************************************/

    /**
     * Called when there is new data to deliver to the client. The superclass will
     * deliver it to the registered listener (i.e. the LoaderManager), which will
     * forward the results to the client through a call to onLoadFinished.
     */
    @Override
    public void deliverResult(GRoutes data) {
        if (isReset()) {
            if (DEBUG) Log.w(TAG, "+++ Warning! An async query came in while the Loader was reset! +++");
            // The Loader has been reset; ignore the result and invalidate the data.
            // This can happen when the Loader is reset while an asynchronous query
            // is working in the background. That is, when the background thread
            // finishes its work and attempts to deliver the results to the client,
            // it will see here that the Loader has been reset and discard any
            // resources associated with the new data as necessary.
            if (data != null) {
                releaseResources(data);
                return;
            }
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        GRoutes oldData = _datas;
        _datas = data;

        if (isStarted()) {
            if (DEBUG) Log.i(TAG, "+++ Delivering results to the LoaderManager for" +
                    " the ListFragment to display! +++");
            // If the Loader is in a started state, have the superclass deliver the
            // results to the client.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            if (DEBUG) Log.i(TAG, "+++ Releasing any old data associated with this Loader. +++");
            releaseResources(oldData);
        }
    }

    /*********************************************************/
    /** (3) Implement the Loader�s state-dependent behavior **/
    /*********************************************************/

    @Override
    protected void onStartLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStartLoading() called! +++");

        if (_datas != null) {
            // Deliver any previously loaded data immediately.
            if (DEBUG) Log.i(TAG, "+++ Delivering previously loaded data to the client...");
            deliverResult(_datas);
        }

        if (takeContentChanged()) {
            // When the observer detects a new installed application, it will call
            // onContentChanged() on the Loader, which will cause the next call to
            // takeContentChanged() to return true. If this is ever the case (or if
            // the current data is null), we force a new load.
            if (DEBUG) Log.i(TAG, "+++ A content change has been detected... so force load! +++");
            forceLoad();
        } else if (_datas == null) {
            // If the current data is null... then we should make it non-null! :)
            if (DEBUG) Log.i(TAG, "+++ The current data is data is null... so force load! +++");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStopLoading() called! +++");

        // The Loader has been put in a stopped state, so we should attempt to
        // cancel the current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is; Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        if (DEBUG) Log.i(TAG, "+++ onReset() called! +++");

        // Ensure the loader is stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'apps'.
        if (_datas != null) {
            releaseResources(_datas);
            _datas = null;
        }
    }

    @Override
    public void onCanceled(GRoutes datas) {
        if (DEBUG) Log.i(TAG, "+++ onCanceled() called! +++");

        // Attempt to cancel the current asynchronous load.
        super.onCanceled(datas);

        // The load has been canceled, so we should release the resources
        // associated with '_datas'.
        releaseResources(datas);
    }

    @Override
    public void forceLoad() {
        if (DEBUG) Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }

    /**
     * Helper method to take care of releasing resources associated with an
     * actively loaded data set.
     */
    private void releaseResources(GRoutes datas) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
    }
}
