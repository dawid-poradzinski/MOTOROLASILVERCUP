package com.bluescript.youknowit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

public class ManageUserReply extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Check user answer
        String userAnswer = getMessageText(intent).toString();
        String correct = intent.getStringExtra("Correct");

        if(userAnswer.equals(correct)) {
            NotificationsTemplates.answerNotification(context, context.getString(R.string.correct_answer), context.getString(R.string.good_job));
        } else {
            NotificationsTemplates.answerNotification(context, context.getString(R.string.not_correct_answer), context.getString(R.string.correct_answer_is) + " " + correct);
        }
    }

    private CharSequence getMessageText(Intent intent)
    {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if(remoteInput != null)
        {
            return remoteInput.getCharSequence("TEXT_REPLY");
        }
        return null;
    }
}
