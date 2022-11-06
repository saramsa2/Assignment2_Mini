package nz.ac.unitec.cs.assignment2_mini;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import nz.ac.unitec.cs.assignment2_mini.RecyclerView.RVQuizAdapter;

public class UserActivity extends AppCompatActivity {

    Button btLogout;
    RecyclerView recyclerView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog dialog;
    ArrayList quizzes = new ArrayList();
    DocumentSnapshot quizProgress;
    Task<QuerySnapshot> myTask;
    int loadingChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btLogout = findViewById(R.id.bt_user_logout);
        recyclerView = findViewById(R.id.rv_user_main);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        addEventListeners();
        setLoadingDialog();
        loadQuizList();
    }


    private void addEventListeners() {
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDBuilder = new AlertDialog.Builder(UserActivity.this);
                alertDBuilder.setTitle("Do you want to sign out?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
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


    private void setLoadingDialog() {
        dialog = new ProgressDialog(UserActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Connecting Server");
    }


    private void loadQuizList() {
        loadingChecker = 0;
        db.collection("UserProgress")
                .document(getIntent().getExtras().get("UID").toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        loadingChecker++;
                        quizProgress = documentSnapshot;
                        if(loadingChecker == 2) {
                            setScreen();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        db.collection("QuizList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            loadingChecker++;
                            myTask = task;

                            if(loadingChecker == 2) {
                                setScreen();
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(UserActivity.this, "Load Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog.show();
    }

    private void setScreen() {
        dialog.dismiss();
        quizzes.clear();
        for(QueryDocumentSnapshot document: myTask.getResult()){
            HashMap<String,String> quiz = new HashMap<String, String>();
            String name = document.get("name").toString();
            String startDate = document.get("start_date").toString();
            String endDate = document.get("end_date").toString();
            String key = document.getId();

            if(quizProgress.get(key) != null) {
                quiz.put("progress",  quizProgress.get(key).toString());
            }

            quiz.put("key", key);
            quiz.put("name", name);
            quiz.put("start_date", startDate);
            quiz.put("end_date", endDate);
            quizzes.add(quiz);
        }
        RVQuizAdapter adapter = new RVQuizAdapter(quizzes);
        adapter.setMyRVClickListener(new RVQuizAdapter.RVClickListener() {
            @Override
            public void itemClickListener(String quizListKey) {
                Intent intent = new Intent(UserActivity.this, QuizActivity.class);
                intent.putExtra("key", quizListKey);
                intent.putExtra("UID", getIntent().getExtras().get("UID").toString());
                if(quizProgress.get(quizListKey) == null) {
                    intent.putExtra("progress", "-");
                } else {
                    intent.putExtra("progress", quizProgress.get(quizListKey).toString());
                }
                startActivityForResult(intent, 200);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadQuizList();
    }
}