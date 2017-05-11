package com.qinlei.coustomview5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qinlei.coustomview5.qqdrag.QQDrawActivity;
import com.qinlei.coustomview5.shopping.ShoppingActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click1(View view) {
        startActivity(new Intent(this, ShoppingActivity.class));
    }

    public void click2(View view) {
        startActivity(new Intent(this, QQDrawActivity.class));
    }
}
