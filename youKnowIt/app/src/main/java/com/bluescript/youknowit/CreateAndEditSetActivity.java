package com.bluescript.youknowit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class CreateAndEditSetActivity extends AppCompatActivity {
    private ViewGroup parent;
    private Context context;
    private boolean editIsOn = false;
    private int editNotificationsInterval = 0;
    UUID uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_create_and_edit_set);
        parent = findViewById(R.id.AddRepeatsLinear);

        Intent intent = getIntent();
        String editUuid = intent.getStringExtra("uuid");
        this.context = getApplicationContext();

        TextInputLayout name = findViewById(R.id.projectname);
        Switch notifiSwitch = findViewById(R.id.notificationSetSwitch);
        EditText intervalText = findViewById(R.id.notificationSetInterval);

        UUID uuid;
        if(editUuid == null){
            uuid = UUID.randomUUID();
        }else{
            uuid = UUID.fromString(editUuid);
            QuestionSet qs = MainActivity.readFromJSON("questionSets/" + uuid.toString() + ".json", context);
            ArrayList<Question> list = qs.getQuestions();
            final LayoutInflater inflater = LayoutInflater.from(this);
            ViewGroup parent = findViewById(R.id.AddRepeatsLinear);
            for(int i = 0;  i < list.size(); i++){
                View v = inflater.inflate(R.layout.single_question_and_answer_tile, parent, false);
                TextInputLayout questionEdit = v.findViewById(R.id.question_place);
                TextInputLayout answerEdit = v.findViewById(R.id.answer_place);

                Question question = list.get(i);
                questionEdit.getEditText().setText(question.getQuestion());
                answerEdit.getEditText().setText(question.getAnswer());

                parent.addView(v);
            }

            name.getEditText().setText(qs.getSetName());
            notifiSwitch.setChecked(qs.isOn());
            intervalText.setText(String.valueOf(qs.getTimeInterval()));

            editIsOn = qs.isOn();
            editNotificationsInterval = qs.getTimeInterval();
        }

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);
        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemID = item.getItemId();

                if(itemID == R.id.action_delete_set){

                    File file = new File(context.getFilesDir().getAbsolutePath().toString() + "/questionSets/" + uuid.toString() + ".json");
                    Log.e("tag", file.getAbsolutePath().toString());
                    boolean deleted = file.delete();
                    finish();

                }else if(itemID == R.id.action_save_set){

                    LinearLayout scroll = findViewById(R.id.AddRepeatsLinear);
                    int childCount = scroll.getChildCount();

                    ArrayList list = new ArrayList<Question>();

                    for(int i = 0; i < childCount; i++){
                        TextInputLayout questionPlace = scroll.getChildAt(i).findViewById(R.id.question_place);
                        String question = questionPlace.getEditText().getText().toString();
                        TextInputLayout answerPlace = scroll.getChildAt(i).findViewById(R.id.answer_place);
                        String answer = answerPlace.getEditText().getText().toString();
                        if(question == null || answer == null){

                        }else{
                            Question q1 = new Question(question, answer);
                            list.add(q1);
                        }


                    }

                    String intervalString = intervalText.getText().toString();
                    int interval = 0;
                    if(!intervalString.equals("")) {
                        interval = Integer.parseInt(intervalText.getText().toString());
                    }
                    if(notifiSwitch.isChecked() && !editIsOn) {
                        if(interval > 0)  {
                            ManageNotifications.startNotificationTask(getApplicationContext(), "questionSets/" + uuid.toString() + ".json", interval);
                        }
                    } else if(notifiSwitch.isChecked() && editIsOn && interval > 0 && interval != editNotificationsInterval) {
                        ManageNotifications.stopNotificationTask("questionSets/" + uuid.toString() + ".json");
                        ManageNotifications.startNotificationTask(getApplicationContext(), "questionSets/" + uuid.toString() + ".json", interval);
                    } else if(!notifiSwitch.isChecked() && editIsOn) {
                        ManageNotifications.stopNotificationTask("questionSets/" + uuid.toString() + ".json");
                    }

                    int intervalToSave;
                    if(interval == 0) {
                        intervalToSave = 20;
                    }
                    else {
                        intervalToSave = interval;
                    }

                    QuestionSet set =  new QuestionSet(uuid, name.getEditText().getText().toString(), list, intervalToSave, notifiSwitch.isChecked());
                    File folder = new File(context.getFilesDir().getAbsolutePath() + "/questionSets");
                    if(list.size() != 0){
                        MainActivity.writeToJSON("questionSets/" + uuid.toString() + ".json", set, getApplicationContext());
                    }
                }
                finish();
                return false;
            }
        });
        FloatingActionButton fab = findViewById(R.id.AddQuestionFAB);
        final LayoutInflater inflater = LayoutInflater.from(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ViewGroup parent = findViewById(R.id.AddRepeatsLinear);
                inflater.inflate(R.layout.single_question_and_answer_tile, parent);

            }
        });
    }

    public void deleteButton(View v){
        if(parent.getChildCount() > 1){
            View view = (View)v.getParent();
            int indexOfChildOfParent = parent.indexOfChild(view);
            parent.removeViewAt(indexOfChildOfParent);
        }
    }
}