package nz.ac.unitec.cs.assignment2_mini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    AlertDialog.Builder alertDBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDBuilder.setTitle("Please input email and password.")
                            .setCancelable(false)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = alertDBuilder.create();
                    alertDialog.show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "Authentication succeed.",
                                        Toast.LENGTH_SHORT).show();
                                if(user.getUid().equals("AsUJaklGqiR3lzIHcDpDLJCrYu22")){
                                    Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                                    intent.putExtra("UID", user.getUid());
                                    startActivity(intent);
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
    }



}