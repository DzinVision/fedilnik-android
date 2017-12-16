package fedilnik.android.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedilnik.android.utils.FileUtils;

public class DataStore {
    private static final String FILE_NAME = "data.json";
    private static final String TAG = "DataStore";
    private static final String URL = "http://www.fe.uni-lj.si/o_fakulteti/restavracija/tedenski_meni/";
    private static DataStore instance = null;

    private Map<String, DataStoreCallback> callbacks;
    private List<List<Meal>> data;
    private Date downloadedDate = null;
    private boolean loadedData = false;
    private String validDate = "";


    private DataStore() {
        callbacks = new HashMap<>();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public void loadData(Context context) {
        if (loadedData) return;
        loadedData = true;
        Gson gson = new Gson();

        try {
            String json = FileUtils.readFromFile(FILE_NAME, context);
            DataStoreHolder dataStoreHolder = gson.fromJson(json, DataStoreHolder.class);
            if (dataStoreHolder != null) {
                downloadedDate = dataStoreHolder.downloadedDate;
                data = dataStoreHolder.data;
                validDate = dataStoreHolder.validDate;
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Could not load data", e);
            downloadedDate = null;
            data = new ArrayList<>();
        }

        for (DataStoreCallback callback : callbacks.values())
            callback.dataDidChange(Status.SUCCESS);

        if (downloadedDate == null) {
            downloadData();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(downloadedDate);
        int downloadedDay = calendar.get(Calendar.DAY_OF_MONTH);
        int downloadedMonth = calendar.get(Calendar.MONTH);
        int downloadedYear = calendar.get(Calendar.YEAR);

        calendar.setTime(new Date());
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        if (!(downloadedDay == currentDay &&
                downloadedMonth == currentMonth &&
                downloadedYear == currentYear)) {
            downloadData();
        }
    }

    public void saveData(Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(new DataStoreHolder(data, downloadedDate, validDate));
        FileUtils.writeToFile(FILE_NAME, json, context);
    }

    public void addCallback(String key, DataStoreCallback callback) {
        callbacks.put(key, callback);
    }

    public void removeCallback(String key) {
        callbacks.remove(key);
    }

    public void downloadData() {
        Log.d(TAG, "Downloading data...");
        refreshingDataCallback();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<List<Meal>> newData = new ArrayList<>();

                    Document doc = Jsoup.connect(URL).maxBodySize(0).timeout(0).get();

                    Element container = doc.getElementById("text-content-container");

                    String date = container.child(1).html().replaceAll("&nbsp;","");
                    date = date.replaceAll("<[a-z/]+>", "");

                    Elements days = container.getElementsByTag("table");

                    for (Element day : days) {
                        List<Meal> meals = new ArrayList<>();
                        Element body = day.getElementsByTag("tbody").first();
                        boolean first = true;
                        for (Element row : body.children()) {
                            Elements dataCells = row.children();
                            for (int i = 0; i < dataCells.size(); ++i) {
                                String content = dataCells.get(i).child(0).html();
                                content = URLDecoder.decode(content, "UTF-8");
                                content = content.replaceAll("&nbsp;", "");
                                if (content.isEmpty()) continue;
                                if (first) {
                                    Meal meal = new Meal();
                                    meal.setTitle(content);
                                    meals.add(meal);
                                } else meals.get(i).addContent(content);
                            }
                            first = false;
                        }
                        newData.add(meals);
                    }

                    data = newData;
                    downloadedDate = new Date();
                    validDate = date;
                    downloadDataCallback(Status.SUCCESS);

                } catch (IOException e) {
                    downloadDataCallback(Status.NETWORK_ERROR);
                }
            }
        }).start();
    }

    private void downloadDataCallback(final Status status) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (DataStoreCallback callback : callbacks.values()) {
                    callback.dataDidChange(status);
                }
            }
        });
    }

    private void refreshingDataCallback() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (DataStoreCallback callback : callbacks.values()) {
                    callback.dataDidStartRefreshing();
                }
            }
        });
    }

    public List<Meal> getData(int day) {
        if (data == null || day >= data.size()) return new ArrayList<>();
        return data.get(day);
    }

    public String getValidDate() {
        return validDate;
    }

}

class DataStoreHolder {
    public List<List<Meal>> data;
    public Date downloadedDate;
    public String validDate;

    DataStoreHolder(List<List<Meal>> data, Date downloadedDate, String validDate) {
        this.data = data;
        this.downloadedDate = downloadedDate;
        this.validDate = validDate;
    }
}
