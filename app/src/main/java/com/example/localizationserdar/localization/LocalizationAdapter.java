package com.example.localizationserdar.localization;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localizationserdar.databinding.LocalizationItemBinding;
import com.example.localizationserdar.datamodels.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LocalizationAdapter extends RecyclerView.Adapter<LocalizationAdapter.LocalizationViewHolder> implements Filterable {

    private Context context;
    private List<Beacon> beaconList;
    private List<Beacon> allBeaconsList;
    LocalizationItemBinding binding;


    public LocalizationAdapter(Context context, List<Beacon> beaconList) {
        this.context = context;
        this.beaconList = beaconList;
        this.allBeaconsList = new ArrayList<>(beaconList);
    }

    @NonNull
    @Override
    public LocalizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //initialize view
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LocalizationItemBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new LocalizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalizationAdapter.LocalizationViewHolder holder, int position) {
        // Initialize the object
        Beacon beacon = beaconList.get(position);
        holder.tvBeaconName.setText(beacon.beaconName);
        holder.tvBeaconDesc.setText(beacon.beaconDesc);
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

    public class LocalizationViewHolder extends RecyclerView.ViewHolder {

        TextView tvBeaconName, tvBeaconDesc;

        public LocalizationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBeaconName = binding.tvTitleRv;
            tvBeaconDesc = binding.tvSubtitle;

            itemView.setOnClickListener(v ->  {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    Beacon clickedBeacon = allBeaconsList.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Do you want to navigate to: ");
                    builder.setMessage(clickedBeacon.beaconName+"?");

                    builder.setNegativeButton("NO", ((dialog, which) -> {

                    }));

                    builder.setPositiveButton("YES", ((dialog, which) -> {

                    }));

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }
}