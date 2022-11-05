package nz.ac.unitec.cs.assignment2_mini;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import nz.ac.unitec.cs.assignment2_mini.RecyclerView.RVQuizAdapter;

public class TournamentActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog dialog;
    ArrayList quizzes = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        recyclerView = findViewById(R.id.rv_tournament_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        setLoadingDialog();
        loadQuizList();
    }

    private void setLoadingDialog() {
        dialog = new ProgressDialog(TournamentActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Connecting Server");
    }

    private void loadQuizList() {
        db.collection("QuizList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()) {
                            quizzes.clear();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                HashMap<String,String> quiz = new HashMap<String, String>();
                                String name = document.get("name").toString();
                                String startDate = document.get("start_date").toString();
                                String endDate = document.get("end_date").toString();
                                String key = document.getId();

                                quiz.put("key", key);
                                quiz.put("name", name);
                                quiz.put("start_date", startDate);
                                quiz.put("end_date", endDate);
                                quizzes.add(quiz);
                            }
                            setScreen();
                        } else {
                            Toast.makeText(TournamentActivity.this, "Load Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog.show();
    }

    private void setScreen() {
        RVQuizAdapter adapter = new RVQuizAdapter(quizzes);
        adapter.setMyRVClickListener(new RVQuizAdapter.RVClickListener() {
            @Override
            public void itemClickListener(String quizListKey) {
                Intent intent = new Intent(TournamentActivity.this, NewTournamentActivity.class);
                intent.putExtra("key", quizListKey);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}