package com.unibuc.badhabbits;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private List<Habit> habitList;
    private HabitAdapter habitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }
        LocalDateTime now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = LocalDateTime.now();
        }

        auth = FirebaseAuth.getInstance();
        Button logoutButton = findViewById(R.id.buttonLogout);
        Button addHabitButton = findViewById(R.id.buttonAddHabit);

        RecyclerView recyclerViewHabits = findViewById(R.id.recyclerViewHabits);
        TextView currentDate = findViewById(R.id.mainTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate.setText("Today, " + dtf.format(now));
        }

        loadHabits();
        habitAdapter = new HabitAdapter(habitList,this );


        recyclerViewHabits.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHabits.setAdapter(habitAdapter);

        addHabitButton.setOnClickListener(view -> showAddHabitDialog());

        logoutButton.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showAddHabitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new habit");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_habit, null, false);
        final EditText input = viewInflated.findViewById(R.id.editTextHabitName);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String habitName = input.getText().toString();
                if (!habitName.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        habitList.add(new Habit(habitName, LocalDate.now()));
                    }
                    habitAdapter.notifyItemInserted(habitList.size() - 1);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        saveHabits();
    }

    void showEditHabitDialog(int position) {
        Habit habitToEdit = habitList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Habit");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(habitToEdit.getName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newHabitName = input.getText().toString();
            habitToEdit.setName(newHabitName);
            habitAdapter.notifyItemChanged(position);
            saveHabits(); // Salvează modificările în SharedPreferences
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveHabits() {
        SharedPreferences sharedPreferences = getSharedPreferences("habits", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("habitCount", habitList.size()); // Salvăm numărul total de obiecte

        for (int i = 0; i < habitList.size(); i++) {
            Habit habit = habitList.get(i);
            editor.putString("habitName_" + i, habit.getName());
            editor.putString("habitStartDate_" + i, habit.getStartDate().toString());
        }
        editor.apply();
    }




    private void loadHabits() {
        SharedPreferences sharedPreferences = getSharedPreferences("habits", MODE_PRIVATE);

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
    protected void onPause() {
        super.onPause();
        saveHabits();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveHabits();
    }


}
