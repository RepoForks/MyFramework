package com.kevadiyakrunalk.myframework.viewmodels;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.view.View;

import com.kevadiyakrunalk.commonutils.common.Logs;
import com.kevadiyakrunalk.mvvmarchitecture.common.BaseViewModel;
import com.kevadiyakrunalk.myframework.BR;
import com.kevadiyakrunalk.rxpermissions.Permission;
import com.kevadiyakrunalk.rxpermissions.PermissionResult;
import com.kevadiyakrunalk.rxpermissions.RxPermissions;

import java.util.HashMap;
import java.util.List;

public class PermissionFragmentViewModel extends BaseViewModel {
    private Activity activity;
    private Logs logs;
    private String message;
    private String msg;

    public PermissionFragmentViewModel(Activity activity, Logs logs) {
        this.activity = activity;
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

    public void onPermission(View view) {
        msg = "";
        RxPermissions.getInstance(activity)
            .checkMPermission((status, value) -> {
                if(status == Permission.DENIED || status == Permission.DENIED_REVOKED) {
                    RxPermissions.getInstance(activity)
                            .checkMDeniedRevokedPermission(new PermissionResult() {
                                @Override
                                public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                                    msg += ("Permission -> " + status + ", granted - > " + value.toString() + "\n");
                                    setMessage(msg);
                                    logs.error("Permission", msg);
                                }
                            }, value.get(Permission.DENIED), value.get(Permission.REVOKED));
                } else {
                    msg += ("Permission -> " + status + ", granted - > " + value.toString() + "\n");
                    setMessage(msg);
                    logs.error("Permission", msg);
                }
            },  Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
