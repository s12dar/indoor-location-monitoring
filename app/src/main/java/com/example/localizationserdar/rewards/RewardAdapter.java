package com.example.localizationserdar.rewards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localizationserdar.databinding.RewardContainerItemBinding;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {
    private RewardContainerItemBinding binding;
    private List<RewardItem> rewardItemList;
    private Context context;

    public RewardAdapter(Context context, List<RewardItem> rewardItemList) {
        this.rewardItemList = rewardItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = RewardContainerItemBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        holder.setRewardData(rewardItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return rewardItemList.size();
    }

    class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        ImageView ivReward;

        RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = binding.tvTitle;
            tvSubtitle = binding.tvSubtitle;
            ivReward = binding.ivReward1;
        }

        void setRewardData(RewardItem rewardItem) {
            tvTitle.setText(rewardItem.getTitle());
            tvSubtitle.setText(rewardItem.getDescription());
            ivReward.setImageResource(rewardItem.getImage());
        }
    }
}
