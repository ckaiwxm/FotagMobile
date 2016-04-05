package io.github.dongwoo1005.fotagmobile;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by dwson on 3/24/16.
 */
public class EnterUriDialog extends DialogFragment {

    private EnterUriDialogListener mListener;

    public EnterUriDialog() {

    }

    public static EnterUriDialog newInstance(String title) {
        EnterUriDialog frag = new EnterUriDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EnterUriDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View promptView = inflater.inflate(R.layout.fragment_enter_uri, null);
        final EditText input = (EditText) promptView.findViewById(R.id.text_enter_uri);
        alertDialogBuilder.setView(promptView);

        String title = getArguments().getString("title");
        alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                mListener.onDialogPositiveClick(EnterUriDialog.this, input.getText().toString());
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    public interface EnterUriDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String uri);
    }
}
