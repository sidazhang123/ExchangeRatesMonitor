package com.example.hacunamatata.exchangerates.usd_aud;

/**
 * Created by Hacunamatata on 2016/12/30.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class us_au_RE {

    public String webdata() {
        String res="";
        try {


//           Sina finance
            URL url = new URL("http://hq.sinajs.cn/?_=0.8359572991459969&list=fx_susdaud");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();// return an URLConnection obj
            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(isr);//create a character buffer using the input stream

            String temp = br.readLine().trim();
            String[] sp = temp.split("\"");
            String[]v = sp[1].split(",");
            res+=v[1]+"|"+v[v.length-1].substring(5,10)+" "+v[0].substring(0,5);
            br.close();
            isr.close();
        }catch(Exception e){
            System.out.println(e);
            return "Err";
        }
        return res;
    }
}
