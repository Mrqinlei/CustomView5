package com.qinlei.coustomview5.qqdrag;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.qinlei.coustomview5.R;

public class QQDrawActivity extends AppCompatActivity {
    private QQDragView qqDragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqdraw);
        qqDragView = (QQDragView) findViewById(R.id.qq_drag_view);
    }

    public void click(View view) {
        qqDragView.reset();
    }
}
