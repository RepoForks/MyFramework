package com.kevadiyakrunalk.myframework.viewmodels;

import android.Manifest;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.kevadiyakrunalk.commonutils.common.Compressor;
import com.kevadiyakrunalk.commonutils.common.DrawableColorChange;
import com.kevadiyakrunalk.commonutils.common.Logs;
import com.kevadiyakrunalk.mvvmarchitecture.common.BaseViewModel;
import com.kevadiyakrunalk.myframework.R;
import com.kevadiyakrunalk.rxpermissions.PermissionResult;
import com.kevadiyakrunalk.rxpermissions.RxPermissions;
import com.kevadiyakrunalk.rxphotopicker.PhotoInterface;
import com.kevadiyakrunalk.rxphotopicker.RxPhotoPicker;
import com.kevadiyakrunalk.rxphotopicker.Sources;
import com.kevadiyakrunalk.rxphotopicker.Transformers;

import java.io.File;

import rx.functions.Action1;

/**
 * Created by Krunal.Kevadiya on 24/10/16.
 */
public class OtherFragmentViewModel extends BaseViewModel {
    private Context context;
    private Logs logs;
    private ImageView imageView;

    public OtherFragmentViewModel(Context context, Logs logs) {
        this.context = context;
        this.logs = logs;
    }

    public void setImageViewCha(ImageView imageView) {
        this.imageView = imageView;
    }

    public void onColorChange(View view) {
        int value = Integer.parseInt(String.valueOf(view.getTag()));
        if(value == 1) {
            imageView.setImageDrawable(DrawableColorChange.getInstance(context)
                    .changeColorById(R.drawable.ic_file, R.color.colorAccent));
            view.setTag(2);
        } else {
            imageView.setImageDrawable(DrawableColorChange.getInstance(context)
                    .changeColorById(R.drawable.ic_file, android.R.color.black));
            view.setTag(1);
        }
    }

    public void onGalleryFile(View view) {
        if(RxPermissions.getInstance(context).isMarshmallow()) {
            RxPermissions.getInstance(context).checkMPermission(new PermissionResult() {
                @Override
                public void onPermissionResult(String permission, boolean granted) {
                    if(granted) {
                        RxPhotoPicker.getInstance(context)
                                .pickSingleImage(Sources.GALLERY, Transformers.FILE, new PhotoInterface<File>() {
                                    @Override
                                    public void onPhotoResult(File file) {
                                        logs.error("gallery", "File -> " + (file.length()/1024) + " KB");

                                        new Compressor.Builder(context, context.getFilesDir().getPath())
                                                .setMaxWidth(612.0f).setMaxHeight(816.0f).setQuality(80) //default option
                                                .build().compressToFileAsObservable(file)
                                                .subscribe(new Action1<File>() {
                                                    @Override
                                                    public void call(File file) {
                                                        logs.error("Compressor", "File -> " + (file.length()/1024) + " KB");
                                                    }
                                                });
                                    }
                                }, context.getFilesDir());
                    }
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            RxPhotoPicker.getInstance(context)
                    .pickSingleImage(Sources.GALLERY, Transformers.FILE, new PhotoInterface<File>() {
                        @Override
                        public void onPhotoResult(File file) {
                            logs.error("gallery", "File -> " + (file.length()/1024) + " KB");

                            new Compressor.Builder(context, context.getFilesDir().getPath())
                                    .setMaxWidth(612.0f).setMaxHeight(816.0f).setQuality(80) //default option
                                    .build().compressToFileAsObservable(file)
                                    .subscribe(new Action1<File>() {
                                        @Override
                                        public void call(File file) {
                                            logs.error("Compressor", "File -> " + (file.length()/1024) + " KB");
                                        }
                                    });
                        }
                    }, context.getFilesDir());
        }
    }
}
