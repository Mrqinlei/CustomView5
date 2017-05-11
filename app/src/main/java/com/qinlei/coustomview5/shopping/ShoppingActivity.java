package com.qinlei.coustomview5.shopping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.qinlei.coustomview5.R;

public class ShoppingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gou_wu_che);
    }

    public void click(View view) {
        ShoppingView gouWuCheView = (ShoppingView) view;
        gouWuCheView.start();
    }
}
