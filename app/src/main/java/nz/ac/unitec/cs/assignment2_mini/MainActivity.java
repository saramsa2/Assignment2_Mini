package nz.ac.unitec.cs.assignment2_mini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btLogin, btRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etEmail = findViewById(R.id.et_main_email);
        etPassword = findViewById(R.id.et_main_password);

        btLogin = findViewById(R.id.bt_main_login);
        btRegister=findViewById(R.id.bt_main_register);

        mAuth = FirebaseAuth.getInstance();

        addEventListeners();

    }

    private void addEventListeners() {

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);

//                String email = etEmail.getText().toString();
//                String password = etPassword.getText().toString();
//                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(MainActivity.this, "Authentication succeed.",
//                                    Toast.LENGTH_SHORT).show();
//                            if(user.getDisplayName().equals("admin")){
//                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
//                                startActivity(intent);
//                            }
//
//                        }
//                        else{
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }



}