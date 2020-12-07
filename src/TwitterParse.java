import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

class TwitterParse {
    private static long cursor=-1;
    private static final int COUNT_OF_OBJECTS_IN_RESPONSE=9;
    private static final Logger logger = Logger.getLogger(TwitterParse.class);
    private static final String ACCESS_TOKEN = "Token";
    public static void main(String[] args) throws IOException {
        String[] screenNames=Aplication.getList().clone();
        JSONArray users = new JSONArray();
        int infoCount=0;
        for(int k=0;k<screenNames.length;k++) {
            cursor = -1;
            while (cursor != 0) {
                String url = "https://api.twitter.com/1.1/friends/list.json?cursor=" + cursor +
                        "&screen_name=" + screenNames[k] +
                        "&skip_status=true&include_user_entities=false&count=200";
                logger.debug("Parsing name - " + screenNames[k]);
                JSONObject actualResult = sendGetRestRequest(url);
                JSONObject infoName=new JSONObject();
                infoName.put("info",screenNames[k]);
                actualResult.getJSONArray("users").put(infoName);
                cursor = actualResult.getLong("next_cursor");
                users.put(actualResult.getJSONArray("users"));
                infoCount += actualResult.getJSONArray("users").length();
            }
        }
        String [] name=new String[infoCount*COUNT_OF_OBJECTS_IN_RESPONSE];
        int count=0;
        for(int i=0;i<users.length();i++){
            JSONArray arrayRec=users.getJSONArray(i);
            for(int j=0;j<arrayRec.length()-1;j++) {
                JSONObject rec = arrayRec.getJSONObject(j);
                JSONObject jsonNameOfTable=arrayRec.getJSONObject(arrayRec.length()-1);
                String nameOfTable= jsonNameOfTable.getString("info");
                name[0+ (count*COUNT_OF_OBJECTS_IN_RESPONSE) + (j*COUNT_OF_OBJECTS_IN_RESPONSE)]=nameOfTable;
                name[1 + (count*COUNT_OF_OBJECTS_IN_RESPONSE)+(j * COUNT_OF_OBJECTS_IN_RESPONSE)] = rec.getString("name");
                name[2 +(count*COUNT_OF_OBJECTS_IN_RESPONSE)+ (j * COUNT_OF_OBJECTS_IN_RESPONSE)] = rec.getString("screen_name");
                name[3 +(count*COUNT_OF_OBJECTS_IN_RESPONSE)+ (j * COUNT_OF_OBJECTS_IN_RESPONSE)] = rec.getString("description");
                name[4 +(count*COUNT_OF_OBJECTS_IN_RESPONSE)+ (j * COUNT_OF_OBJECTS_IN_RESPONSE)] = rec.getString("location");
                name[5 +(count*COUNT_OF_OBJECTS_IN_RESPONSE)+ (j * COUNT_OF_OBJECTS_IN_RESPONSE)] = rec.getString("created_at");
                name[6 +(count*COUNT_OF_OBJECTS_IN_RESPONSE)+ (j * COUNT_OF_OBJECTS_IN_RESPONSE)] = Integer.toString(rec.getInt("followers_count"));
                name[7 +(count*COUNT_OF_OBJECTS_IN_RESPONSE)+ (j * COUNT_OF_OBJECTS_IN_RESPONSE)] = Integer.toString(rec.getInt("friends_count"));
                if (rec.isNull("url"))
                    name[8 +(count*COUNT_OF_OBJECTS_IN_RESPONSE)+ (j * COUNT_OF_OBJECTS_IN_RESPONSE)] = "no url";
                else name[8 +(count*COUNT_OF_OBJECTS_IN_RESPONSE)+(j * COUNT_OF_OBJECTS_IN_RESPONSE)] = rec.getString("url");

            }
            count+=users.getJSONArray(i).length()-1;
        }
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        Exfile.writeIntoExcel("Output_"+timeStamp+".xls",name);
        logger.info("Completely!");
        try{
            Thread.sleep(3000); }
        catch (InterruptedException e){
            logger.error("Take interrupt ",e);}
    }



    public static JSONObject sendGetRestRequest(String url) throws IOException  {
        StringBuffer response = new StringBuffer();
        String authorizationString = "Bearer " + ACCESS_TOKEN;
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("Authorization", authorizationString);
        con.addRequestProperty("Accept", "application/json");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                char ch=inputLine.charAt(0);
                if(ch=='[') response.append(inputLine.substring(1,inputLine.length()-1));
                response.append(inputLine);
            }

            in.close();
        }

        else {
            con.disconnect();

            try{
                logger.debug("Waiting 15 minutes for limits...");
                Thread.sleep(900000);
                return sendGetRestRequest(url);
            }

            catch(InterruptedException e){
                logger.error("Take interrupt ",e);
            }

        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse;
    }
}
