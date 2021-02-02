package com.example.localizationserdar.onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localizationserdar.databinding.WelcomeContainerItemBinding;

import java.util.List;

public class WelcomeAdapter extends RecyclerView.Adapter<WelcomeAdapter.WelcomeViewHolder> {
    private WelcomeContainerItemBinding binding;
    private List<WelcomeItem> welcomeItemList;
    private Context context;

    public WelcomeAdapter(Context context, List<WelcomeItem> welcomeItemList) {
        this.welcomeItemList = welcomeItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public WelcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = WelcomeContainerItemBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new WelcomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WelcomeAdapter.WelcomeViewHolder holder, int position) {
        holder.setWelcomeData(welcomeItemList.get(position));

    }

    @Override
    public int getItemCount() {
        return welcomeItemList.size();
    }

    class WelcomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        ImageView ivReward;

        WelcomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = binding.tvTitle;
            tvSubtitle = binding.tvSubtitle;
            ivReward = binding.ivReward1;
        }

        void setWelcomeData(WelcomeItem rewardItem) {
            tvTitle.setText(rewardItem.getTitle());
            tvSubtitle.setText(rewardItem.getDescription());
            ivReward.setImageResource(rewardItem.getImage());
        }
    }
}
