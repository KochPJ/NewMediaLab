package com.example.newmedialab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class NewStimuliAddLanguageDialog extends AppCompatDialogFragment {
    private EditText editLanguage;
    private AddLanguageListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_stimuli_add_language, null);

        builder.setView(view)
                .setTitle("Information")
                .setMessage("this is a dialog")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String language = editLanguage.getText().toString();
                        listener.applyText(language);

                    }
                });
        editLanguage = view.findViewById(R.id.dialog_new_stimuli_add_language);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AddLanguageListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddLanguageListener");
        }
    }

    public interface AddLanguageListener{
        void applyText(String language);
    }
}
