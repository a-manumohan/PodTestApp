package com.pod.podtestapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pod.podtestapp.PodApplication;
import com.pod.podtestapp.R;
import com.pod.podtestapp.network.PodServiceManager;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HomeFragment extends Fragment {
    @Inject
    protected PodServiceManager mPodServiceManager;
    private Subscription mOrganizationsSubscription;


    private OnFragmentInteractionListener mListener;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PodApplication)getActivity().getApplication()).getPodComponent().inject(this);
        fetchOrganizations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void fetchOrganizations() {
        mOrganizationsSubscription = mPodServiceManager.getOrganizations()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        organizations -> {
                            Log.e("Org",organizations.toString());
                        },
                        throwable -> {
                            Log.e("Error",throwable.toString());;
                        },
                        () -> {
                        }
                );
    }


    public interface OnFragmentInteractionListener {
    }

}
