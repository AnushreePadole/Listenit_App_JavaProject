package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.login.databinding.ActivityItaBinding;
import com.example.login.databinding.ActivityPtaBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ITA extends AppCompatActivity implements SaveDialog.SaveDialogListener{

    ActivityItaBinding binding;
    ImageView clear,getImage,copy,play,pause,save,back_ita;
    EditText extracttxt;
    Uri imageuri;
    TextRecognizer textRecognizer;
    ProgressDialog progressDialog;
    TextToSpeech tts;
    public static String audioFileName;
   // AudioFileName fileName;
    private int mTTSstatus;
    boolean isPaused = false;
    int lastPosition=0;
    String utteranceId = "resume";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    DatabaseReference reference2;
    String email="";
    //AudioFile name=new AudioFile();
    private StorageReference mStorageRef;
    boolean isPlaying = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityItaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        extracttxt=findViewById(R.id.img_edittext);
        back_ita=findViewById(R.id.back_ita);
        progressDialog = new ProgressDialog(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();
        email=login.loginresultemail;

        textRecognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.pause:
                    if (tts != null) {
                        tts.stop();
                        isPaused = true;
                    }
                    break;
                case R.id.delete:
                    String text= extracttxt.getText().toString();
                    if(text.isEmpty())
                    {
                        Toast.makeText(ITA.this,"There is no text to clear",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        extracttxt.setText("");
                        tts.stop();
                    }

                    break;
                case R.id.browse:
                    ImagePicker.with(ITA.this)
                            .crop()	    			//Crop image(Optional), Check Customization for more option
                            .compress(1024)			//Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                            .start();



                    break;
                case R.id.play:
                    if(!isPlaying){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status==TextToSpeech.SUCCESS){
                                mTTSstatus = TextToSpeech.SUCCESS;
                                tts.setLanguage(new Locale("en","US"));
                                tts.setSpeechRate(1.0f);
                                tts.speak(extracttxt.getText().toString(),TextToSpeech.QUEUE_ADD,null);
                                isPlaying=true;

                            }
                        }
                    });
                        }
                    });}
                    break;
                case R.id.savepdf:
                    openDialog();
                    break;
            }

            return true;
        });

        back_ita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ITA.this,Navigation.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void openDialog() {
        SaveDialog saveDialog = new SaveDialog();
        saveDialog.show(getSupportFragmentManager(),"save dialog");
    }
    @Override
    public void applyText(String fname) {

        audioFileName=fname;

        try {
            generateAudioFile2();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateAudioFile2() throws IOException {
        File tempFile2 = File.createTempFile("tts_audio2", ".mp3",getApplicationContext().getCacheDir());
        // synthesize the text to speech and write the audio data to the temporary file
        tts.synthesizeToFile(extracttxt.getText().toString().trim(), null, tempFile2,TTA.audioFileName);
        uploadToFirebase(tempFile2);
        tempFile2.delete();
    }
    private void uploadToFirebase(File tempfile2) {
        progressDialog.setMessage("Please wait while Saving...");
        progressDialog.setTitle("Upload");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Uri uri=Uri.fromFile(tempfile2);
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
                        String downloadUrl = uri.toString();
                        AudioFile audio=new AudioFile(downloadUrl,audioFileName);
                        reference=firebaseDatabase.getInstance().getReference().child("Users");
                        reference2=reference.child(email);
                        reference2.child("Audio Files").child(audioFileName).setValue(audio);
                        progressDialog.dismiss();
                        Toast.makeText(ITA.this, "Audio uploaded sucessfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ITA.this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
        {
            if(data != null)
            {
                imageuri=data.getData();
                Toast.makeText(this,"image selected",Toast.LENGTH_SHORT).show();
                recognizeText();
            }
        }
        else
        {
            Toast.makeText(this,"Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizeText() {
        if(imageuri!=null){
            try {
                InputImage inputImage=InputImage.fromFilePath(ITA.this,imageuri);
                Task<Text> result=textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        String recognizeText=text.getText();
                        extracttxt.setText(recognizeText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ITA.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (IOException e) {
                //throw new RuntimeException(e);
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tts != null) {
            tts.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}