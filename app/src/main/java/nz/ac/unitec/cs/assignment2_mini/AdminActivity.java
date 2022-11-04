package nz.ac.unitec.cs.assignment2_mini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class AdminActivity extends AppCompatActivity {

    Button btNewTournament, btTournamentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btNewTournament = findViewById(R.id.bt_admin_new_tournament);
        btTournamentList = findViewById(R.id.bt_admin_tournament_list);

        addEventListeners();
    }


    private void addEventListeners() {

        btNewTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminActivity.this, NewTournamentActivity.class);
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
    }
}