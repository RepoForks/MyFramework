package com.kevadiyakrunalk.myframework.viewmodels;

import android.app.Activity;
import android.databinding.Bindable;
import android.text.TextUtils;
import android.view.View;

import com.kevadiyakrunalk.commonutils.common.Logs;
import com.kevadiyakrunalk.mvvmarchitecture.common.BaseViewModel;
import com.kevadiyakrunalk.myframework.BR;
import com.kevadiyakrunalk.myframework.R;
import com.kevadiyakrunalk.rxfilepicker.model.FileType;

import java.util.ArrayList;

public class FileFragmentViewModel extends BaseViewModel {
    private Activity activity;
    private Logs logs;
    private int MAX_ATTACHMENT_COUNT;
    private ArrayList<String> docPaths;
    private ArrayList<FileType> fileTypes;

    private String message;

    public FileFragmentViewModel(Activity activity, Logs logs) {
        this.activity = activity;
        this.logs = logs;

        MAX_ATTACHMENT_COUNT = 10;
        docPaths = new ArrayList<>();

        fileTypes = new ArrayList<>();
        FileType fileType = new FileType();
        fileType.setGroupTitle("IMAGE");
        fileType.setGroupIcon(R.drawable.ic_pdf);
        fileType.setGroupExtension("jpg,jpeg,png");
        fileTypes.add(fileType);

        fileType = new FileType();
        fileType.setGroupTitle("PDF");
        fileType.setGroupIcon(R.drawable.ic_pdf);
        fileType.setGroupExtension("pdf");
        fileTypes.add(fileType);

        fileType = new FileType();
        fileType.setGroupTitle("PPT");
        fileType.setGroupIcon(R.drawable.icon_ppt);
        fileType.setGroupExtension("ppt,pptx");
        fileTypes.add(fileType);

        fileType = new FileType();
        fileType.setGroupTitle("DOC");
        fileType.setGroupIcon(R.drawable.ic_doc);
        fileType.setGroupExtension("doc,docx,dot,dotx");
        fileTypes.add(fileType);

        fileType = new FileType();
        fileType.setGroupTitle("XLS");
        fileType.setGroupIcon(R.drawable.ic_xls);
        fileType.setGroupExtension("xls,xlsx");
        fileTypes.add(fileType);

        fileType = new FileType();
        fileType.setGroupTitle("TXT");
        fileType.setGroupIcon(R.drawable.ic_txt);
        fileType.setGroupExtension("txt");
        fileTypes.add(fileType);

        /*fileType.setGroupTitle("JPG");
        fileType.setGroupIcon(R.drawable.ic_pdf);
        fileType.setGroupExtension("jpg,jpeg");
        fileTypes.add(fileType);*/
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

    public void onFilePick(View view) {
        /*RxPermissions.getInstance(activity)
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            if(docPaths.size()==MAX_ATTACHMENT_COUNT)
                                Toast.makeText(activity, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
                            else
                                RxFilePicker.getInstance(activity)
                                        .setMaxCount(MAX_ATTACHMENT_COUNT)
                                        .setFileType(fileTypes)
                                        .setDirectory(Environment.getExternalStorageDirectory())
                                        .setActivityTheme(R.style.FilePickerTheme)
                                        .pickDocument(activity, new RxFilePicker.FileResult() {
                                            @Override
                                            public void PickFileList(ArrayList<String> list) {
                                                logs.error("Files", list.toString());
                                                docPaths.addAll(list);
                                                setMessage(list.toString());
                                            }
                                        });
                        }
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);*/
    }
}
