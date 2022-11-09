package nz.ac.unitec.cs.assignment2_mini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nz.ac.unitec.cs.assignment2_mini.DataModel.Categories;
import nz.ac.unitec.cs.assignment2_mini.DataModel.Category;
import nz.ac.unitec.cs.assignment2_mini.DataModel.Quizzes;
import nz.ac.unitec.cs.assignment2_mini.Volley.VolleyAPI;

public class NewTournamentActivity extends AppCompatActivity {

    EditText etName, etStartDate, etEndDate;
    Spinner spinCategory, spinDifficulty;
    TextView tvDelete;
    Button btSubmit, btCancel, btDelete;

    Categories categories;
    String quizCategoryId;
    String quizDifficulty;

    Calendar calendar = Calendar.getInstance();

    private final String CATEGORY_URL = "https://opentdb.com/api_category.php";
    private final String QUIZ_URL_BASE = "https://opentdb.com/api.php?amount=";
    private String QUIZ_URL;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog dialog;
    VolleyAPI volleyAPI;
    String quizListKey, quizDetailKey;
    int loadCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tournament);

        etName = findViewById(R.id.et_edit_tournament_name);
        etStartDate = findViewById(R.id.et_edit_tournament_start);
        etEndDate = findViewById(R.id.et_edit_tournament_end);

        spinCategory = findViewById(R.id.spinner_edit_tournament_category);
        spinDifficulty = findViewById(R.id.spinner_edit_tournament_difficulty);

        btSubmit = findViewById(R.id.bt_admin_create);
        btCancel = findViewById(R.id.bt_admin_cancel);

        tvDelete = findViewById(R.id.tv_admin_delete);
        btDelete = findViewById(R.id.bt_admin_delete);

        volleyAPI = new VolleyAPI(NewTournamentActivity.this, CATEGORY_URL);
        quizListKey = getIntent().getExtras().get("key").toString();


        QUIZ_URL = QUIZ_URL_BASE + getResources().getInteger(R.integer.quiz_size) + getResources().getString(R.string.quiz_type);

        setLoadingDialog();
        addEventListeners();
        setInit();

    }

    private void setLoadingDialog() {
        dialog = new ProgressDialog(NewTournamentActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Connecting Server");
    }

    private void setInit() {
        Calendar initDate = Calendar.getInstance();
        String txtInitStartDate = String.valueOf(initDate.get(Calendar.DAY_OF_MONTH));
        txtInitStartDate += "-" + (initDate.get(Calendar.MONTH) + 1);
        txtInitStartDate += "-" + initDate.get(Calendar.YEAR);
        initDate.add(Calendar.MONTH, 1);
        String txtInitEndDate = String.valueOf(initDate.get(Calendar.DAY_OF_MONTH));
        txtInitEndDate += "-" + (initDate.get(Calendar.MONTH) + 1);
        txtInitEndDate += "-" + initDate.get(Calendar.YEAR);

        etStartDate.setText(txtInitStartDate);
        etEndDate.setText(txtInitEndDate);

        volleyAPI.setReadAPIListener(new VolleyAPI.readAPIListener() {
            @Override
            public void readAPISucceed(String response) {
                Gson gson = new Gson();
                categories = gson.fromJson(response, Categories.class);
                updateCategorySpin(categories);
                if(!quizListKey.equals("new")) {
                    getQuizInfo();
                } else {
                    dialog.dismiss();
                }
            }

            @Override
            public void readAPIFailed() {
                dialog.dismiss();
            }
        });
        volleyAPI.getAPI();
        dialog.show();

    }

    // load selected quiz detail and enable delete button
    private void getQuizInfo() {
        db.collection("QuizList")
                .document(quizListKey)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()) {
                            etName.setText(task.getResult().get("name").toString());
                            etStartDate.setText(task.getResult().get("start_date").toString());
                            etEndDate.setText(task.getResult().get("end_date").toString());
                            spinCategory.setSelection(Integer.parseInt(task.getResult().get("category_id").toString()));
                            spinCategory.setEnabled(false);
                            spinDifficulty.setSelection(Integer.parseInt(task.getResult().get("difficulty_id").toString()));
                            spinDifficulty.setEnabled(false);
                            quizDetailKey = task.getResult().get("detail_key").toString();
                        }
                        else {
                            finish();
                        }
                    }
                });
        btDelete.setVisibility(View.VISIBLE);
        tvDelete.setVisibility(View.VISIBLE);
    }


    private void updateCategorySpin(Categories categories) {
        if(categories != null) {
            List<String> categoryName = new ArrayList<>();
            List<Integer> categoryId = new ArrayList<>();
            categoryId.add(0);
            categoryName.add("Any Category");
            for(Category cate: categories.getCategories()) {
                categoryId.add(cate.getId());
                categoryName.add(cate.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewTournamentActivity.this, android.R.layout.simple_spinner_item, categoryName);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinCategory.setAdapter(adapter);

        }
    }


    private void addEventListeners() {
        DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                String dateFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
                etStartDate.setText(sdf.format(calendar.getTime()));
            }
        };

        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dPDialog = new DatePickerDialog(NewTournamentActivity.this, startDate, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker dPicker = dPDialog.getDatePicker();
                dPDialog.show();
            }
        });


        DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                String dateFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
                etEndDate.setText(sdf.format(calendar.getTime()));
            }
        };

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dPDialog = new DatePickerDialog(NewTournamentActivity.this, endDate, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker dPicker = dPDialog.getDatePicker();
                dPDialog.show();
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> quiz = new HashMap<>();
                try {
                    String QuizName = etName.getText().toString();
                    String QuizDifficulty = spinDifficulty.getSelectedItem().toString();
                    quizDifficulty = QuizDifficulty;
                    String QuizDifficultyId = String.valueOf(spinDifficulty.getSelectedItemPosition());
                    String QuizStartDate = etStartDate.getText().toString();
                    String QuizEndDate = etEndDate.getText().toString();
                    String QuizCategoryText = spinCategory.getSelectedItem().toString();
                    String QuizCategoryId =  String.valueOf(spinCategory.getSelectedItemId());
                    quizCategoryId =  QuizCategoryId;

                    quiz.put("name", QuizName);
                    quiz.put("category", QuizCategoryText);
                    quiz.put("category_id", QuizCategoryId);
                    quiz.put("difficulty", quizDifficulty);
                    quiz.put("difficulty_id", QuizDifficultyId);
                    quiz.put("start_date", QuizStartDate);
                    quiz.put("end_date", QuizEndDate);
                    
                    if(quizListKey.equals("new")) {
                        generateNewQuizzes(quiz);
                    }
                    else {
                        updateQuizList(quiz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDBuilder = new AlertDialog.Builder(NewTournamentActivity.this);
                alertDBuilder.setTitle("Do you want to delete?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                loadCounter = 0;
                                db.collection("QuizList").document(quizListKey)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                loadCounter++;
                                                if(loadCounter == 2) {
                                                    finish();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(NewTournamentActivity.this,
                                                        "Error. Retry later.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                db.collection("QuizDetail").document(quizDetailKey)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                loadCounter++;
                                                if(loadCounter == 2) {
                                                    finish();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(NewTournamentActivity.this,
                                                        "Error. Retry later.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                AlertDialog alertDialog = alertDBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void updateQuizList(Map<String, Object> quiz) {
        db.collection("QuizList")
                .document(quizListKey)
                .update(
                        "name", quiz.get("name"),
                        "start_date", quiz.get("start_date"),
                        "end_date", quiz.get("end_date")
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(NewTournamentActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void generateNewQuizzes(Map<String, Object> quiz) {
        String myQuizUrl = QUIZ_URL;

        if(!quizCategoryId.equals("0")) {
            myQuizUrl += "&category=" + quizCategoryId;
        }

        if(!quizDifficulty.equals("Any Difficulty")) {
            myQuizUrl += "&difficulty=" + quizDifficulty;
        }

        VolleyAPI quizVolleyAPI = new VolleyAPI(NewTournamentActivity.this, myQuizUrl);
        quizVolleyAPI.setReadAPIListener(new VolleyAPI.readAPIListener() {
            @Override
            public void readAPISucceed(String response) {                
                Gson gson = new Gson();
                Quizzes quizzes = gson.fromJson(response, Quizzes.class);
                if(quizzes.getResponseCode() == 0) {
                    addNewQuizDetail(response, quiz);
                }
            }
            @Override
            public void readAPIFailed() {
                dialog.dismiss();
                Toast.makeText(NewTournamentActivity.this, "Loading fail", Toast.LENGTH_SHORT).show();
            }
        });
        quizVolleyAPI.getAPI();
        dialog.show();
    }

    private void addNewQuizDetail(String response, Map<String, Object> quiz) {
        Map<String, Object> myQuiz = new HashMap<>();
        myQuiz.put("quiz", response);
        db.collection("QuizDetail")
            .add(myQuiz)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String key = documentReference.getId();
                    quiz.put("detail_key", key);
                    addNewQuizList(quiz);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewTournamentActivity.this, e.toString(),
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        dialog.show();
    }

    private void addNewQuizList(Map<String, Object> quiz) {
        db.collection("QuizList")
                .add(quiz)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewTournamentActivity.this, e.toString(),
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
    }
}