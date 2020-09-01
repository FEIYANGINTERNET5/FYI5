package com.example.fyi5.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fyi5.R;

public class CommentActivity extends AppCompatActivity {

    private InputTextMsgDialog inputTextMsgDialog;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.input_text_msg_dialog);

        setContentView(R.layout.activity_comment);

        button = findViewById(R.id.bt_test_comment);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTextMsgDialog.show();
            }
        });

        inputTextMsgDialog = new InputTextMsgDialog(CommentActivity.this, R.style.dialog_center);
        inputTextMsgDialog.setmOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {
            @Override
            public void onTextSend(String msg) {
                //点击发送按钮后，回调此方法，msg为输入的值
            }
        });
    }
}