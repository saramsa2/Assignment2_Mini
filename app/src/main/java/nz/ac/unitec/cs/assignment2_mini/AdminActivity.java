package nz.ac.unitec.cs.assignment2_mini;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class AdminActivity extends AppCompatActivity {

    Button btNewTournament, btTournamentList, btLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btNewTournament = findViewById(R.id.bt_admin_new_tournament);
        btTournamentList = findViewById(R.id.bt_admin_tournament_list);
        btLogout = findViewById(R.id.bt_admin_logout);

        addEventListeners();
    }


    private void addEventListeners() {

        btNewTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminActivity.this, NewTournamentActivity.class);
                intent.putExtra("key", "new");
                startActivity(intent);
            }
        });

        btTournamentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, TournamentActivity.class);
                startActivity(intent);
            }
        });

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDBuilder = new AlertDialog.Builder(AdminActivity.this);
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
}