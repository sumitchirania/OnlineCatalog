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

public class EditContent extends AppCompatActivity implements View.OnClickListener{

    EditText editTextTitle, editTextDescription, editTextQuantity, editTextPrice;
    String username, description, price, quantity, imageURI, title, message = "Network problem";
    Button finalUpdate;
    ProgressDialog pDialog;
    private static String TAG = EditContent.class.getSimpleName();
    Boolean editStatus = false, success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);

        username = getIntent().getExtras().getString("Username");
        setUpSubViews();
        getFieldValues();
        populateViews();

        finalUpdate.setOnClickListener(this);
    }

    private void setUpSubViews() {

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);

        finalUpdate = (Button) findViewById(R.id.buttonUpdateProduct);
    }

    private void getFieldValues(){

        title = getIntent().getExtras().getString("title");
        description = getIntent().getExtras().getString("description");
        quantity = getIntent().getExtras().getString("quantity");
        price = getIntent().getExtras().getString("price");
        imageURI = getIntent().getExtras().getString("imageURI");
    }

    private void populateViews(){

        editTextTitle.setText(title);
        editTextDescription.setText(description);
        editTextDescription.setSelection(description.length());
        editTextQuantity.setText(quantity);
        editTextQuantity.setSelection(quantity.length());
        editTextPrice.setText(price);
        editTextPrice.setSelection(price.length());
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.buttonUpdateProduct:

                description = editTextDescription.getText().toString();
                price = editTextPrice.getText().toString();
                quantity = editTextQuantity.getText().toString();
                title = editTextTitle.getText().toString();

                if (quantity.isEmpty() || price.isEmpty() || description.isEmpty()) {
                    Toast.makeText(EditContent.this, "Fill in everything Please..!!", Toast.LENGTH_SHORT).show();

                } else if (!isValidQuantity(quantity)) {
                    Toast.makeText(EditContent.this, "Enter a valid Quantity", Toast.LENGTH_SHORT).show();

                } else if (!isValidPrice(price)) {
                    Toast.makeText(EditContent.this, "Enter a valid Price", Toast.LENGTH_SHORT).show();

                } else {
                    new UpdateContent().execute();
                }
        }
    }
    private class UpdateContent extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditContent.this);
            pDialog.setMessage("Updating Profile...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("items")
                    .appendPath("edit")
                    .appendPath(username)
                    .appendPath(title)
                    .appendPath("")
                    .appendQueryParameter("description", description)
                    .appendQueryParameter("price", price)
                    .appendQueryParameter("image_uri", imageURI)
                    .appendQueryParameter("quantity", quantity);

            String url = builder.build().toString();
            String jsonStr = httpRequestHandler.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    editStatus = jsonObj.getBoolean("success");
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

            if (editStatus) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            }
        }
    }

    private boolean isValidQuantity(String quantity) {
        Pattern QUANTITY_PATTERN = Pattern.compile("^[1-9][0-9]{0,4}");
        return QUANTITY_PATTERN.matcher(quantity).matches();
    }

    private boolean isValidPrice(String price) {
        Pattern PRICE_PATTERN = Pattern.compile("(^[1-9][0-9]{1,6}[\\.]?[0-9]*)");
        return PRICE_PATTERN.matcher(price).matches();
    }
}


