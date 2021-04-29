package com.example.localizationserdar.localization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localizationserdar.R;
import com.example.localizationserdar.databinding.LocalizationItemBinding;
import com.example.localizationserdar.datamodels.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.localizationserdar.utils.Constants.BEACON_IN_AI_LAB;
import static com.example.localizationserdar.utils.Constants.BEACON_IN_CANTINA;
import static com.example.localizationserdar.utils.Constants.BEACON_IN_LIBRARY;
import static com.example.localizationserdar.utils.Constants.BEACON_IN_TEACHERS_ROOM;

public class LocalizationAdapter extends RecyclerView.Adapter<LocalizationAdapter.LocalizationViewHolder> implements Filterable {

    private Context context;
    private List<Beacon> beaconList;
    private List<Beacon> allBeaconsList;
    private LocalizationItemBinding binding;
    private LocalizationListRecyclerClickListener mClickListener;


    public LocalizationAdapter(Context context, List<Beacon> beaconList, LocalizationListRecyclerClickListener clickListener) {
        this.context = context;
        this.beaconList = beaconList;
        this.allBeaconsList = new ArrayList<>(beaconList);
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public LocalizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //initialize view
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LocalizationItemBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new LocalizationViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalizationAdapter.LocalizationViewHolder holder, int position) {
        // Initialize the object
        Beacon beacon = beaconList.get(position);
        holder.tvBeaconName.setText(beacon.beaconName);
        holder.tvBeaconDesc.setText(beacon.beaconDesc);
        switch (beacon.beaconName) {
            case BEACON_IN_TEACHERS_ROOM:
                holder.ivBeacon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_meeting));
                break;
            case BEACON_IN_AI_LAB:
                holder.ivBeacon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_lab));
                break;
            case BEACON_IN_CANTINA:
                holder.ivBeacon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_cantina));
                break;
            case BEACON_IN_LIBRARY:
                holder.ivBeacon.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_lib));
                break;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        binding = null;
    }

    @Override
    public int getItemCount() {
        return beaconList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        //Run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Beacon> filterList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filterList.addAll(allBeaconsList);
            } else {
                for (Beacon beacon: allBeaconsList) {
                    if (beacon.beaconName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filterList.add(beacon);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        //Run on UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            beaconList.clear();
            beaconList.addAll((Collection<? extends Beacon>) results.values);
            notifyDataSetChanged();
        }
    };

    public class LocalizationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvBeaconName, tvBeaconDesc;
        ImageView ivBeacon;
        LocalizationListRecyclerClickListener localizationListRecyclerClickListener;

        public LocalizationViewHolder(@NonNull View itemView, LocalizationListRecyclerClickListener clickListener) {
            super(itemView);
            tvBeaconName = binding.tvTitleRv;
            tvBeaconDesc = binding.tvSubtitle;
            ivBeacon = binding.ivBeacon;
            localizationListRecyclerClickListener = clickListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            localizationListRecyclerClickListener.onUserClicked(getAdapterPosition());
        }
    }

    public interface LocalizationListRecyclerClickListener {
        void onUserClicked(int position);
    }
}