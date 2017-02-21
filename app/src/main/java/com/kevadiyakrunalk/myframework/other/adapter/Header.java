package com.kevadiyakrunalk.myframework.other.adapter;

import com.kevadiyakrunalk.recycleadapter.utility.DataUtils;
import com.kevadiyakrunalk.recycleadapter.utility.GroupData;

public class Header extends GroupData {
    private long mId;
    private int mPinned;
    private String text;
    private boolean isSectionHeader;
    private long mNextChildId;

    public Header(long id, boolean section, String txt) {
        mId = id;
        text = txt;
        mNextChildId = 0;
        isSectionHeader = section;
        mPinned = DataUtils.SWAP_NONE;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isSectionHeader() {
        return isSectionHeader;
    }

    @Override
    public long getGroupId() {
        return mId;
    }

    @Override
    public void setPinned(int pinned) {
        mPinned = pinned;
    }

    @Override
    public int isPinned() {
        return mPinned;
    }

    public long generateNewChildId() {
        final long id = mNextChildId;
        mNextChildId += 1;
        return id;
    }

    @Override
    public String toString() {
        return "Header{" +
                "mId=" + mId +
                ", mPinned=" + mPinned +
                ", text='" + text + '\'' +
                ", isSectionHeader=" + isSectionHeader +
                ", mNextChildId=" + mNextChildId +
                '}';
    }
}
