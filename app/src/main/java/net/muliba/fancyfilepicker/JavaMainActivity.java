package net.muliba.fancyfilepicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.muliba.fancyfilepickerlibrary.FilePicker;

public class JavaMainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final  int FILE_PICKER_SINGLE = 1024;
    private Button classificationModeBtn;
    private TextView contentShowTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_main);

        contentShowTv = (TextView) findViewById(R.id.tv_java_main_content_show);
        classificationModeBtn = (Button) findViewById(R.id.btn_java_main_classification_mode);
        classificationModeBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.btn_java_main_classification_mode : {
                new FilePicker().withActivity(this)
                        .chooseType(FilePicker.CHOOSE_TYPE_SINGLE())
                        .requestCode(FILE_PICKER_SINGLE)
                        .start();
                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FILE_PICKER_SINGLE: {
                    String result = data.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY());
                    contentShowTv.setText(result);
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
