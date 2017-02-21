package com.kevadiyakrunalk.myframework.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
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
import com.kevadiyakrunalk.myframework.other.adapter.Header;
import com.kevadiyakrunalk.myframework.other.adapter.Items;
import com.kevadiyakrunalk.myframework.viewmodels.AdapterFragmentViewModel;
import com.kevadiyakrunalk.recycleadapter.RxGenericsAdapter;
import com.kevadiyakrunalk.recycleadapter.RxGenericsDataSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class AdapterFragment extends MvvmFragment<FragmentAdapterBinding, AdapterFragmentViewModel> {

    //private List<Object> mData;
    //private RxGenericsDataSource<Object> rxDataSource;
    private List<Pair<Object, List<Object>>> mData;
    private RxGenericsDataSource<Pair<Object, List<Object>>> rxDataSource;

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
        //setData1();
        setData();

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.item_text, null);

        rxDataSource = new RxGenericsDataSource<>(mData);
        rxDataSource.repeat(1)
        .<RxGenericsAdapter.MyBaseViewHolder>bindRecyclerView(
                 RxGenericsAdapter.with(mData, BR.item)
                .map(Header.class, R.layout.item_header)
                .map(Items.class, R.layout.item_text)
                .onSwapMenuListener(R.id.container, -0.8f, 0.8f)
                .onDragListener(R.id.drag_handle)
                .onExpandListener(R.id.indicator, savedInstanceState)
                .onClickListener(new RxGenericsAdapter.OnClickListener() {
                    @Override
                    public void onClick(RxGenericsAdapter.ItemViewTypePosition detail) {
                        Toast.makeText(getActivity(), "Click Button", Toast.LENGTH_SHORT).show();
                    }
                }, R.id.container, R.id.button1)
                //.into(view.getMeasuredHeight(), getBinding().list, new LinearLayoutManager(getActivity())))
                .into(0, getBinding().list, new LinearLayoutManager(getActivity())))
        .subscribe(viewHolder -> {

        });
        mData = rxDataSource.getRxAdapter().getDataSet();

        /*rxDataSource.map(new Func1<Pair<Object, List<Object>>, Pair<Object, List<Object>>>() {
            @Override
            public Pair<Object, List<Object>> call(Pair<Object, List<Object>> objectListPair) {
                if (objectListPair.first instanceof Items)
                    ((Items) objectListPair.first).setText(((Items) objectListPair.first).getText().toLowerCase());
                else if (objectListPair.first instanceof Header)
                    ((Header) objectListPair.first).setText(((Header) objectListPair.first).getText().toLowerCase());

                for(int i=0; i<objectListPair.second.size(); i++){
                    if (objectListPair.second.get(i) instanceof Items)
                        ((Items) objectListPair.second.get(i)).setText(((Items) objectListPair.second.get(i)).getText().toUpperCase());
                    else if (objectListPair.second.get(i) instanceof Header)
                        ((Header) objectListPair.second.get(i)).setText(((Header) objectListPair.second.get(i)).getText().toUpperCase());
                }
                return objectListPair;
            }
        }).updateAdapter();*/
        /*rxDataSource.map(new Func1<Object, Object>() {
            @Override public Object call(Object s) {
                if (s instanceof Items) {
                    ((Items) s).setText(((Items) s).getText().toUpperCase());
                } else if (s instanceof Header) {
                    ((Header) s).setText(((Header) s).getText().toUpperCase());
                }
                return s;
            }
        }).updateAdapter();*/
    }

    @Override
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
                rxDataSource.updateDataSet(mData) //base items should remain the same
                        .flatMap(new Func1<Pair<Object, List<Object>>, Observable<Pair<Object, List<Object>>>>() {
                            @Override
                            public Observable<Pair<Object, List<Object>>> call(Pair<Object, List<Object>> objectListPair) {
                                List<Object> listFilter = Observable.from(objectListPair.second).filter(new Func1<Object, Boolean>() {
                                    @Override
                                    public Boolean call(Object o) {
                                        boolean flag = false;
                                        if (o instanceof Items)
                                            flag = ((Items) o).getText().toLowerCase().contains(newText.toLowerCase());
                                        else if (o instanceof Header)
                                            flag = ((Header) o).getText().toLowerCase().contains(newText.toLowerCase());
                                        return flag;
                                    }
                                }).toList().toBlocking().first();
                                return Observable.just(new Pair<>(objectListPair.first, listFilter));
                            }
                        }).filter(new Func1<Pair<Object, List<Object>>, Boolean>() {
                            @Override
                            public Boolean call(Pair<Object, List<Object>> objectListPair) {
                                boolean flag = false;
                                if (objectListPair.first instanceof Items)
                                    flag = ((Items) objectListPair.first).getText().toLowerCase().contains(newText.toLowerCase());
                                else if (objectListPair.first instanceof Header)
                                    flag = ((Header) objectListPair.first).getText().toLowerCase().contains(newText.toLowerCase());

                                if (!flag && objectListPair.second.size() > 0)
                                    flag = true;
                                return flag;
                            }
                        })
                        .updateAdapter();
                        /*.filter(new Func1<Object, Boolean>() {
                            @Override
                            public Boolean call(Object s) {
                                if (s instanceof Items) {
                                    return ((Items) s).getText().toLowerCase().contains(newText);
                                } else if (s instanceof Header) {
                                    return ((Header) s).getText().toLowerCase().contains(newText);
                                } else
                                    return true;
                            }
                        }).updateAdapter();*/
                return false;
            }
        });
        item.setActionView(sv);
    }

    public void setData1() {
        final String groupItems = "abcdefghijklmnopqrstuvwxyz";
        mData = new LinkedList<>();
        for (int i = 0; i < groupItems.length(); i++) {
            final boolean isSection = false;
            final String groupText = Character.toString(groupItems.charAt(i));
            final Header group = new Header(i, isSection, groupText);
            //mData.add(group);
        }
    }

    public void setData() {
        //final String groupItems = "|ABC|DEF|GHI|JKL|MNO|PQR|STU|VWX|YZ";
        final String groupItems = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String childItems = "abc";

        mData = new LinkedList<>();
        for (int i = 0; i < groupItems.length(); i++) {
            final boolean isSection = true;
            final String groupText = Character.toString(groupItems.charAt(i));
            final Header group = new Header(i, isSection, groupText);
            final List<Object> children = new ArrayList<>();

            for (int j = 0; j < childItems.length(); j++) {
                final long childId = group.generateNewChildId();
                final String childText = Character.toString(childItems.charAt(j));
                children.add(new Items(childId, childText));
            }

            mData.add(new Pair<>(group, children));
        }
    }
}
