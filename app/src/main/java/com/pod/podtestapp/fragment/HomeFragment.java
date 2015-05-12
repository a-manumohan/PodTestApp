package com.pod.podtestapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pod.podtestapp.PodApplication;
import com.pod.podtestapp.R;
import com.pod.podtestapp.adapter.OrganizationsAdapter;
import com.pod.podtestapp.model.Organization;
import com.pod.podtestapp.model.Space;
import com.pod.podtestapp.network.PodServiceManager;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class HomeFragment extends Fragment {
    @Inject
    protected PodServiceManager mPodServiceManager;
    private Subscription mOrganizationsSubscription;


    private OnFragmentInteractionListener mListener;
    private RecyclerView mOrganizationsRecyclerView;
    private OrganizationsAdapter mOrganizationsAdapter;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PodApplication) getActivity().getApplication()).getPodComponent().inject(this);
        fetchOrganizations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
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

    private void initViews(View view) {
        mOrganizationsRecyclerView = (RecyclerView) view.findViewById(R.id.organizations);
        mOrganizationsAdapter = new OrganizationsAdapter(null);
        mOrganizationsRecyclerView.setAdapter(mOrganizationsAdapter);
        mOrganizationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void fetchOrganizations() {
        mOrganizationsSubscription = mPodServiceManager.getOrganizations()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::getOrganizationsAdapterItems)
                .subscribe(
                        organizations -> {
                            Log.e("Org", organizations.toString());
                            mOrganizationsAdapter.setItems(organizations);
                            mOrganizationsAdapter.notifyDataSetChanged();
                        },
                        throwable -> {
                            Log.e("Error", throwable.toString());

                        },
                        () -> {
                        }
                );
    }


    public interface OnFragmentInteractionListener {
    }

    private ArrayList<OrganizationsAdapter.Item> getOrganizationsAdapterItems(ArrayList<Organization> organizations) {
        if (organizations == null) return null;
        ArrayList<OrganizationsAdapter.Item> items = new ArrayList<>();
        for (Organization organization : organizations) {
            OrganizationsAdapter.SectionItem sectionItem = new OrganizationsAdapter.SectionItem(organization.getName());
            items.add(sectionItem);
            for (Space space : organization.getSpaces()) {
                OrganizationsAdapter.SpaceItem spaceItem = new OrganizationsAdapter.SpaceItem(space);
                items.add(spaceItem);
            }
        }
        return items;
    }
}
