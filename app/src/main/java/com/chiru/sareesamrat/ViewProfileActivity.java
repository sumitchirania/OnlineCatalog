package com.chiru.sareesamrat;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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

import java.util.regex.Pattern;

public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editTextName, editTextUserName, editTextEmail, editTextContact;
    Button deleteButton, updateButton, logoutButton;
    ProgressDialog pDialog;
    String username, name, email, contact, url, jsonResponse, message = "Network Problem. Try again later";
    private static String TAG = ViewProfileActivity.class.getSimpleName();
    Boolean updateStatus, deleteStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        username = getIntent().getExtras().getString("usernameString");
        SetupSubviews();
        populateSubViews();

        updateButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    private void SetupSubviews() {

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextContact = (EditText) findViewById(R.id.editTextContact);

        deleteButton = (Button) findViewById(R.id.buttonDeleteProfile);
        updateButton = (Button) findViewById(R.id.buttonUpdateProfile);
        logoutButton = (Button) findViewById(R.id.buttonLogout);
    }

    private void populateSubViews(){

        editTextName.setText(getIntent().getExtras().getString("name"));
        editTextName.setSelection(editTextName.getText().length());
        editTextUserName.setText(getIntent().getExtras().getString("usernameString"));
        editTextEmail.setText(getIntent().getExtras().getString("email"));
        editTextEmail.setSelection(editTextEmail.getText().length());
        editTextContact.setText(getIntent().getExtras().getLong("contact")+"");
        editTextContact.setSelection(editTextContact.getText().length());
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonDeleteProfile: {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewProfileActivity.this);
                alertDialog.setTitle("Confirm To Delete");

                alertDialog.setMessage("Confirm Deteting your Account??");

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        new DeleteProfile().execute();
                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
                break;
            }

            case R.id.buttonUpdateProfile: {

                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                contact = editTextContact.getText().toString();
                if (!isValidEmail(email)) {
                    Toast.makeText(ViewProfileActivity.this, "Enter a valid Email", Toast.LENGTH_LONG).show();

                }else if(!isValidContact(contact)) {
                    Toast.makeText(ViewProfileActivity.this, "Enter a Valid Contact", Toast.LENGTH_LONG).show();

                }else
                    new UpdateProfile().execute();

                break;
            }

            case R.id.buttonLogout: {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewProfileActivity.this);
                alertDialog.setTitle("Confirm To Logout");
                alertDialog.setMessage("want to logout..???");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        }
    }

    private class DeleteProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewProfileActivity.this);
            pDialog.setMessage("Deleting Profile...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequestHandler pwordhandler = new HttpRequestHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("users")
                    .appendPath("delete")
                    .appendPath(username);

            url = builder.build().toString();
            String jsonStr = pwordhandler.makeServiceCall(url);
            jsonResponse = jsonStr;

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    deleteStatus = jsonObj.getBoolean("success");
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

            if (deleteStatus) {
                Toast.makeText(ViewProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(intent);

            } else {
                Toast.makeText(ViewProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewProfileActivity.this);
            pDialog.setMessage("Updating Profile...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequestHandler pwordhandler = new HttpRequestHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("users")
                    .appendPath("update")
                    .appendPath(username)
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("email",email)
                    .appendQueryParameter("contact_no",contact);

            String url = builder.build().toString();
            String jsonStr = pwordhandler.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    updateStatus = jsonObj.getBoolean("success");

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (updateStatus) {
                Toast.makeText(getApplicationContext(),"Profile Successfully Updated", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),ContentActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            }
        }


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
}
