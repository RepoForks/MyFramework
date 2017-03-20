package com.kevadiyakrunalk.rxpermissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The type Rx permissions.
 */
public class RxPermissions {
    /**
     * The S singleton.
     */
    static RxPermissions sSingleton;
    private PermissionResult result;
    private WeakReference<Activity> mActivityReference;

    private boolean isDeniedRevoke;
    private String[] deniedPermission;
    private String[] revokedPermission;

    /**
     * Gets instance.
     *
     * @param activity the activity
     * @return the instance
     */
    public static RxPermissions getInstance(Activity activity) {
        if (sSingleton == null) {
            synchronized (RxPermissions.class) {
                if (sSingleton == null) {
                    sSingleton = new RxPermissions(activity);
                }
            }
        }
        return sSingleton;
    }

    private RxPermissions(Activity activity) {
        mActivityReference = new WeakReference<>(activity);
    }

    private void startPermissionActivity(String[] permissions) {
        if(mActivityReference != null && mActivityReference.get() != null) {
            Intent intent = new Intent(mActivityReference.get(), PermissionActivity.class);
            intent.putExtra("permissions", permissions);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivityReference.get().startActivity(intent);
        }
    }

    public void startRevokePermissionActivity(String[] permissions) {
        if(mActivityReference != null && mActivityReference.get() != null) {
            Intent intent = new Intent(mActivityReference.get(), RevokePermissionActivity.class);
            intent.putExtra("permissions", permissions);
            intent.putExtra("package", mActivityReference.get().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivityReference.get().startActivity(intent);
        }
    }

    /**
     * Is marshmallow boolean.
     *
     * @return the boolean
     */
    public boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isGranted(String[] permissions) {
        if(mActivityReference != null && mActivityReference.get()!= null) {
            for (String permission : permissions) {
                if (mActivityReference.get().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isRationale(String[] permissions) {
        if(mActivityReference != null && mActivityReference.get()!= null) {
            for (String permission : permissions) {
                if (!isGranted(permission) && mActivityReference.get().shouldShowRequestPermissionRationale(permission))
                    return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isGranted(String permission) {
        return mActivityReference != null && mActivityReference.get() != null && mActivityReference.get().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean isRationale(String permission) {
        return mActivityReference != null && mActivityReference.get() != null && mActivityReference.get().shouldShowRequestPermissionRationale(permission);
    }

    /**
     * On request permissions result.
     *
     * @param requestCode  the request code
     * @param permissions  the permissions
     * @param grantResults the grant results
     */
    void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(isDeniedRevoke) {
            isDeniedRevoke = false;
            startRevokePermissionActivity(revokedPermission);
        } else if(revokedPermission != null && deniedPermission != null)
            checkResult(getMargePermission());
        else
            checkResult(permissions);
    }

    /**
     * Check m permission.
     *
     * @param result          the result
     * @param allPermission the param permission
     */
    public void checkMPermission(PermissionResult result, String... allPermission) {
        this.result = result;
        isDeniedRevoke = false;
        if(isMarshmallow()) {
            if(isGranted(allPermission) || isRationale(allPermission))
                checkResult(allPermission);
            else
                startPermissionActivity(allPermission);
        } else
            checkResult(allPermission);
    }

    public void checkMDeniedPermission(PermissionResult result, List<String> deniedPermission) {
        this.result = result;
        isDeniedRevoke = false;
        if(isMarshmallow())
            startPermissionActivity(deniedPermission.toArray(new String[deniedPermission.size()]));
        else
            checkResult(deniedPermission.toArray(new String[deniedPermission.size()]));
    }

    public void checkMRevokedPermission(PermissionResult result, List<String> revokedPermission) {
        this.result = result;
        isDeniedRevoke = false;
        if(isMarshmallow())
            startRevokePermissionActivity(revokedPermission.toArray(new String[revokedPermission.size()]));
        else
            checkResult(revokedPermission.toArray(new String[revokedPermission.size()]));
    }

    public void checkMDeniedRevokedPermission(PermissionResult result, List<String> deniedPermission, List<String> revokedPermission) {
        this.result = result;
        isDeniedRevoke = true;
        this.deniedPermission = deniedPermission.toArray(new String[deniedPermission.size()]);
        this.revokedPermission = revokedPermission.toArray(new String[revokedPermission.size()]);
        if(isMarshmallow())
            startPermissionActivity(deniedPermission.toArray(new String[deniedPermission.size()]));
        else
            checkResult(getMargePermission());
    }

    private void checkResult(String... paramPermission) {
        HashMap<Permission, List<String>> hashMap = new HashMap<>();
        List<String> listGranted = new ArrayList<>();
        List<String> listDenied = new ArrayList<>();
        List<String> listRevoked = new ArrayList<>();
        for (String p : paramPermission) {
            if(isGranted(p))
                listGranted.add(p);
            else if(!isRationale(p))
                listDenied.add(p);
            else
                listRevoked.add(p);
        }
        hashMap.put(Permission.GRANTED, listGranted);
        hashMap.put(Permission.DENIED, listDenied);
        hashMap.put(Permission.REVOKED, listRevoked);
        if(result != null) {
            if(listGranted.size() > 0 && listDenied.size() > 0 && listRevoked.size() > 0)
                result.onPermissionResult(Permission.All, hashMap);
            else if(listGranted.size() > 0 && listDenied.size() > 0 && listRevoked.size() <= 0)
                result.onPermissionResult(Permission.GRANTED_DENIED, hashMap);
            else if(listGranted.size() > 0 && listDenied.size() <= 0 && listRevoked.size() > 0)
                result.onPermissionResult(Permission.GRANTED_REVOKED, hashMap);
            else if(listGranted.size() <= 0 && listDenied.size() > 0 && listRevoked.size() > 0)
                result.onPermissionResult(Permission.DENIED_REVOKED, hashMap);
            else if(listGranted.size() > 0 && listDenied.size() <= 0 && listRevoked.size() <= 0)
                result.onPermissionResult(Permission.GRANTED, hashMap);
            else if(listGranted.size() <= 0 && listDenied.size() > 0 && listRevoked.size() <= 0)
                result.onPermissionResult(Permission.DENIED, hashMap);
            else if(listGranted.size() <= 0 && listDenied.size() <= 0 && listRevoked.size() > 0)
                result.onPermissionResult(Permission.REVOKED, hashMap);
            else
                result.onPermissionResult(Permission.UNKNOWN, hashMap);
        }
    }

    private String[] getMargePermission() {
        String[] data = new String[deniedPermission.length + revokedPermission.length];
        int i = 0;
        for (String p: deniedPermission)
            data[i++] = p;
        for (String p: revokedPermission)
            data[i++] = p;
        return data;
    }
}