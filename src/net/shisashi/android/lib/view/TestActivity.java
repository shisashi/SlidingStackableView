package net.shisashi.android.lib.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TestActivity extends Activity {
    private SlidingStackableView slidingStackableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        slidingStackableView = (SlidingStackableView) findViewById(R.id.ssv);

        findViewById(R.id.menuButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menuButtonClicked();
            }
        });

        final List<String> dummyData = new ArrayList<String>(30);
        for (int i = 0; i < 30; i++) {
            dummyData.add("Hello " + i);
        }

        ListView menuList = (ListView) findViewById(R.id.menu);
        menuList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dummyData));
        menuList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) findViewById(R.id.text)).setText(dummyData.get(position));
                slidingStackableView.hideMenu();
            }
        });
    }

    private void menuButtonClicked() {
        slidingStackableView.toggleMenu();
    }
}
