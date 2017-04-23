package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import com.simair.android.androidutils.R;
import com.simair.android.androidutils.ui.ARadioGroup;

public class UIExamActivity extends AppCompatActivity implements ARadioGroup.CheckedChangeListener {

    private RadioButton radioApple;
    private RadioButton radioBanana;
    private RadioButton radioOrange;
    private RadioButton radioLemon;
    private ARadioGroup fruitRadioGroup;
    private RadioButton radioJava;
    private RadioButton radioCPP;
    private RadioButton radioAda;
    private ARadioGroup languageRadioGroup;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, UIExamActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiexam);
        initView();
    }

    private void initView() {
        initRadioGroup();
    }

    private void initRadioGroup() {
        radioApple = (RadioButton) findViewById(R.id.radioApple);
        radioBanana = (RadioButton) findViewById(R.id.radioBanana);
        radioOrange = (RadioButton) findViewById(R.id.radioOrange);
        radioLemon = (RadioButton) findViewById(R.id.radioLemon);

        fruitRadioGroup = new ARadioGroup(radioApple, radioBanana, radioLemon, radioOrange);
        fruitRadioGroup.setOnCheckedChangeListener(this);

        radioJava = (RadioButton) findViewById(R.id.radioJava);
        radioCPP = (RadioButton) findViewById(R.id.radioCPP);
        radioAda = (RadioButton) findViewById(R.id.radioAda);

        languageRadioGroup = new ARadioGroup(radioJava, radioCPP, radioAda);
        languageRadioGroup.check(radioCPP);
        languageRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(ARadioGroup radioGroup, RadioButton radioButton) {
        if(radioGroup == this.fruitRadioGroup) {
            Toast.makeText(this, "fruit checked : " + radioButton.toString(), Toast.LENGTH_SHORT).show();
        } else if(radioGroup == languageRadioGroup) {
            Toast.makeText(this, "language checked : " + radioButton.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
