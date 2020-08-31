package com.example.fyi5.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.fyi5.AppEnv;
import com.example.fyi5.HelpHelper;
import com.example.fyi5.R;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private ImageView mBackIcon;
    private RelativeLayout mSearchLayout;
    private RelativeLayout mCurrentLocationLayout;
    private RelativeLayout mSettingMenuLayout;
    private LinearLayout mSearchCoverLayout;
    private ImageView mOneKeyHelp;

    private boolean startOneKeyHelp = false;

    private HelpHelper mHelpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialUI();

        //实例化HelpHelper
        mHelpHelper = new HelpHelper(this);

        mSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch2SearchMode();
            }
        });

        mBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch2NormalMode();
            }
        });

        mOneKeyHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOneKeyHelp = !startOneKeyHelp;
                if (startOneKeyHelp) {
                    mHelpHelper.startOneKeyHelp();
                } else {
                    mHelpHelper.stopOneKeyHelp();
                }
            }
        });
    }

    private void switch2NormalMode() {
        //TODO:过于暴力，需要后期迭代修改
        Log.d(AppEnv.TAG, "switch2NormalMode");
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void switch2SearchMode() {
        Log.d(AppEnv.TAG, "switch2SearchMode");
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        mEditText.requestFocusFromTouch();
        InputMethodManager inputManager = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mEditText, 0);
        mBackIcon.setImageResource(R.mipmap.main_back_icon);
        mSearchCoverLayout.setVisibility(View.VISIBLE);
        mCurrentLocationLayout.setVisibility(View.GONE);
        mSettingMenuLayout.setVisibility(View.GONE);
    }

    private void initialUI() {
        mEditText = findViewById(R.id.main_search_edit);
        mBackIcon = findViewById(R.id.main_back_icon);
        mSearchLayout = findViewById(R.id.main_search_layout);
        mSearchCoverLayout = findViewById(R.id.main_search_cover_layout);
        mCurrentLocationLayout = findViewById(R.id.main_current_location);
        mSettingMenuLayout = findViewById(R.id.main_setting_menu);
        mOneKeyHelp = findViewById(R.id.main_one_key_help);
    }

}