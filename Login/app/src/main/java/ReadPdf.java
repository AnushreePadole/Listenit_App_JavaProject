package com.example.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;

public class ReadPdf extends AppCompatActivity {
    TextView output;
    private static final String PRIMARY="primary";
    private static final String LOCAL_STORAGE ="/storage/self/primary/";
    private static final String EXT_STORAGE ="/storage/7764-A034";
    private static final String COLON =":";
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        output=findViewById(R.id.outputtextView);

    }


    public void readPdfFile(Uri uri) {

        String fullpath;
        if (uri.getPath().contains(PRIMARY)) {
            fullpath = LOCAL_STORAGE + uri.getPath().split(COLON)[1];
        } else {
            fullpath = EXT_STORAGE + uri.getPath().split(COLON)[1];
        }
        Log.v("URI", uri.getPath() + " " + fullpath);

        String stringParser="";
        try {
            PdfReader pdfReader=new PdfReader(fullpath);
            int n=pdfReader.getNumberOfPages();
            stringParser=PdfTextExtractor.getTextFromPage(pdfReader,1).trim();

            pdfReader.close();
            output.setText(stringParser);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }



}
