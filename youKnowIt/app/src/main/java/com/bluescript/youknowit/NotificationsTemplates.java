package com.bluescript.youknowit;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import java.util.ArrayList;
import java.util.Random;

public class NotificationsTemplates {
    static void QuestionNotificationTemplate(Context context, String id) {
        QuestionSet set = MainActivity.readFromJSON(id, context);
        ArrayList<Question> questions = set.getQuestions();

        Random rand = new Random();
        int r = rand.nextInt(questions.size());

        Question singleQuestion = questions.get(r);
        String question = singleQuestion.getQuestion();
        String correctAnswer = singleQuestion.getAnswer();
        String setName = set.getSetName();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "QUESTION_NOTIFICATION_CHANNEL");
        mBuilder
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setContentTitle(setName)
                .setContentText(question)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(question))
                .setPriority(NotificationCompat.PRIORITY_MAX);

        String replyLabel = context.getString(R.string.reply);
        RemoteInput remoteInput = new RemoteInput.Builder("TEXT_REPLY")
                .setLabel(replyLabel)
                .build();

        Intent intent1 = new Intent(context, ManageUserReply.class);
        intent1.putExtra("Correct", correctAnswer);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context,
                11, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notifications_24,
                context.getString(R.string.reply), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        mBuilder.addAction(action);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(11, mBuilder.build());

    }

    static void answerNotification(Context context, String title, String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "ANSWER_NOTIFICATION_CHANNEL")
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(11, mBuilder.build());
    }
}
