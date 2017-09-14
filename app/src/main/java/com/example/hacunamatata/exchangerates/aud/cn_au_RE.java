package com.example.hacunamatata.exchangerates.aud;

/**
 * Created by Hacunamatata on 2016/12/30.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class cn_au_RE {

    public String webdata() {
        try{


//            URL url = new URL("http://www.boc.cn/sourcedb/whpj/enindex.html"); //create URl obj
            URL url = new URL("http://www.boc.cn/sourcedb/whpj/");
            HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();// return an URLConnection obj
            InputStreamReader isr = new java.io.InputStreamReader(conn.getInputStream());
            BufferedReader br = new java.io.BufferedReader(isr);//create a character buffer using the input stream
            boolean isFound=false;
            int count=0;
            String temp;
            String value="",timestamp="";
            Pattern pattern = Pattern.compile(">([^>]*)<");//find the line of "澳大利亚元"

            while ((temp = br.readLine().trim()) != null) { //read output stream by lines

                if(!temp.equals("")&&temp.contains("<td>澳大利亚元</td>")){
                    isFound=true;

                }
                if(isFound){
                    count++;
                }
                if(count==4){

                    Matcher matcher = pattern.matcher(temp);  //locate rating value
                    if (matcher.find()) {
                        value= matcher.group(1);

                    }else{
                        return "notFound";
                    }
                }else if(count==7){ // date line

                    timestamp= temp.substring(9,14)+" ";
                }else if(count==8){

                    timestamp+= temp.substring(4,9);
                    break;
                }
            }
            br.close();
            isr.close();

            return value+"|"+timestamp;

        }catch(Exception e){
            e.printStackTrace();
            return "Err";
        }

    }
}
