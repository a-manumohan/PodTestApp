package com.pod.podtestapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pod.podtestapp.R;
import com.pod.podtestapp.model.Space;

import java.util.ArrayList;

/**
 * Created by manuMohan on 15/05/11.
 */
public class OrganizationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Item> mItems;

    OrganizationsAdapter(ArrayList<Item> items) {
        mItems = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof SectionItem)
            return 0;
        else
            return 1;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0:
                return new SectionViewHolder(getSectionView(viewGroup));
            case 1:
                return new SpaceViewHolder(getSpaceView(viewGroup));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SectionViewHolder) {
            SectionItem sectionItem = (SectionItem) mItems.get(position);
            ((SectionViewHolder) viewHolder).sectionNameTextView.setText(sectionItem.sectionName);
        } else {
            Space space = ((SpaceItem) mItems.get(position)).space;
            ((SpaceViewHolder) viewHolder).spaceNameTextView.setText(space.getName());
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    private View getSectionView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.view_section, parent, false);
    }

    private View getSpaceView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.view_space, parent, false);
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView sectionNameTextView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionNameTextView = (TextView) itemView.findViewById(R.id.section_name);
        }
    }

    public class SpaceViewHolder extends RecyclerView.ViewHolder {
        TextView spaceNameTextView;

        public SpaceViewHolder(View itemView) {
            super(itemView);
            spaceNameTextView = (TextView) itemView.findViewById(R.id.space_name);
        }
    }

    /**
     * wrapper interface for recycler view items
     */
    public interface Item {
    }

    /**
     * Section item wrapper - will display organization name
     */
    public class SectionItem implements Item {
        String sectionName;
    }

    /**
     * Space item wrapper
     */
    public class SpaceItem implements Item {
        Space space;
    }

    /**
     * function to update recycler view contents. call notifydatasetchanged after settings items
     * @param items Items to be displayed on recycler view
     */
    public void setItems(ArrayList<Item> items) {
        this.mItems = mItems;
    }
}
