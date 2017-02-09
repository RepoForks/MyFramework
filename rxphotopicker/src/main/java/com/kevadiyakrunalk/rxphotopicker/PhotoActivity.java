package com.kevadiyakrunalk.rxphotopicker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.kevadiyakrunalk.commonutils.common.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Photo activity.
 */
public class PhotoActivity extends Activity {
    private static final String KEY_CAMERA_PICTURE_URL = "cameraPictureUrl";
    private static final String KEY_CROP_PICTURE_URL = "cropPictureUrl";

    /**
     * The constant IMAGE_SOURCE.
     */
    public static final String IMAGE_SOURCE = "image_source";
    /**
     * The constant ALLOW_MULTIPLE_IMAGES.
     */
    public static final String ALLOW_MULTIPLE_IMAGES = "allow_multiple_images";
    public static final String ALLOW_IMAGE_CROP = "allow_image_crop";

    private Uri cameraPictureUrl, cropPictureUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_CAMERA_PICTURE_URL, cameraPictureUrl);
        outState.putParcelable(KEY_CROP_PICTURE_URL, cropPictureUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cameraPictureUrl = savedInstanceState.getParcelable(KEY_CAMERA_PICTURE_URL);
        cropPictureUrl = savedInstanceState.getParcelable(KEY_CROP_PICTURE_URL);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.SELECT_PHOTO:
                    handleGalleryResult(data);
                    break;
                case Constant.TAKE_PHOTO:
                    handleCameraResult(cameraPictureUrl);
                    break;

                case Constant.CROPING_CODE:
                    RxPhotoPicker.getInstance(getApplicationContext()).onImagePicked(cropPictureUrl);
                    finish();
                    break;
            }
        } else
            finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void handleGalleryResult(Intent data) {
        if (getIntent().getBooleanExtra(ALLOW_MULTIPLE_IMAGES, false)) {
            ArrayList<Uri> imageUris = new ArrayList<>();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                int size = clipData.getItemCount();
                for (int i = 0; i < size; i++) {
                    imageUris.add(clipData.getItemAt(i).getUri());
                }
            } else {
                imageUris.add(data.getData());
            }
            RxPhotoPicker.getInstance(getApplicationContext()).onImagesPicked(imageUris);
            finish();
        } else {
            if(getIntent().getBooleanExtra(ALLOW_IMAGE_CROP, false)) {
                try {
                    cropPictureUrl = Uri.fromFile(FileUtil.getInstance(getApplicationContext())
                            .createImageTempFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                    CropingIMG(data.getData(), cropPictureUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                    RxPhotoPicker.getInstance(getApplicationContext()).onImagePicked(Uri.EMPTY);
                    finish();
                }
            } else {
                RxPhotoPicker.getInstance(getApplicationContext()).onImagePicked(data.getData());
                finish();
            }
        }
    }

    private void handleCameraResult(Uri cameraPictureUrl) {
        if(getIntent().getBooleanExtra(ALLOW_IMAGE_CROP, false)) {
            try{
                cropPictureUrl = Uri.fromFile(FileUtil.getInstance(getApplicationContext())
                        .createImageTempFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                CropingIMG(cameraPictureUrl, cropPictureUrl);
            } catch (IOException e) {
                e.printStackTrace();
                RxPhotoPicker.getInstance(getApplicationContext()).onImagePicked(Uri.EMPTY);
                finish();
            }
        } else {
            RxPhotoPicker.getInstance(getApplicationContext()).onImagePicked(cameraPictureUrl);
            finish();
        }
    }

    private void CropingIMG(final Uri sourceImage, Uri destinationImage) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0 );
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            CropOption.Builder cropBuilder = RxPhotoPicker.getInstance(getApplicationContext()).getBuilder();
            if(cropBuilder == null)
                cropBuilder = new CropOption.Builder();

            intent.setData(sourceImage);
            intent.putExtra("outputX", cropBuilder.getOutputX());
            intent.putExtra("outputY", cropBuilder.getOutputY());
            intent.putExtra("aspectX", cropBuilder.getAspectX());
            intent.putExtra("aspectY", cropBuilder.getAspectY());
            intent.putExtra("scale", cropBuilder.isScale());

            //intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, destinationImage);

            if (size == 1) {
                Intent i   = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                startActivityForResult(i, Constant.CROPING_CODE);
            } else  {
                Intent i   = new Intent(intent);
                i.putExtra(Intent.EXTRA_INITIAL_INTENTS, list.toArray(new Parcelable[list.size()]));
                startActivityForResult(i, Constant.CROPING_CODE);
            }
        }
    }

    private void handleIntent(Intent intent) {
        Sources sourceType = Sources.values()[intent.getIntExtra(IMAGE_SOURCE, 0)];
        final int[] chooseCode = {0};
        final Intent[] pictureChooseIntent = {null};

        switch (sourceType) {
            case CAMERA:
                cameraPictureUrl = FileUtil.getInstance(getApplicationContext()).createImageUri();
                pictureChooseIntent[0] = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pictureChooseIntent[0].putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl);
                chooseCode[0] = Constant.TAKE_PHOTO;
                startActivityForResult(pictureChooseIntent[0], chooseCode[0]);
                break;
            case GALLERY:
                pictureChooseIntent[0] = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //pictureChooseIntent[0] = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    pictureChooseIntent[0].putExtra(Intent.EXTRA_ALLOW_MULTIPLE, getIntent().getBooleanExtra(ALLOW_MULTIPLE_IMAGES, false));
                    pictureChooseIntent[0].addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

                } else {
                    //pictureChooseIntent[0] = new Intent(Intent.ACTION_GET_CONTENT);
                }
                //pictureChooseIntent[0].putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                pictureChooseIntent[0].addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pictureChooseIntent[0].setType("image/*");
                chooseCode[0] = Constant.SELECT_PHOTO;
                startActivityForResult(pictureChooseIntent[0], chooseCode[0]);
                break;
        }
    }
}