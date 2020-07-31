package com.xiaopo.flying.stickerview.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import com.xiaopo.flying.stickerview.MainActivity;
import com.xiaopo.flying.stickerview.R;

public class TextContrastChooseActivity extends AppCompatActivity {

    RadioButton btn_all_text_color_contrast,btn_only_black_and_white_text_color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_color_contrast);

        btn_all_text_color_contrast= (RadioButton) findViewById(R.id.all_text_contrast_color);
        btn_only_black_and_white_text_color= (RadioButton) findViewById(R.id.only_black_and_white_text_color);
        btn_all_text_color_contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent1 = new Intent(TextContrastChooseActivity.this, MainActivity.class);
                    intent1.putExtra("text contrast", 1);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent1);

            }
        });
        btn_only_black_and_white_text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TextContrastChooseActivity.this, MainActivity.class);
                intent1.putExtra("text contrast", 2);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1);

            }
        });
    }
}
