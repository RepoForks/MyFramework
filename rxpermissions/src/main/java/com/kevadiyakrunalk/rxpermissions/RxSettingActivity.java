package com.kevadiyakrunalk.rxpermissions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import static com.kevadiyakrunalk.rxpermissions.RxPermissions.REQUEST_CODE;

public class RxSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + RxPermissions.getInstance(this).getPermissionBean().getStrPackage()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RxPermissions.getInstance(this).onActivityResult(requestCode, resultCode, data);
        finish();
    }
}