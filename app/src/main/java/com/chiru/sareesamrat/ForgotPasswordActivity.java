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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {


    EditText username, newPassword, newPasswordAgain, email;
    Button applyToChangePassword;
    String usernameString, newPasswordString, newPasswordAgainString, emailString, message = "Network Problem. Try again later.";
    ProgressDialog pDialog;
    public static String TAG = ForgotPasswordActivity.class.getSimpleName();
    Boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username = (EditText) findViewById(R.id.editTextUsername);
        newPassword = (EditText) findViewById(R.id.editTextNewPassword);
        newPasswordAgain = (EditText) findViewById(R.id.editTextNewPasswordAgain);
        email = (EditText) findViewById(R.id.editTextEmail) ;

        applyToChangePassword = (Button) findViewById(R.id.buttonApplyForNewPassword);

        applyToChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                usernameString = username.getText().toString();
                newPasswordString = newPassword.getText().toString();
                newPasswordAgainString = newPasswordAgain.getText().toString();
                emailString = email.getText().toString();

                if(usernameString.isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this,"Username required",Toast.LENGTH_LONG).show();

                } else if(emailString.isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this,"Email required",Toast.LENGTH_LONG).show();

                } else if (!isPasswordEqual(newPasswordString, newPasswordAgainString)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password doesn't match.", Toast.LENGTH_LONG).show();

                } else{
                    new applyForNewPassword().execute();
                }
            }
        });
    }

    private class applyForNewPassword extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setMessage("Changing Password");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("forgotpassword")
                    .appendPath(usernameString)
                    .appendPath("")
                    .appendQueryParameter("email", emailString)
                    .appendQueryParameter("password", newPasswordString);
            String url = builder.build().toString();


            String jsonStr = httpRequestHandler.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
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
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (success) {
                Toast.makeText(ForgotPasswordActivity.this, "Password Updated successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isPasswordEqual(String Password1, String Password2) {
        return Password1.equals(Password2);

    }
}
