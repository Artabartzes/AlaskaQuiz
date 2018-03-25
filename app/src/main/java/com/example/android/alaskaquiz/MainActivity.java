package com.example.android.alaskaquiz;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.BitSet;

public class MainActivity extends AppCompatActivity {
    final static int CHECKBOX_ANSWER = 7;
    final static int NUMBER_OF_LAKES = 3000000;
    final static int QUESTION_6_RANGE = 100000;
    int[] answerKey = new int[9];
    BitSet answerBitSet = new BitSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAnswerKey();
        initRadioGroupIds();
    }

    /**
     * OnSubmit: Processes quiz results
     *
     * @param view - unused, but necessary for the call, apparently.
     */
    @SuppressLint("SetTextI18n")
    public void onSubmit(View view) {
        //The thought of putting in events for every RadioGroup was too much... So, I will search all View Id's here.
        int correctAnswers = 0;
        View myView = null;

        int quesVal = -1;
        LinearLayout myLayout = findViewById((R.id.quiz_layout));
        for (int i = 0; i < myLayout.getChildCount(); i++) {
            //Test type of class
            //Extract question index from Id (id - 1)
            //Compare question answer with answerKey
            //If question matches answer key then set drawableRight to check else set to x
            //  Helper Functions:
            //  Check RadioGroup: get checked id and compare with answer key and increment correct answers
            //  Check EditText: get number from text box, hmm, this may be tricky, check answer key and incr
            //  Check CheckBoxes: maintain bitset, hmm, ticky, at end of loop will have to check answer key and increment
            //  Check Answer
            //  Set right or wrong drawables

            myView = myLayout.getChildAt(i);
            if (myView instanceof RadioGroup) {
                quesVal = checkRadioGroup(myView);
                correctAnswers += checkAnswer(myView, quesVal);
            } else if (myView instanceof EditText) {
                quesVal = checkEditText(myView);
                correctAnswers += checkAnswer(myView, quesVal);
            } else if (myView instanceof CheckBox)
                checkCheckBox(myView);
        }

        //Process CheckBox answers
        quesVal = bitSetToInt(answerBitSet);
        TextView tv = findViewById(R.id.ques8);
        Drawable[] drawables = tv.getCompoundDrawables();    //Preserve existing drawables
        if (quesVal == answerKey[CHECKBOX_ANSWER]) {
            correctAnswers += 1;
            Log.i(this.getPackageName(), "The checkboxes were correct quesVal=" + quesVal + "=" + answerKey[CHECKBOX_ANSWER] + "  correct answers=" + correctAnswers);
            Drawable img = this.getResources().getDrawable(R.drawable.check);
            tv.setCompoundDrawablesWithIntrinsicBounds(img, drawables[1],
                    drawables[2], drawables[3]);
        } else {
            Log.i(this.getPackageName(), "The checkboxes were incorrect quesVal=" + quesVal + "=" + answerKey[CHECKBOX_ANSWER] + "  correct answers=" + correctAnswers);
            Drawable img = this.getResources().getDrawable(R.drawable.x);
            tv.setCompoundDrawablesWithIntrinsicBounds(img, drawables[1],
                    drawables[2], drawables[3]);
        }

        String bread = "You got " + correctAnswers + " out of " + answerKey.length + " correct!";
        Toast.makeText(getApplicationContext(), bread, Toast.LENGTH_LONG).show();
    }

    /**
     * checkAnswer: compares the answer given with the answer key
     *
     * @param myView,  the view being tested
     * @param quesVal, the question value given by the user
     * @return result, an int of how many points to add to the correctAnswers variable.
     */
    private int checkAnswer(View myView, int quesVal) {
        int result = 0;
        String viewIdName = "";
        int rgId = myView.getId();
        try {
            viewIdName = getResources().getResourceEntryName(rgId);
            int questId = Integer.parseInt(viewIdName.substring(4, 5));
            if (quesVal == answerKey[questId - 1]) {
                result = 1;
                Log.i(this.getPackageName(), viewIdName + " was correct quesVal=" + quesVal + "=" + answerKey[questId - 1]);
                Drawable img = this.getResources().getDrawable(R.drawable.check);
                if (myView instanceof EditText) {
                    TextView tv = (TextView) myView;
                    Drawable[] drawables = tv.getCompoundDrawables();    //Preserve existing drawables
                    tv.setCompoundDrawablesWithIntrinsicBounds(img, drawables[1],
                            drawables[2], drawables[3]);
                } else {
                    RadioGroup rg = (RadioGroup) myView;
                    TextView tv = (TextView) rg.getChildAt(quesVal);
                    Drawable[] drawables = tv.getCompoundDrawables();    //Preserve existing drawables
                    tv.setCompoundDrawablesWithIntrinsicBounds(img, drawables[1],
                            drawables[2], drawables[3]);
                }

            } else {
                Log.i(this.getPackageName(), viewIdName + " was incorrect quesVal=" + quesVal + "=" + answerKey[questId - 1]);
                Drawable img = this.getResources().getDrawable(R.drawable.x);
                if (myView instanceof EditText) {
                    TextView tv = (TextView) myView;
                    Drawable[] drawables = tv.getCompoundDrawables();    //Preserve existing drawables
                    tv.setCompoundDrawablesWithIntrinsicBounds(img, drawables[1],
                            drawables[2], drawables[3]);
                } else {
                    RadioGroup rg = (RadioGroup) myView;
                    TextView tv = (TextView) rg.getChildAt(quesVal);
                    Drawable[] drawables = tv.getCompoundDrawables();  //Preserve existing drawables
                    tv.setCompoundDrawablesWithIntrinsicBounds(img, drawables[1],
                            drawables[2], drawables[3]);
                }
            }
        } catch (IllegalStateException ise) {
            Log.i(this.getPackageName(), "Not a view we are interested in.");
        } catch (Resources.NotFoundException rnfe) {
            Log.i(this.getPackageName(), "Not a view we are interested in.");
        } catch (Exception e) {
            Log.e(this.getPackageName(), e.getMessage());
        }
        return result;
    }

    /**
     * checkRadioGroup is a helper function to process RadioGroup answers.
     * Locates which question the radio group is associated with,
     * returns the answer.
     *
     * @param view: the RadioGroup view
     * @return answer: int value of the answer
     */
    private int checkRadioGroup(View view) {
        RadioGroup rg = (RadioGroup) view;
        int quesVal = rg.getCheckedRadioButtonId();
//        RadioButton btn = (RadioButton) rg.getChildAt(quesVal);
        Log.i(this.getPackageName(), view.toString() + " has a value of " + quesVal); //+ " " + btn.getText().toString());
        return quesVal;
    }

    /**
     * checkEditText is a helper function to process RadioGroup answers.
     * Locates which question the edit text view is associated with, attempts to cast it
     * to int and handle any format errors.
     * returns the answer.
     *
     * @param view: the EditText view
     * @return answer: int value of the answer
     */
    private int checkEditText(View view) {
        EditText et6 = (EditText) view;
        Editable editEt6 = et6.getText();
        String strEt6 = editEt6.toString();
        int quesVal = -1;
        try {
            quesVal = Integer.parseInt(strEt6);
        } catch (NumberFormatException e) {
            quesVal = 0;
            TextView errorText = findViewById(R.id.et6_error);
            errorText.layout(20, 0, 20, 0);
            errorText.setText("Answer must be a number.  Found '" + strEt6 + "'");
        }
        Log.i(this.getPackageName(), "Edit Text=" + strEt6);
        if (Math.abs(quesVal - NUMBER_OF_LAKES) <= QUESTION_6_RANGE)  // Round to correct answer if within the bounds.
            quesVal = NUMBER_OF_LAKES;
        return quesVal;
    }

    /**
     * checkCheckBox is a helper function to process RadioGroup answers.
     * Locates which question the check box is associated with, sets the bit in the BitSet answer.
     * returns the answer.
     *
     * @param view: the CheckBox view
     */
    private void checkCheckBox(View view) {
        int rgId = view.getId();
        try {
            String viewIdName = getResources().getResourceEntryName(rgId);
            int quesCheckBoxId = Integer.parseInt(viewIdName.substring(6, 7));
            CheckBox cb = (CheckBox) view;
            if (cb.isChecked())
                answerBitSet.set(quesCheckBoxId);
            Log.i(this.getPackageName(), "correct checkCheckBox ID=" + quesCheckBoxId + " new value =" + bitSetToInt(answerBitSet));
        } catch (IllegalStateException ise) {
            Log.i(this.getPackageName(), "Not a view we are interested in.");
        } catch (Resources.NotFoundException rnfe) {
            Log.i(this.getPackageName(), "Not a view we are interested in.");
        } catch (Exception e) {
            Log.e(this.getPackageName(), e.getMessage());
        }
    }

    /**
     * initAnswerKey: method to set up the answer key
     */
    private void initAnswerKey() {
        answerKey[0] = 2;  //Country
        answerKey[1] = 0;  //Electricity
        answerKey[2] = 0;  //Indoor Plumbing
        answerKey[3] = 1;  //Main form of transportation
        answerKey[4] = 1;  //Snows all year
        answerKey[5] = NUMBER_OF_LAKES;  //Number of lakes over 20 acres
        answerKey[6] = 3;  //Any states larger?
        answerKey[7] = 42;  // Top Three industries
        answerKey[8] = 2; // State Bird
    }

    /**
     * initRadioGroupIds: method to set up sequencial id's for all radio group buttons
     */
    private void initRadioGroupIds() {
        LinearLayout myLayout = findViewById((R.id.quiz_layout));
        for (int i = 0; i < myLayout.getChildCount(); i++) {
            View myView = myLayout.getChildAt(i);
            if (myView instanceof RadioGroup) {
                RadioGroup rg = (RadioGroup) myView;
                int rgCnt = rg.getChildCount();
                for (int j = 0; j < rgCnt; j++) {
                    View v = rg.getChildAt(j);
                    if (v instanceof RadioButton) {
                        RadioButton rgBtn = (RadioButton) v;
                        rgBtn.setId(j);
                    }
                }
            }
        }
    }

    /**
     * Copied from st0le of https://stackoverflow.com/users/216517/st0le
     * Found at https://stackoverflow.com/questions/4873952/convert-bitset-to-int
     *
     * @param bitSet: the BitSet to be converted to an integer
     * @return bitInteger: the value of the BitSet as an integer
     */
    private static int bitSetToInt(BitSet bitSet) {
        int bitInteger = 0;
        for (int i = 0; i < 32; i++)
            if (bitSet.get(i))
                bitInteger |= (1 << i);
        return bitInteger;
    }
}
