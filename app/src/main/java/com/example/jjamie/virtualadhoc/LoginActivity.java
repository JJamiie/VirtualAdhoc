package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity  {
    private SharedPreferences sharedPreferences;
    private EditText usernameEditText;
    private CheckBox rememberCheckBox;
    private Button logInButton;
    private Activity th = this;
    private ConnectionManager connectionManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rememberCheckBox = (CheckBox) findViewById(R.id.rememberUsername);
        usernameEditText = (EditText) findViewById(R.id.usernamEditText);
        logInButton = (Button) findViewById(R.id.login);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameEditText.getText().length()==0){
                    th.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(th, "Please fill your username.", Toast.LENGTH_LONG).show();
                        }
                    });
                }else {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    intent.putExtra("username", usernameEditText.getText().toString());
                    startActivity(intent);
                    connectionManager.start();
                }
            }
        });
        sharedPreferences = getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE);
        usernameEditText.setText(sharedPreferences.getString("USERNAME", ""));
        rememberCheckBox.setChecked(sharedPreferences.getBoolean("REMEMBER", false));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", usernameEditText.getText().toString());
        editor.putBoolean("REMEMBER", rememberCheckBox.isChecked());
        editor.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
