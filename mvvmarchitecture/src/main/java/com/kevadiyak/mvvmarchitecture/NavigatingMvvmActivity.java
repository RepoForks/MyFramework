package com.kevadiyak.mvvmarchitecture;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import com.kevadiyak.mvvmarchitecture.common.NavigatingViewModel;
import com.kevadiyak.mvvmarchitecture.common.Navigator;
import com.kevadiyak.mvvmarchitecture.delegates.ActivityDelegate;
import com.kevadiyak.mvvmarchitecture.delegates.ActivityDelegateCallback;
import com.kevadiyak.mvvmarchitecture.delegates.NavigatingActivityDelegate;
import com.kevadiyak.mvvmarchitecture.delegates.NavigatingDelegateCallback;

/**
 * MvvmActivity that supports Navigator
 *
 * @param <T> the type of {@link Navigator}
 * @param <S> the type of {@link ViewDataBinding}
 * @param <U> the type of binded {@link NavigatingViewModel}
 */
public abstract class NavigatingMvvmActivity<T extends Navigator, S extends ViewDataBinding,
        U extends NavigatingViewModel<T>>
        extends MvvmActivity<S, U> implements ActivityDelegateCallback<S, U>, NavigatingDelegateCallback<T> {

    private NavigatingActivityDelegate<T, S, U> delegate;

    @NonNull
    @Override
    protected ActivityDelegate<S, U> getMvvmDelegate() {
        if (delegate == null) {
            delegate = new NavigatingActivityDelegate<>(this, this, this);
        }
        return delegate;
    }
}
