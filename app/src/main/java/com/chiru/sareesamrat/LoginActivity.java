package com.chiru.sareesamrat;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editText_name, editText_password;
    Button loginButton, signUpButton;
    TextView textViewForgotPassword;
    private String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    String match_request,username,password,message = "Network Problem. Try again later.";
    Boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpSubViews();

        textViewForgotPassword.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    private void setUpSubViews() {

        editText_name = (EditText) findViewById(R.id.editTextName);
        editText_password = (EditText) findViewById(R.id.editText_password);
        loginButton = (Button) findViewById(R.id.buttonLogin);
        signUpButton = (Button) findViewById(R.id.buttonSignUp);
        textViewForgotPassword = (TextView) findViewById(R.id.textView3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonLogin:
                username = editText_name.getText().toString();
                password = editText_password.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please Fill the required Fields", Toast.LENGTH_LONG).show();

                } else {
                    new attemptLogin().execute();
                }
                break;

            case R.id.buttonSignUp:
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                break;

            case R.id.textView3:
                Intent intent2 = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent2);
                break;
        }
    }

    private class attemptLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Logging in...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("login")
                    .appendPath(username)
                    .appendPath(password);
            String url = builder.build().toString();

            String jsonStr = httpRequestHandler.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    match_request = jsonObj.getString("match");
                    success = jsonObj.getBoolean("success");
                    message = jsonObj.getString("msg");

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (success && match_request.equals("True")) {
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
                finish();

            } else if (success && match_request.equals("False")) {
                Toast.makeText(LoginActivity.this, "Username and Password do not match", Toast.LENGTH_LONG).show();

            }else if(!success){
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
