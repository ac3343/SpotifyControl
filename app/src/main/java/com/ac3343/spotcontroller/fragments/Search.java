package com.ac3343.spotcontroller.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.LibraryItems;
import com.ac3343.spotcontroller.R;
import com.ac3343.spotcontroller.views.LoadingScrollView;

public class Search extends Fragment {

    public Search(){
        super(R.layout.fragment_search);
    }

    View view;
    final String TAG = "SEARCHFRAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CoreActivity daddy = (CoreActivity) view.getContext();
        EditText searchText = (EditText) view.findViewById(R.id.editTextSearch);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    daddy.search(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}