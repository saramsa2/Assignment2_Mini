package nz.ac.unitec.cs.assignment2_mini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
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
import nz.ac.unitec.cs.assignment2_mini.DataModel.Quiz;
import nz.ac.unitec.cs.assignment2_mini.DataModel.Quizzes;
import nz.ac.unitec.cs.assignment2_mini.DataModel.MyQuiz;
import nz.ac.unitec.cs.assignment2_mini.Volley.VolleyAPI;

public class NewTournamentActivity extends AppCompatActivity {

    Button btNewTournament, btTournamentList;
    EditText etName, etStartDate, etEndDate;
    Spinner spinCategory, spinDifficulty;

    Button btCreate, btCancel;

    Categories categories;
    String quizCategoryId;
    String quizDifficulty;

    Calendar calendar = Calendar.getInstance();

    private final String CATEGORY_URL = "https://opentdb.com/api_category.php";
    private final String QUIZ_URL = "https://opentdb.com/api.php?amount=10&type=multiple";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    VolleyAPI volleyAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tournament);

        etName = findViewById(R.id.et_edit_tournament_name);
        etStartDate = findViewById(R.id.et_edit_tournament_start);
        etEndDate = findViewById(R.id.et_edit_tournament_end);

        spinCategory = findViewById(R.id.spinner_edit_tournament_category);
        spinDifficulty = findViewById(R.id.spinner_edit_tournament_difficulty);

        btCreate = findViewById(R.id.bt_admin_create);
        btCancel = findViewById(R.id.bt_admin_cancel);

        volleyAPI = new VolleyAPI(NewTournamentActivity.this, CATEGORY_URL);

        addEventListeners();
        setInit();

    }

    private void setInit() {
        Calendar initDate = Calendar.getInstance();
        String txtInitStartDate = String.valueOf(initDate.get(Calendar.DAY_OF_MONTH));
        txtInitStartDate += "-" + String.valueOf(initDate.get(Calendar.MONTH)+1);
        txtInitStartDate += "-" + String.valueOf(initDate.get(Calendar.YEAR));
        initDate.add(Calendar.MONTH, 1);
        String txtInitEndDate = String.valueOf(initDate.get(Calendar.DAY_OF_MONTH));
        txtInitEndDate += "-" + String.valueOf(initDate.get(Calendar.MONTH)+1);
        txtInitEndDate += "-" + String.valueOf(initDate.get(Calendar.YEAR));

        etStartDate.setText(txtInitStartDate);
        etEndDate.setText(txtInitEndDate);

        ProgressDialog dialog = new ProgressDialog(NewTournamentActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Connecting Server");

        volleyAPI.setReadAPIListener(new VolleyAPI.readAPIListener() {
            @Override
            public void readAPISucceed(String response) {
                Gson gson = new Gson();
                categories = gson.fromJson(response, Categories.class);
                updateCategorySpin(categories);
                dialog.dismiss();
            }

            @Override
            public void readAPIFailed() {
                dialog.dismiss();
            }
        });
        volleyAPI.getAPI();
        dialog.show();

    }


    private void updateCategorySpin(Categories categories) {
//        this.categories = categories;

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
//                dPicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
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
//                dPicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
                dPDialog.show();
            }
        });

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> quiz = new HashMap<>();
                try {
                    String QuizName = etName.getText().toString();
                    String QuizDifficulty = spinDifficulty.getSelectedItem().toString();
                    quizDifficulty = QuizDifficulty;
                    String QuizStartDate = etStartDate.getText().toString();
                    String QuizEndDate = etEndDate.getText().toString();
                    String QuizCategoryText = spinCategory.getSelectedItem().toString();
                    String QuizCategoryId =  String.valueOf(spinCategory.getSelectedItemId());
                    quizCategoryId =  QuizCategoryId;

                    quiz.put("name", QuizName);
                    quiz.put("category_id", QuizCategoryText);
                    quiz.put("difficulty", quizDifficulty);
                    quiz.put("start_date", QuizStartDate);
                    quiz.put("end_date", QuizEndDate);

                    generateNewQuizzes(quiz);
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
                    saveNewQuiz(response, quiz);
                }
            }

            @Override
            public void readAPIFailed() {

            }
        });
        quizVolleyAPI.getAPI();
    }

    private void saveNewQuiz(String response, Map<String, Object> quiz) {
        Map<String, Object> myQuiz = new HashMap<>();
        myQuiz.put("quiz", response);
        db.collection("QuizDetail")
            .add(myQuiz)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String key = documentReference.getId();
                    quiz.put("detail_key", key);
                    Toast.makeText(NewTournamentActivity.this, "succeed.",
                            Toast.LENGTH_SHORT).show();
                    updateQuizList(quiz);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewTournamentActivity.this, e.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateQuizList(Map<String, Object> quiz) {
        MyQuiz myquiz = new MyQuiz();
        db.collection("QuizList")
                .add(quiz)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(NewTournamentActivity.this, "succeed.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewTournamentActivity.this, e.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}