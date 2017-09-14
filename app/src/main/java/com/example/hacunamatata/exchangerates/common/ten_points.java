package com.example.hacunamatata.exchangerates.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;


/**
 * Created by Hacunamatata on 2017/1/3.
 */
public class ten_points {
    private String currency;
    private SharedPreferences rates;
    private SharedPreferences.Editor editor;

    public ten_points(Context context, String currency) {
        rates = context.getSharedPreferences("rates",
                Activity.MODE_PRIVATE);
        editor = rates.edit();
        this.currency = currency;
    }

    public void push(String rate) {
        for (int i = 9; i > 0; i--) {
            String key = currency + "_" + i;
            String value = rates.getString(key,"null|null");
            editor.putString(currency + "_" + (i+1),value);
        }
        editor.putString(currency + "_1",rate);
        editor.apply();
    }

    public String getHead() {
        return rates.getString(currency+"_1","null|null");
    }

    public String getTail() {
        for (int i = 10; i > 0; i--) {
            String key = currency + "_" + i;
            String value = rates.getString(key,"null|null");
            if(!value.equals("null|null")){
                return value;
            }
        }
        return "null|null";
    }

    public ArrayList<String>[] getAll() {
        ArrayList<String> rate_value = new ArrayList<String>();
        ArrayList<String> timestamp = new ArrayList<String>();
        for (int i = 10; i > 0; i--) {
            String key = currency + "_" + i;
            String value = rates.getString(key,"null|null");
            if(!value.equals("null|null")){

                String[] pair = value.split("\\|");
                rate_value.add(pair[0]);
                timestamp.add(pair[1]);
            }
        }
        ArrayList[] res = {rate_value,timestamp};
        return res;
    }
}
