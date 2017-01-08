package com.kevadiyakrunalk.recycleadapter.draggable.annotation;

import android.support.annotation.IntDef;

import com.kevadiyakrunalk.recycleadapter.draggable.DraggableItemConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(flag = true, value = {
        DraggableItemConstants.STATE_FLAG_DRAGGING,
        DraggableItemConstants.STATE_FLAG_IS_ACTIVE,
        DraggableItemConstants.STATE_FLAG_IS_IN_RANGE,
        DraggableItemConstants.STATE_FLAG_IS_UPDATED,
})
@Retention(RetentionPolicy.SOURCE)
public @interface DraggableItemStateFlags {
}
