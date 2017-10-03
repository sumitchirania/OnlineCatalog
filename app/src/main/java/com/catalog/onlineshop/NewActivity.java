package com.catalog.onlineshop;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class NewActivity extends AppCompatActivity implements View.OnClickListener {


    TextView textView1, textView2, textView3, textView4, textView5, textView6;
    ImageButton imageButton1, imageButton2;
    ImageView imageView;
    String seller_id, imageUri, userName, itemTitle, sellerName, sellerContact, message = "Network Problem",
            description, price, quantity;
    ProgressDialog pDialog;
    public static String TAG = NewActivity.class.getSimpleName();
    Boolean deleteStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        getFieldValues();
        setUpSubViews();
        activateImageButtons();
    }

    private void getFieldValues() {

        seller_id = getIntent().getExtras().getString("seller_id");
        imageUri = getIntent().getExtras().getString("imageURL");
        userName = getIntent().getExtras().getString("username");
        itemTitle = getIntent().getExtras().getString("title");
        description = getIntent().getExtras().getString("description");
        price = getIntent().getExtras().getString("price");
        quantity = getIntent().getExtras().getString("quantity");
        imageUri = "http://54.201.16.130" + imageUri;

        new getSellerDetail().execute();
    }

    private void setUpSubViews() {


        textView1 = (TextView) findViewById(R.id.fullView1);
        textView2 = (TextView) findViewById(R.id.fullView2);
        textView3 = (TextView) findViewById(R.id.fullView3);
        textView4 = (TextView) findViewById(R.id.fullView4);
        textView5 = (TextView) findViewById(R.id.fullView5);
        textView6 = (TextView) findViewById(R.id.fullView6);

        imageButton1 = (ImageButton) findViewById(R.id.buttonEditProduct);
        imageButton2 = (ImageButton) findViewById(R.id.buttonDeleteProduct);

        imageView = (ImageView) findViewById(R.id.fullViewImage);
    }

    private void activateImageButtons() {

        if (userName != null && !userName.isEmpty()) {
            imageButton1.setVisibility(View.VISIBLE);
            imageButton2.setVisibility(View.VISIBLE);
            imageButton1.setOnClickListener(this);
            imageButton2.setOnClickListener(this);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.buttonEditProduct:

                Intent intent = new Intent(getApplicationContext(), EditContent.class);
                intent.putExtra("title", itemTitle);
                intent.putExtra("description", description);
                intent.putExtra("price", price);
                intent.putExtra("quantity", quantity);
                intent.putExtra("imageURI", imageUri);
                intent.putExtra("Username", userName);
                startActivity(intent);
                break;


            case R.id.buttonDeleteProduct:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewActivity.this);
                alertDialog.setTitle("Confirm To Delete");

                alertDialog.setMessage("Are you sure to delete this product permanently??");

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        new DeleteProduct().execute();
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
    }

    private class getSellerDetail extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewActivity.this);
            pDialog.setMessage("Loading...");
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
                    .appendPath("get")
                    .appendPath(seller_id);
            ;
            String url = builder.build().toString();
            String jsonStr = pwordhandler.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    sellerName = jsonObj.getString("name");
                    sellerContact = jsonObj.getString("contact");


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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

            textView1.setText(itemTitle);
            textView2.setText(description);
            textView3.setText("Rs. " + price);
            textView4.setText(quantity + " pieces available");
            textView5.setText("Listed by : " + sellerName);
            textView6.setText("Seller Contact : " + sellerContact);

            Picasso.with(NewActivity.this).load(imageUri).placeholder(R.drawable.add_image).fit().into(imageView);
        }
    }

    private class DeleteProduct extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewActivity.this);
            pDialog.setMessage("Deleting Product...");
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
                    .appendPath(userName)
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
                Toast.makeText(getApplicationContext(), "Item Deleted Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
                intent.putExtra("Username", userName);
                startActivity(intent);
            } else
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        }
    }
}
