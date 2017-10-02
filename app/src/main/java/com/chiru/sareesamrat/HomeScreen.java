package com.chiru.sareesamrat;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener{
    TextView textView;
    Button buttonSeller, buttonCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        setUpSubViews();

        buttonSeller.setOnClickListener(this);
        buttonCustomer.setOnClickListener(this);

    }

    private void setUpSubViews(){
        textView = (TextView) findViewById(R.id.home_text);
        buttonSeller = (Button) findViewById(R.id.seller_button);
        buttonCustomer = (Button) findViewById(R.id.customer_button);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.seller_button: {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.customer_button: {
                Intent intent = new Intent(getApplicationContext(), RecyclerViewActivity.class);
                intent.putExtra("Username", "");
                startActivity(intent);
                finish();
                break;
            }
        }
    }
}
