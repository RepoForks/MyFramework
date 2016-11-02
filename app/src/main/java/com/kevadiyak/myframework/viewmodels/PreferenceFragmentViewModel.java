package com.kevadiyak.myframework.viewmodels;

import android.content.Context;
import android.databinding.Bindable;
import android.text.TextUtils;

import com.kevadiyak.commonutils.common.Logs;
import com.kevadiyak.mvvmarchitecture.common.BaseViewModel;
import com.kevadiyak.myframework.BR;

public class PreferenceFragmentViewModel extends BaseViewModel {
    private Context context;
    private Logs logs;

    private String message;

    public PreferenceFragmentViewModel(Context context, Logs logs) {
        this.context = context;
        this.logs = logs;
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if(TextUtils.equals(this.message, message)) return;

        this.message = message;
        notifyPropertyChanged(BR.message);
    }
}
