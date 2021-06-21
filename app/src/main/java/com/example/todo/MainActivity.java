package com.example.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todo.adapter.ToDoAdapter;
import com.example.todo.database.DatabaseHandler;
import com.example.todo.model.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener, ToDoAdapter.OnNoteListener {

    private RecyclerView recyclerView;
    private ToDoAdapter tasksAdapter;
    private List<ToDoModel> taskList;
    private DatabaseHandler db;
    private FloatingActionButton addFAB;
    private EditText searchText;
    private ImageView searchImage,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=new DatabaseHandler(this);
        db.openDataBase();

        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter=new ToDoAdapter(db,this,this);
        recyclerView.setAdapter(tasksAdapter);

        addFAB=findViewById(R.id.addFAB);
        searchText=findViewById(R.id.searchText);
        searchImage=findViewById(R.id.searchImage);
        back=findViewById(R.id.back);

        back.setVisibility(View.INVISIBLE);


        taskList=db.getAllTasks();
        tasksAdapter.setTask(taskList);

        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Edit_Task.newInstance().show(getSupportFragmentManager(),Add_Edit_Task.TAG);
            }
        });

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                back.setVisibility(View.VISIBLE);
                String search= searchText.getText().toString().trim();
                if(!search.equals("")){
                    taskList=db.getSearchList(search);
                }
                else
                    taskList=db.getAllTasks();

                tasksAdapter.setTask(taskList);
                tasksAdapter.notifyDataSetChanged();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskList=db.getAllTasks();
                tasksAdapter.setTask(taskList);
                tasksAdapter.notifyDataSetChanged();
                back.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList=db.getAllTasks();
        tasksAdapter.setTask(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClick(int position) {
        tasksAdapter.editItem(position);
        System.out.println("CLICKED" + position);
    }

    @Override
    public void onDeleteClick(int position) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(tasksAdapter.getContext());
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this Task?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tasksAdapter.deleteItem(position);
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tasksAdapter.notifyItemChanged(position);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}