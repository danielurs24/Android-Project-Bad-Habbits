package com.unibuc.badhabbits;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RewardsFragment extends Fragment {

    private List<Reward> rewardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        rewardList = new ArrayList<>();
        rewardList.add(new Reward("O intrare gratuita in sectiunea The Palm pentru un Adult la Therme Bucuresti", 7));
        rewardList.add(new Reward("O vacanta de vis in Atena", 30));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRewards);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RewardAdapter rewardAdapter = new RewardAdapter(rewardList);
        recyclerView.setAdapter(rewardAdapter);

        return view;
    }

    private class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {
        private List<Reward> rewardList;

        public RewardAdapter(List<Reward> rewardList) {
            this.rewardList = rewardList;
        }

        @NonNull
        @Override
        public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reward_item, parent, false);
            return new RewardViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
            Reward reward = rewardList.get(position);
            holder.rewardName.setText(reward.getName());
            holder.requiredDays.setText("Days required: " + reward.getRequiredDays());
            holder.collectButton.setEnabled(!reward.isCollected());

            holder.collectButton.setOnClickListener(v -> {
                if (!reward.isCollected()) {
                    if (canCollectReward(reward.getRequiredDays())) {
                        reward.setCollected(true);
                        holder.collectButton.setEnabled(false);
                        Toast.makeText(getContext(), "Reward collected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Not enough days to collect this reward.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return rewardList.size();
        }

        public class RewardViewHolder extends RecyclerView.ViewHolder {
            public TextView rewardName;
            public TextView requiredDays;
            public Button collectButton;

            public RewardViewHolder(@NonNull View itemView) {
                super(itemView);
                rewardName = itemView.findViewById(R.id.textViewRewardName);
                requiredDays = itemView.findViewById(R.id.textViewRequiredDays);
                collectButton = itemView.findViewById(R.id.buttonCollect);
            }
        }
    }

    private boolean canCollectReward(int requiredDays) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("HabitsPrefs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.now();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().endsWith("_startDate")) {
                String startDateString = (String) entry.getValue();
                LocalDate startDate = LocalDate.parse(startDateString, formatter);
                int daysBetween = (int) ChronoUnit.DAYS.between(startDate, currentDate);

                if (daysBetween >= requiredDays) {
                    return true;
                }
            }
        }
        return false;
    }
}
