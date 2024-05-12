// Leonardo Daniel lima de veras, RA: 212143

package com.example.ac2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ac2.R;
import com.example.ac2.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText editTextTaskTitle;
    private EditText editTextTaskDescription;
    private EditText editTextTaskValue;
    private EditText editTextTaskDate;
    private Spinner spinnerPriority;
    private Button buttonAddTask;
    private Button buttonSendTasks;
    private RecyclerView recyclerViewTasks;

    private Button buttonFoto;
    private TaskAdapter taskAdapter;

    private ImageView imageView;

    private List<Task> tasks = new ArrayList<>();

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);
        editTextTaskValue = findViewById(R.id.editTextTaskValue);
        editTextTaskDate = findViewById(R.id.editTextTaskDate);
        spinnerPriority = findViewById(R.id.spinnerPriority);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        buttonSendTasks = findViewById(R.id.buttonSendTasks);
        imageView = findViewById(R.id.imageView);
        buttonFoto = findViewById(R.id.foto);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("tasks");


        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(tasks);
        recyclerViewTasks.setAdapter(taskAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String taskTitle = editTextTaskTitle.getText().toString().trim();
                String taskDescription = editTextTaskDescription.getText().toString().trim();
                String taskValue = editTextTaskValue.getText().toString().trim();
                String taskDate = editTextTaskDate.getText().toString().trim();
                String taskPriority = spinnerPriority.getSelectedItem().toString();

                if (!taskTitle.isEmpty()) {
                    Task task = new Task();
                    task.setTitle(taskTitle);
                    task.setDescription(taskDescription);
                    task.setValue(taskValue);
                    task.setDate(taskDate);
                    task.setPriority(taskPriority);

                    tasks.add(task);
                    taskAdapter.notifyDataSetChanged();

                    String taskId = databaseReference.push().getKey();
                    databaseReference.child(taskId).setValue(task);

                    // Limpeza dos campos de entrada de dados
                    editTextTaskTitle.setText("");
                    editTextTaskDescription.setText("");
                    editTextTaskDate.setText("");
                    editTextTaskValue.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Digite um título para a tarefa", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSendTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTasksByEmail();
            }
        });

        buttonFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tirarFoto(view);
            }
        });

        buttonSendTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTasksByEmail();
            }
        });
    }
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tasks.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Task task = postSnapshot.getValue(Task.class);
                    tasks.add(task);
                }
                taskAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    ActivityResultLauncher<Intent> fotografarLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
            });

    public void tirarFoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            fotografarLauncher.launch(takePictureIntent);
        }
    }

    /***************************************************************************************/

    private void sendTasksByEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Lista de Tarefas");
        emailIntent.putExtra(Intent.EXTRA_TEXT, generateTasksText());
        startActivity(Intent.createChooser(emailIntent, "Enviar Lista de Tarefas"));
    }

    private String generateTasksText() {
        StringBuilder sb = new StringBuilder();
        for (Task task : tasks) {
            sb.append("Título: ").append(task.getTitle()).append("\n");
            sb.append("Descrição: ").append(task.getDescription()).append("\n");
            sb.append("Prioridade: ").append(task.getPriority()).append("\n");
            sb.append("Valor: ").append(task.getValue()).append("\n");
            sb.append("Data: ").append(task.getDate()).append("\n");
            sb.append("Concluída: ").append(task.isCompleted() ? "Sim" : "Não").append("\n\n");
        }
        return sb.toString();
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

        private List<Task> tasks;
        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }
        @NonNull
        @Override

        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
            return new TaskViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.bind(task);

        }
        @Override
        public int getItemCount() {
            return tasks.size();
        }
        public class TaskViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewTitle;
            private TextView textViewDescription;
            private TextView textViewValue;
            private TextView textViewDate;
            private TextView textViewPriority;
            private CheckBox checkBoxCompleted;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                // Inicializa os elementos de interface do item de tarefa
                textViewTitle = itemView.findViewById(R.id.textViewTitle);
                textViewDescription = itemView.findViewById(R.id.textViewDescription);
                textViewValue = itemView.findViewById(R.id.textViewValue);
                textViewDate = itemView.findViewById(R.id.textViewDate);
                textViewPriority = itemView.findViewById(R.id.textViewPriority);
                checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            }

            public void bind(Task task) {
                textViewTitle.setText("Título: " + task.getTitle());
                textViewDescription.setText("Descrição: " + task.getDescription());
                textViewValue.setText("Valor: " + task.getValue());
                textViewDate.setText("Data: "   + task.getDate());
                textViewPriority.setText("Prioridade: " + task.getPriority());
                checkBoxCompleted.setChecked(task.isCompleted());
            }
        }
    }
}
