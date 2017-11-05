package com.spondonict.nazmul.my_location_finder_and_gsm_mapper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int myLatitude, myLongitude;
    TextView textGeo;
    int cid,lac,mcc,mnc,strength;
    TextView textCID;
    TextView textLAC;
    TextView textGsmCellLocation,tvcarrier;
    Button button2;
    TextView tv2;
    String signalstrength="0";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         textGsmCellLocation = (TextView)findViewById(R.id.gsmcelllocation);
         textCID = (TextView)findViewById(R.id.cid);
         textLAC = (TextView)findViewById(R.id.lac);
         textGeo = (TextView)findViewById(R.id.geo);
        tvcarrier= (TextView) findViewById(R.id.tvcarrier);
        tv2=(TextView)findViewById(R.id.textView2);
        button2=(Button)findViewById(R.id.button2);

        //retrieve a reference to an instance of TelephonyManager
        //TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

        Button btncell=(Button)findViewById(R.id. btncell);
        btncell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NeighbouringCell.class));
            }
        });

        //textGsmCellLocation.setText(cellLocation.toString());
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //getInfos();
                printInfos();
                /*send_Request send_request=new send_Request();
                send_request.execute();*/
                googleLocation location=new googleLocation();
                location.execute();
                //createJson(1193344,1903,470,04,-96,0,15);
            }
        });


    }

    public class GetParams extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.getGsmSignalStrength() == 99) {
                signalstrength = "Unknown";
            } else {
                signalstrength = new StringBuilder(String.valueOf(String.valueOf((signalStrength.getGsmSignalStrength() * 2) - 113))).append(" dBm").toString();
            }
            //signalstrength =""+ signalStrength.getEvdoDbm();
        }
    }

    int  signalStrength(){
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> all = telephonyManager.getAllCellInfo();
        int strengthDbm=0;
        for(CellInfo info:all) {
            if(info instanceof CellInfoGsm){
            CellInfoGsm cellinfogsm =(CellInfoGsm) info;
            CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
             strengthDbm = cellSignalStrengthGsm.getDbm();
        }
        }
        return strengthDbm;
    }
    @TargetApi(18)
    void printInfos(){
        TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new GetParams(),PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        GsmCellLocation cellLocation = (GsmCellLocation)tm.getCellLocation();
        String networkOperator = tm.getNetworkOperator();
        String mcc = networkOperator.substring(0, 3);
        String mnc = networkOperator.substring(3);
        //tvmcc.setText("MCC: " + mcc);
         cid = cellLocation.getCid();
         lac = cellLocation.getLac();
           // int signalstrength=signalStrength();

        //textCID.setText("gsm cell id: " + String.valueOf(cid));
        textGeo.setText(""+signalstrength);
        tvcarrier.setText(tm.getNetworkOperatorName());


    }

    void getInfos(){
        TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        try{
            List<CellInfo> cellinofs=tm.getAllCellInfo();
            //tv2.setText("size: "+cellinofs.size());

            for(CellInfo cinfo:cellinofs) {
                if(cinfo instanceof CellInfoGsm) {
                    CellInfo info = (CellInfoGsm)cinfo;
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
                    CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
                    CellSignalStrengthGsm cellStrength = cellInfoGsm.getCellSignalStrength();
                    cid = cellIdentity.getCid();
                     lac = cellIdentity.getLac();
                    textCID.setText("gsm cell id: " + String.valueOf(cid));
                    textLAC.setText("gsm location area code: " + String.valueOf(lac));

                }

            }
        }catch (Exception e){
            tv2.setText(e.getMessage());
        }
        /*CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
        CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
        CellSignalStrengthGsm cellStrength = cellInfoGsm.getCellSignalStrength();
        int cid=cellIdentity.getCid();
        int lac=cellIdentity.getLac();
        tvmcc.setText("MCC: "+ cellIdentity.getMcc());
        tvmnc.setText("MNC: " + cellIdentity.getMnc());
        tvcellid.setText("CellID: "+cid);
        tvlac.setText("LAC: "+lac);
        tvimei.setText("IMEI: "+tm.getDeviceId());*/
    }

    class send_Request extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            String error="sal";
            try{
                error=RqsLocation(cid,lac);
            }catch (Exception e){
                error=e.getMessage();
            }
            return error;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            textGeo.setText(
                    String.valueOf((float)myLatitude/1000000)
                            + " : "
                            + String.valueOf((float)myLongitude/1000000));

        }
    }


    /*{ "homeMobileCountryCode": 310,
  "homeMobileNetworkCode": 410,
  "radioType": "gsm",
  "carrier": "Vodafone",
  "considerIp": "true",
        "cellTowers": [
        {
            "cellId": 42,
                "locationAreaCode": 415,
                "mobileCountryCode": 310,
                "mobileNetworkCode": 410,
                "age": 0,
                "signalStrength": -60,
                "timingAdvance": 15
        }
  ]
    }*/

    //AIzaSyAdx7nDBJnI6OxmJCgLlbyMjrhlsZTX_zs google map api key

    JSONObject createJson(int cid,int lac,int mcc,int mnc,int singnal,int age,int timingadvance){
        JSONObject jsonObject=null;
        JSONObject object=new JSONObject();
        try {
            //JSONObject object=new JSONObject("cellTowers");
            object.put("homeMobileCountryCode",mcc);
            object.put("homeMobileNetworkCode",mnc);
            object.put("radioType","gsm");
            object.put("carrier","GrameenPhone");
            object.put("considerIp","true");
             jsonObject=new JSONObject();

            jsonObject.put("cellId",cid);
            jsonObject.put("locationAreaCode",lac);
            jsonObject.put("mobileCountryCode",mcc);
            jsonObject.put("mobileNetworkCode",mnc);
            jsonObject.put("age",age);
            jsonObject.put("signalStrength",singnal);
            jsonObject.put("timingAdvance",timingadvance);


        } catch (JSONException e) {
            //jsonObject.put("Error","Error");
            //Toast.makeText(getApplicationContext(),"Error "+e.getMessage(),Toast.LENGTH_SHORT).show();

        }

        JSONObject wifi=new JSONObject();

        try {
            jsonObject.put("macAddress","bc:67:1c:40:5d:ef");
            jsonObject.put("signalStrength",-59);
            jsonObject.put("age",0);
            jsonObject.put("channel",6);
            jsonObject.put("signalToNoiseRatio",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray wifiarray=new JSONArray();
        wifiarray.put(wifi);
        JSONArray array=new JSONArray();
        array.put(jsonObject);
        try {
            object.put("cellTowers",array);
            //object.put("wifiAccessPoints",wifiarray);
        } catch (JSONException e) {
            //Toast.makeText(getApplicationContext(),"Error "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }



       // Toast.makeText(getApplicationContext(),""+String.valueOf(object),Toast.LENGTH_LONG).show();
        return  object;
    }
    JSONObject jsondata;
    public class googleLocation extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... params) {
            String result="Error";
            try {
                jsondata=createJson(1193341,1903,470,04,-85,0,0);
                String urlmmap="https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyAdx7nDBJnI6OxmJCgLlbyMjrhlsZTX_zs";
                URL url=new URL(urlmmap);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(8000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");


                OutputStream outputStream=httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data=String.valueOf(jsondata);
                bufferedWriter.write(data);
                bufferedWriter.flush();

                outputStream.close();
                bufferedWriter.close();
                int code=httpURLConnection.getResponseCode();

                try {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }

                    bufferedReader.close();
                    inputStream.close();

                    return response.toString();
                }catch (Exception e){
                    result+="   Inputsteram  "+e.getMessage()+"  "+code;
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                result+="IOException : "+e.getMessage();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            try {
                JSONObject jsonObject=new JSONObject(s);

                JSONObject obj=jsonObject.getJSONObject("location");
                String lat=obj.getString("lat");
                String lng=obj.getString("lng");
                String accuracy=jsonObject.getString("accuracy");
                Toast.makeText(getApplicationContext(),lat+"  "+lng,Toast.LENGTH_LONG).show();
                textGsmCellLocation.setText("lat : "+lat);
                textCID.setText("lng : "+lng);
                textLAC.setText("Accuracy : "+accuracy);
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();

            }
        }
    }

    private String RqsLocation(int cid, int lac){

        String result = "Error ";

        String urlmmap = "http://www.google.com/glm/mmap";

        try {
            URL url = new URL(urlmmap);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.connect();

            OutputStream outputStream = httpConn.getOutputStream();
            try {
                WriteData(outputStream, cid, lac);
            }catch (IOException e){
                result="Error in WriteData";
            }

            outputStream.close();
            /*DataInputStream dataInputStream = new DataInputStream(inputStream);

            dataInputStream.readShort();
            dataInputStream.readByte();
            int code = dataInputStream.readInt();
            result = "faild  "+code+"  ";
                myLatitude = dataInputStream.readInt();
                myLongitude = dataInputStream.readInt();*/
            try {
                InputStream inputStream = httpConn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                bufferedReader.close();
                inputStream.close();

                result +="  Response : "+ response.toString();
            }catch (Exception e){
                result+="   INputsteram";
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
           result+="IOException : "+e.getMessage();
        }

        return result;

    }

    private void WriteData(OutputStream out, int cid, int lac)
            throws IOException
    {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cid);
        dataOutputStream.writeInt(lac);

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
    }

}
