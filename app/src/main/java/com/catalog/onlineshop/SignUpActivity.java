package com.catalog.onlineshop;
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

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editTextName, editTextUserName, editTextPassword, editTextRePassword, editTextEmail, editTextContact;
    Button userSignUp;
    private ProgressDialog pDialog;
    private String TAG = SignUpActivity.class.getSimpleName();
    String name, username, password, rePassword, email, contact, message = "Network Problem. Try again later.";
    Boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setUpSubviews();

        userSignUp.setOnClickListener(this);
    }

    private void setUpSubviews() {

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextRePassword = (EditText) findViewById(R.id.editTextRePassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextContact = (EditText) findViewById(R.id.editTextContact);
        userSignUp = (Button) findViewById(R.id.userSignUpButton);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.userSignUpButton:

                name = editTextName.getText().toString();
                username = editTextUserName.getText().toString();
                password = editTextPassword.getText().toString();
                rePassword = editTextRePassword.getText().toString();
                email = editTextEmail.getText().toString();
                contact = editTextContact.getText().toString();

                validateFields();
        }
    }

    private void validateFields(){

        if (name.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please Enter a Name", Toast.LENGTH_LONG).show();

        } else if (username.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Username required", Toast.LENGTH_LONG).show();

        } else if (email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Email required", Toast.LENGTH_LONG).show();

        }else if (!isValidEmail(email)) {
            Toast.makeText(SignUpActivity.this, "Enter a valid Email", Toast.LENGTH_LONG).show();

        }else if (password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please Choose a Password", Toast.LENGTH_LONG).show();

        } else if (rePassword.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please Enter the same Password again", Toast.LENGTH_LONG).show();

        } else if (!isPasswordEqual(password, rePassword)) {
            Toast.makeText(SignUpActivity.this, "Password doesn't match.", Toast.LENGTH_LONG).show();

        } else if (contact.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Contact required if you are a Seller", Toast.LENGTH_LONG).show();

        } else if (!isValidContact(contact)) {
            Toast.makeText(SignUpActivity.this, "Enter a valid Contact (6-13 digits)", Toast.LENGTH_LONG).show();

        } else {
            new checkAvailability().execute();
        }
    }

    private class checkAvailability extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setMessage("Checking Username...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("users")
                    .appendPath("check")
                    .appendQueryParameter("user_name", username);

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
                //Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                userNameValid(true);
            } else {
                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
                userNameValid(false);
            }
        }
    }

    private class RegisterNewUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setMessage("Registering...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("users")
                    .appendPath("create")
                    .appendPath("")
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("user_name", username)
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("contact_no", contact)
                    .appendQueryParameter("password", password)
                    .appendQueryParameter("is_seller", "True");
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
                Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isPasswordEqual(String Password1, String Password2) {
        return Password1.equals(Password2);
    }

    private boolean isValidEmail(String email){

        final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+");
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    private boolean isValidContact(String contact){
        Pattern CONTACT_NO_PATTERN = Pattern.compile("[0-9]{6,13}");
        return CONTACT_NO_PATTERN.matcher(contact).matches();
    }

    private void userNameValid(Boolean isUserNameValid) {

        if(isUserNameValid){
            new RegisterNewUser().execute();
        }
    }
}