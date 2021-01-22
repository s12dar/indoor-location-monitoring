package com.example.localizationserdar.localization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localizationserdar.databinding.LocalizationItemBinding;
import com.example.localizationserdar.datamodels.Beacon;

import java.util.List;

public class LocalizationAdapter extends RecyclerView.Adapter<LocalizationAdapter.LocalizationViewHolder> {

    private Context context;
    private List<Beacon> beaconList;
    LocalizationItemBinding binding;

    public LocalizationAdapter(Context context, List<Beacon> beaconList) {
        this.context = context;
        this.beaconList = beaconList;
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

    public class LocalizationViewHolder extends RecyclerView.ViewHolder {

        TextView tvBeaconName;

        public LocalizationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBeaconName = binding.tvTitleRv;
        }
    }
}