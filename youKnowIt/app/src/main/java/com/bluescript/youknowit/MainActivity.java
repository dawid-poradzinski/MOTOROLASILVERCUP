package com.bluescript.youknowit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.bluescript.youknowit.Question;
import android.view.View;
import android.view.Window;

import com.bluescript.youknowit.api.MyUrlRequestCallback;
import com.bluescript.youknowit.api.RequestRunner;
import com.bluescript.youknowit.utils.PathInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public Context context;

    public static Intent serviceIntent = null;
    NotificationsService notificationsService;

    //Helper function for writeToJSON
    static public void writeToFile(String text, String fileName, Context context){

        try {
            FileWriter out = new FileWriter(new File(context.getFilesDir(), fileName));
            out.write(text);
            out.close();
        } catch(IOException e ){
            Log.e("ERROR", e.toString());
        }

    }

    //Function to read from JSON file
    static public QuestionSet readFromJSON(String fileName, Context context) {
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

        String JSONtext = readFile(fileName, context);
        QuestionSet questionSet = new QuestionSet(JSONtext);
        return questionSet;
    }

    static public void writeToJSON(String fileName, QuestionSet data, Context context) {
        JSONObject JSONData = data.toJSON();
        MainActivity.writeToFile(JSONData.toString(), fileName, context);
    }

    //Helper function for readFromJson
    static public String readFile(String filename, Context context){

        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(context.getFilesDir() + PathInfo.PATH_TO_SETS, filename)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);
        } catch (IOException e ){
            Log.e("ERROR", e.toString());
        }
        String output = stringBuilder.toString();
        return output;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();

        RequestRunner.sendRequest(context, "GET", "");



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        FloatingActionButton fab = findViewById(R.id.addingActivities);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemID = v.getId();
                if(itemID == R.id.michal){
//                    todo
//                    start activity
                }else{
                    startActivity(new Intent(v.getContext(), CreateAndEditSetActivity.class));
                }

            }
        });

        ManageNotifications.createAndBindService(this);

    }


    protected void onResume(){
        super.onResume();

        File folder = new File(context.getFilesDir().getAbsolutePath() + "/questionSets");
        if(!folder.exists()){
            folder.mkdir();
        }
        String path = context.getFilesDir().toString();
        File dic = new File(path + PathInfo.PATH_TO_SETS);
        File[] listOfFiles = dic.listFiles();
        final LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup parent = findViewById(R.id.scroll_in_main);

        parent.removeAllViews();
        if(listOfFiles.length > 0) {


            for (int i = 0; i < listOfFiles.length; i++) {
                QuestionSet questionSet = MainActivity.readFromJSON(listOfFiles[i].toString(), context);


                View singleTileSet = inflater.inflate(R.layout.tile_set, parent, false);
                ImageButton dots = singleTileSet.findViewById(R.id.imageButton);
                dots.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout parent = (RelativeLayout) v.getParent();
                        TextView uuidView = parent.findViewById(R.id.hidden_uuid);
                        String uuid = uuidView.getText().toString();

                        Intent intent = new Intent(getApplicationContext(), CreateAndEditSetActivity.class);
                        intent.putExtra("uuid", uuid);
                        startActivity(intent);

                    }
                });
                TextView setName = singleTileSet.findViewById(R.id.projectname);
                setName.setText(questionSet.getSetName());
                setName = singleTileSet.findViewById(R.id.hidden_uuid);
                setName.setText(questionSet.getId().toString());
                parent.addView(singleTileSet);
            }
        }

    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.foreground_channel_name);
            String description = getString(R.string.foreground_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("FOREGROUND_SERVICE_CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            CharSequence name2 = getString(R.string.foreground_channel_name);
            String description2 = getString(R.string.foreground_channel_description);
            int importance2 = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel2 = new NotificationChannel("QUESTION_NOTIFICATION_CHANNEL", name2, importance2);
            channel2.setDescription(description2);

            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel2);

            CharSequence name3 = getString(R.string.foreground_channel_name);
            String description3 = getString(R.string.foreground_channel_description);
            int importance3 = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel3 = new NotificationChannel("ANSWER_NOTIFICATION_CHANNEL", name3, importance3);
            channel3.setDescription(description3);

            NotificationManager notificationManager3 = getSystemService(NotificationManager.class);
            notificationManager3.createNotificationChannel(channel3);
        }
    }


}