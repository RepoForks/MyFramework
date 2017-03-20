package com.kevadiyakrunalk.myframework.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.kevadiyakrunalk.commonutils.common.Logs;
import com.kevadiyakrunalk.mvvmarchitecture.NavigatingMvvmFragment;
import com.kevadiyakrunalk.mvvmarchitecture.common.BindingConfig;
import com.kevadiyakrunalk.myframework.R;
import com.kevadiyakrunalk.myframework.databinding.FragmentLocationBinding;
import com.kevadiyakrunalk.myframework.navigators.LocationNavigator;
import com.kevadiyakrunalk.myframework.viewmodels.LocationFragmentViewModel;
import com.kevadiyakrunalk.rxlocation.ReactiveLocationProvider;
import com.kevadiyakrunalk.rxpermissions.Permission;
import com.kevadiyakrunalk.rxpermissions.PermissionResult;
import com.kevadiyakrunalk.rxpermissions.RxPermissions;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class LocationFragment extends NavigatingMvvmFragment<LocationNavigator, FragmentLocationBinding, LocationFragmentViewModel> {
    private static final int REQUEST_CHECK_SETTINGS = 0;
    private ReactiveLocationProvider locationProvider;

    private Observable<Location> lastKnownLocationObservable;
    private Observable<Location> locationUpdatesObservable;
    private Observable<ActivityRecognitionResult> activityObservable;

    private Subscription lastKnownLocationSubscription;
    private Subscription updatableLocationSubscription;
    private Subscription addressSubscription;
    private Subscription activitySubscription;
    private Observable<String> addressObservable;

    private Logs logs;

    @NonNull
    @Override
    public LocationFragmentViewModel createViewModel() {
        logs = Logs.getInstance(getActivity());
        return new LocationFragmentViewModel(getActivity(), logs);
    }

    @NonNull
    @Override
    public BindingConfig getBindingConfig() {
        return new BindingConfig(R.layout.fragment_location);
    }

    @NonNull
    @Override
    public LocationNavigator getNavigator() {
        Log.e("getNavigator", "Init");
        return new LocationNavigator(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        locationProvider = new ReactiveLocationProvider(getActivity());
        lastKnownLocationObservable = locationProvider.getLastKnownLocation();

        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(5)
                .setInterval(100);

        locationUpdatesObservable = locationProvider
                .checkLocationSettings(
                        new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest)
                                .setAlwaysShow(true)
                                .build()
                )
                .doOnNext(new Action1<LocationSettingsResult>() {
                    @Override
                    public void call(LocationSettingsResult locationSettingsResult) {
                        Status status = locationSettingsResult.getStatus();
                        if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException th) {
                                logs.error("MainActivity", "Error opening settings activity." + th);
                            }
                        }
                    }
                })
                .flatMap(new Func1<LocationSettingsResult, Observable<Location>>() {
                    @Override
                    public Observable<Location> call(LocationSettingsResult locationSettingsResult) {
                        return locationProvider.getUpdatedLocation(locationRequest);
                    }
                });

        addressObservable = locationProvider.getUpdatedLocation(locationRequest)
                .flatMap(new Func1<Location, Observable<List<Address>>>() {
                    @Override
                    public Observable<List<Address>> call(Location location) {
                        return locationProvider.getReverseGeocodeObservable(location.getLatitude(), location.getLongitude(), 1);
                    }
                })
                .map(new Func1<List<Address>, Address>() {
                    @Override
                    public Address call(List<Address> addresses) {
                        return addresses != null && !addresses.isEmpty() ? addresses.get(0) : null;
                    }
                })
                .map(new Func1<Address, String>() {
                    @Override
                    public String call(Address address) {
                        if (address == null)
                            return "";
                        String addressLines = "";
                        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                            addressLines += address.getAddressLine(i) + '\n';
                        }
                        return addressLines;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        activityObservable = locationProvider.getDetectedActivity(50);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add("Geofencing").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                RxPermissions.getInstance(getActivity())
                        .checkMPermission(new PermissionResult() {
                            @Override
                            public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                                if(status == Permission.GRANTED) {
                                    getViewModel().onStartGeofenceActivity();
                                    //getNavigator().navigateToGeofence();
                                }
                            }
                        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
                return true;
            }
        });
        menu.add("Places").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (TextUtils.isEmpty(getString(R.string.API_KEY))) {
                    Toast.makeText(getActivity(), "First you need to configure your API Key - see README.md", Toast.LENGTH_SHORT).show();
                } else {
                    RxPermissions.getInstance(getActivity())
                            .checkMPermission(new PermissionResult() {
                                @Override
                                public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                                    if(status == Permission.GRANTED) {
                                        getViewModel().onStartPlacesActivity();
                                        //getNavigator().navigateToPlaces();
                                    }
                                }
                            }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                return true;
            }
        });
        menu.add("Mock Locations").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                RxPermissions.getInstance(getActivity())
                        .checkMPermission(new PermissionResult() {
                            @Override
                            public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                                if(status == Permission.GRANTED) {
                                    getViewModel().onStartMockLocationActivity();
                                    //getNavigator().navigateToMockLocation();
                                }
                            }
                        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);//intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        logs.error("SETTING", "User enabled location");
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        logs.error("SETTING", "User Cancelled enabling location");
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        RxPermissions.getInstance(getActivity())
                .checkMPermission(new PermissionResult() {
                    @Override
                    public void onPermissionResult(Permission status, HashMap<Permission, List<String>> value) {
                        if(status == Permission.GRANTED) {
                            lastKnownLocationSubscription = lastKnownLocationObservable
                                    .map(new Func1<Location, String>() {
                                        @Override
                                        public String call(Location location) {
                                            if (location != null)
                                                return location.getLatitude() + " " + location.getLongitude() + " (" + location.getAccuracy() + ")";
                                            return "no location available";
                                        }
                                    })
                                    .subscribe(new Action1<String>() {
                                        @Override
                                        public void call(String s) {
                                            getViewModel().setLastKnownLocation(s);
                                        }
                                    }, new ErrorHandler());

                            updatableLocationSubscription = locationUpdatesObservable
                                    .map(new Func1<Location, String>() {
                                        @Override
                                        public String call(Location location) {
                                            if (location != null)
                                                return location.getLatitude() + " " + location.getLongitude() + " (" + location.getAccuracy() + ")";
                                            return "no location available";
                                        }
                                    })
                                    .map(new Func1<String, String>() {
                                        int count = 0;
                                        @Override
                                        public String call(String s) {
                                            return s + " " + count++;
                                        }
                                    })
                                    .subscribe(new Action1<String>() {
                                        @Override
                                        public void call(String s) {
                                            getViewModel().setUpdatableLocation(s);
                                        }
                                    }, new ErrorHandler());

                            addressSubscription = addressObservable.subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    getViewModel().setAddressLocation(s);
                                }
                            }, new ErrorHandler());

                            activitySubscription = activityObservable
                                    .map(new Func1<ActivityRecognitionResult, DetectedActivity>(){
                                        @Override
                                        public DetectedActivity call(ActivityRecognitionResult activityRecognitionResult) {
                                            return activityRecognitionResult.getMostProbableActivity();
                                        }
                                    })
                                    .map(new Func1<DetectedActivity, String>(){
                                        @Override
                                        public String call(DetectedActivity detectedActivity) {
                                            return getNameFromType(detectedActivity.getType()) + " with confidence " + detectedActivity.getConfidence();
                                        }

                                        private String getNameFromType(int activityType) {
                                            switch (activityType) {
                                                case DetectedActivity.RUNNING:
                                                    return "running";
                                                case DetectedActivity.IN_VEHICLE:
                                                    return "in_vehicle";
                                                case DetectedActivity.ON_BICYCLE:
                                                    return "on_bicycle";
                                                case DetectedActivity.ON_FOOT:
                                                    return "on_foot";
                                                case DetectedActivity.STILL:
                                                    return "still";
                                                case DetectedActivity.UNKNOWN:
                                                    return "unknown";
                                                case DetectedActivity.TILTING:
                                                    return "tilting";
                                            }
                                            return "unknown";
                                        }
                                    })
                                    .subscribe(new Action1<String>(){
                                        @Override
                                        public void call(String s) {
                                            getViewModel().setCurrentActivity(s);
                                        }
                                    }, new ErrorHandler());
                        }
                    }
                }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    @Override
    public void onStop() {
        super.onStop();
        updatableLocationSubscription.unsubscribe();
        addressSubscription.unsubscribe();
        lastKnownLocationSubscription.unsubscribe();
        activitySubscription.unsubscribe();
    }

    private class ErrorHandler implements Action1<Throwable> {
        @Override
        public void call(Throwable throwable) {
            Toast.makeText(getActivity(), "Error occurred.", Toast.LENGTH_SHORT).show();
            logs.error("MainActivity", "Error occurred" + throwable);
        }
    }
}
