package com.example.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SaveDialog extends AppCompatDialogFragment
{
    private EditText sFile;
    public static String filename;
    private SaveDialogListener listener;
    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view).setTitle("Audio filename").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                filename = sFile.getText().toString();
                listener.applyText(filename);

            }
        });
        sFile = view.findViewById(R.id.filename);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SaveDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"Must implement SaveDialogListener");
        }
    }

    public interface SaveDialogListener{
        default void applyText(String fname)
        {

        }
    }
}
