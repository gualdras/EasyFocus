package university.ssii.easyfocus;

import android.content.Intent;

/**
 * Created by gualdras on 30/11/15.
 */
public class ModeItem {

    public static final String ITEM_SEP = System.getProperty("line.separator");

    public final static String TITLE = "title";
    public final static String CONNECTION = "connection";
    public final static String AUDIO = "audio";
    public final static String ACTIVATION = "activation";
    public final static String ACTIVE ="active";
    public final static String FIRSTTIME = "firsttime";

    public final static String WIFI = "WiFi";
    public final static String DATA = "Data";
    public final static String NO_CONNECION = "No Connection";
    public final static String RING = "Ring";
    public final static String MUTE = "Mute";
    public final static String VIBRATE = "Vibrate";
    public final static String LOUDEST = "Loudest";

    private String mTitle, mConnection, mAudio;
    private int mActivationMode;
    
    private boolean mActive, mFirstTime;

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean mActive) {
        this.mActive = mActive;
    }

    public boolean isFirstTime() {
        return mFirstTime;
    }

    public void setFirstTime(boolean mFirstTime) {
        this.mFirstTime = mFirstTime;
    }

    public int getmActivationMode() {
        return mActivationMode;
    }

    public void setmActivationMode(int mActivationMode) {
        this.mActivationMode = mActivationMode;
    }

    public ModeItem(String mTitle, String mConnection, String mAudio, int mActivationMode, boolean mActive, boolean mFirstTime) {
        this.mTitle = mTitle;
        this.mConnection = mConnection;
        this.mAudio = mAudio;
        this.mActivationMode = mActivationMode;
        this.mFirstTime = mFirstTime;
        this.mActive = mActive;
    }

    public ModeItem(String mTitle, String mConnection, String mAudio, int mActivationMode) {
        this.mTitle = mTitle;
        this.mConnection = mConnection;
        this.mAudio = mAudio;
        this.mActivationMode = mActivationMode;
        this.mActive = false;
        this.mFirstTime = true;
    }

    public ModeItem(Intent intent) {
        mTitle = intent.getStringExtra(TITLE);
        mConnection = intent.getStringExtra(CONNECTION);
        mAudio = intent.getStringExtra(AUDIO);
        mActivationMode = intent.getIntExtra(ACTIVATION, 0);
        mActive = intent.getBooleanExtra(ACTIVE, false);
        mFirstTime = intent.getBooleanExtra(FIRSTTIME, false);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getConnection() {
        return mConnection;
    }

    public void setConnection(String mConnection) {
        this.mConnection = mConnection;
    }

    public String getAudio() {
        return mAudio;
    }

    public void setAudio(String mAudio) {
        this.mAudio = mAudio;
    }

    public void packageIntent(Intent intent){
        intent.putExtra(TITLE, mTitle);
        intent.putExtra(CONNECTION, mConnection);
        intent.putExtra(AUDIO, mAudio);
        intent.putExtra(ACTIVATION, mActivationMode);
        intent.putExtra(ACTIVE, mActive);
        intent.putExtra(FIRSTTIME, mFirstTime);
    }

    @Override
    public String toString() {
        return mTitle + ITEM_SEP + mAudio + ITEM_SEP + mConnection + ITEM_SEP + mActivationMode + ITEM_SEP + mActive + ITEM_SEP + mFirstTime;
    }
}
