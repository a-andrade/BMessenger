package com.bmessenger.bmessenger.Models;

/**
 * Created by uli on 4/13/2017.
 */

public class ChannelItem {


    private String mName;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String mSumamry) {
        this.mSummary = mSummary;
    }

    private String mSummary;

    public ChannelItem(String name, String summary) {
        mName = name;
        mSummary = summary;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!ChannelItem.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final ChannelItem other = (ChannelItem) obj;
        if ((this.mName == null) ? (other.mName != null) : !this.mName.equals(other.mName)) {
            return false;
        }
        return true;
    }


}
