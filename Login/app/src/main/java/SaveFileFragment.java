package com.example.login;
import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
public class SaveFileFragment extends Fragment {
    private DatabaseReference databaseReference;
    DatabaseReference reference;
    DatabaseReference reference2;
    DatabaseReference reference3;
    private AudioAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_file, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(requireContext(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        List<AudioFile> audioFiles = new ArrayList<>();
        adapter = new AudioAdapter(requireContext(),audioFiles);
        recyclerView.setAdapter(adapter);
        //AudioFile audioFile=new AudioFile(TTA.downloadUrl,TTA.audioFileName);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        reference=databaseReference.child(login.loginresultemail);
        reference2=reference.child("Audio Files");
        reference3=reference2.child(TTA.audioFileName);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    try {
                        // Attempt to get the value of AudioFile
                        AudioFile audioFile = dataSnapshot.getValue(AudioFile.class);
                        String itemSnapshot;
                        audioFile.setKey(dataSnapshot.getKey());



                        audioFiles.add(audioFile);
                    } catch (DatabaseException e) {

                    }
                }
                adapter.notifyDataSetChanged();
            }
            @SuppressLint("RestrictedApi")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
        return view;
    }
}

