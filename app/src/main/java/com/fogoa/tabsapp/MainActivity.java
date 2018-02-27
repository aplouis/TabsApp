package com.fogoa.tabsapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.fogoa.tabsapp.extensions.BaseActivity;
import com.fogoa.tabsapp.misc.Constants;
import com.fogoa.tabsapp.misc.ImageDownloaderCache;
import com.fogoa.tabsapp.models.GalleryItem;
import com.fogoa.tabsapp.models.TabItem;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(mViewPager_OnPageChangeListener);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setVisibility(View.VISIBLE);
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //check for read external storage permision
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.PERMISSIONS_REQUEST_READ_STORAGE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static ArrayList<GalleryItem> getCameraImages(Context context, int limit, int offset) {

        final String[] projection = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME  };
        final String selection = null;
        final String[] selectionArgs = null;
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC LIMIT " +(limit)+ " OFFSET "+offset;
        final Uri mediaQueryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final Cursor cursor = context.getContentResolver().query(mediaQueryUri,
                projection,
                selection,
                selectionArgs,
                orderBy);

        ArrayList<GalleryItem> result = new ArrayList<GalleryItem>();
        int itemCnt = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                final int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                final int bidColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
                final int bnameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                do {
                    String sdata = cursor.getString(dataColumn);
                    final String sid = cursor.getString(idColumn);
                    final String sname = cursor.getString(nameColumn);
                    final String sbid = cursor.getString(bidColumn);
                    final String sbname = cursor.getString(bnameColumn);
                    final GalleryItem data = new GalleryItem(sdata, sid, sname, sbid, sbname);
                    result.add(data);
                    itemCnt++;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        else {
            if (Constants.DEBUG) Log.d(TAG, "getCameraImages query cursor null");
        }

        return result;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public ArrayList<TabItem> tabItems;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            tabItems = new ArrayList<TabItem>();
            //tabItems.add(new TabItem("T One", R.layout.fragment_one));
            //tabItems.add(new TabItem("T Two", R.layout.fragment_two));
            //tabItems.add(new TabItem("T Three", R.layout.fragment_three));

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                //get first 3 images from device
                int cnt = 0;
                for (GalleryItem imgItem : getCameraImages(getActivity(), 3, 0)) {
                    String title = "T One";
                    int lrid = R.layout.fragment_one;
                    if (cnt == 1) {
                        title = "T Two";
                        lrid = R.layout.fragment_two;
                    } else if (cnt == 2) {
                        title = "T Three";
                        lrid = R.layout.fragment_three;
                    }
                    tabItems.add(new TabItem(title, lrid, imgItem.img_uri));
                    cnt++;
                }
            }
            else {
                tabItems.add(new TabItem("T One", R.layout.fragment_one));
                tabItems.add(new TabItem("T Two", R.layout.fragment_two));
                tabItems.add(new TabItem("T Three", R.layout.fragment_three));
            }

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            TabItem tabItem = tabItems.get(position);
            if (tabItem.tabFrag==null) {
                tabItem.tabFrag = PlaceholderFragment.newInstance(position, tabItem);
            }
            return tabItem.tabFrag;
        }

        @Override
        public int getCount() {
            return tabItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String tabTitle = null;
            if (tabItems != null && tabItems.size() > position) {
                tabTitle = tabItems.get(position).title;
            }
            return tabTitle;
        }

    }

    private ViewPager.OnPageChangeListener mViewPager_OnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            TabItem tabItem = mSectionsPagerAdapter.tabItems.get(position);
            if (getSupportActionBar()!=null) {
                getSupportActionBar().setTitle(tabItem.title);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_TAB_TITLE = "tab_title";
        private static final String ARG_LAYOUT_RESOURCE = "layout_resource";
        private static final String ARG_IMG_PATH = "image_path";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, TabItem tabItem) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_TAB_TITLE, tabItem.title);
            args.putInt(ARG_LAYOUT_RESOURCE, tabItem.layoutRes);
            args.putString(ARG_IMG_PATH, tabItem.imgUri);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_one, container, false);
            View rootView = inflater.inflate(getArguments().getInt(ARG_LAYOUT_RESOURCE), container, false);
            TextView tvPageTitle = (TextView) rootView.findViewById(R.id.tvPageTitle);
            tvPageTitle.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)+1 ));
            ImageView ivPageImg = (ImageView) rootView.findViewById(R.id.ivPageImg);

            //ImageDownloaderCache imgDownloader = new ImageDownloaderCache();
            //imgDownloader.loadBitmapPath(getArguments().getString(ARG_IMG_PATH), ivPageImg);
            Bitmap mBM = ImageDownloaderCache.decodeSampledBitmapFromPath(getArguments().getString(ARG_IMG_PATH), ImageDownloaderCache.maxImageSize, ImageDownloaderCache.maxImageSize);
            ivPageImg.setImageBitmap(mBM);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            int pos = getArguments().getInt(ARG_SECTION_NUMBER);
            String tabTitle = getArguments().getString(ARG_TAB_TITLE);
            //if (getActivity()!=null) {
            //    ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
            //    if (actionBar!=null) {
            //        actionBar.setTitle(tabTitle);
            //    }
            //}

        }


    }

}
