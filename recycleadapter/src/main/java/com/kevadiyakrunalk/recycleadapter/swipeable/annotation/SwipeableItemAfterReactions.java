package com.kevadiyakrunalk.recycleadapter.swipeable.annotation;

import android.support.annotation.IntDef;

import com.kevadiyakrunalk.recycleadapter.swipeable.SwipeableItemConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(flag = true, value = {
        /*SwipeableItemConstants.AFTER_SWIPE_REACTION_DEFAULT,*/
        SwipeableItemConstants.AFTER_SWIPE_REACTION_MOVE_TO_ORIGIN,
        SwipeableItemConstants.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION,
        SwipeableItemConstants.AFTER_SWIPE_REACTION_REMOVE_ITEM,
        SwipeableItemConstants.AFTER_SWIPE_REACTION_DO_NOTHING,
})
@Retention(RetentionPolicy.SOURCE)
public @interface SwipeableItemAfterReactions {
}
