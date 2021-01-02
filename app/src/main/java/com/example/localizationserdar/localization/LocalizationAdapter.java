package com.example.localizationserdar.localization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localizationserdar.R;

public class LocalizationAdapter extends RecyclerView.Adapter<LocalizationAdapter.LocalizationViewHolder> {

    private Context context;

    public LocalizationAdapter(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @NonNull
    @Override
    public LocalizationAdapter.LocalizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //initialize view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.localization_item, parent, false);
        return new LocalizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalizationAdapter.LocalizationViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class LocalizationViewHolder extends RecyclerView.ViewHolder {

        public LocalizationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}