package com.pod.podtestapp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pod.podtestapp.PodApplication;
import com.pod.podtestapp.R;
import com.pod.podtestapp.adapter.OrganizationsAdapter;
import com.pod.podtestapp.model.Organization;
import com.pod.podtestapp.model.Space;
import com.pod.podtestapp.network.PodServiceManager;
import com.pod.podtestapp.util.PreferenceUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HomeFragment extends Fragment {
    private static final String ARG_LOADING = "arg_loading";
    private static final String ARG_ITEMS = "arg_items";
    private boolean mIsLoading = false;

    private ArrayList<OrganizationsAdapter.Item> mItems;

    @Inject
    protected PodServiceManager mPodServiceManager;
    private Subscription mOrganizationsSubscription;


    private OnFragmentInteractionListener mListener;
    private OrganizationsAdapter mOrganizationsAdapter;

    private RecyclerView mOrganizationsRecyclerView;
    private ProgressBar mLoadingProgressBar;


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
        setHasOptionsMenu(true);
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
        if (savedInstanceState != null) {
            mIsLoading = savedInstanceState.getBoolean(ARG_LOADING, false);
            mItems = savedInstanceState.getParcelableArrayList(ARG_ITEMS);
        }
        initViews(view);
        if (mIsLoading || mItems == null) {
            fetchOrganizations();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOrganizationsSubscription != null)
            mOrganizationsSubscription.unsubscribe();
    }

    private void initViews(View view) {
        mOrganizationsRecyclerView = (RecyclerView) view.findViewById(R.id.organizations);
        mOrganizationsAdapter = new OrganizationsAdapter(mItems);
        mOrganizationsRecyclerView.setAdapter(mOrganizationsAdapter);
        mOrganizationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_progress);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                fetchOrganizations();
                return true;
        }
        return false;
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
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mOrganizationsRecyclerView.setVisibility(View.INVISIBLE);
        mIsLoading = true;
        mOrganizationsSubscription = mPodServiceManager.getOrganizations()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::getOrganizationsAdapterItems)
                .subscribe(
                        organizations -> {
                            mLoadingProgressBar.setVisibility(View.GONE);
                            mOrganizationsRecyclerView.setVisibility(View.VISIBLE);
                            mItems = organizations;
                            mOrganizationsAdapter.setItems(organizations);
                            mOrganizationsAdapter.notifyDataSetChanged();
                        },
                        throwable -> {
                            mIsLoading = false;
                            mLoadingProgressBar.setVisibility(View.GONE);
                            Log.e("Error", throwable.toString());
                            Response response = ((RetrofitError) throwable).getResponse();
                            // clear all credentials and show login screen if 401 (after one refresh)
                            if (response != null) {
                                if (response.getStatus() == 401) {
                                    PreferenceUtil.Session.setAccessToken(getActivity(), "");
                                    PreferenceUtil.Session.setRefreshToken(getActivity(), "");
                                    mListener.showLoginScreen();
                                } else {
                                    showGenericErrorMessage();
                                }
                            } else {
                                showNetworkErrorMessage(); //if response is null possibly network problem.
                            }
                        },
                        () -> mIsLoading = false
                );
    }

    private void showNetworkErrorMessage() {
        Toast.makeText(getActivity(), getString(R.string.message_error_network), Toast.LENGTH_SHORT).show();
    }

    private void showGenericErrorMessage() {
        Toast.makeText(getActivity(), getString(R.string.message_error_generic), Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void showLoginScreen();
    }

    private ArrayList<OrganizationsAdapter.Item> getOrganizationsAdapterItems(ArrayList<Organization> organizations) {
        if (organizations == null) return null;
        ArrayList<OrganizationsAdapter.Item> items = new ArrayList<>();
        for (Organization organization : organizations) {
            OrganizationsAdapter.SectionItem sectionItem = new OrganizationsAdapter.SectionItem(organization.getName());
            items.add(sectionItem);
            for (Space space : organization.getSpaces()) {
                OrganizationsAdapter.SpaceItem spaceItem = new OrganizationsAdapter.SpaceItem(space.getName());
                items.add(spaceItem);
            }
        }
        return items;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_LOADING, mIsLoading);
        outState.putParcelableArrayList(ARG_ITEMS, mItems);
    }
}
