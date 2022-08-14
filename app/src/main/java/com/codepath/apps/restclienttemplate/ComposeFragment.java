package com.codepath.apps.restclienttemplate;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ComposeFragment extends DialogFragment {
    private final static String TAG = "ComposeFragment";
    private final static int GET_IMAGE = 7;
    TextView name;
    TextView username;
    ImageView profile;
    EditText content;
    ImageView close;
    Button send;
    ImageView imgSend;

    final int maxCount = 280;

    TextView counter;


    public ComposeFragment() {}

    public static ComposeFragment newInstance(String title) {
        ComposeFragment frg = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frg.setArguments(args);
        return frg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compose_fragment, container);
        name = view.findViewById(R.id.tvName2);
        username = view.findViewById(R.id.tvUsername2);
        profile = view.findViewById(R.id.imgProfile2);
        counter = view.findViewById(R.id.tvCounter);
        content = view.findViewById(R.id.etTweetContent);
        counter.setText(String.valueOf(maxCount));

        close = view.findViewById(R.id.close);

        send = view.findViewById(R.id.btnTweet);
        imgSend = view.findViewById(R.id.imgSend);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username.setText("Bogosla");
        name.setText("James Destine");
        String already = Utils.readPref(getContext(), "draft", "..");
        if (already != null && !already.equals("..")) {
            content.setText(already);
            counter.setText(String.valueOf((maxCount - already.length())));
        }


        imgSend.setOnClickListener(view13 -> getImage());

        close.setOnClickListener(view12 -> {
            if(content != null && !content.getText().toString().trim().isEmpty())
                ((TimelineActivity)getActivity()).showAlert(content.getText().toString().trim());
            dismiss();
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int l = editable.length();
                int c = maxCount - l;
                if (c < 0) counter.setTextColor(getResources().getColor(R.color.red));
                counter.setText(String.valueOf((c)));
            }
        });
        send.setOnClickListener(view1 -> {
           if(content == null || content.getText().toString().trim().isEmpty())
               return;
           ((TimelineActivity)getActivity()).postTweet(content.getText().toString());
           dismiss();
        });

        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }


    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);

        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(TAG, String.valueOf(requestCode));
        if(resultCode == RESULT_OK && requestCode == GET_IMAGE) {
            Uri imgUri = data.getData();
            imgSend.setImageURI(imgUri);
        }
    }

    private void getImage() {
        Intent gal = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gal, GET_IMAGE);
    }
}
