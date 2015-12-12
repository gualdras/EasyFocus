package university.ssii.easyfocus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final String FILE_NAME = "ModeItemData.txt";

    private static final int ADD_MODE_ITEM_REQUEST = 0;
    ModeListAdapter mAdapter;
    ImageButton mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ModeListAdapter(getApplicationContext());
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.mode_list);

        listView.setAdapter(mAdapter);

        mAddButton = (ImageButton) findViewById(R.id.add_mode_btn);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddMode();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        saveItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter.getCount() == 0){
            loadItems();
        }
    }

    public void startAddMode(){
        Intent addModeIntent = new Intent(MainActivity.this, AddModeItemActivity.class);
        startActivityForResult(addModeIntent, ADD_MODE_ITEM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_MODE_ITEM_REQUEST && resultCode == RESULT_OK){
            ModeItem resultModeItem = new ModeItem(data);
            mAdapter.add(resultModeItem);
        }
    }

    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < mAdapter.getCount(); idx++) {

                writer.println(mAdapter.getItem(idx));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String title = null;
            String audio = null;
            String connection = null;

            while (null != (title = reader.readLine())) {
                audio = reader.readLine();
                connection = reader.readLine();
                mAdapter.add(new ModeItem(title, connection, audio));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
