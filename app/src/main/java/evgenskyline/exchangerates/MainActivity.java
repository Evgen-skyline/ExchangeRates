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
    private String strTMP; //потом удалить
    private String strTMP2; //потом удалить

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final HashMap<String, MoneyBox> hMap;
        mTextView = (TextView)findViewById(R.id.mTextViev);
        m2TextView = (TextView)findViewById(R.id.m2textView);
        final String fileName = "mytestingxml.xml";
        mTextView.setText(fileName);

//////////////////////////
        hMap = new HashMap<String, MoneyBox>(50);
//////////////////////////
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                  /*try {   //это для выкачки xml в файл на диск
                      downloadUrl(mURL, fileName);
                  } catch (IOException e) {
                      e.printStackTrace();
                  }catch (Throwable t){
                      t.printStackTrace();
                  }
                  */

                try {
                    /*
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true); //если надо
                    XmlPullParser parser = factory.newPullParser();
                    URL input = new URL(mURL); //url удаленного документа
                    parser.setInput(input.openStream(), null);


                    while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if(parser.getEventType()==XmlPullParser.START_TAG && parser.getName().
                                equals("currency")){
                            //MoneyBox mb = new MoneyBox();
                            //mb.someThing = parser.getAttributeValue(0);
                            strTMP2 += parser.getAttributeValue(0);
                            //mb.ukrName = parser.getAttributeValue(1);
                            strTMP2 +=parser.getAttributeValue(1);
                            //mb.rate = parser.getAttributeValue(2);
                            strTMP2 +=parser.getAttributeValue(2);
                            //mb.name = parser.getAttributeValue(3);
                            strTMP2 +=parser.getAttributeValue(3);
                            //mb.exchangedate = parser.getAttributeValue(4);
                            strTMP2 +=parser.getAttributeValue(4);
                            //hMap.put(mb.name, mb);
                            //strTMP2 ="ffff";
                        }
                        parser.next();
                    */

                    //следующая хуйня работает
                    URL input = new URL(mURL);
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(input.openStream(), null);

                    /*
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if(eventType == XmlPullParser.START_DOCUMENT) {
                            strTMP2 += " " + "\n";
                        } else if(eventType == XmlPullParser.END_DOCUMENT) {
                            strTMP2 +=" " + "\n";
                        } else if(eventType == XmlPullParser.START_TAG) {
                            strTMP2 +="  "+xpp.getName() + "\n";
                        } else if(eventType == XmlPullParser.END_TAG) {
                            strTMP2 +="  "+xpp.getName() + "\n";
                        } else if(eventType == XmlPullParser.TEXT) {
                            strTMP2 +=" "+xpp.getText() + "\n";
                        }
                        eventType = xpp.next();
                    }
                    */
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("currency")){
                            //доделать парсинг и фильтр

                        }
                        eventType = xpp.next();
                    }
                }catch (Throwable e){
                    strTMP="something error in parsing";
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

            mTextView.setText(strTMP2);
            if(strTMP2 == "" | strTMP2== null){
                mTextView.setText("EMPTY");
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
