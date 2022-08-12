package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    TextView name;
    TextView username;
    ImageView profile;
    EditText content;
    ImageView close;
    Button send;
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
        return inflater.inflate(R.layout.compose_fragment, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.tvName2);
        username = view.findViewById(R.id.tvUsername2);
        profile = view.findViewById(R.id.imgProfile2);
        counter = view.findViewById(R.id.tvCounter);
        content = view.findViewById(R.id.etTweetContent);
        close = view.findViewById(R.id.close);

        send = view.findViewById(R.id.btnTweet);
        int maxCount = 280;
        counter.setText(String.valueOf(maxCount));
        username.setText("Bogosla");
        name.setText("James Destine");

        close.setOnClickListener(view12 -> {
            dismiss();
        });

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int l = editable.length();
                int c = maxCount - l;
                if (c < 0) counter.setTextColor(getResources().getColor(R.color.red));
                counter.setText(String.valueOf((c)));
            }
        });
        send.setOnClickListener(view1 -> {
           if(content == null && content.getText().toString().trim().isEmpty())
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
}
