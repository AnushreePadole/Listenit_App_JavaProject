package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    private List<AudioFile> audioList;
    private Context context;


    //AudioFile name=new AudioFile();
    public AudioAdapter(Context context,List<AudioFile> audioList){
        this.context=context;
        this.audioList=audioList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AudioFile audioFile = audioList.get(position);
       // holder.bind(audioFile);
        // set the audio URL on the view
        holder.audioUrlTextView.setText(audioFile.getAudiofilename());


        // set up a MediaPlayer object to play the audio when the user clicks on it

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,mediaPlayer.class);
                intent.putExtra("audio",audioFile.getUrl());
                intent.putExtra("key",audioFile.getKey());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView audioUrlTextView;
        public Button playButton;

        public ImageView deleteSaved;
        public CardView card;



        public ViewHolder(View view) {
            super(view);
            audioUrlTextView = view.findViewById(R.id.recTitle);
            playButton = view.findViewById(R.id.saved_play_audio);
            //deleteSaved=view.findViewById(R.id.de);
            card=view.findViewById(R.id.recCard);

        }
    }
}

