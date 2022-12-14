package com.example.geoactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mCheatTimesLeftTextView;
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;
    private Questions[] mQuestionBank = new Questions[]{
            new Questions(R.string.question_australia, true),
            new Questions(R.string.question_oceans, true),
            new Questions(R.string.question_mideast, false),
            new Questions(R.string.question_africa, false),
            new Questions(R.string.question_americas, true),
            new Questions(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private int mCorrectNumber = 0;
    private int mAnsweredNumber = 0;
    private int mCheatedNumber = 0;
    private boolean mIsCheater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//继承父类的super重载方法
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);//将xml文件载入到activity中显示
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mCheatTimesLeftTextView = (TextView) findViewById(R.id.cheat_times_left);
        updateQuestion();
        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCheatedNumber == 3){
                    Toast.makeText(MainActivity.this,"No more cheating chance.",Toast.LENGTH_SHORT).show();
                }
                else{
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

                    Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
                    startActivityForResult(intent,REQUEST_CODE_CHEAT);
                }

            }
        });
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!mQuestionBank[mCurrentIndex].getIfAnswered()){
                    checkAnswer(true);
                    mQuestionBank[mCurrentIndex].turnOnIfAnswered();
                //}
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!mQuestionBank[mCurrentIndex].getIfAnswered()){
                    checkAnswer(false);
                    mQuestionBank[mCurrentIndex].turnOnIfAnswered();
                //}
            }
        });
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsCheater = false;
                if (mCurrentIndex == 0) mCurrentIndex = mQuestionBank.length - 1;
                else mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mQuestionBank[mCurrentIndex].Cheated();
            mCheatedNumber++;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        mCheatTimesLeftTextView.setText("Cheating:" + mCheatedNumber + "/3");
        //Toast.makeText(this,"Cheat times:" + mCheatedNumber + "/3",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if(mIsCheater || mQuestionBank[mCurrentIndex].getIfCheated()){
            messageResId = R.string.judgment_toast;
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        } else {
            if(!mQuestionBank[mCurrentIndex].getIfAnswered()) {
                mAnsweredNumber++;
                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.correct_toast;
                    mCorrectNumber++;
                } else {
                    messageResId = R.string.incorrect_toast;
                }

            }
            if(messageResId != 0){
                Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
            }

        }
        if(mAnsweredNumber + mCheatedNumber == mQuestionBank.length){
            Toast.makeText(this,"Accuracy:" + mCorrectNumber + "/" + mQuestionBank.length,Toast.LENGTH_SHORT).show();
        }
    }
}