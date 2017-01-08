package com.kevadiyakrunalk.myframework.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kevadiyakrunalk.commonutils.common.Logs;
import com.kevadiyakrunalk.mvvmarchitecture.MvvmFragment;
import com.kevadiyakrunalk.mvvmarchitecture.common.BindingConfig;
import com.kevadiyakrunalk.myframework.BR;
import com.kevadiyakrunalk.myframework.R;
import com.kevadiyakrunalk.myframework.databinding.FragmentAdapterBinding;
import com.kevadiyakrunalk.myframework.databinding.ItemHeaderBinding;
import com.kevadiyakrunalk.myframework.databinding.ItemHeaderFirstBinding;
import com.kevadiyakrunalk.myframework.databinding.ItemTextBinding;
import com.kevadiyakrunalk.myframework.other.adapter.Data;
import com.kevadiyakrunalk.myframework.other.adapter.Header;
import com.kevadiyakrunalk.myframework.other.adapter.Items;
import com.kevadiyakrunalk.myframework.viewmodels.AdapterFragmentViewModel;
import com.kevadiyakrunalk.recycleadapter.RxBinderAdapter;
import com.kevadiyakrunalk.recycleadapter.RxBinderAdapterDemo;
import com.kevadiyakrunalk.recycleadapter.RxDataSource;
import com.kevadiyakrunalk.recycleadapter.animator.GeneralItemAnimator;
import com.kevadiyakrunalk.recycleadapter.animator.SwipeDismissItemAnimator;
import com.kevadiyakrunalk.recycleadapter.decoration.ItemShadowDecorator;
import com.kevadiyakrunalk.recycleadapter.decoration.SimpleListDividerDecorator;
import com.kevadiyakrunalk.recycleadapter.draggable.RecyclerViewDragDropManager;
import com.kevadiyakrunalk.recycleadapter.expandable.RecyclerViewExpandableItemManager;
import com.kevadiyakrunalk.recycleadapter.swipeable.RecyclerViewSwipeManager;
import com.kevadiyakrunalk.recycleadapter.touchguard.RecyclerViewTouchActionGuardManager;
import com.kevadiyakrunalk.recycleadapter.utility.ChildData;
import com.kevadiyakrunalk.recycleadapter.utility.GroupData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static android.R.attr.data;

public class AdapterFragment extends MvvmFragment<FragmentAdapterBinding, AdapterFragmentViewModel> {

    private List<Pair<Object, List<Object>>> mData;
    RxDataSource rxDataSource;

    @NonNull
    @Override
    public AdapterFragmentViewModel createViewModel() {
        return new AdapterFragmentViewModel(getActivity(), Logs.getInstance(getActivity()));
    }

    @NonNull
    @Override
    public BindingConfig getBindingConfig() {
        return new BindingConfig(R.layout.fragment_adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setData();

        int childItemHeight = getActivity().getResources().getDimensionPixelSize(com.kevadiyakrunalk.recycleadapter.R.dimen.list_item_height);
        int topMargin = (int) (getActivity().getResources().getDisplayMetrics().density * 16); // top-spacing: 16dp
        int bottomMargin = topMargin; // bottom-spacing: 16dp

        rxDataSource = new RxDataSource(mData);
        rxDataSource.repeat(1)
        .<RxBinderAdapter.ViewHolder>bindRecyclerView(RxBinderAdapterDemo.with(mData, BR.item)
                .map(Header.class, R.layout.item_header)
                .map(Items.class, R.layout.item_text)
                .onSwipMenuListener(R.id.container, -0.8f, 0.8f)
                //.onDragListener(R.id.drag_handle)
                .onExpandListener(R.id.indicator, childItemHeight, topMargin, bottomMargin, savedInstanceState)
                .onClickListener(new RxBinderAdapterDemo.OnClickListener() {
                    @Override
                    public void onClick(RxBinderAdapterDemo.ItemViewTypePosition detail) {

                    }
                })
                .into(getBinding().list, new LinearLayoutManager(getActivity())))
        .subscribe(viewHolder -> {

        });
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search); // sets icon
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());

        // implementing the listener
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rxDataSource.updateDataSet(dataSet) //base items should remain the same
                        .filter(new Func1<Object, Boolean>() {
                            @Override
                            public Boolean call(Object s) {
                                if (s instanceof Items) {
                                    return ((Items) s).getText().toLowerCase().contains(newText);
                                } else if (s instanceof Header) {
                                    return ((Header) s).getText().toLowerCase().contains(newText);
                                } else
                                    return true;
                            }
                        }).updateAdapter();
                return false;
            }
        });
        item.setActionView(sv);
    }*/

    public void setData() {
        final String groupItems = "|ABC|DEF|GHI|JKL|MNO|PQR|STU|VWX|YZ";
        final String childItems = "abc";

        mData = new LinkedList<>();
        int sectionCount = 1;
        for (int i = 0; i < groupItems.length(); i++) {
            final long groupId = i;
            final boolean isSection = (groupItems.charAt(i) == '|');
            final String groupText = isSection ? ("Section " + sectionCount) : Character.toString(groupItems.charAt(i));
            final Header group = new Header(groupId, isSection, groupText);
            final List<Object> children = new ArrayList<>();

            if (isSection) {
                sectionCount += 1;
            } else {
                // add child items
                for (int j = 0; j < childItems.length(); j++) {
                    final long childId = group.generateNewChildId();
                    final String childText = Character.toString(childItems.charAt(j));
                    children.add(new Items(childId, childText));
                }
            }

            mData.add(new Pair<Object, List<Object>>(group, children));
        }

        Log.e("DATA", mData.toString());
    }
}
