package evgenskyline.exchangerates;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    ArrayAdapter<String> arrayAdapter;
    Context context;

    public DownloadTask (String url, Context con){
        super();
        mURL = url;
        context = con;
    }

    @Override
    protected void onPreExecute() {
        MainActivity.m2TextView.setText("Downloading data...");
    }

    @Override
    protected HashMap<String, MoneyBox> doInBackground(String... params) {
        HashMap<String, MoneyBox> hMap = new HashMap<String, MoneyBox>();
        list = new ArrayList<String>();
        int i = 0; //FOR DEBUG(проверка корректности ответа от сервера(извращённая))
        try {
            URL input = new URL(mURL);//url с адресом xml
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();//получаем фабрику
            factory.setNamespaceAware(true);
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
                mb.name = "Server maintenance\n(работы на сервере)\nили неверная дата" +
                "\nP.S. у НБУ нет курса за выходные дни\nт.е. попробуйте соседнюю дату +/- 2 деня";
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
        try{
        if (hMap.containsKey("Exeption") || hMap.isEmpty() || hMap == null ||
                hMap.containsKey("maintenance") || hMap.containsKey("Error_date")) {
            if(hMap.containsKey("Error_date")){
                MainActivity.mTextView.setText("Неподходящая дата");
            } else {
                if (hMap.containsKey("maintenance")) {
                    MainActivity.mTextView.setText(hMap.get("maintenance").name);
                } else {
                    String tmp = "error in parsing\nCheck your internet connection\nor server return incorrect data\n";
                    MainActivity.mTextView.setText(tmp);
                    if (hMap.containsKey("Exeption")) {
                        MainActivity.mTextView.setText(MainActivity.mTextView.getText() + "\n" + hMap.get("Exeption").name);
                    }
                }
            }
        } else {
            Toast.makeText(context, "data received", Toast.LENGTH_SHORT).show();
            mShowResult(list, hMap);
        }
    } catch (Exception e) {
        MainActivity.mTextView.setText(e.toString());
    }

        MainActivity.m2TextView.setText("Download complete!");
    }

    private void mShowResult(final ArrayList<String> list, final HashMap<String, MoneyBox> hMap) {
        //R.layout.support_simple_spinner_dropdown_item
        arrayAdapter = new ArrayAdapter<String>(context, R.layout.my_spinner_item, list);
        //arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        MainActivity.spinner.setBackgroundColor(context.getResources().getColor(R.color.mTextColor));
        MainActivity.spinner.setPopupBackgroundResource(R.color.mTextColor);
        MainActivity.spinner.setAdapter(arrayAdapter);
        MainActivity.spinner.setPrompt("Выберите валюту");
        MainActivity.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MoneyBox mb = hMap.get(list.get(position));//где-то тут проблемма
                    String result = "Данные на " + mb.exchangedate + "\n" +
                            mb.ukrName + "\n" +
                            mb.name + "\n" +
                            "курс: " + mb.rate;
                    MainActivity.mTextView.setText(result);
                } catch (Throwable e) {
                    MainActivity.mTextView.setText(e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
