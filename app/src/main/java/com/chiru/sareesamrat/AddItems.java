package com.chiru.sareesamrat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

public class AddItems extends AppCompatActivity implements View.OnClickListener {

    EditText editTextTitle, editTextDescription, editTextQuantity, editTextPrice;
    ImageButton imageButton;
    Button buttonAddProduct;
    private static int RESULT_LOAD_IMAGE = 1;
    ProgressDialog pDialog;
    String username, title, description, price, quantity, imageString, pic_uri = "/static/images/default.jpg";
    String message = "Network Problem. Try again later.";
    private static String TAG = AddItems.class.getSimpleName();
    Boolean addStatus = false, success = false, imageStatus = false;
    public static String URL = "http://54.201.16.130/saveimage/";
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        username = getIntent().getExtras().getString("Username");
        SetupSubViews();

        imageButton.setOnClickListener(this);
        buttonAddProduct.setOnClickListener(this);
    }

    private void SetupSubViews() {

        editTextTitle = (EditText) findViewById(R.id.editText_title);
        editTextDescription = (EditText) findViewById(R.id.editText_description);
        editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        editTextPrice = (EditText) findViewById(R.id.editText_price);

        imageButton = (ImageButton) findViewById(R.id.imageButton);

        buttonAddProduct = (Button) findViewById(R.id.finalAddButton);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton: {
                imageBrowse();
                break;
            }
            case R.id.finalAddButton: {

                title = editTextTitle.getText().toString();
                description = editTextDescription.getText().toString();
                quantity = editTextQuantity.getText().toString();
                price = editTextPrice.getText().toString();

                validateFields();
            }
        }
    }

    private void validateFields(){

        if(title.isEmpty()||description.isEmpty()||quantity.isEmpty()||price.isEmpty()){
            Toast.makeText(AddItems.this, "All Fields are required.Don't leave it blank", Toast.LENGTH_SHORT).show();

        }else if(!isValidTitle(title)){
            Toast.makeText(AddItems.this, "Enter a valid Title", Toast.LENGTH_SHORT).show();

        }else if(!isValidQuantity(quantity)){
            Toast.makeText(AddItems.this, "Enter a valid Quantity", Toast.LENGTH_SHORT).show();

        }else if(!isValidPrice(price)){
            Toast.makeText(AddItems.this, "Enter a valid Price", Toast.LENGTH_SHORT).show();

        }else {
            new AddNewItems().execute();
        }
    }

    private void imageBrowse(){

        final CharSequence[] items = { "Take Photo", "Choose from Device"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItems.this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intentCamera, 0);

                } else if (items[item].equals("Choose from Device")) {
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentGallery, 1);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK && null!= data) {
                    //Uri imagePath = data.getData();
                    //imageButton.setImageURI(imagePath);
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageButton.setImageBitmap(photo);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK && null!= data) {
                    Uri imagePath = data.getData();
                    imageButton.setImageURI(imagePath);

                }
                break;
        }
        uploadImage();
    }

    public String getStringForImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] image = byteArrayOutputStream.toByteArray();
        imageString = Base64.encodeToString(image,Base64.DEFAULT);
        return imageString;
    }




   private void uploadImage() {

       bitmap = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
       StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String s) {
                       try {
                           JSONObject jsonObject = new JSONObject(s);
                           success = jsonObject.getBoolean("success");
                           pic_uri = jsonObject.getString("url");
                           if (success) {
                               imageUploaded(true);
                           }
                           else if(!success){
                               Toast.makeText(AddItems.this, "Image Uploading failed.", Toast.LENGTH_LONG).show();
                           }

                       } catch (JSONException e) {

                       }
                   }
               },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError volleyError) {
                       Toast.makeText(AddItems.this, "Network Error", Toast.LENGTH_LONG).show();
                   }
               }) {
           @Override
           protected Map<String, String> getParams() throws AuthFailureError {

               String image = getStringForImage(bitmap);
               String imageName = editTextTitle.getText().toString();
               String user = username;
               Map<String, String> params = new Hashtable<>();

               params.put("image", image);
               params.put("title", imageName);
               params.put("username", user);

               return params;
           }
       };
       RequestQueue requestQueue = Volley.newRequestQueue(this);
       requestQueue.add(stringRequest);
   }

   private void imageUploaded(boolean imageUploadStatus){

       if(imageUploadStatus){
           buttonAddProduct.setVisibility(View.VISIBLE);
           buttonAddProduct.setClickable(true);
           buttonAddProduct.setEnabled(true);
       }
   }

   private class AddNewItems extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(AddItems.this);
            pDialog.setMessage("Adding Items...");
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
                    .appendPath("add")
                    .appendPath(username)
                    .appendPath("")
                    .appendQueryParameter("title", title)
                    .appendQueryParameter("description", description)
                    .appendQueryParameter("price", price)
                    .appendQueryParameter("quantity", quantity)
                    .appendQueryParameter("image_uri", pic_uri);

            String url = builder.build().toString();
            String jsonStr = pwordhandler.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    addStatus = jsonObj.getBoolean("success");
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

            if (addStatus) {
                Toast.makeText(getApplicationContext(), "Item added Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
                finish();

            } else if (!addStatus) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
                //intent.putExtra("Username", username);
                //startActivity(intent);
            }
        }
   }

   private boolean isValidQuantity(String quantity){
        Pattern QUANTITY_PATTERN = Pattern.compile("^[1-9][0-9]{0,4}");
        return QUANTITY_PATTERN.matcher(quantity).matches();
    }

    private boolean isValidPrice(String price){
        Pattern PRICE_PATTERN = Pattern.compile("(^[1-9][0-9]{1,6}[\\.]?[0-9]*)");
        return PRICE_PATTERN.matcher(price).matches();
    }

    private boolean isValidTitle(String title){
        Pattern TITLE_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9\\-\\.@&]+");
        return TITLE_PATTERN.matcher(title).matches();
    }
}







