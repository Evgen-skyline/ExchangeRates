package evgenskyline.exchangerates;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;

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
    public static Spinner spinner;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.mTextViev);
        m2TextView = (TextView) findViewById(R.id.m2textView);
        editText = (EditText) findViewById(R.id.editText);
        spinner = (Spinner) findViewById(R.id.spinner);

        DownloadTask downloadTask = new DownloadTask(mURL, getApplicationContext());
        downloadTask.execute();
    }

    //================================================================================================//
    public void mButtonClicked(View view) {
        EditText ed = (EditText) findViewById(R.id.editText);
        String str = ed.getText().toString();
        str = mURL_DATE + str;
        if(ed.getText().length() != 8){
            String st ="Неверный формат даты\nпример: 19980120(ГГГГММДД)";
            m2TextView.setText(st);
        }else {
            DownloadTask downloadTask = new DownloadTask(str, getApplicationContext());
            downloadTask.execute();
        }
    }
}


