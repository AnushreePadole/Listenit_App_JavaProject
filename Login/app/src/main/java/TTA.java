package com.example.login;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class TTA extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SaveDialog.SaveDialogListener {
    private TextToSpeech mTTS;
    public static String audioFileName="";
    private EditText mEditText;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private Button mButtonSpeak;
    private Button btnSave;
    private ImageView back_tta;

    ProgressDialog progressDialog;
    private String text="";

    private final String mUtteranceID=" TextToSpeech";
    private StorageReference mStorageRef;
    private ProgressDialog mProgress;
    String email="";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    DatabaseReference reference2;
    //AudioFileName fileName;
    //AudioFile name=new AudioFile();
    private Context context;
    public static  String downloadUrl="";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tta);

        mProgress=new ProgressDialog(this);
        mButtonSpeak = findViewById(R.id.buttonspeak);
        mEditText = findViewById(R.id.edittextTTA);
        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);
        btnSave=findViewById(R.id.save);
        back_tta=findViewById(R.id.back_tta);
        email=login.loginresultemail;
        progressDialog = new ProgressDialog(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();
        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                mTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {

                            if(status == TextToSpeech.SUCCESS){
                                if(text.equals("ENGLISH")){
                                mTTS.setLanguage(new Locale("en","US"));
                                speak();
                                }
                                if(text.equals("HINDI")){
                                        mTTS.setLanguage(new Locale("hin","IND"));
                                        speak();
                                }
                            }

                    }
                });
                    }
                });
            }
        });
        // spinner logic
        Spinner spinner =findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.Languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        back_tta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TTA.this,Navigation.class);
                startActivity(intent);
                finish();
            }
        });

        //for saving audio files
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }
    private void openDialog() {
        SaveDialog saveDialog = new SaveDialog();
        saveDialog.show(getSupportFragmentManager(),"save dialog");
    }
    @Override
    public void applyText(String fname) {

        //name.setAudiofilename(fname);

    audioFileName = fname;
    //Toast.makeText(this,audioFileName+" file saved",Toast.LENGTH_SHORT).show();
        try {
            generateAudioFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //uploadData();
    }

    public void generateAudioFile() throws IOException {
        File tempFile = File.createTempFile("tts_audio", ".mp3",getApplicationContext().getCacheDir());
        // synthesize the text to speech and write the audio data to the temporary file
        mTTS.synthesizeToFile(mEditText.getText().toString().trim(), null, tempFile, audioFileName);
        uploadToFirebase(tempFile);
        tempFile.delete();
    }
    private void uploadToFirebase(File tempfile) {
        progressDialog.setMessage("Please wait while Saving...");
        progressDialog.setTitle("Upload");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Uri uri=Uri.fromFile(tempfile);
        StorageReference sr = mStorageRef.child("myaudio/"+audioFileName+".mp3");
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mp3")
                .build();
        UploadTask uploadTask = sr.putFile(uri,metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrl = uri.toString();
                        AudioFile audio=new AudioFile(downloadUrl,audioFileName);
                        reference=firebaseDatabase.getInstance().getReference().child("Users");
                        reference2=reference.child(email);
                        reference2.child("Audio Files").child(audioFileName).setValue(audio);
                        progressDialog.dismiss();
                        Toast.makeText(TTA.this, "Audio uploaded sucessfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(TTA.this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text= parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void speak(){
        String inputtext = mEditText.getText().toString();
        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if(pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if(speed < 0.1) speed = 0.1f;
        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
        mTTS.speak(inputtext,TextToSpeech.QUEUE_ADD,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTTS != null) {
            mTTS.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
    }


}