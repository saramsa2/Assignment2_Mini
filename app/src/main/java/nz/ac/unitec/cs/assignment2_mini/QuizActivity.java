package nz.ac.unitec.cs.assignment2_mini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nz.ac.unitec.cs.assignment2_mini.DataModel.Progress;
import nz.ac.unitec.cs.assignment2_mini.DataModel.Quiz;
import nz.ac.unitec.cs.assignment2_mini.DataModel.Quizzes;

public class QuizActivity extends AppCompatActivity {

    TextView tvQuestion;
    RadioButton[] radioAnswer;
    Button btnSubmit, btnCancel;

    ProgressDialog dialog;
    String quizJsonString;
    Quizzes myQuizzes;
    int progressQuestion;
    int correctAnswer;
    FirebaseFirestore db;
    Progress myProgress = new Progress();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tv_quiz_question);

        radioAnswer = new RadioButton[4];
        radioAnswer[0] = findViewById(R.id.radio_btn_quiz_answer_1);
        radioAnswer[1] = findViewById(R.id.radio_btn_quiz_answer_2);
        radioAnswer[2] = findViewById(R.id.radio_btn_quiz_answer_3);
        radioAnswer[3] = findViewById(R.id.radio_btn_quiz_answer_4);

        btnSubmit = findViewById(R.id.bt_quiz_submit);
        btnCancel = findViewById(R.id.bt_quiz_cancel);

        db = FirebaseFirestore.getInstance();

        setLoadingDialog();
        loadDataBase();
        addEventListeners();
    }

    // find current question.
    private void findFirstQuestion() {
        Object currentProgress =  getIntent().getExtras().get("progress");
        if(currentProgress.equals("-")){
            progressQuestion = 0;
            myProgress.setProgress(progressQuestion);
            showQuizObject();
        } else {
            JSONObject objectProgress = new JSONObject();
            try {
                objectProgress = new JSONObject(currentProgress.toString());
                progressQuestion = objectProgress.getInt("progress");
                if(progressQuestion >= myQuizzes.getResults().size()){
                    AlertDialog.Builder alertDBuilder = new AlertDialog.Builder(QuizActivity.this);
                    alertDBuilder.setTitle("Do you want to restart?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    String temp = FirebaseAuth.getInstance().getUid();
                                    db.collection("UserProgress")
                                            .document(FirebaseAuth.getInstance().getUid())
                                            .update(getIntent().getExtras().get("key").toString(), FieldValue.delete())
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(QuizActivity.this, "Failed to update. Try again", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressQuestion = 0;
                                                    myProgress.setProgress(progressQuestion);
                                                    showQuizObject();
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = alertDBuilder.create();
                    alertDialog.show();
                } else {
                    for(int i = 0; i < progressQuestion; i++) {
                        myProgress.addResult(i, (Boolean) objectProgress.getJSONObject("results").get(String.valueOf(i)));
                    }
                    myProgress.setProgress(progressQuestion);
                    showQuizObject();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        myProgress.setProgress(progressQuestion);
//        showQuizObject();
    }


    private void addEventListeners() {

        // summite button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title, contents;
                if (radioAnswer[correctAnswer].isChecked()) {
                    myProgress.addResult(progressQuestion, true);
                    title = "Correct";
                    contents = "Your answer is correct.";
                } else {
                    myProgress.addResult(progressQuestion, false);
                    title = "Incorrect";
                    contents = "Correct answer is " + radioAnswer[correctAnswer].getText() + ".";
                }
                for(int i = 0; i < radioAnswer.length; i++) {
                    radioAnswer[i].setChecked(false);
                }
                progressQuestion++;
                myProgress.setProgress(progressQuestion);

                db.collection("UserProgress")
                        .document(getIntent().getExtras().get("UID").toString())
                        .update(getIntent().getExtras().get("key").toString(), new Gson().toJson(myProgress))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(QuizActivity.this, "Failed to update. Try again", Toast.LENGTH_SHORT).show();
                            }
                        });

                // Result for each question.
                AlertDialog.Builder alertDBuilder = new AlertDialog.Builder(QuizActivity.this);
                alertDBuilder.setTitle(title)
                        .setMessage(contents)
                        .setCancelable(false)
                        .setNeutralButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showQuizObject();
                            }
                        });
                AlertDialog alertDialog = alertDBuilder.create();
                alertDialog.show();
            }
        });

        // save the progress and results and finish activity
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDBuilder = new AlertDialog.Builder(QuizActivity.this);
                alertDBuilder.setTitle("Do you want to stop this quiz?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                myProgress.setProgress(progressQuestion);
                                db.collection("UserProgress")
                                        .document(getIntent().getExtras().get("UID").toString())
                                        .update(getIntent().getExtras().get("key").toString(), new Gson().toJson(myProgress))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(QuizActivity.this, "Failed to update. Try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDBuilder.create();
                alertDialog.show();

            }
        });
    }

    // load data from DB
    private void loadDataBase() {
        db.collection("QuizList").document(getIntent().getExtras().get("key").toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String quizDetailKey = documentSnapshot.getString("detail_key");
                        db.collection("QuizDetail").document(quizDetailKey)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        dialog.dismiss();
                                        quizJsonString = documentSnapshot.getString("quiz");
                                        myQuizzes = new Gson().fromJson(quizJsonString, Quizzes.class);
                                        findFirstQuestion();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(QuizActivity.this, "Load Fail", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(QuizActivity.this, "Load Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.show();
    }

    // show current question
    private void showQuizObject() {


        int counter = 0;

        if(progressQuestion < myQuizzes.getResults().size()){
            for(Quiz myQuiz : myQuizzes.getResults()) {
                if(counter == progressQuestion) {
                    tvQuestion.setText( "Q" + (progressQuestion + 1) + ". " + myQuiz.getQuestion());
                    int quizSize = myQuiz.getIncorrectAnswers().size() + 1;
                    correctAnswer = (int)(Math.ceil(Math.random() * quizSize));
                    if(correctAnswer >= quizSize) {
                        correctAnswer = quizSize-1;
                    }
                    String[] myIncorrectAnswer = new String[quizSize-1];
                    int inCorrectAnswerCounter = 0;
                    Collections.shuffle(myQuiz.getIncorrectAnswers());
                    for(String wrongAnswer: myQuiz.getIncorrectAnswers()) {
                        myIncorrectAnswer[inCorrectAnswerCounter] = wrongAnswer;
                        inCorrectAnswerCounter++;
                    }

                    inCorrectAnswerCounter = 0;
                    for(int i = 0; i < quizSize; i++) {
                        if(i == correctAnswer) {
                            radioAnswer[i].setText(myQuiz.getCorrectAnswer());
                        } else {
                            radioAnswer[i].setText(myIncorrectAnswer[inCorrectAnswerCounter]);
                            inCorrectAnswerCounter++;
                        }
                    }
                }
                counter++;
            }
        } else {
            calculateScore();
        }


    }

    private void calculateScore() {
        int score = 0;
        for(int i = 0; i < myProgress.getResults().size(); i++) {
            if(myProgress.getResults().get(i)) {
                score++;
            }
        }
        AlertDialog.Builder alertDBuilder = new AlertDialog.Builder(QuizActivity.this);
        alertDBuilder.setTitle("Score")
                .setMessage("Your score is " + score + "/" + myProgress.getResults().size())
                .setCancelable(false)
                .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDBuilder.create();
        alertDialog.show();

    }

    // set loading dialog.
    private void setLoadingDialog() {
        dialog = new ProgressDialog(QuizActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Connecting Server");
    }
}