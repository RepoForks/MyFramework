package com.kevadiyak.rxphotopicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

//import com.mylibrary.common.FileUtil;
//import com.mylibrary.common.ImageUtil;
import com.kevadiyak.commonutils.common.FileUtil;
import com.kevadiyak.commonutils.common.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * The type Rx image picker.
 */
public class RxPhotoPicker {
    private static RxPhotoPicker sSingleton;

    private Context context;
    private PublishSubject<Uri> publishSubject;
    private PublishSubject<List<Uri>> publishSubjectMultipleImages;

    private RxPhotoPicker(Context ctx) {
        context = ctx;
    }

    /**
     * Gets instance.
     *
     * @param ctx the ctx
     * @return the instance
     */
    public static RxPhotoPicker getInstance(Context ctx) {
        if (sSingleton == null) {
            synchronized (RxPhotoPicker.class) {
                if (sSingleton == null) {
                    sSingleton = new RxPhotoPicker(ctx);
                }
            }
        }
        return sSingleton;
    }

    /**
     * Gets active subscription.
     *
     * @return the active subscription
     */
    public Observable<Uri> getActiveSubscription() {
        return publishSubject;
    }

    /**
     * Request image observable.
     *
     * @param imageSource the image source
     * @return the observable
     */
    public Observable<Uri> requestImage(Sources imageSource) {
        publishSubject = PublishSubject.create();
        startImagePickPhotoActivity(imageSource.ordinal(), false);
        return publishSubject;
    }

    /**
     * Request multiple images observable.
     *
     * @return the observable
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Observable<List<Uri>> requestMultipleImages() {
        publishSubjectMultipleImages = PublishSubject.create();
        startImagePickPhotoActivity(Sources.GALLERY.ordinal(), true);
        return publishSubjectMultipleImages;
    }

    /**
     * On image picked.
     *
     * @param uri the uri
     */
    void onImagePicked(Uri uri) {
        if (publishSubject != null) {
            publishSubject.onNext(uri);
            publishSubject.onCompleted();
        }
    }

    /**
     * On images picked.
     *
     * @param uris the uris
     */
    void onImagesPicked(List<Uri> uris) {
        if (publishSubjectMultipleImages != null) {
            publishSubjectMultipleImages.onNext(uris);
            publishSubjectMultipleImages.onCompleted();
        }
    }

    private void startImagePickPhotoActivity(int imageSource, boolean allowMultipleImages) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PhotoActivity.ALLOW_MULTIPLE_IMAGES, allowMultipleImages);
        intent.putExtra(PhotoActivity.IMAGE_SOURCE, imageSource);
        context.startActivity(intent);
    }

    /**
     * Pick single image.
     *
     * @param sources      the sources
     * @param transformers the transformers
     * @param result       the result
     * @param filePathDir  the file path dir
     */
    //User use to activity
    @SuppressWarnings({"Convert2MethodRef", "unchecked"})
    public void pickSingleImage(Sources sources, Transformers transformers, PhotoInterface result, File... filePathDir) {
        if (transformers == Transformers.FILE) {
            requestImage(sources)
                    .flatMap(new Func1<Uri, Observable<File>>() {
                        @Override
                        public Observable<File> call(Uri uri) {
                            File filePath = null;
                            try {
                                if (filePathDir.length > 0)
                                    filePath = FileUtil.getInstance(context).createImageTempFile(filePathDir[0]);
                            }catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(filePath != null)
                                return ImageUtil.getInstance(context).uriToFile(uri, filePath);
                            else
                                return null;
                        }
                    })
                    .subscribe(file -> {
                        result.onPhotoResult(file);
                    });
        } else if (transformers == Transformers.BITMAP) {
            requestImage(sources)
                    .flatMap(new Func1<Uri, Observable<Bitmap>>() {
                        @Override
                        public Observable<Bitmap> call(Uri uri) {
                            return ImageUtil.getInstance(context).uriToBitmap(uri);
                        }
                    })
                    .subscribe(bitmap -> {
                        result.onPhotoResult(bitmap);
                    });
        } else {
            requestImage(sources)
                    .subscribe(uri -> {
                        result.onPhotoResult(uri);
                    });
        }
    }

    /**
     * Pick multiple image.
     *
     * @param transformers the transformers
     * @param result       the result
     * @param filePathDir  the file path dir
     */
    @SuppressWarnings({"Convert2MethodRef", "unchecked"})
    public void pickMultipleImage(Transformers transformers, PhotoInterface result, File... filePathDir) {
        if (transformers == Transformers.FILE) {
            requestMultipleImages()
                    .flatMap(new Func1<List<Uri>, Observable<List<File>>>() {
                        @Override
                        public Observable<List<File>> call(List<Uri> uris) {
                            List<File> filePath = null;
                            if(filePathDir.length > 0) {
                                filePath = new ArrayList<>();
                                int size = uris.size();
                                try {
                                    for(int i=0; i<size; i++)
                                        filePath.add(FileUtil.getInstance(context).createImageTempFile(filePathDir[0]));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(filePath != null)
                                return ImageUtil.getInstance(context).uriToFile(uris, filePath);
                            else
                                return null;
                        }
                    })
                    .subscribe(files -> {
                        result.onPhotoResult(files);
                    });
        } else if (transformers == Transformers.BITMAP) {
            requestMultipleImages()
                    .flatMap(new Func1<List<Uri>, Observable<List<Bitmap>>>() {
                        @Override
                        public Observable<List<Bitmap>> call(List<Uri> uris) {
                            return ImageUtil.getInstance(context).uriToBitmap(uris);
                        }
                    })
                    .subscribe(bitmaps -> {
                        result.onPhotoResult(bitmaps);
                    });
        } else {
            requestMultipleImages()
                    .subscribe(uris -> {
                        result.onPhotoResult(uris);
                    });
        }
    }
}
