package com.kevadiyak.myframework.other.location.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kevadiyak.mvvmarchitecture.MvvmActivity;
import com.kevadiyak.mvvmarchitecture.common.BindingConfig;
import com.kevadiyak.myframework.R;
import com.kevadiyak.myframework.databinding.ActivityMocklocationsBinding;
import com.kevadiyak.myframework.other.location.viewmodels.MockLocationViewModel;

public class MockLocationsActivity extends MvvmActivity<ActivityMocklocationsBinding, MockLocationViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Toolbar toolbar = getBinding().toolbar;
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @NonNull
    @Override
    public MockLocationViewModel createViewModel() {
        return new MockLocationViewModel(this);
    }

    @NonNull
    @Override
    public BindingConfig getBindingConfig() {
        return new BindingConfig(R.layout.activity_mocklocations);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
