package whatisnext.ritesh.com.whatisnext;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NerdStat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nerd_stat);
        setTitle("Stats For Nerds");
        String[] strs = {"completed", "skipped", "speechTried" , "hintsSeen"};
        String[] names = {"Completed", "Skipped", "Number of Tries", "Number of hints"};
        ArrayAdapter<String> ad= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        int ind = 0;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        for(String s : strs){
            ad.add(names[ind] +" : " + settings.getInt(s, 0));
            ind ++;
        }
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(ad);
        listView.deferNotifyDataSetChanged();
    }
}
