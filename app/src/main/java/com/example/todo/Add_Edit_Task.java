package com.example.todo;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.todo.database.DatabaseHandler;
import com.example.todo.model.ToDoModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class Add_Edit_Task extends BottomSheetDialogFragment {
   public static final String TAG="BottomActionDialog";

    private EditText taskText;
    private Button saveButton;
    private DatabaseHandler db;

    public static Add_Edit_Task newInstance(){
        return new Add_Edit_Task();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,R.style.Dialog);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.new_task,container,false);
       getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
       return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskText=getView().findViewById(R.id.newTaskText);
        saveButton=getView().findViewById(R.id.newTaskButton);
        saveButton.setEnabled(false);
        db=new DatabaseHandler(getActivity());
        db.openDataBase();

        boolean isEdit=false;
        final Bundle bundle=getArguments();

        if(bundle!=null){
            isEdit=true;
            String task = bundle.getString("task");
            taskText.setText(task);
            if(task.length()>0){
                saveButton.setEnabled(true);
                saveButton.setTextColor(Color.BLUE);
            }
        }
        taskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().equals("")){
                        saveButton.setEnabled(false);
                        saveButton.setTextColor(Color.GRAY);
                    }
                    else{
                        saveButton.setEnabled(true);
                        saveButton.setTextColor(Color.parseColor("#70F1AE"));
                    }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        final boolean finalIsEdit=isEdit;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=taskText.getText().toString();
                if(finalIsEdit){
                    db.updateTask(bundle.getInt("id"),text );
                }
                else
                {
                    ToDoModel task= new ToDoModel();
                    task.setTask(text);
                    db.insertTask(task);
                }
                dismiss();

            }
        });
    }
    @Override
    public void onDismiss(DialogInterface dialog){
        Activity activity=getActivity();
        if(activity instanceof DialogCloseListener){
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }

}
