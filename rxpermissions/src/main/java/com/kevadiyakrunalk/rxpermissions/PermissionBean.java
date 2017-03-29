package com.kevadiyakrunalk.rxpermissions;

import java.util.Arrays;

/**
 * Created by KevadiyaKrunalK on 25-03-2017.
 */
public class PermissionBean {
    private String[] permission;
    private String strPackage;
    private String rationalTitle;
    private String rationalMessage;
    private String accessRemovedTitle;
    private String accessRemovedMessage;
    private PermissionStatus status;

    public PermissionBean() {
        strPackage = "";
        rationalTitle = "";
        rationalMessage = "";
        accessRemovedTitle = "";
        accessRemovedMessage = "";
        status = PermissionStatus.ERROR;
    }

    public String[] getPermission() {
        return permission;
    }

    public void setPermission(String... permission) {
        this.permission = permission;
    }

    public String getStrPackage() {
        return strPackage;
    }

    public void setStrPackage(String strPackage) {
        this.strPackage = strPackage;
    }

    public String getRationalTitle() {
        return rationalTitle;
    }

    public void setRationalTitle(String rationalTitle) {
        this.rationalTitle = rationalTitle;
    }

    public String getRationalMessage() {
        return rationalMessage;
    }

    public void setRationalMessage(String rationalMessage) {
        this.rationalMessage = rationalMessage;
    }

    public String getAccessRemovedTitle() {
        return accessRemovedTitle;
    }

    public void setAccessRemovedTitle(String accessRemovedTitle) {
        this.accessRemovedTitle = accessRemovedTitle;
    }

    public String getAccessRemovedMessage() {
        return accessRemovedMessage;
    }

    public void setAccessRemovedMessage(String accessRemovedMessage) {
        this.accessRemovedMessage = accessRemovedMessage;
    }

    public PermissionStatus getStatus() {
        return status;
    }

    public void setStatus(PermissionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PermissionBean{" +
                "permission=" + Arrays.toString(permission) +
                ", strPackage='" + strPackage + '\'' +
                ", rationalTitle='" + rationalTitle + '\'' +
                ", rationalMessage='" + rationalMessage + '\'' +
                ", accessRemovedTitle='" + accessRemovedTitle + '\'' +
                ", accessRemovedMessage='" + accessRemovedMessage + '\'' +
                ", status=" + status +
                '}';
    }
}
