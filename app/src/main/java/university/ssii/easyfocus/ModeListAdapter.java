package university.ssii.easyfocus;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by gualdras on 30/11/15.
 */
public class ModeListAdapter extends BaseAdapter {

    private static final long INITIAL_ALARM_DELAY = 5 * 1000L;
    private static final long DELAY_BETWEEN_ALARM = INITIAL_ALARM_DELAY * 3;
    private static LayoutInflater mInflater = null;
    private final ArrayList<ModeItem> mItems = new ArrayList<ModeItem>();
    private final Context mContext;
    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;


    public ModeListAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(mContext,
                RemindUserToFocusReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                mContext, 0, mNotificationReceiverIntent, 0);
    }


    public void update(ListView list){
        int start = list.getFirstVisiblePosition();

        for(ModeItem item: mItems){
            for(int i=start, j=list.getLastVisiblePosition();i<=j;i++)
                if(item==list.getItemAtPosition(i)){
                    View view = list.getChildAt(i-start);
                    list.getAdapter().getView(i, view, list);
                }
        }
    }

    public void add(ModeItem item) {
        mItems.add(item);
        CheckPattern.addAvailablePattern(item.getmActivationMode());
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        CheckPattern.removeAvailablePatterns(mItems.get(pos).getmActivationMode());
        mItems.remove(pos);
        mAlarmManager.cancel(mNotificationReceiverPendingIntent);
        notifyDataSetChanged();
    }

    public void changeSwitchState(int activationMode, ListView list){
        for(ModeItem item: mItems){
            if(item.getmActivationMode() == activationMode){
                item.setActive(!item.isActive());
                changeSwitchState(item.isActive(), item);
                item.setFirstTime(false);

                int start = list.getFirstVisiblePosition();
                for(int i=start, j=list.getLastVisiblePosition();i<=j;i++)
                    if(item==list.getItemAtPosition(i)){
                        View view = list.getChildAt(i-start);
                        list.getAdapter().getView(i, view, list);
                        break;
                    }

            }
        }
    }

    private void changeSwitchState(boolean isChecked, ModeItem curr){
        if (isChecked) {
            WifiManager wifi;
            final Class conmanClass;
            final ConnectivityManager conman = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                    DELAY_BETWEEN_ALARM,
                    mNotificationReceiverPendingIntent);

            switch (curr.getConnection()) {

                case ModeItem.WIFI:
                    //Enable wifi
                    wifi = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
                    wifi.setWifiEnabled(true);

                    //Disable data
                    try {
                        conmanClass = Class.forName(conman.getClass().getName());
                        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                        iConnectivityManagerField.setAccessible(true);
                        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
                        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                        setMobileDataEnabledMethod.setAccessible(true);

                        setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ModeItem.DATA:
                    //Enable data
                    try {
                        conmanClass = Class.forName(conman.getClass().getName());
                        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                        iConnectivityManagerField.setAccessible(true);
                        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
                        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                        setMobileDataEnabledMethod.setAccessible(true);

                        setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Turn off Wifi
                    wifi = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
                    wifi.setWifiEnabled(false);

                    break;
                case ModeItem.NO_CONNECION:
                    try {
                        conmanClass = Class.forName(conman.getClass().getName());
                        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                        iConnectivityManagerField.setAccessible(true);
                        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                        final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
                        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                        setMobileDataEnabledMethod.setAccessible(true);

                        setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Turn off Wifi
                    wifi = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
                    wifi.setWifiEnabled(false);

                    break;

            }

            switch (curr.getAudio()) {

                case ModeItem.MUTE:
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    break;

                case ModeItem.VIBRATE:
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;

                case ModeItem.RING:
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;

                case ModeItem.LOUDEST:
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    for(int i=0; i<20; i++){
                        mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_ALLOW_RINGER_MODES);
                    }
                    break;
            }

        } else {
            mAlarmManager.cancel(mNotificationReceiverPendingIntent);
        }
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View newView = convertView;
        final ViewHolder holder;

        final ModeItem curr = (ModeItem) getItem(position);

        if (null == convertView) {
            holder = new ViewHolder();
            newView = mInflater
                    .inflate(R.layout.mode_item, parent, false);
            holder.title = (TextView) newView.findViewById(R.id.title);
            holder.audio = (TextView) newView.findViewById(R.id.audio_profile);
            holder.connection = (TextView) newView.findViewById(R.id.connection_profile);
            holder.onoff = (Switch) newView.findViewById(R.id.onoff);
            holder.onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(curr.isActive() != isChecked){
                        changeSwitchState(isChecked, curr);
                        curr.setFirstTime(false);
                        curr.setActive(isChecked);
                    }
                }
            });
            holder.delete = (ImageView) newView.findViewById(R.id.delete);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(position);
                }
            });

            newView.setTag(holder);

        } else {
            holder = (ViewHolder) newView.getTag();
        }

        if(curr.isActive() != holder.onoff.isChecked()){
            holder.onoff.setChecked(curr.isActive());
        }

        holder.title.setText(curr.getTitle());
        holder.audio.setText(curr.getAudio());
        holder.connection.setText(curr.getConnection());


        if(!MainActivity.isRunning){
            saveItems();
        }
        return newView;

    }

    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = mContext.openFileOutput(Constants.FILE_NAME, mContext.MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < getCount(); idx++) {

                writer.println(getItem(idx));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }


    static class ViewHolder {

        TextView title;
        TextView audio;
        TextView connection;
        Switch onoff;
        ImageView delete;
    }


}
