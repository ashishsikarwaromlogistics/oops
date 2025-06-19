package com.example.omoperation.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.omoperation.R;
import com.example.omoperation.model.PunchInMod;
import com.example.omoperation.model.PunchInResp;
import com.example.omoperation.model.PunchOutMod;
import com.example.omoperation.model.PunchOutResp;
import com.example.omoperation.network.ApiClient;
import com.example.omoperation.network.ServiceInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button btn1,btn2,change;
    EditText code, lat,lng,compcode,add;

    String formattedDate;
   ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd=new ProgressDialog(this);
        Calendar calendar = Calendar.getInstance();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
         formattedDate = df.format(c);
        code=findViewById(R.id.code);
        lat=findViewById(R.id.lat);
        lng=findViewById(R.id.lng);
        compcode=findViewById(R.id.compcode);
        add=findViewById(R.id.add);
        //,,,



        lat.setText("28.6775124");
        lng.setText("77.1419103");
        compcode.setText("1306");
        add.setText("130, 130, Electricity Colony Rd, Indira Colony, Shalimar Bagh, New Delhi, Delhi 110035, India");


        change=findViewById(R.id.change);
        btn1=findViewById(R.id.btn1);
        btn2=findViewById(R.id.btn2);




        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lat.setText("28.6756809");
                lng.setText("77.1432056");
                compcode.setText("7500");
                add.setText("141, Indira Colony, Tri Nagar, Delhi, 110035");
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                PunchInMod punchINMod=new PunchInMod();
                punchINMod.setShift("D");
                punchINMod.setTemp("98");
                punchINMod.setSetu_status("G");

              /*  punchINMod.setLat(28.6775124);
                punchINMod.setLong(77.1419103);
                punchINMod.setAddress("130, 130, Electricity Colony Rd, Indira Colony, Shalimar Bagh, New Delhi, Delhi 110035, India");
                punchINMod.setBcode("1306");
                */
                punchINMod.setBcode(compcode.getText().toString());
                punchINMod.setLat(Double.parseDouble(lat.getText().toString()));
                punchINMod.setLong(Double.parseDouble(lng.getText().toString()));
                punchINMod.setAddress(add.getText().toString());
                punchINMod.setEmpcode(code.getText().toString());

                punchINMod.setTmode("Walking");
                ApiClient.getClient().create(ServiceInterface.class).punchin(punchINMod).enqueue(new Callback<PunchInResp>() {
                    @Override
                    public void onResponse(Call<PunchInResp> call, Response<PunchInResp> response) {
                       pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<PunchInResp> call, Throwable t) {
                        pd.dismiss();
                    }
                });
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                PunchOutMod punchOUTMod=new PunchOutMod();
                punchOUTMod.setStatus("visit_entry");
                punchOUTMod.setEmpcode(code.getText().toString());

                punchOUTMod.setVisit_type("Attendance");

                punchOUTMod.setFrom_date(formattedDate);
                punchOUTMod.setTo_date(formattedDate);
                punchOUTMod.setTime("OUT-TIME");



                punchOUTMod.setSource(compcode.getText().toString());
                punchOUTMod.setDestination(compcode.getText().toString());
                punchOUTMod.setBcode(compcode.getText().toString());
                punchOUTMod.setLat(lat.getText().toString());
                punchOUTMod.setLong(lng.getText().toString());
                punchOUTMod.setAddress(add.getText().toString());

                punchOUTMod.setReporting_to("");
                punchOUTMod.setRemarks("");
                ApiClient.getClient().create(ServiceInterface.class).punchout(punchOUTMod).enqueue(new Callback<PunchOutResp>() {
                    @Override
                    public void onResponse(Call<PunchOutResp> call, Response<PunchOutResp> response) {
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<PunchOutResp> call, Throwable t) {
                        pd.dismiss();
                    }
                });

    }


});


    }




}