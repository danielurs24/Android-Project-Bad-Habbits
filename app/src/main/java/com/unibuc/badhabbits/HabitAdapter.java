package com.unibuc.badhabbits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private List<Habit> habitList;
    private HomeActivity homeActivity;

    public HabitAdapter(List<Habit> habitList, HomeActivity homeActivity) {
        this.habitList = habitList;
        this.homeActivity = homeActivity;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.textViewHabit.setText(habit.getName());
        holder.textViewDaysCount.setText("Days: " + habit.getDaysCount());
        holder.editButton.setOnClickListener(view -> homeActivity.showEditHabitDialog(position));

        holder.deleteButton.setOnClickListener(view -> {
            habitList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, habitList.size());
        });
    }


    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView textViewHabit, textViewDaysCount;
        Button editButton, deleteButton;;


        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHabit = itemView.findViewById(R.id.textViewHabit);
            textViewDaysCount = itemView.findViewById(R.id.textViewDaysCount);
            editButton = itemView.findViewById(R.id.buttonEditHabit);
            deleteButton = itemView.findViewById(R.id.buttonDeleteHabit);
        }
    }
}
