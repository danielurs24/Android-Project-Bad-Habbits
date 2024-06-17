package com.unibuc.badhabbits;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private List<Habit> habitList;
    private HabitAdapter habitAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        DateTimeFormatter dtf = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }
        LocalDateTime now = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }

        auth = FirebaseAuth.getInstance();
        Button logoutButton = view.findViewById(R.id.buttonLogout);
        Button addHabitButton = view.findViewById(R.id.buttonAddHabit);

        RecyclerView recyclerViewHabits = view.findViewById(R.id.recyclerViewHabits);
        TextView currentDate = view.findViewById(R.id.mainTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate.setText("Today, " + dtf.format(now));
        }

        loadHabits();
        habitAdapter = new HabitAdapter(habitList, this);

        recyclerViewHabits.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewHabits.setAdapter(habitAdapter);

        addHabitButton.setOnClickListener(v -> showAddHabitDialog());

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        return view;
    }

    private void showAddHabitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add a new habit");

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_habit, null, false);
        final EditText input = viewInflated.findViewById(R.id.editTextHabitName);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String habitName = input.getText().toString();
            if (!habitName.isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    habitList.add(new Habit(habitName, LocalDate.now()));
                }
                habitAdapter.notifyItemInserted(habitList.size() - 1);
                saveHabits();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    void showEditHabitDialog(int position) {
        Habit habitToEdit = habitList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Habit");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(habitToEdit.getName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newHabitName = input.getText().toString();
            habitToEdit.setName(newHabitName);
            habitAdapter.notifyItemChanged(position);
            saveHabits();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveHabits() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("habits", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("habitCount", habitList.size());

        for (int i = 0; i < habitList.size(); i++) {
            Habit habit = habitList.get(i);
            editor.putString("habitName_" + i, habit.getName());
            editor.putString("habitStartDate_" + i, habit.getStartDate().toString());
        }
        editor.apply();
    }

    private void loadHabits() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("habits", getContext().MODE_PRIVATE);

        int habitCount = sharedPreferences.getInt("habitCount", 0);
        habitList = new ArrayList<>();

        for (int i = 0; i < habitCount; i++) {
            String habitName = sharedPreferences.getString("habitName_" + i, null);
            String habitStartDateStr = sharedPreferences.getString("habitStartDate_" + i, null);

            if (habitName != null && habitStartDateStr != null) {
                LocalDate habitStartDate = LocalDate.parse(habitStartDateStr);
                habitList.add(new Habit(habitName, habitStartDate));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveHabits();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveHabits();
    }
}
