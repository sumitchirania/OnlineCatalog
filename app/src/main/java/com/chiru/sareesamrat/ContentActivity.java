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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textview;
    Button viewProfile, addItems, viewItems;
    String username, url, name, email, viewUrl;
    long contact;
    ProgressDialog pDialog;
    String TAG = ContentActivity.class.getSimpleName();
    Boolean seller, readStatus =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        username = getIntent().getExtras().getString("Username");
        setupSubviews();

    }

    private void setupSubviews() {
        textview = (TextView) findViewById(R.id.textViewWelcomeMsg);
        textview.setText("Welcome" + " " + getIntent().getExtras().getString("Username"));
        addItems = (Button) findViewById(R.id.buttonAddProduct);
        viewItems = (Button) findViewById(R.id.buttonViewProduct);
        viewProfile = (Button) findViewById(R.id.buttonViewProfile);

        viewProfile.setOnClickListener(this);
        addItems.setOnClickListener(this);
        viewItems.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonViewProfile: {
                new ViewProfile().execute();
                break;
            }
            case R.id.buttonAddProduct: {
                Intent intent = new Intent(getApplicationContext(),AddItems.class);
                intent.putExtra("Username", username);
                startActivity(intent);
                break;
            }
            case R.id.buttonViewProduct: {
                Intent intent = new Intent(getApplicationContext(), RecyclerViewActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
                break;
            }
        }
    }

    private class ViewProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ContentActivity.this);
            pDialog.setMessage("Fetching Profile...");
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
                    .appendPath("read")
                    .appendPath(username);

            viewUrl = builder.build().toString();
            String jsonStr = pwordhandler.makeServiceCall(viewUrl);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray detail = jsonObj.getJSONArray("data");
                    JSONObject c = detail.getJSONObject(0);
                    readStatus = jsonObj.getBoolean("success");
                    name = c.getString("name");
                    email = c.getString("email");
                    contact = c.getLong("contact_no");
                    seller = c.getBoolean("is_seller");

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

            if (readStatus) {
                Intent intent = new Intent(getApplicationContext(),ViewProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("usernameString", username);
                intent.putExtra("email", email);
                intent.putExtra("contact", contact);
                startActivity(intent);
            }else {
                Toast.makeText(ContentActivity.this, "Network Problem", Toast.LENGTH_SHORT).show();
            }
        }
    }
}






