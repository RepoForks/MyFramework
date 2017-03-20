package com.kevadiyakrunalk.myframework.viewmodels;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.kevadiyakrunalk.commonutils.common.Logs;
import com.kevadiyakrunalk.mvvmarchitecture.common.BaseViewModel;
import com.kevadiyakrunalk.rxpermissions.Permission;
import com.kevadiyakrunalk.rxpermissions.PermissionResult;
import com.kevadiyakrunalk.rxpermissions.RxPermissions;
import com.kevadiyakrunalk.rxphotopicker.CropOption;
import com.kevadiyakrunalk.rxphotopicker.PhotoInterface;
import com.kevadiyakrunalk.rxphotopicker.RxPhotoPicker;
import com.kevadiyakrunalk.rxphotopicker.Sources;
import com.kevadiyakrunalk.rxphotopicker.Transformers;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class PhotoFragmentViewModel extends BaseViewModel {
    private Activity context;
    private Logs logs;
    private ImageView imageView;

    public PhotoFragmentViewModel(Activity context, Logs logs) {
        this.context = context;
        this.logs = logs;
    }

    public void setImageView(ImageView view) {
        imageView = view;
    }

    public void onGalleryUri(View view) {
        CropOption.Builder builder = new CropOption.Builder();
        builder.setOutputHW(690, 690);
        builder.setAspectRatio(3, 2);
        builder.setScale(true);
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickSingleImage(Sources.GALLERY, Transformers.URI, true, builder, new PhotoInterface<Uri>() {
                                        @Override
                                        public void onPhotoResult(Uri uri) {
                                            if(uri != Uri.EMPTY) {
                                                logs.error("gallery", "Uri -> " + uri);
                                                imageView.setImageURI(uri);
                                            } else
                                                logs.error("gallery", "Uri -> EMPTY");
                                        }
                                    });
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void onGalleryBitmap(View view) {
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickSingleImage(Sources.GALLERY, Transformers.BITMAP, true, new PhotoInterface<Bitmap>() {
                                        @Override
                                        public void onPhotoResult(Bitmap bitmap) {
                                            if(bitmap != null) {
                                                logs.error("gallery", "Bitmap -> " + bitmap.toString());
                                                imageView.setImageBitmap(bitmap);
                                            } else
                                                logs.error("gallery", "Bitmap -> NULL");
                                        }
                                    });
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void onGalleryFile(View view) {
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickSingleImage(Sources.GALLERY, Transformers.FILE, true, new PhotoInterface<File>() {
                                        @Override
                                        public void onPhotoResult(File file) {
                                            if(file != null) {
                                                logs.error("gallery", "File -> " + file.getName() + " ,Size -> " + fileSize(file.length()));
                                                imageView.setImageURI(Uri.parse(file.getPath()));
                                            } else
                                                logs.error("gallery", "File -> NULL");
                                        }
                                    }, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));//context.getFilesDir());
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void onCameraUri(View view) {
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickSingleImage(Sources.CAMERA, Transformers.URI, true, new PhotoInterface<Uri>() {
                                        @Override
                                        public void onPhotoResult(Uri uri) {
                                            if(uri != Uri.EMPTY) {
                                                logs.error("camera", "Uri -> " + uri);
                                                imageView.setImageURI(uri);
                                            } else
                                                logs.error("camera", "Uri -> EMPTY");
                                        }
                                    });
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public void onCameraBitmap(View view) {
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickSingleImage(Sources.CAMERA, Transformers.BITMAP, true, new PhotoInterface<Bitmap>() {
                                        @Override
                                        public void onPhotoResult(Bitmap bitmap) {
                                            if(bitmap != null) {
                                                logs.error("camera", "Bitmap -> " + bitmap.toString());
                                                imageView.setImageBitmap(bitmap);
                                            } else
                                                logs.error("camera", "Bitmap -> NULL");
                                        }
                                    });
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public void onCameraFile(View view) {
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickSingleImage(Sources.CAMERA, Transformers.FILE, true, new PhotoInterface<File>() {
                                        @Override
                                        public void onPhotoResult(File file) {
                                            if(file != null) {
                                                logs.error("camera", "File -> " + file.getName() + " ,Size -> " + fileSize(file.length()));
                                                imageView.setImageURI(Uri.parse(file.getPath()));
                                            } else
                                                logs.error("camera", "File -> NULL");
                                        }
                                    }, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));//context.getFilesDir());
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    public void onGalleryMultipleUri(View view) {
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickMultipleImage(Transformers.URI, new PhotoInterface<List<Uri>>() {
                                        @Override
                                        public void onPhotoResult(List<Uri> uri) {
                                            for (Uri uri1 : uri) {
                                                if(uri1 != Uri.EMPTY) {
                                                    logs.error("gallery multiple", "Uri -> " + uri1);
                                                    imageView.setImageURI(uri1);
                                                } else
                                                    logs.error("gallery multiple", "Uri -> EMPTY");
                                            }
                                        }
                                    });
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void onGalleryMultipleBitmap(View view) {
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickMultipleImage(Transformers.BITMAP, new PhotoInterface<List<Bitmap>>() {
                                        @Override
                                        public void onPhotoResult(List<Bitmap> bitmap) {
                                            for (Bitmap bitmap1 : bitmap) {
                                                if(bitmap1 != null) {
                                                    logs.error("gallery multiple", "Bitmap -> " + bitmap1.toString());
                                                    imageView.setImageBitmap(bitmap1);
                                                } else
                                                    logs.error("gallery multiple", "Bitmap -> NULL");
                                            }
                                        }
                                    });
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public void onGalleryMultipleFile(View view) {
        RxPermissions.getInstance(context)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            RxPhotoPicker.getInstance(context)
                                    .pickMultipleImage(Transformers.FILE, new PhotoInterface<List<File>>() {
                                        @Override
                                        public void onPhotoResult(List<File> file) {
                                            for(File file1 : file) {
                                                if(file1 != null) {
                                                    logs.error("gallery multiple", "File -> " + file1.getName() + " ,Size -> " + fileSize(file1.length()));
                                                    imageView.setImageURI(Uri.parse(file1.getPath()));
                                                } else
                                                    logs.error("gallery multiple", "File -> NULL");
                                            }
                                        }
                                    }, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));//context.getFilesDir());
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public String fileSize(long size) {
        long kb = size / 1024;
        if(kb >= 1024)
            return (kb/1024) + " Mb";
        else
            return kb + " Kb";
    }
}
