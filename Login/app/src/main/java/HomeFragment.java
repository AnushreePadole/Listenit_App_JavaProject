package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class HomeFragment extends Fragment {

    public static Activity context;
    //Activity context1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context=getActivity();
       // context1=getActivity();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }
    public void onStart(){
        super.onStart();
        TextView tta=getView().findViewById(R.id.tta);
        TextView pta=getView().findViewById(R.id.pta);
        TextView ita=getView().findViewById(R.id.ita);
        tta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,TTA.class);
                startActivity(intent);
            }
        });
        pta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,PTA.class);
                startActivity(intent);
            }
        });
        ita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ITA.class);
                startActivity(intent);
            }
        });

    }

}