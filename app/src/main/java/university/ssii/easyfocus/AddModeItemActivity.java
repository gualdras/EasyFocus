package university.ssii.easyfocus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * Created by gualdras on 30/11/15.
 */
public class AddModeItemActivity extends AppCompatActivity{

    private String mTitle, mConnection, mAudio;

    private EditText mTitleText;
    private Spinner mConnectionSpinner, mAudioSpinner;
    private Button mSubmitButton, mResetButton;
    private RadioGroup mRadioGroup;
    private RadioButton mShakeRB, mUpDownRB, mLeftRB, mRigthRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_mode_item);

        mTitleText = (EditText) findViewById(R.id.title);
        mConnectionSpinner = (Spinner) findViewById(R.id.connection_spinner);
        mAudioSpinner = (Spinner) findViewById(R.id.audio_spinner);
        mSubmitButton = (Button) findViewById(R.id.submit_btn);
        mResetButton = (Button) findViewById(R.id.reset_btn);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMode();
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultValues();
            }
        });

        mRadioGroup = (RadioGroup) findViewById(R.id.activation_radiogroup);
        mShakeRB = (RadioButton) findViewById(R.id.shakeRB);
        mUpDownRB = (RadioButton) findViewById(R.id.upDownRB);
        mRigthRB = (RadioButton) findViewById(R.id.rightRB);
        mLeftRB = (RadioButton) findViewById(R.id.leftRB);
    }

    private void addMode(){
        mTitle = mTitleText.getText().toString();
        mConnection = mConnectionSpinner.getSelectedItem().toString();
        mAudio = mAudioSpinner.getSelectedItem().toString();
        int activationMode = Constants.DEFAULT_ID;

        int selectedID = mRadioGroup.getCheckedRadioButtonId();

        if(selectedID == mShakeRB.getId()) {
            activationMode = Constants.SHAKEPATTERN_ID;
        }else if(selectedID == mUpDownRB.getId()){
            activationMode = Constants.UPDOWNPATTERN_ID;
        }else if(selectedID == mLeftRB.getId()){
            activationMode = Constants.LEFTPATTERN_ID;
        }else if(selectedID == mRigthRB.getId()){
            activationMode = Constants.RIGHTPATTERN_ID;
        }


        ModeItem modeItem = new ModeItem(mTitle, mConnection, mAudio, activationMode);
        Intent data = new Intent();
        modeItem.packageIntent(data);

        setResult(RESULT_OK, data);
        finish();
    }

    //TODO
    private void setDefaultValues(){

    }

}
