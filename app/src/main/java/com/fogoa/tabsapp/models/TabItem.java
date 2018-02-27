package com.fogoa.tabsapp.models;

import android.support.v4.app.Fragment;

public class TabItem {
    public String title;
    public int layoutRes;
    public Fragment tabFrag;
    public String imgUri;

    public TabItem (String pTitle, int pLayoutRes) {
        this.title = pTitle;
        this.layoutRes = pLayoutRes;
        this.tabFrag = null;
    }
    public TabItem (String pTitle, int pLayoutRes, String pImgUri) {
        this(pTitle, pLayoutRes);
        this.imgUri = pImgUri;
    }

}
