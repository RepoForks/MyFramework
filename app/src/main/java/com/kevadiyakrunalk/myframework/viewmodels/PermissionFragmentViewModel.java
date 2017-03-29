package com.kevadiyakrunalk.myframework.viewmodels;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.view.View;

import com.kevadiyakrunalk.commonutils.common.Logs;
import com.kevadiyakrunalk.mvvmarchitecture.common.BaseViewModel;
import com.kevadiyakrunalk.myframework.BR;
import com.kevadiyakrunalk.rxpermissions.DialogCallback;
import com.kevadiyakrunalk.rxpermissions.PermissionCallback;
import com.kevadiyakrunalk.rxpermissions.PermissionStatus;
import com.kevadiyakrunalk.rxpermissions.RxPermissions;

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
                //.showRationalDialog("Allow camera, storage and location access", "Without camera, storage and location permission we are unable to take product image or save and take product image location, .Go ahead and grand permission.")
                //.showAccessRemovedDialog("Permission Error", "Setting dialog message")
                .checkPermission(new PermissionCallback() {
                    @Override
                    public void onPermission(PermissionStatus status, String... permission) {
                        msg += ("Permission -> " + status + ", granted - > " + permission + "\n");
                        setMessage(msg);
                        logs.error("Permission:2", msg);
                    }

                    @Override
                    public void onRational(DialogCallback callback, String... permission) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage("custom rational");
                        alertDialogBuilder.setTitle("Rational");

                        alertDialogBuilder.setPositiveButton("SETTING", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg) {
                                dialog.dismiss();
                                callback.onPositiveButton();
                            }
                        });
                        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg) {
                                dialog.dismiss();
                                callback.onNegativeButton();
                            }
                        });

                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.show();
                    }

                    @Override
                    public void onAccessRemoved(DialogCallback callback, String... permission) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage("custom Access Removed");
                        alertDialogBuilder.setTitle("Setting");

                        alertDialogBuilder.setPositiveButton("SETTING", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg) {
                                dialog.dismiss();
                                callback.onPositiveButton();
                            }
                        });
                        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg) {
                                dialog.dismiss();
                                callback.onNegativeButton();
                            }
                        });

                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.show();
                    }
                }, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
