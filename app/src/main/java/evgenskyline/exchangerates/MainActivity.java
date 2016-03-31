package evgenskyline.exchangerates;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.net.HttpURLConnection;
import java.io.*;
import java.util.HashMap;
//AppCompatActivity
public class MainActivity extends Activity {
    public class MoneyBox{
        public String someThing;
        public String ukrName;
        public String rate;
        public String name;
        public String exchangedate;
    }

    private final String mURL ="http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange.xml";
            //"http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange.xml";
    //"http://xml.nsu.ru/xml/note.xml";
    private  TextView mTextView;
    private TextView m2TextView;
    private String strTMP=""; //потом удалить
    private String strTMP2 = ""; //потом удалить

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final HashMap<String, MoneyBox> hMap;
        mTextView = (TextView)findViewById(R.id.mTextViev);
        m2TextView = (TextView)findViewById(R.id.m2textView);
        final String fileName = "mytestingxml.xml";
        mTextView.setText(fileName);

//////////////////////////
        hMap = new HashMap<String, MoneyBox>(50);
        //final MoneyBox[] mbCol = new MoneyBox[200];
//////////////////////////
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL input = new URL(mURL);
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(input.openStream(), null);

                    while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                      if(xpp.getEventType()==XmlPullParser.START_TAG && xpp.getName().equals("currency")){
                          MoneyBox mb = new MoneyBox();
                          //xpp.next();
                          while(!(xpp.getName().equals("r030"))){ xpp.nextTag();};xpp.next();
                          //strTMP2 += xpp.getText() + "\n";
                          mb.someThing = xpp.getText();
                          xpp.next();
                          while(!xpp.getName().equals("txt")){xpp.nextTag();};xpp.next();
                          //strTMP2 += xpp.getText() + "\n";
                          mb.ukrName = xpp.getText();
                          xpp.next();
                          while(!xpp.getName().equals("rate")){xpp.nextTag();};xpp.next();
                          //strTMP2 += xpp.getText() + "\n";
                          mb.rate = xpp.getText();
                          xpp.next();
                          while(!xpp.getName().equals("cc")){xpp.nextTag();};xpp.next();
                          //strTMP2 += xpp.getText() + "\n";
                          mb.name = xpp.getText();
                          xpp.next();
                          while(!xpp.getName().equals("exchangedate")){xpp.nextTag();};xpp.next();
                          //strTMP2 += xpp.getText() + "\n";
                          mb.exchangedate = xpp.getText();
                          xpp.next();
                          hMap.put(mb.name, mb);
                      }
                    xpp.nextTag();
                    }
                }catch (Throwable e){
                    strTMP="something error in parsing " + e.toString();
                }

            }});
        t.start();
        int i =0;
        while (t.isAlive()){
            try {
                ++i;
                Thread.currentThread().sleep(1000);
                m2TextView.setText("waiting data..." + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
                mTextView.setText(e.toString());
            }
        }
        Toast.makeText(MainActivity.this, "data received", Toast.LENGTH_SHORT).show();
            try {
                mTextView.setText(hMap.get("USD").rate);
                if (strTMP == "" | strTMP == null) {
                    mTextView.setText("EMPTY");
                }
            }catch (Exception e){
                mTextView.setText(e.toString());
            }
        try{
            m2TextView.setText(strTMP2);
        }catch (Exception e){
            m2TextView.setText(e.toString());
        }

//================================================================================================//
    }

    private void downloadUrl(String urlStr, String file) throws IOException {
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlStr);
            bis = new BufferedInputStream(url.openStream());
            fos = openFileOutput(file, Context.MODE_PRIVATE);//пишет в std директорию приложения
            //с доступом только этому приложению, в file передаём просто имя(без пути)
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fos.write(buffer, 0, count);
            }
        }catch (IOException IOE){
            throw IOE;
        }finally {
            fos.close();
            bis.close();
        }
    }
}
