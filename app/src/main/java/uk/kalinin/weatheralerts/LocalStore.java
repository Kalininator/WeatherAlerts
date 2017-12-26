package uk.kalinin.weatheralerts;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;

/**
 * Created by kal on 20/12/2017.
 */

public class LocalStore {



    public static void writeToFile(String data,Context context, int id) {
        File dir = new File(context.getFilesDir().getAbsolutePath());
        dir.mkdirs();
        File file = new File(dir + "/" + id + ".txt");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e("write to localstore",e.toString());
        }
    }

    public static String readFromFile(Context context, int id) {

        String line = "";
        String ret = "";
        File file = new File(context.getFilesDir() + "/" + id + ".txt");
        try {
            Log.d("kalcat","can read from file: " + file.canRead());
            FileReader fReader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(fReader);
            Log.d("kalcat","read from file: " + file.getAbsolutePath());

            /** Reading the contents of the file , line by line */
            while( (line=bReader.readLine()) != null  ){
                ret+= line + "\n";
            }
        } catch (FileNotFoundException e) {
            Log.e("read from localstore","no cached file");
        }catch(IOException e){
            Log.e("read from localstore",e.toString());
        }
        return ret;
    }
}
