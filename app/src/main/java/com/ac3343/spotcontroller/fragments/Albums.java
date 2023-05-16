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
import com.google.android.material.textfield.TextInputEditText;

public class Albums extends Fragment {
    View view;


    public Albums(){
        super(R.layout.fragment_albums);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null){
            Log.i("Albums", "return of the mack");
        }
        return view = inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoadingScrollView lsv = view.findViewById(R.id.albumScroll);
        lsv.setLinearLayout(view.findViewById(R.id.albumList), LibraryItems.Album);

        EditText searchText = view.findViewById(R.id.albumLibrarySearch);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lsv.search(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}