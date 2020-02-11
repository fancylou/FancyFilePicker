package net.muliba.fancyfilepicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.muliba.fancyfilepickerlibrary.FilePicker;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class JavaMainActivity extends AppCompatActivity implements View.OnClickListener {


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
                        .forResult(new Function1<List<String>, Unit>() {
                            @Override
                            public Unit invoke(List<String> strings) {
                                if (!strings.isEmpty()) {
                                    contentShowTv.setText(strings.get(0));
                                }
                                return Unit.INSTANCE;
                            }
                        });
                break;
            }
        }
    }

}
