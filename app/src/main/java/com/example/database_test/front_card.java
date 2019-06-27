package com.example.database_test;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class front_card extends AppCompatActivity{

    public static final String EXTRA_SUBJECT = "com.example.database_test.QuestionActivity";
    private StudyDatabase mStudyDb;
    private String mSubject;
    private TextView mAnswerLabel;
    private TextView mAnswerText;
    private TextView mQuestionText;
    private List<Question> mQuestionList;
    private int mCurrentQuestionIndex;
    private final int REQUEST_CODE_NEW_QUESTION = 0;
    private final int REQUEST_CODE_UPDATE_QUESTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.card_front);

        // Hosting activity provides the subject of the questions to display
        mSubject = intent.getStringExtra(EXTRA_SUBJECT);

        // Load all questions for this subject
        mStudyDb = StudyDatabase.getInstance(getApplicationContext());
        mQuestionList = mStudyDb.getQuestions(mSubject);

        mQuestionText = findViewById(R.id.questionText);

        Log.e("front_card", "==============================================\nit did it\n==========================================================\n");
        mCurrentQuestionIndex = 0;

        finish();

    }

}
