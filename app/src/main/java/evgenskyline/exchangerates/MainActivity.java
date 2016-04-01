package evgenskyline.exchangerates;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.io.*;
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
    }

    private static final String mURL ="http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange.xml";
            //"http://bank.gov.ua/NBUStatService/v1/statdirectory/exchange.xml";
    //"http://xml.nsu.ru/xml/note.xml";
    private  TextView mTextView;
    private TextView m2TextView;
    HashMap<String, MoneyBox> hMap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView)findViewById(R.id.mTextViev);
        m2TextView = (TextView)findViewById(R.id.m2textView);
        final String fileName = "mytestingxml.xml";
        mTextView.setText(fileName);

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(mURL);

        try {
            hMap = downloadTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            m2TextView.setText(e.toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
            m2TextView.setText(e.toString());
        }

        Toast.makeText(MainActivity.this, "data received", Toast.LENGTH_SHORT).show();
            try {
                mTextView.setText(hMap.get("EUR").rate);
            }catch (Exception e){
                mTextView.setText(e.toString());
            }
//================================================================================================//
    }

    private void downloadUrl(String urlStr, String file) throws IOException {//метод для скачивания чего-либо
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
