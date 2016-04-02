package logic;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import evgenskyline.exchangerates.MainActivity;
import evgenskyline.exchangerates.MainActivity.*;
import evgenskyline.exchangerates.R;

/**
 * Created by evgen on 01.04.2016.
 */
public class DownloadTask extends AsyncTask<String, Integer, HashMap<String, MoneyBox>> {
    //String[] arrayStr;
    ArrayList<String> list;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected HashMap<String, MoneyBox> doInBackground(String... params) {
        HashMap<String, MoneyBox> hMap = new HashMap<String, MoneyBox>(50);
        String mURL = params[0];
        list = new ArrayList<String>();
        try {
            URL input = new URL(mURL);//url с адресом xml
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//получаем фабрику
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(input.openStream(), null);

            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(xpp.getEventType()==XmlPullParser.START_TAG && xpp.getName().equals("currency")) {
                    MoneyBox mb = new MoneyBox();
                    while (!(xpp.getName().equals("r030"))) {xpp.nextTag();};
                        xpp.next();
                        mb.someThing = xpp.getText();
                        xpp.next();
                    while (!xpp.getName().equals("txt")) {xpp.nextTag();};
                        xpp.next();
                        mb.ukrName = xpp.getText();
                        xpp.next();
                    while (!xpp.getName().equals("rate")) {xpp.nextTag();};
                        xpp.next();
                        mb.rate = xpp.getText();
                        xpp.next();
                    while (!xpp.getName().equals("cc")) {xpp.nextTag();};
                        xpp.next();
                        mb.name = xpp.getText();
                        xpp.next();
                    while (!xpp.getName().equals("exchangedate")) {xpp.nextTag();};
                        xpp.next();
                        mb.exchangedate = xpp.getText();
                        mb.id = mb.name + " " + mb.ukrName;
                        hMap.put(mb.id, mb);
                        list.add(mb.id);
                        xpp.next();
                }
                if(xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    xpp.next();
                }else break;
            }
        }catch (Throwable e){
            MoneyBox mb = new MoneyBox();//это конечно бред, но так меньше писать
            mb.name="something error in parsing " + e.toString();
            hMap.put("Exeption", mb);
        }
        return hMap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(HashMap<String, MoneyBox> hMap) {
        super.onPostExecute(hMap);
    }
    public ArrayList<String> getMyArrayList(){
        return list;
    }

}
