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

    public final static String WIFI = "WiFi";
    public final static String DATA = "Data";
    public final static String NO_CONNECION = "No Connection";
    public final static String RING = "Ring";
    public final static String MUTE = "Mute";
    public final static String VIBRATE = "Vibrate";
    public final static String LOUDEST = "Loudest";

    private String mTitle, mConnection, mAudio;

    public ModeItem(String mTitle, String mConnection, String mAudio) {
        this.mTitle = mTitle;
        this.mConnection = mConnection;
        this.mAudio = mAudio;
    }

    public ModeItem(Intent intent) {
        mTitle = intent.getStringExtra(TITLE);
        mConnection = intent.getStringExtra(CONNECTION);
        mAudio = intent.getStringExtra(AUDIO);
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
    }

    @Override
    public String toString() {
        return mTitle + ITEM_SEP + mAudio + ITEM_SEP + mConnection;
    }
}
