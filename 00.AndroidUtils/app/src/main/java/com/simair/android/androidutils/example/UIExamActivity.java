package com.simair.android.androidutils.example;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.simair.android.androidutils.R;
import com.simair.android.androidutils.ui.ARadioGroup;
import com.simair.android.androidutils.ui.ColorTextView;

public class UIExamActivity extends AppCompatActivity implements ARadioGroup.CheckedChangeListener, View.OnClickListener {

    private RadioButton radioApple;
    private RadioButton radioBanana;
    private RadioButton radioOrange;
    private RadioButton radioLemon;
    private ARadioGroup fruitRadioGroup;
    private RadioButton radioJava;
    private RadioButton radioCPP;
    private RadioButton radioAda;
    private ARadioGroup languageRadioGroup;
    private ColorTextView textColor;

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
        initStateButton();
        initColorTextView();
    }

    private void initColorTextView() {
        textColor = (ColorTextView)findViewById(R.id.textColor);

        textColor.setBGColor("ABC", Color.parseColor("#F5A9BC"));
        textColor.setBGColor("DEF", Color.parseColor("#E3CEF6"));
        textColor.setBGColor("GHI", Color.parseColor("#CECEF6"));
        textColor.setBGColor("JKL", Color.parseColor("#CEF6F5"));
        textColor.setBGColor("MNO", Color.parseColor("#CEF6D8"));
        textColor.setBGColor("PQR", Color.parseColor("#E3F6CE"));
        textColor.setBGColor("STU", Color.parseColor("#F6E3CE"));
        textColor.setBGColor("VWX", Color.parseColor("#0B3B39"));
        textColor.setBGColor("YZ", Color.parseColor("#F6CECE"));

        textColor.setTextColor("A", Color.parseColor("#FF0000"));
        textColor.setTextColor("B", Color.parseColor("#FF4000"));
        textColor.setTextColor("C", Color.parseColor("#FF8000"));
        textColor.setTextColor("D", Color.parseColor("#FFBF00"));
        textColor.setTextColor("E", Color.parseColor("#FFFF00"));
        textColor.setTextColor("F", Color.parseColor("#BFFF00"));
        textColor.setTextColor("G", Color.parseColor("#80FF00"));
        textColor.setTextColor("H", Color.parseColor("#40FF00"));
        textColor.setTextColor("I", Color.parseColor("#00FF00"));
        textColor.setTextColor("J", Color.parseColor("#00FF40"));
        textColor.setTextColor("K", Color.parseColor("#00FF80"));
        textColor.setTextColor("L", Color.parseColor("#00FFBF"));
        textColor.setTextColor("M", Color.parseColor("#00FFFF"));
        textColor.setTextColor("N", Color.parseColor("#00BFFF"));
        textColor.setTextColor("O", Color.parseColor("#0080FF"));
        textColor.setTextColor("P", Color.parseColor("#0080FF"));
        textColor.setTextColor("Q", Color.parseColor("#0040FF"));
        textColor.setTextColor("R", Color.parseColor("#0000FF"));
        textColor.setTextColor("S", Color.parseColor("#4000FF"));
        textColor.setTextColor("T", Color.parseColor("#8000FF"));
        textColor.setTextColor("U", Color.parseColor("#BF00FF"));
        textColor.setTextColor("V", Color.parseColor("#FF00FF"));
        textColor.setTextColor("W", Color.parseColor("#FF00BF"));
        textColor.setTextColor("X", Color.parseColor("#FF0080"));
        textColor.setTextColor("Y", Color.parseColor("#FF0040"));
        textColor.setTextColor("Z", Color.parseColor("#848484"));
    }

    private void initStateButton() {
        findViewById(R.id.btnState).setOnClickListener(this);
        findViewById(R.id.btnImageState).setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnState:
            case R.id.btnImageState:
                Toast.makeText(this, "button clicked ; " + view.toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
