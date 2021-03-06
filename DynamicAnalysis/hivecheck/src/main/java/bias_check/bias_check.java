package bias_check;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class bias_check {
    public static void main(String [] arg){
        String url="";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result="";
        HttpEntity entity = response.getEntity();

        try {
            if(entity != null)
                result = EntityUtils.toString(entity);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(result);
        JSONObject object= new JSONObject(result);
        JSONArray js=object.getJSONObject("tasks").getJSONArray("task");
        int maxtime=0;
        int mintime=Integer.MAX_VALUE;
        for(int i=0;i<js.length();i++){

            JSONObject tmp=js.getJSONObject(i);
            if(tmp.getString("type").equals("REDUCE")){
                int time=tmp.getInt("elapsedTime");
                if (time >maxtime){maxtime=time;}
                if(time <mintime){mintime=time;}
            }
        }
        System.out.println("max time:"+maxtime);
        System.out.println("min time:"+mintime );


        if((1.0*maxtime/mintime) >1.5){
            System.out.println("attention：检测到reduce完成时间差异过大，可能存在数据倾斜");
        }

}
}
