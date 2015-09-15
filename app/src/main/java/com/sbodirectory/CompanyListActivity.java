package com.sbodirectory;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.sbodirectory.controller.CategoryLoader;
import com.sbodirectory.controller.CompanyLoader;
import com.sbodirectory.controller.FeatureCompanyLoader;
import com.sbodirectory.controller.FindNearWithDistanceCompanyLoader;
import com.sbodirectory.controller.SearchCompanyLoader;
import com.sbodirectory.model.Category;
import com.sbodirectory.model.Company;
import com.sbodirectory.util.Config;
import com.sbodirectory.util.Utils;
import com.sbodirectory.view.CompanyListAdapter;
import com.sbodirectory.view.MenuExpandableListAdapter;

import java.util.List;
import java.util.Objects;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

/**
 * Created by AnhNDT on 3/27/15.
 */
public class CompanyListActivity extends ActionBarActivity {
    private final int LOADER_CATEGORY_ID = 99;
    public static enum TYPE {
        FIND_NEAR,
        LIST_ALL,
        FEATURE_COMPANY,
        SEARCH,
        FIND_NEAR_WITH_DISTANCE
    }

    private ListView mListView;
    private CompanyListAdapter mAdapter;
    private List<Company> mCompanies;
    private List<Category> mCategories;
    private ProgressDialog mLoadingDialog;
    private Location mMyLocation;
    private boolean flagGoSettingEnableGPS;
    private TYPE mLastType;
    private ExpandableListView mListMenuView;
    private MenuExpandableListAdapter mMenuAdapter;
    private EditText mSearchBox;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private Category mSelectedCategory;
    private double mSeletedMaxDistance;
    private PublisherAdView mAdView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        init(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (outState != null) {
            outState.putInt("last_task_id", mLastType.ordinal());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_list_company);

        if (savedInstanceState != null) {
            final int lastIndex = savedInstanceState.getInt("last_task_id");
            mLastType = TYPE.values()[lastIndex];
        } else {
            mLastType = TYPE.FIND_NEAR;
        }

        //load Category before
        loadCategory();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        setupDrawer();

        mListView = (ListView) findViewById(R.id.listView);
        if (mLastType == TYPE.FIND_NEAR) {
            startFindNear();
        } else if (mLastType == TYPE.FEATURE_COMPANY) {
            startListFeatureCompany();
        } else {
            startListAllCompany();
        }
        loadAdmobBanner();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    private void loadCategory() {
        getSupportLoaderManager().initLoader(LOADER_CATEGORY_ID, null, new LoaderManager.LoaderCallbacks<List<Category>>() {
            @Override
            public Loader<List<Category>> onCreateLoader(int id, Bundle args) {
                return new CategoryLoader(CompanyListActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<List<Category>> loader, List<Category> data) {
                mCategories = data;
                setupMenu();
            }

            @Override
            public void onLoaderReset(Loader<List<Category>> loader) {
            }
        });
    }

    private void loadAdmobBanner() {
        mAdView = (PublisherAdView) findViewById(R.id.adView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setupMenu() {
        mListMenuView = (ExpandableListView) findViewById(R.id.listMenuView);
        mMenuAdapter = new MenuExpandableListAdapter(this, mCategories);
        mListMenuView.setAdapter(mMenuAdapter);
        mListMenuView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 0) {
                    mDrawerLayout.closeDrawers();
                    startListFeatureCompany();
                    return true;
                }else if (groupPosition == 4){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.OUR_WEBSITE));
                    startActivity(browserIntent);
                }
                return false;
            }
        });
        mListMenuView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                mDrawerLayout.closeDrawers();
                if (groupPosition == 1 || groupPosition == 2) {
                    double minValue = 0, maxValue = 0;
                    if (childPosition == 0) {
                        minValue = 0;
                        maxValue = 10;
                    } else if (childPosition == 1) {
                        minValue = 0;
                        maxValue = 20;
                    } else if (childPosition == 2) {
                        minValue = 0;
                        maxValue = 30;
                    } else if (childPosition == 3) {
                        minValue = 0;
                        maxValue = 40;
                    } else if (childPosition == 4) {
                        minValue = 0;
                        maxValue = Integer.MAX_VALUE;
                    }
                    if (groupPosition == 1) {//is miles group menu
                        //convert from miles to km
                        minValue = Utils.convertMileToKM(minValue);
                        maxValue = Utils.convertMileToKM(maxValue);
                    } else if (groupPosition == 2) {//kilometes
                        //do not need to convert, just keep the value
                    }
                    mSeletedMaxDistance = maxValue;
                    startLoadNearCompanyWithDistance(minValue, maxValue);

//                    loadData(true, TYPE.FIND_NEAR_WITH_DISTANCE);

                } else if (groupPosition == 3) {//click category
                    Object menuItem = mMenuAdapter.getChild(groupPosition, childPosition);
                    if (menuItem != null && menuItem instanceof Category) {
                        Category category = (Category)menuItem;
                        mSelectedCategory = category;
                        loadData(true, mLastType);
                    }
                }
                return true;
            }
        });

        mSearchBox = (EditText)findViewById(R.id.searchBox);
        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mDrawerLayout.closeDrawers();
                    startSearch(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void startListAllCompany() {
        loadData(true, TYPE.LIST_ALL);
    }
    private void startListFeatureCompany() {
        loadData(true, TYPE.FEATURE_COMPANY);
    }
    private void startLoadNearCompanyWithDistance(final double minKM, final double maxKM) {
        showLoadingDialog();
        cancelCurrentLoader();
        if (mMyLocation != null) {
            loadNearCompanyWithDistance(mMyLocation, minKM, maxKM);
            return;
        }
        boolean isGPSEnable = isGPSEnable(true);
        if (isGPSEnable) {
            mLastType = TYPE.FIND_NEAR_WITH_DISTANCE;

            TrackerSettings settings = new TrackerSettings();
            settings.setUseGPS(true);
            settings.setUseNetwork(true);
            settings.setUsePassive(false);
            settings.setTimeout(15 * 1000);

            // You can pass an ui Context but it is not mandatory getApplicationContext() would also works
            new LocationTracker(this) {
                @Override
                public void onTimeout(LocationTracker tracker) {
                    if (tracker != null) {
                        tracker.stopListen();
                    }
                    dismissLoadingDialog();
//                Toast.makeText(CompanyListActivity.this, R.string.msg_error_get_my_location, Toast.LENGTH_SHORT).show();
                    showDialogError();
                }

                @Override
                public void onLocationFound(LocationTracker tracker, Location location) {
                    if (tracker != null) {
                        tracker.stopListen();
                    }
                    if (mLastType != TYPE.FIND_NEAR_WITH_DISTANCE) return;
                    // Do some stuff
                    if (location != null && (location.getLongitude() != 0 || location.getLongitude() != 0)) {
                        mMyLocation = location;
//                    //anhndt fake location
//                        mMyLocation.setLatitude(31.8105743);
//                        mMyLocation.setLongitude(-85.9600595);
                        //load list companies from Network
                        loadNearCompanyWithDistance(mMyLocation, minKM, maxKM);
                    } else {
                        showLoadingDialog();
                        Toast.makeText(CompanyListActivity.this, R.string.msg_error_get_my_location, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
    }
    private void loadNearCompanyWithDistance(Location location, final double minKM, final double maxKM) {
        getSupportLoaderManager().initLoader(mLastType.ordinal(), null, new LoaderManager.LoaderCallbacks<List<Company>>() {
            @Override
            public Loader<List<Company>> onCreateLoader(int id, Bundle args) {
                return new FindNearWithDistanceCompanyLoader(CompanyListActivity.this, mMyLocation, minKM, maxKM);
            }

            @Override
            public void onLoadFinished(Loader<List<Company>> loader, List<Company> data) {
                mSelectedCategory = null;
                dismissLoadingDialog();
                CompanyListActivity.this.onLoadFinished(data);
            }
            @Override
            public void onLoaderReset(Loader<List<Company>> loader) {}
        });
    }
    private void loadData(boolean showLoadingDialog, final TYPE type) {
        if (showLoadingDialog) {
            showLoadingDialog();
        }
        cancelCurrentLoader();

        mLastType = type;

        getSupportLoaderManager().initLoader(mLastType.ordinal(), null, new LoaderManager.LoaderCallbacks<List<Company>>() {
            @Override
            public Loader<List<Company>> onCreateLoader(int id, Bundle args) {
                if (mLastType == TYPE.FIND_NEAR || mLastType == TYPE.LIST_ALL) {
                    return new CompanyLoader(CompanyListActivity.this, type == TYPE.FIND_NEAR ? mMyLocation : null, mSelectedCategory);
                } else if (mLastType == TYPE.FIND_NEAR_WITH_DISTANCE) {
                    return new FindNearWithDistanceCompanyLoader(CompanyListActivity.this, mMyLocation, 0, mSeletedMaxDistance, mSelectedCategory);
                } else if (mLastType == TYPE.FEATURE_COMPANY) {
                    return new FeatureCompanyLoader(CompanyListActivity.this, mSelectedCategory);
                }
                return null;
            }

            @Override
            public void onLoadFinished(Loader<List<Company>> loader, List<Company> data) {
                dismissLoadingDialog();
                CompanyListActivity.this.onLoadFinished(data);
            }

            @Override
            public void onLoaderReset(Loader<List<Company>> loader) {

            }
        });
    }

    private void startSearch(final String search) {
        if (TextUtils.isEmpty(search)) return;
        showLoadingDialog();
        cancelCurrentLoader();

        mLastType = TYPE.SEARCH;

        getSupportLoaderManager().initLoader(mLastType.ordinal(), null, new LoaderManager.LoaderCallbacks<List<Company>>() {
            @Override
            public Loader<List<Company>> onCreateLoader(int id, Bundle args) {
                return new SearchCompanyLoader(CompanyListActivity.this, search);
            }

            @Override
            public void onLoadFinished(Loader<List<Company>> loader, List<Company> data) {
                dismissLoadingDialog();
                CompanyListActivity.this.onLoadFinished(data);
            }
            @Override
            public void onLoaderReset(Loader<List<Company>> loader) {}
        });
    }

    private void startFindNear() {
        final boolean gpsEnable = isGPSEnable(true);
        if (gpsEnable) {
            cancelCurrentLoader();
            mLastType = TYPE.FIND_NEAR;
            //get current location before
            startGetMyLocation();
        }
    }
    private boolean isGPSEnable(boolean showDialogError) {
        final boolean gpsEnable = Utils.checkGPSLocatorEnalable(this);
        if (!gpsEnable && showDialogError) {
            //show dialog in here
            final Dialog dialog = new Dialog(this, R.style.NoTitleDialog);
            dialog.setContentView(R.layout.dialog_gps_locator);

            View btnEnableLocator = dialog.findViewById(R.id.btnEnableLocator);
            btnEnableLocator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    flagGoSettingEnableGPS = true;
                    //open setting app
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            View btnListCompany = dialog.findViewById(R.id.btnListCompany);
            btnListCompany.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //load all companies
                    loadData(true, TYPE.LIST_ALL);
                }
            });
            dialog.show();
        }
        return gpsEnable;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flagGoSettingEnableGPS) {
            final boolean gpsEnable = Utils.checkGPSLocatorEnalable(this);
            if (gpsEnable) {
                //get current location before
                startGetMyLocation();
            }
            flagGoSettingEnableGPS = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMyLocation != null) {
            outState.putParcelable("my_location", mMyLocation);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("my_location")) {
            mMyLocation = (Location) savedInstanceState.getParcelable("my_location");
        }
    }

    private void startGetMyLocation() {
        showLoadingDialog();

        TrackerSettings settings = new TrackerSettings();
        settings.setUseGPS(true);
        settings.setUseNetwork(true);
        settings.setUsePassive(false);
        settings.setTimeout(15 * 1000);

        // You can pass an ui Context but it is not mandatory getApplicationContext() would also works
        new LocationTracker(this) {
            @Override
            public void onTimeout(LocationTracker tracker) {
                if (tracker != null) {
                    tracker.stopListen();
                }
                dismissLoadingDialog();
//                Toast.makeText(CompanyListActivity.this, R.string.msg_error_get_my_location, Toast.LENGTH_SHORT).show();
                showDialogError();
            }

            @Override
            public void onLocationFound(LocationTracker tracker, Location location) {
                if (tracker != null) {
                    tracker.stopListen();
                }
                if (mLastType != TYPE.FIND_NEAR) return;
                // Do some stuff
                if (location != null && (location.getLongitude() != 0 || location.getLongitude() != 0)) {
                    mMyLocation = location;
//                    //anhndt fake location
//                    mMyLocation.setLatitude(37);
//                    mMyLocation.setLongitude(-122);
                    //load list companies from Network
                    loadData(false, TYPE.FIND_NEAR);
                } else {
                    showLoadingDialog();
                    Toast.makeText(CompanyListActivity.this, R.string.msg_error_get_my_location, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.list_company_menu, menu);
        menuInflater.inflate(R.menu.main_activity_actions, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchViewAction.setIconifiedByDefault(false);
        searchViewAction.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                Toast.makeText(CompanyListActivity.this, "Search: " + s, Toast.LENGTH_SHORT).show();
                searchViewAction.clearFocus();
                searchMenuItem.collapseActionView();
                startSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuListAll:
                mMyLocation = null;
                mSelectedCategory = null;
                mSeletedMaxDistance = 0;
                startListAllCompany();
                return true;
            case R.id.menuNearbyLocation:
                mSelectedCategory = null;
                mSeletedMaxDistance = 0;
                startFindNear();
                return true;
            case R.id.menuLogout:
                Utils.setLogin(this, false);
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(myIntent);
                finish();
                return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAdapter() {
        mAdapter = new CompanyListAdapter(this, mCompanies);
        mAdapter.setOnItemListener(new CompanyListAdapter.OnItemListener() {
            @Override
            public void onGetDirectionClicked(Company company) {
                Intent i = new Intent(CompanyListActivity.this, MapDirectionActivity.class);
                i.putExtra(MapDirectionActivity.EXTRA_COMPANY, company);
                if (mMyLocation != null) {
                    i.putExtra(MapDirectionActivity.EXTRA_MY_LOCATION, new com.sbodirectory.model.Location(mMyLocation.getLatitude(), mMyLocation.getLongitude()));
                }
                startActivity(i);
            }

            @Override
            public void onWebsiteClicked(Company company) {
            }
        });
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onDetachedFromWindow() {
        dismissLoadingDialog();
        flagGoSettingEnableGPS = false;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        flagGoSettingEnableGPS = false;
        super.onDestroy();
    }

    private void showLoadingDialog() {
        dismissLoadingDialog();
        mLoadingDialog = ProgressDialog.show(this, null, getString(R.string.loading));
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //stop current task
                cancelCurrentLoader();
            }
        });
    }

    private void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private void cancelCurrentLoader() {
        getSupportLoaderManager().destroyLoader(mLastType.ordinal());
    }

    private AlertDialog mDialogError;

    private void showDialogError() {
        if (mCompanies != null && mCompanies.size() > 0) return;
        if (mLastType == TYPE.FIND_NEAR) {
            if (mDialogError == null || !mDialogError.isShowing()) {
                mDialogError = new AlertDialog.Builder(CompanyListActivity.this)
                        .setTitle("")
                        .setMessage(R.string.msg_empty_list)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mDialogError = null;
                                //show full list
                                loadData(true, TYPE.LIST_ALL);
                            }
                        })
                        .create();
                mDialogError.show();
            }
        }
    }


    private void onLoadFinished(List<Company> data) {
        mCompanies = data;
        createAdapter();
        if (mCompanies == null || mCompanies.size() == 0) {
            showDialogError();
        }
    }
}
