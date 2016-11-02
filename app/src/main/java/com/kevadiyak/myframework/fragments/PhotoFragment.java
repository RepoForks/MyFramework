package com.kevadiyak.myframework.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kevadiyak.commonutils.common.Logs;
import com.kevadiyak.mvvmarchitecture.MvvmFragment;
import com.kevadiyak.mvvmarchitecture.common.BindingConfig;
import com.kevadiyak.myframework.R;
import com.kevadiyak.myframework.databinding.FragmentPhotoBinding;
import com.kevadiyak.myframework.viewmodels.PhotoFragmentViewModel;

public class PhotoFragment extends MvvmFragment<FragmentPhotoBinding, PhotoFragmentViewModel> {

    @NonNull
    @Override
    public PhotoFragmentViewModel createViewModel() {
        return new PhotoFragmentViewModel(getActivity(), Logs.getInstance(getActivity()));
    }

    @NonNull
    @Override
    public BindingConfig getBindingConfig() {
        return new BindingConfig(R.layout.fragment_photo);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getViewModel().setImageView(getBinding().imageView2);
    }
}
