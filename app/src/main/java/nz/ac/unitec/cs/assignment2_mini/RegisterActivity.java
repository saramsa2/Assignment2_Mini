package nz.ac.unitec.cs.assignment2_mini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    EditText etEmail, etPassword1, etPassword2, etFullName;
    Button btnCreate, btnCancel;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_signup_email);
        etPassword1 = findViewById(R.id.et_signup_password);
        etPassword2 = findViewById(R.id.et_signup_password_confirm);
        etFullName = findViewById(R.id.et_signup_full_name);
        btnCreate = findViewById(R.id.btn_signup_create);
        btnCancel = findViewById(R.id.btn_signup_cancel);


        setEventListeners();
    }

    private void setEventListeners() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password1 = etPassword1.getText().toString();
                String password2 = etPassword2.getText().toString();
                if(isValidInput(email, password1, password2)) {
                    mAuth.createUserWithEmailAndPassword(email, password1)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(etFullName.getText().toString())
                                                .build();
                                        user.updateProfile(profileUpdates);
                                        Toast.makeText(RegisterActivity.this, "New account is created.",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Creating failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean isValidInput(String email, String password1, String password2) {
        boolean response = true;
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            response = false;
        } else if(password1.isEmpty() || !password1.equals(password2)) {
            response = false;
        }
        return response;
    }
}