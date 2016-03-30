package evgenskyline.exchangerates;

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

public class MainActivity extends AppCompatActivity {

    private final String mURL =
            "http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange.xml";
    //"http://xml.nsu.ru/xml/note.xml";
    private  TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView)findViewById(R.id.mTextViev);
        //File sdPath = Environment.getExternalStorageDirectory();
        //final String filePath = sdPath.toString() + "/1111my.xml";
        final String fileName = "mytestingxml.xml";
        mTextView.setText(fileName);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                  try {
                      downloadUrl(mURL, fileName);
                  } catch (IOException e) {
                      e.printStackTrace();
                  }catch (Throwable t){
                      t.printStackTrace();
                  }
            }});
        t.start();
        int i =0;
        while (t.isAlive()){
            try {
                ++i;
                Thread.currentThread().sleep(1000);
                Toast.makeText(MainActivity.this, "waiting data..." + i, Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(MainActivity.this, "data received", Toast.LENGTH_SHORT).show();

        HashMap<String, MoneyBox> hMap = new HashMap<String, MoneyBox>(50);
        try {
            XmlPullParser parser = getResources().getXml(R.xml.mytestingxml);
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType()==XmlPullParser.START_TAG && parser.getName().
                                                                        equals("currency")){
                    MoneyBox mb = new MoneyBox();
                    mb.someThing = parser.getAttributeValue(0);
                    mb.ukrName = parser.getAttributeValue(1);
                    mb.rate = parser.getAttributeValue(2);
                    mb.name = parser.getAttributeValue(3);
                    mb.exchangedate = parser.getAttributeValue(4);
                    hMap.put(mb.name, mb);
                }
                parser.next();
            }
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "error in parsing", Toast.LENGTH_SHORT).show();
        }
        mTextView.setText(hMap.get("USD").rate);
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

    class MoneyBox{
        String someThing;
        String ukrName;
        String rate;
        String name;
        String exchangedate;
    }
}
