package com.wingsmight.bibleloop;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class JsonManager
{
    public static JSONArray ReadJsonArray(String arrayName)
    {
        try
        {
            String json = loadJSONFromAsset();
            JSONObject jsonObj = new JSONObject(json);

            return jsonObj.getJSONArray(arrayName);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }

    }

//    public void WriteJson(Context context) throws JSONException
//    {
//        JSONObject jsonObj = new JSONObject();
//
//        jsonObj.put("id", 2);
//        jsonObj.put("language", "english");
//        jsonObj.put("language", "Rus");
//        jsonObj.put("language", "UKRAINA");
//
//        JSONArray list = new JSONArray();
//        list.put("chapter 1");
//        list.put("chapter 2");
//        list.put("chapter 3");
//        jsonObj.put("chapters", list);
//
//        System.out.println(jsonObj.toString(2));
//
//        String str = jsonObj.toString();
//        try {
//            FileOutputStream fos = context.openFileOutput("lyrics.json", Context.MODE_PRIVATE);
//            fos.write(str.getBytes(), 0, str.length());
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static String loadJSONFromAsset()
    {
        String json = null;
        try
        {
            InputStream is = MainActivity.GetContext().getResources().openRawResource(R.raw.lyrics);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }
}