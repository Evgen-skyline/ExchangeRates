package evgenskyline.exchangerates;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import logic.DownloadTask;

//AppCompatActivity
 public class MainActivity extends Activity {

    public static class MoneyBox{
        public String someThing;
        public String ukrName;
        public String rate;
        public String name;
        public String exchangedate;
        public String id;
    }

    private TextView mTextView, m2TextView;
    private EditText editText;
    private static final String mURL ="http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange.xml";
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


        final TextView mTextView, m2TextView;
        mTextView = (TextView)findViewById(R.id.mTextViev);
        m2TextView = (TextView)findViewById(R.id.m2textView);
        editText = (EditText)findViewById(R.id.editText);
        spinner = (Spinner)findViewById(R.id.spinner);
        final String fileName = "mytestingxml.xml";
        ArrayAdapter<String> arrayAdapter;
        final ArrayList<String> list;
        //editText.addTextChangedListener();

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(mURL);

        try {
            hMap = downloadTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            m2TextView.setText(e.toString());
        }

        try {
        if(hMap.containsKey("Exeption")|| hMap.isEmpty() || hMap == null){
            String tmp = "error in parsing\nlook at your internet connection\nor server return incorrect data\n";
            mTextView.setText(tmp);
            if(hMap.containsKey("Exeption")){
                mTextView.setText(mTextView.getText() + "\n" + hMap.get("Exeption").name);
            }
        }else {
            Toast.makeText(MainActivity.this, "data received", Toast.LENGTH_SHORT).show();
//----------------------------------------
            list = downloadTask.getMyArrayList();
            //mShowResults(downloadTask.getMyArrayList(), hMap);
            //ArrayAdapter<String> arrayAdapter;
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
                    }catch (Throwable e){
                        // mTextView.setText(e.toString());
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            }
            }catch (Exception e){
                mTextView.setText(e.toString());
            }
    }
//================================================================================================//
    private void mButtonClicked(View view) {

    }

    private void mShowResults(final ArrayList<String> list, final HashMap<String, MoneyBox> hMap2){
        ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setPrompt("Выберите валюту");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MoneyBox mb = hMap2.get(list.get(position));//где-то тут проблемма
                    String result = "Данные на " + mb.exchangedate + "\n" +
                            mb.ukrName + "\n" +
                            mb.name + "\n" +
                            "курс: " + mb.rate;
                    mTextView.setText(result);
                }catch (Throwable e){
                   // mTextView.setText(e.toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
