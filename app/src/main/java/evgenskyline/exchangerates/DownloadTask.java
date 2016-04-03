package evgenskyline.exchangerates;

import android.os.AsyncTask;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import evgenskyline.exchangerates.MainActivity.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by evgen on 01.04.2016.
 */

public  class DownloadTask extends AsyncTask<String, Integer, HashMap<String, MainActivity.MoneyBox>> {
    //String[] arrayStr;
    ArrayList<String> list;
    String mURL;

    public DownloadTask (String url){
        super();
        mURL = url;
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        //MainActivity.this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //MainActivity requestWindowFeature(Window.PROGRESS_VISIBILITY_ON);
        MainActivity.m2TextView.setText("Downloading data...");
    }

    @Override
    protected HashMap<String, MoneyBox> doInBackground(String... params) {
        HashMap<String, MoneyBox> hMap = new HashMap<String, MoneyBox>(50);
        //String mURL = params[0];
        list = new ArrayList<String>();
        int i = 0; //FOR DEBUG(проверка корректности ответа от сервера(извращённая))
        try {
            URL input = new URL(mURL);//url с адресом xml
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//получаем фабрику
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(input.openStream(), null);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(xpp.getEventType()==XmlPullParser.START_TAG && xpp.getName().equals("currency")) {
                    MoneyBox mb = new MoneyBox();
                    while (!(xpp.getName().equals("r030"))) {xpp.nextTag(); i++;};
                    xpp.next(); i++;
                    mb.someThing = xpp.getText();
                    xpp.next(); i++;
                    while (!xpp.getName().equals("txt")) {xpp.nextTag(); i++;};
                    xpp.next(); i++;
                    mb.ukrName = xpp.getText();
                    xpp.next(); i++;
                    while (!xpp.getName().equals("rate")) {xpp.nextTag();i++;};
                    xpp.next(); i++;
                    mb.rate = xpp.getText();
                    xpp.next(); i++;
                    while (!xpp.getName().equals("cc")) {xpp.nextTag();i++;};
                    xpp.next(); i++;
                    mb.name = xpp.getText();
                    xpp.next(); i++;
                    while (!xpp.getName().equals("exchangedate")) {xpp.nextTag();i++;};
                    xpp.next(); i++;
                    mb.exchangedate = xpp.getText();
                    mb.id = mb.name + " " + mb.ukrName;
                    hMap.put(mb.id, mb);
                    list.add(mb.id);
                    xpp.next(); i++;
                }
                if(xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    xpp.next(); i++;
                }else break;
            }
            if(i < 10){ //maintenance
                MoneyBox mb = new MoneyBox();
                mb.name = "Server maintenance\n(работы на сервере)";
                hMap.put("maintenance", mb);
            }
        }catch (Throwable e){
            MoneyBox mb = new MoneyBox();//это конечно бред, но так меньше писать
            mb.name="something error in parsing " + e.toString();
            hMap.put("Exeption", mb);
        }
        /*try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return hMap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //super.onProgressUpdate(values);
        MainActivity.m2TextView.setText("Downloading data...");
    }

    @Override
    protected void onPostExecute(HashMap<String, MoneyBox> hMap) {
        //super.onPostExecute(hMap);
        //MainActivity.this.requestWindowFeature(Window.PROGRESS_VISIBILITY_OFF);
        MainActivity.m2TextView.setText("Download complete!");
    }
    public ArrayList<String> getMyArrayList(){
        return list;
    }

}
