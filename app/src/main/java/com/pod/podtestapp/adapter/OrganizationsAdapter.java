package com.pod.podtestapp.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pod.podtestapp.R;

import java.util.ArrayList;

/**
 * Created by manuMohan on 15/05/11.
 */
public class OrganizationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Item> mItems;

    public OrganizationsAdapter(ArrayList<Item> items) {
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
            SpaceItem spaceItem = (SpaceItem) mItems.get(position);
            ((SpaceViewHolder) viewHolder).spaceNameTextView.setText(spaceItem.spaceName);
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    /**
     * @param parent
     * @return view for section item . organization text view
     */
    private View getSectionView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.view_section, parent, false);
    }

    /**
     *
     * @param parent
     * @return view for space item
     */
    private View getSpaceView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.view_space, parent, false);
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {
        final TextView sectionNameTextView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionNameTextView = (TextView) itemView.findViewById(R.id.section_name);
        }
    }

    public class SpaceViewHolder extends RecyclerView.ViewHolder {
        final TextView spaceNameTextView;

        public SpaceViewHolder(View itemView) {
            super(itemView);
            spaceNameTextView = (TextView) itemView.findViewById(R.id.space_name);
        }
    }

    /**
     * wrapper interface for recycler view items
     * Parcelable
     */
    public interface Item extends Parcelable {
    }

    /**
     * Section item wrapper - will display organization name
     */
    public static class SectionItem implements Item {
        final String sectionName;

        public SectionItem(String sectionName) {
            this.sectionName = sectionName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(sectionName);
        }

        public static final Parcelable.Creator<SectionItem> CREATOR
                = new Parcelable.Creator<SectionItem>() {
            public SectionItem createFromParcel(Parcel in) {
                return new SectionItem(in);
            }

            public SectionItem[] newArray(int size) {
                return new SectionItem[size];
            }
        };

        private SectionItem(Parcel in) {
            sectionName = in.readString();
        }
    }

    /**
     * Space item wrapper
     */
    public static class SpaceItem implements Item {
        final String spaceName;

        public SpaceItem(String spaceName) {
            this.spaceName = spaceName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(spaceName);
        }

        public static final Parcelable.Creator<SpaceItem> CREATOR
                = new Parcelable.Creator<SpaceItem>() {
            public SpaceItem createFromParcel(Parcel in) {
                return new SpaceItem(in);
            }

            public SpaceItem[] newArray(int size) {
                return new SpaceItem[size];
            }
        };

        private SpaceItem(Parcel in) {
            spaceName = in.readString();
        }
    }

    /**
     * function to update recycler view contents. call notifydatasetchanged after settings items
     *
     * @param items Items to be displayed on recycler view
     */
    public void setItems(ArrayList<Item> items) {
        this.mItems = items;
    }
}
