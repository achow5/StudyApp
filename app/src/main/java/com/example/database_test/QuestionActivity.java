package com.example.database_test;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

public class QuestionActivity extends AppCompatActivity implements SensorEventListener{

    public static final String EXTRA_SUBJECT = "com.example.database_test.subject";

    private StudyDatabase mStudyDb;
    private String mSubject;
    private List<Question> mQuestionList;
    private TextView mAnswerLabel;
    private TextView mAnswerText;
    private Button mAnswerButton;
    private TextView mQuestionText;
    private int mCurrentQuestionIndex;
    private ViewGroup mShowQuestionsLayout;
    private ViewGroup mNoQuestionsLayout;
    private final int REQUEST_CODE_NEW_QUESTION = 0;
    private final int REQUEST_CODE_UPDATE_QUESTION = 1;

    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor gyroscope;
    Vibrator vibrator;

    private final String TAG = "GestureDemo";
    private GestureDetectorCompat mDetector;

    float threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_question);

        findViews();
        loadAnimations();
        changeCameraDistance();

        // Hosting activity provides the subject of the questions to display
        mSubject = intent.getStringExtra(EXTRA_SUBJECT);

        // Load all questions for this subject
        mStudyDb = StudyDatabase.getInstance(getApplicationContext());
        mQuestionList = mStudyDb.getQuestions(mSubject);


        mQuestionText = findViewById(R.id.questionText);
        mAnswerLabel = findViewById(R.id.answerLabel);
        mAnswerText = findViewById(R.id.answerText);

        mAnswerButton = findViewById(R.id.answerButton);
        mShowQuestionsLayout = findViewById(R.id.showQuestionsLayout);
        mNoQuestionsLayout = findViewById(R.id.noQuestionsLayout);

        // Show first question

        Intent intent2 = new Intent(QuestionActivity.this, front_card.class);
        intent2.putExtra(front_card.EXTRA_SUBJECT, mSubject);
        startActivity(intent2);

        Intent intent3 = new Intent(QuestionActivity.this, back_card.class);
        intent3.putExtra(back_card.EXTRA_SUBJECT, mSubject);
        startActivity(intent3);

        showQuestion(0);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            threshold = accelerometer.getMaximumRange()/8;
        }
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class MyGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.d(TAG, "onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            editQuestion();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling");

            int check = 0;

            if(velocityY < -1200) {
                addQuestion();
                Toast.makeText(getApplicationContext(), "Swipe up", Toast.LENGTH_SHORT).show();

                if(!mIsBackVisible) {
                    mOutUp.setTarget(mCardFrontLayout);
                    mOutUp.start();

                    mSnapLeftToRight.setTarget(mCardBackLayout);
                    mSnapLeftToRight.start();
                }

                else {
                    mOutUp.setTarget(mCardBackLayout);
                    mOutUp.start();

                    mSnapLeftToRight.setTarget(mCardFrontLayout);
                    mSnapLeftToRight.start();
                }
                /*
                mInRight.setTarget(mCardFrontLayout);
                mInRight.start();

                */

                try {
                    //set time in mili
                    Thread.sleep(100);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            else if (velocityY > 1200) {
                deleteQuestion();
                Toast.makeText(getApplicationContext(), "Swipe down", Toast.LENGTH_SHORT).show();

                if(!mIsBackVisible) {
                    mOutDown.setTarget(mCardFrontLayout);
                    mOutDown.start();

                    mSnapLeftToRight.setTarget(mCardBackLayout);
                    mSnapLeftToRight.start();
                }

                else {
                    mOutDown.setTarget(mCardBackLayout);
                    mOutDown.start();

                    mSnapLeftToRight.setTarget(mCardFrontLayout);
                    mSnapLeftToRight.start();
                }

                try {
                    //set time in mili
                    Thread.sleep(100);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            else if (velocityX > 600) {

                mOutRight.setTarget(mCardFrontLayout);
                mOutRight.start();

                mSnapRightToLeft.setTarget(mCardBackLayout);
                mSnapRightToLeft.start();

                mInLeft.setTarget(mCardFrontLayout);
                mInLeft.start();

                try {
                    //set time in mili
                    Thread.sleep(100);

                }catch (Exception e){
                    e.printStackTrace();
                }

                showQuestion(mCurrentQuestionIndex - 1);
                nextFlip();

            }
            else if(velocityX < -600) {
                //Toast.makeText(getApplicationContext(), "Swipe left", Toast.LENGTH_SHORT).show();

                mOutLeft.setTarget(mCardFrontLayout);
                mOutLeft.start();

                mSnapLeftToRight.setTarget(mCardBackLayout);
                mSnapLeftToRight.start();

                mInRight.setTarget(mCardFrontLayout);
                mInRight.start();

                try {
                    //set time in mili
                    Thread.sleep(100);

                }catch (Exception e){
                    e.printStackTrace();
                }

                showQuestion(mCurrentQuestionIndex + 1);
                nextFlip();
            }
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Are there questions to display?
        if (mQuestionList.size() == 0) {
            updateAppBarTitle();
            displayQuestion(false);
        } else {
            displayQuestion(true);
            toggleAnswerVisibility();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu for the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Determine which app bar item was chosen
        switch (item.getItemId()) {
            case R.id.difficult:
                copyQuestion();
                return true;
            case R.id.add:
                addQuestion();
                return true;
            case R.id.edit:
                editQuestion();
                return true;
            case R.id.delete:
                deleteQuestion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addQuestionButtonClick(View view) {
        addQuestion();
    }

    public void answerButtonClick(View view) {
        toggleAnswerVisibility();
    }

    private void displayQuestion(boolean display) {

        // Show or hide the appropriate screen
        if (display) {
            mShowQuestionsLayout.setVisibility(View.VISIBLE);
            mNoQuestionsLayout.setVisibility(View.GONE);
        } else {
            mShowQuestionsLayout.setVisibility(View.GONE);
            mNoQuestionsLayout.setVisibility(View.VISIBLE);
        }
    }


    private void updateAppBarTitle() {

        // Display subject and number of questions in app bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = getResources().getString(R.string.question_number,
                    mSubject, mCurrentQuestionIndex + 1, mQuestionList.size());
            setTitle(title);
        }
    }


    private void showQuestion(int questionIndex) {

        // Show question at the given index
        if (mQuestionList.size() > 0) {
            if (questionIndex < 0) {
                questionIndex = mQuestionList.size() - 1;
            } else if (questionIndex >= mQuestionList.size()) {
                questionIndex = 0;
            }

            mCurrentQuestionIndex = questionIndex;
            updateAppBarTitle();

            Question question = mQuestionList.get(mCurrentQuestionIndex);
            //mQuestionText.setText(question.getText());
            mQuestionText.setText(question.getText());
            mAnswerText.setText(question.getAnswer());
        }
        else {
            // No questions yet
            mCurrentQuestionIndex = -1;
        }
    }

    private void toggleAnswerVisibility() {
        mAnswerButton.setText(R.string.hide_answer);
        mAnswerButton.setVisibility(View.INVISIBLE);
        mAnswerText.setVisibility(View.VISIBLE);
        mAnswerLabel.setVisibility(View.VISIBLE);
    }

    private void addQuestion() {
        Intent intent = new Intent(this, QuestionEditActivity.class);
        intent.putExtra(QuestionEditActivity.EXTRA_SUBJECT, mSubject);
        startActivityForResult(intent, REQUEST_CODE_NEW_QUESTION);
    }

    private void editQuestion() {
        if (mCurrentQuestionIndex >= 0) {
            Intent intent = new Intent(this, QuestionEditActivity.class);
            intent.putExtra(EXTRA_SUBJECT, mSubject);
            long questionId = mQuestionList.get(mCurrentQuestionIndex).getId();
            intent.putExtra(QuestionEditActivity.EXTRA_QUESTION_ID, questionId);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_QUESTION);
        }
    }

    private void copyQuestion() {
        if (mCurrentQuestionIndex >= 0) {
            Intent intent = new Intent(this, QuestionEditActivity.class);
            intent.putExtra(QuestionEditActivity.EXTRA_SUBJECT, mSubject);
            long questionId = mQuestionList.get(mCurrentQuestionIndex).getId();
            intent.putExtra(QuestionEditActivity.EXTRA_QUESTION_ID, questionId);
            intent.putExtra("diff", true);
            startActivityForResult(intent, REQUEST_CODE_NEW_QUESTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_QUESTION) {
            // Get added question
            long questionId = data.getLongExtra(QuestionEditActivity.EXTRA_QUESTION_ID, -1);
            Question newQuestion = mStudyDb.getQuestion(questionId);

            // Add newly created question to the question list and show it
            mQuestionList.add(newQuestion);
            showQuestion(mQuestionList.size() - 1);

            Toast.makeText(this, R.string.question_added, Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_QUESTION) {
            // Get updated question
            long questionId = data.getLongExtra(QuestionEditActivity.EXTRA_QUESTION_ID, -1);
            Question updatedQuestion = mStudyDb.getQuestion(questionId);

            // Replace current question in question list with updated question
            Question currentQuestion = mQuestionList.get(mCurrentQuestionIndex);
            currentQuestion.setText(updatedQuestion.getText());
            currentQuestion.setAnswer(updatedQuestion.getAnswer());
            showQuestion(mCurrentQuestionIndex);

            Toast.makeText(this, R.string.question_updated, Toast.LENGTH_SHORT).show();
        }
    }

    private Question mDeletedQuestion;

    private void deleteQuestion() {
        if (mCurrentQuestionIndex >= 0) {
            // Save question in case user undoes delete
            mDeletedQuestion = mQuestionList.get(mCurrentQuestionIndex);

            mStudyDb.deleteQuestion(mDeletedQuestion.getId());
            mQuestionList.remove(mCurrentQuestionIndex);

            if (mQuestionList.size() == 0) {
                // No questions to show
                mCurrentQuestionIndex = -1;
                updateAppBarTitle();
                displayQuestion(false);
            } else {
                showQuestion(mCurrentQuestionIndex);
            }

            // Show delete message with Undo button
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                    R.string.question_deleted, Snackbar.LENGTH_LONG);

            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Add question back
                    mStudyDb.addQuestion(mDeletedQuestion);
                    mQuestionList.add(mDeletedQuestion);
                    showQuestion(mQuestionList.size() - 1);
                }
            });
            snackbar.show();

        }
    }

    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;

    private AnimatorSet mOutLeft;
    private AnimatorSet mOutRight;
    private AnimatorSet mInLeft;
    private AnimatorSet mInRight;

    private AnimatorSet mSnapLeftToRight;
    private AnimatorSet mSnapRightToLeft;

    private AnimatorSet mOutUp;
    private AnimatorSet mOutDown;

    private boolean mIsBackVisible = false;
    private View mCardFrontLayout;
    private View mCardBackLayout;

    private void changeCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        mCardFrontLayout.setCameraDistance(scale);
        mCardBackLayout.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);

        mOutLeft = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_left);
        mOutRight = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.out_right);
        mInLeft = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_left);
        mInRight = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_right);

        mSnapLeftToRight = (AnimatorSet)AnimatorInflater.loadAnimator(this,R.animator.snap_left_to_right);
        mSnapRightToLeft = (AnimatorSet)AnimatorInflater.loadAnimator(this,R.animator.snap_right_to_left);

        mOutUp = (AnimatorSet)AnimatorInflater.loadAnimator(this,R.animator.out_up);
        mOutDown = (AnimatorSet)AnimatorInflater.loadAnimator(this,R.animator.out_down);
    }

    private void findViews() {
        mCardBackLayout = findViewById(R.id.card_back);
        mCardFrontLayout = findViewById(R.id.card_front);
    }

    public void flipCard(View view) {
        if (!mIsBackVisible) {
            mSetRightOut.setTarget(mCardFrontLayout);
            mSetLeftIn.setTarget(mCardBackLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
        } else {
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
        }
    }
    public void nextFlip() {
        if (mIsBackVisible) {
            mSetRightOut.setTarget(mCardBackLayout);
            mSetLeftIn.setTarget(mCardFrontLayout);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float ax, ay, az;
        float gx, gy, gz;

        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];
        gx = event.values[0];
        gy = event.values[1];

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if(ax < 2) {
                ax = 0;
            }

            if(ay < 2) {
                ay = 0;
            }
            if(az < 2) {
                az = 0;
            }

            if(ax > threshold || az > threshold) {

                //DETECTS WHEN SHAKEN
                mQuestionList = mStudyDb.getRandomQuestions(mSubject);
                showQuestion(0);
                Toast.makeText(this, "Shuffled cards", Toast.LENGTH_SHORT).show();
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //remove noise
            if(gx < 2) {
                gx = 0;
            }

            if(gy < 2) {
                gy = 0;
            }

            if(gy > (threshold/2)) {
                if (!mIsBackVisible) {
                    flipCard(mCardBackLayout);
                }
                else {
                    flipCard(mCardFrontLayout);
                }
            }
        }
    }
    //unused
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}