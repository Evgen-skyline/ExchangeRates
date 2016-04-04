package evgenskyline.exchangerates;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

//AppCompatActivity
 public class MainActivity extends Activity {

    public static class MoneyBox {
        public String someThing;
        public String ukrName;
        public String rate;
        public String name;
        public String exchangedate;
        public String id;
    }

    public static TextView mTextView, m2TextView;
    private EditText editText;
    private static final String mURL = "http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange.xml";
    private static final String mURL_DATE = "http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=";
    //"http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange.xml"; //основной
    //"http://xml.nsu.ru/xml/note.xml"; //тестовый
    //http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=20160401  //за дату
    HashMap<String, MoneyBox> hMap;
    Spinner spinner;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //final TextView mTextView, m2TextView;
        mTextView = (TextView) findViewById(R.id.mTextViev);
        m2TextView = (TextView) findViewById(R.id.m2textView);
        editText = (EditText) findViewById(R.id.editText);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter;
        final ArrayList<String> list;
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);


        DownloadTask downloadTask = new DownloadTask(mURL);
        downloadTask.execute();

        try {
            hMap = downloadTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            m2TextView.setText(e.toString());
        }

        try {
            if (hMap.containsKey("Exeption") || hMap.isEmpty() || hMap == null || hMap.containsKey("maintenance")) {
                if (hMap.containsKey("maintenance")) {
                    mTextView.setText(hMap.get("maintenance").name);
                } else {
                    String tmp = "error in parsing\nCheck your internet connection\nor server return incorrect data\n";
                    mTextView.setText(tmp);
                    if (hMap.containsKey("Exeption")) {
                        mTextView.setText(mTextView.getText() + "\n" + hMap.get("Exeption").name);
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "data received", Toast.LENGTH_SHORT).show();
                list = downloadTask.getMyArrayList();
                mShowResult(list, hMap);
            }
        } catch (Exception e) {
            mTextView.setText(e.toString());
        }
    }

    //================================================================================================//
    public void mButtonClicked(View view) {
        mTextView = (TextView) findViewById(R.id.mTextViev);
        EditText ed = (EditText) findViewById(R.id.editText);
        String str = ed.getText().toString();
        str = mURL_DATE + str;
        DownloadTask downloadTask = new DownloadTask(str);
        downloadTask.execute();
        try {
            hMap = downloadTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            m2TextView.setText(e.toString());
        }

        try {
            if (hMap.containsKey("Exeption") || hMap.isEmpty() || hMap == null || hMap.containsKey("maintenance")) {
                if (hMap.containsKey("maintenance")) {
                    mTextView.setText(hMap.get("maintenance").name);
                } else {
                    String tmp = "error in parsing\nCheck your internet connection\nor server return incorrect data\n";
                    mTextView.setText(tmp);
                    if (hMap.containsKey("Exeption")) {
                        mTextView.setText(mTextView.getText() + "\n" + hMap.get("Exeption").name);
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "data received", Toast.LENGTH_SHORT).show();
                ArrayList<String> lis;
                lis = downloadTask.getMyArrayList();
                mShowResult(lis, hMap);
            }
        } catch (Exception e) {
            mTextView.setText(e.toString());
        }
    }

    private void mShowResult(final ArrayList<String> list, final HashMap<String, MoneyBox> hMap) {
        mTextView = (TextView) findViewById(R.id.mTextViev);
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setPrompt("Выберите валюту");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MoneyBox mb = hMap.get(list.get(position));//где-то тут проблемма
                    String result = "Данные на " + mb.exchangedate + "\n" +
                            mb.ukrName + "\n" +
                            mb.name + "\n" +
                            "курс: " + mb.rate;
                    mTextView.setText(result);
                } catch (Throwable e) {
                    mTextView.setText(e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}


