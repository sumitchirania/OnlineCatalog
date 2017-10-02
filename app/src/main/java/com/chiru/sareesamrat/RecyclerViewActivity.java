package com.chiru.sareesamrat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    private List<Item> itemList = new ArrayList<>();
    private RecyclerView myRecycleView;
    private ItemsAdapter myItemAdapter;
    String username;
    ProgressDialog pDialog;
    private static String TAG = RecyclerViewActivity.class.getSimpleName();
    Boolean detailStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        username = getIntent().getExtras().getString("Username");

        setUpSubViews();
        new GetItems().execute();
    }

    private void setUpSubViews(){

        myRecycleView = (RecyclerView) findViewById(R.id.recyclerView);
        myItemAdapter = new ItemsAdapter(itemList,getApplicationContext());
    }


    private class GetItems extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecyclerViewActivity.this);
            pDialog.setMessage("Displaying Items. Please wait..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(Void... arg0) {
            HttpRequestHandler sh = new HttpRequestHandler();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("54.201.16.130")
                    .appendPath("items")
                    .appendPath("detail")
                    .appendQueryParameter("userName", username);

            String url = builder.build().toString();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    detailStatus = jsonObj.getBoolean("success");
                    
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            if(detailStatus){
                return (jsonStr);
            }
            else {
                return ("Failed");
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            try{
                JSONObject jsonObj = new JSONObject(result);
                JSONArray items = jsonObj.getJSONArray("data");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject c = items.getJSONObject(i);
                    Item item = new Item();
                    item.title = c.getString("title");
                    item.description = c.getString("description");
                    item.quantity = c.getString("quantity");
                    item.price = c.getString("price");
                    item.imageurl = c.getString("image_uri");
                    item.seller_id = c.getString("seller_id");

                    itemList.add(item);
                }

                myRecycleView.setLayoutManager(new LinearLayoutManager(RecyclerViewActivity.this));
                myRecycleView.setItemAnimator(new DefaultItemAnimator());
                myRecycleView.addItemDecoration(new DividerForItems(RecyclerViewActivity.this, LinearLayoutManager.VERTICAL));
                myRecycleView.setAdapter(myItemAdapter);

                myRecycleView.addOnItemTouchListener(new RecycleViewTouchListener(getApplicationContext(), myRecycleView, new RecycleViewTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Item item = itemList.get(position);
                        Intent intent = new Intent(getApplicationContext(),NewActivity.class);
                        intent.putExtra("title", item.getTitle());
                        intent.putExtra("description", item.getDescription());
                        intent.putExtra("price", item.getPrice());
                        intent.putExtra("quantity", item.getQuantity());
                        intent.putExtra("imageURL", item.getImageurl());
                        intent.putExtra("seller_id", item.getSeller_id());
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));



            }catch (JSONException e ){
                Toast.makeText(RecyclerViewActivity.this, e.toString(), Toast.LENGTH_LONG).show();

            }






        }


    }
}



