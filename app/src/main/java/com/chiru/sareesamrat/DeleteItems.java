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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class DeleteItems extends AppCompatActivity {

    EditText editText;
    Button button;
    ProgressDialog pDialog;
    private static String TAG = DeleteItems.class.getSimpleName();
    String username, itemTitle,message = "Network Problem";
    Boolean deleteStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_items);

        setUpSubViews();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemTitle = editText.getText().toString();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DeleteItems.this);
                alertDialog.setTitle("Confirm To Delete");

                alertDialog.setMessage("Are you sure you want to delete this Item??");

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        new DeleteSelectedItem().execute();
                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();


            }
        });

    }
    private void setUpSubViews(){

        editText = (EditText) findViewById(R.id.edittexttodeleteitemfinal);
        button = (Button) findViewById(R.id.finaldeletebutton);

        username = getIntent().getExtras().getString("Username");


    }

    private class DeleteSelectedItem extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DeleteItems.this);
            pDialog.setMessage("Deleting Item...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequestHandler pwordhandler = new HttpRequestHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("items")
                    .appendPath("delete")
                    .appendPath(username)
                    .appendPath(itemTitle);
            ;

            String url = builder.build().toString();


            String jsonStr = pwordhandler.makeServiceCall(url);

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
                Toast.makeText(getApplicationContext(),"Item Deleted Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),ContentActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            }
            else{
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),ContentActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            }
        }


    }
}
