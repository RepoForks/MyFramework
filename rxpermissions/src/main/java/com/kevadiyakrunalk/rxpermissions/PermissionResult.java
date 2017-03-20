package com.kevadiyakrunalk.rxpermissions;

import java.util.HashMap;
import java.util.List;

/**
 * The interface Permission result.
 */
public interface PermissionResult {
    /**
     * On permission result.
     *
     * @param status the status
     * @param value  the value
     */
    void onPermissionResult(Permission status, HashMap<Permission, List<String>> value);
}
