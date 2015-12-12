package university.ssii.easyfocus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by gualdras on 30/11/15.
 */
public class AddModeItemActivity extends AppCompatActivity{

    private String mTitle, mConnection, mAudio;

    private EditText mTitleText;
    private Spinner mConnectionSpinner, mAudioSpinner;
    private Button mSubmitButton, mResetButton;

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
    }

    private void addMode(){
        mTitle = mTitleText.getText().toString();
        mConnection = mConnectionSpinner.getSelectedItem().toString();
        mAudio = mAudioSpinner.getSelectedItem().toString();

        ModeItem modeItem = new ModeItem(mTitle, mConnection, mAudio);
        Intent data = new Intent();
        modeItem.packageIntent(data);

        setResult(RESULT_OK, data);
        finish();
    }

    private void setDefaultValues(){

    }

}
