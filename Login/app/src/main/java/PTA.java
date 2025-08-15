package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.login.databinding.ActivityPtaBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class PTA extends AppCompatActivity implements SaveDialog.SaveDialogListener {

    ActivityPtaBinding binding;
    ImageView browse;
    //TextView output;
    private final int CHOOSE_PDF_FROM_DEVICE=1001;

    public static String audioFileName="";
    //AudioFileName fileName;
    private static final String TAG="PTA";
    ProgressDialog progressDialog;

    private EditText extracttextpdf;
    private Intent intent;
    private boolean isPaused = false;
    ImageView play_icon,pause_icon,delete,save,back_pta;
    TextToSpeech mtts;
    int currentPos;
    String selectedText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    DatabaseReference reference2;
    String email="";
    boolean isPlaying = false;


    //AudioFile name=new AudioFile();
    private StorageReference mStorageRef;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityPtaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        extracttextpdf = findViewById(R.id.pdf_edittext);
        back_pta=findViewById(R.id.back_pta);
        progressDialog = new ProgressDialog(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();
        email=login.loginresultemail;

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.pause:
                    if (mtts != null) {
                        mtts.stop();
                        isPaused = true;
                        isPlaying = false;
                    }
                    break;
                case R.id.delete:
                    String text= extracttextpdf.getText().toString();
                    if(text.isEmpty())
                    {
                        Toast.makeText(PTA.this,"There is no text to clear",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        extracttextpdf.setText("");
                        mtts.stop();
                    }

                    break;
                case R.id.browse:
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/pdf");

                    startActivityForResult(intent, CHOOSE_PDF_FROM_DEVICE);

                    break;
                case R.id.play:
                    if(!isPlaying) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mtts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        if (status == TextToSpeech.SUCCESS) {

                                            mtts.setLanguage(new Locale("en", "US"));
                                            mtts.setSpeechRate(1.0f);

                                            mtts.speak(extracttextpdf.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                            isPlaying = true;
                                        }
                                    }
                                });
                            }
                        });
                    }
                    break;
                case R.id.savepdf:
                    openDialog();
                    break;
            }

            return true;
        });
        back_pta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PTA.this,Navigation.class);
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
        //fileName=new AudioFileName(fname);

       audioFileName=fname;
        //Toast.makeText(this,audioFileName+" file saved",Toast.LENGTH_SHORT).show();
        try {
            generateAudioFile1();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateAudioFile1() throws IOException {
        File tempFile2 = File.createTempFile("tts_audio2", ".mp3",getApplicationContext().getCacheDir());
        // synthesize the text to speech and write the audio data to the temporary file
        mtts.synthesizeToFile(extracttextpdf.getText().toString().trim(), null, tempFile2,audioFileName);
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
                        Toast.makeText(PTA.this, "Audio uploaded sucessfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PTA.this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent resultData)
    {
        super.onActivityResult(requestCode,resultCode,resultData);
        if(requestCode==CHOOSE_PDF_FROM_DEVICE && resultCode==RESULT_OK)
        {
            if(resultData!=null)
            {
                Log.d(TAG,"onActivityResult: "+resultData.getData());
                extractTextPdf(resultData.getData());
            }
        }


    }
    InputStream inputStream;
    private void extractTextPdf(Uri uri)
    {
        try {
            inputStream=PTA.this.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    new Thread(new Runnable() {
        @Override
        public void run() {

        String filecontent="";
        StringBuilder builder=new StringBuilder();
        PdfReader reader=null;
        try {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            {
                reader=new PdfReader(inputStream);

                int pages=reader.getNumberOfPages();
                for(int i=1;i<=pages;i++)
                {
                    filecontent+=PdfTextExtractor.getTextFromPage(reader,i);

                }
                builder.append(filecontent);

            }
            reader.close();
            runOnUiThread(() ->  extracttextpdf.setText(builder.toString()));

        }
        catch(Exception e)
        {
            Log.d(TAG,"run"+e.getMessage());
        }
        }
    }).start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mtts != null) {
            mtts.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mtts != null) {
            mtts.stop();
            mtts.shutdown();
        }
    }

}