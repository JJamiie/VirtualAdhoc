package com.example.jjamie.virtualadhoc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity implements BlankFragment.OnFragmentInteractionListener {
    private SharedPreferences sharedPreferences;
    private EditText usernameEditText;
    private CheckBox rememberCheckBox;
    private Button logInButton;
    private Activity th = this;
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
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    intent.putExtra("username", usernameEditText.getText().toString());
                    startActivity(intent);
                }
            }
        });
        sharedPreferences = getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE);
        usernameEditText.setText(sharedPreferences.getString("USERNAME", ""));
        rememberCheckBox.setChecked(sharedPreferences.getBoolean("REMEMBER", false));

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jjamie.virtualadhoc/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.jjamie.virtualadhoc/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
