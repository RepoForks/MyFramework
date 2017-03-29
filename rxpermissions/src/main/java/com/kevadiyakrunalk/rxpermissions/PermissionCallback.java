package com.kevadiyakrunalk.rxpermissions;

/**
 * Created by KevadiyaKrunalK on 25-03-2017.
 */

public interface PermissionCallback {
    void onPermission(PermissionStatus status, String... permission);
    void onRational(DialogCallback callback, String... permission);
    void onAccessRemoved(DialogCallback callback, String... permission);
}
