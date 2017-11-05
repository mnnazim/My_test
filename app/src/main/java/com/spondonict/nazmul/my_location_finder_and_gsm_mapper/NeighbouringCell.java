package com.spondonict.nazmul.my_location_finder_and_gsm_mapper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by USER on 11/5/2017.
 */

public class NeighbouringCell extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neighbouringcell);
        TextView textGsmCellLocation = (TextView)findViewById(R.id.gsmcelllocation);
        TextView textMCC = (TextView)findViewById(R.id.mcc);
        TextView textMNC = (TextView)findViewById(R.id.mnc);
        TextView textCID = (TextView)findViewById(R.id.cid);


        //retrieve a reference to an instance of TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();

        String networkOperator = telephonyManager.getNetworkOperator();
        String mcc = networkOperator.substring(0, 3);
        String mnc = networkOperator.substring(3);
        textMCC.setText("mcc: " + mcc);
        textMNC.setText("mnc: " + mnc);

        int cid = cellLocation.getCid();
        int lac = cellLocation.getLac();
        textGsmCellLocation.setText(cellLocation.toString());
        textCID.setText("gsm cell id: " + String.valueOf(cid));

        TextView Neighboring = (TextView)findViewById(R.id.neighboring);
        List<NeighboringCellInfo> NeighboringList = telephonyManager.getNeighboringCellInfo();
        Toast.makeText(getApplicationContext(),NeighboringList.size()+" size",Toast.LENGTH_SHORT).show();
        String stringNeighboring = "Neighboring List- Lac : Cid : RSSI\n";
        for(int i=0; i < NeighboringList.size(); i++){

            String dBm;
            int rssi = NeighboringList.get(i).getRssi();
            if(rssi == NeighboringCellInfo.UNKNOWN_RSSI){
                dBm = "Unknown RSSI";
            }else{
                dBm = String.valueOf(-113 + 2 * rssi) + " dBm";
            }

            stringNeighboring = stringNeighboring
                    + String.valueOf(NeighboringList.get(i).getLac()) +" : "
                    + String.valueOf(NeighboringList.get(i).getCid()) +" : "
                    + dBm +"\n";
        }

        Neighboring.setText(stringNeighboring);

    }
}
