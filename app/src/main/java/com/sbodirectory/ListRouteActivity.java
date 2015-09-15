package com.sbodirectory;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.sbodirectory.model.maps.GRoute;
import com.sbodirectory.view.RouteListAdapter;

/**
 * Created by AnhNDT on 3/29/15.
 */
public class ListRouteActivity extends ActionBarActivity{
    public final static String EXTRA_ROUTE = "extra_route";
    private ListView mListView;
    private RouteListAdapter mAdapter;
    private GRoute mRoute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_list_route);

        mListView = (ListView) findViewById(R.id.listView);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_ROUTE)) {
            mRoute = getIntent().getParcelableExtra(EXTRA_ROUTE);
            mAdapter = new RouteListAdapter(this, mRoute);
            mListView.setAdapter(mAdapter);
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
}
