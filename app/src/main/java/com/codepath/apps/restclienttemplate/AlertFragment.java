package com.codepath.apps.restclienttemplate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AlertFragment extends DialogFragment {
    private final static String TAG = "AlertFragment";
    public String draft;
    public AlertFragment() {}

    public static AlertFragment newInstance(String title) {
        AlertFragment frg = new AlertFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        frg.setArguments(bundle);
        return frg;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage("Wanna save your message??");
        alertBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
            if (dialogInterface != null) {
                Utils.savePref(getContext(), "draft", draft);
                dialogInterface.dismiss();
            }

        });

        alertBuilder.setNegativeButton("No", (dialogInterface, i) -> {
            if (dialogInterface != null) {
                Utils.savePref(getContext(), "draft", "..");
                dialogInterface.dismiss();
            }
        });

        return alertBuilder.create();
    }
}

