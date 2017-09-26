package fedilnik.android.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static void writeToFile(String fileName, String content, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput
                    (fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(content);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not write to file: " + fileName, e);
        }
    }

    public static String readFromFile(String fileName, Context context) {
        StringBuilder content = new StringBuilder();
        try {
            InputStream inputStream = context.openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) content.append(line);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not open file: " + fileName, e);
        } catch (IOException e) {
            Log.e(TAG, "Could not read from file: " + fileName, e);
        }
        return content.toString();
    }
}
