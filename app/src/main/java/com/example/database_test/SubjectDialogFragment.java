package com.example.database_test;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

public class SubjectDialogFragment extends DialogFragment {

    // Host activity must implement
    public interface OnSubjectEnteredListener {
        void onSubjectEntered(String subject);
    }

    private OnSubjectEnteredListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText subjectEditText = new EditText(getActivity());
        subjectEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        subjectEditText.setMaxLines(1);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.subject)
                .setView(subjectEditText)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String subject = subjectEditText.getText().toString();
                        mListener.onSubjectEntered(subject.trim());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnSubjectEnteredListener) activity;
    }
}
