package br.facens.aula.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTask;
    private Spinner spinnerPriority;
    private Button buttonAddTask;
    private LinearLayout linearLayoutTasks;

    private List<String> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        linearLayoutTasks = findViewById(R.id.linearLayoutTasks);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        spinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String priority = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Priority selected: " + priority, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = editTextTask.getText().toString().trim();
                if (!task.isEmpty()) {
                    tasks.add(task);
                    addTaskToView(task);
                    editTextTask.setText("");
                    showTaskAddedDialog(task); // Mostrar o AlertDialog
                } else {
                    Toast.makeText(getApplicationContext(), "Digite uma tarefa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addTaskToView(String task) {
        TextView textView = new TextView(this);
        textView.setText(task);
        linearLayoutTasks.addView(textView);
    }

    private void showTaskAddedDialog(String task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tarefa adicionada: " + task)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
