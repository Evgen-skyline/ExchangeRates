package evgenskyline.exchangerates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import logic.DownloadTask;

public class MyAdapter{
    private static ArrayList<String> al;
    public static HashMap<String, MainActivity.MoneyBox> taskMeneg(String url) throws InterruptedException, ExecutionException {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
        HashMap<String, MainActivity.MoneyBox> hm = null;
        try {
            hm = downloadTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        al = downloadTask.getMyArrayList();
        return hm;
    }
    public static ArrayList<String> getArrayList(){ //вызывать после  taskMeneg
        return al;
    }

}
