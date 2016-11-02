package com.kevadiyak.rxlocation.observables.location;

import android.Manifest;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kevadiyak.rxlocation.observables.BaseLocationObservable;

import rx.Observable;
import rx.Observer;

/**
 * The type Location updates observable.
 */
public class LocationUpdatesObservable extends BaseLocationObservable<Location> {
    private Context context;

    /**
     * Create observable observable.
     *
     * @param ctx             the ctx
     * @param locationRequest the location request
     * @return the observable
     */
    public static Observable<Location> createObservable(Context ctx, LocationRequest locationRequest) {
        return Observable.create(new LocationUpdatesObservable(ctx, locationRequest));
    }

    private final LocationRequest locationRequest;
    private LocationListener listener;

    private LocationUpdatesObservable(Context ctx, LocationRequest locationRequest) {
        super(ctx);
        context = ctx;
        this.locationRequest = locationRequest;
    }

    @Override
    protected void onGoogleApiClientReady(GoogleApiClient apiClient, final Observer<? super Location> observer) {
        listener = location -> observer.onNext(location);
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, listener);
    }

    @Override
    protected void onUnsubscribed(GoogleApiClient locationClient) {
        if (locationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, listener);
        }
    }
}
