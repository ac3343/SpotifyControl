package com.ac3343.spotcontroller.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.ac3343.spotcontroller.CoreActivity;
import com.ac3343.spotcontroller.R;
import com.ac3343.spotcontroller.SpotifyApplication;
import com.ac3343.spotcontroller.database.DBAlbum;
import com.ac3343.spotcontroller.library.Album;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ImageUri;

public class AlbumView extends LinearLayout {
    private ImageView albumCover;
    private TextView albumName;
    private TextView artistName;
    private DBAlbum savedAlbum;
    private LinearLayout textLayout;
    private Button removeButton;


    public AlbumView(Context context){
        super(context);
        super.setOrientation(LinearLayout.HORIZONTAL);

        albumCover = new ImageView(context);
        albumName = new TextView(context);
        artistName = new TextView(context);
        removeButton = new Button(context);

        textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.addView(albumName);
        textLayout.addView(artistName);


        albumName.setAllCaps(false);
        albumName.setSingleLine(true);
        albumName.setTextColor(Color.parseColor("#e1ece3"));
        albumName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        artistName.setAllCaps(false);
        artistName.setSingleLine(true);
        artistName.setTextColor(Color.parseColor("#a8b2a8"));
        artistName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        artistName.setEllipsize(TextUtils.TruncateAt.END);

        removeButton.setAllCaps(false);
        removeButton.setSingleLine(true);
        removeButton.setTextColor(Color.parseColor("#a8b2a8"));
        removeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        setPadding(0,5,0,5);


        addView(albumCover);
        addView(textLayout);

        setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        albumCover.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        textLayout.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, .8f));


    }
    public AlbumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOrientation(LinearLayout.HORIZONTAL);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AlbumView, 0, 0);
        a.recycle();
    }

    public void setAlbumInfo(DBAlbum info){
        CoreActivity core = (CoreActivity) getContext();
        SpotifyAppRemote remote = core.getRemote();
        if(remote != null){
            remote.getImagesApi().getImage(new ImageUri(info.getmImageURI()), Image.Dimension.THUMBNAIL).setResultCallback((imageBitMap) ->{
                albumCover.setImageBitmap(imageBitMap);
            });
        }{
            core.setOnRemoteConnect(() ->{
                core.getRemote().getImagesApi().getImage(new ImageUri(info.getmImageURI()), Image.Dimension.THUMBNAIL).setResultCallback((imageBitMap) ->{
                    albumCover.setImageBitmap(imageBitMap);
                });
            });
        }
        int textLimit = 30;
        String nameText = info.getmName();
        if(nameText.length() > textLimit){
            nameText = nameText.substring(0, textLimit - 3) + "...";
        }
        else if(nameText.length() < textLimit){
            StringBuilder sb = new StringBuilder(nameText);
            while (sb.length() < textLimit){
                sb.append(" ");
            }
            nameText = sb.toString();
        }

        albumName.setText(nameText);
        artistName.setText(info.getmArtistName());
        savedAlbum = info;
        albumCover.setPadding(10, 0, 15, 0);
        removeButton.setText("X");
        invalidate();
        requestLayout();
    }

    public void AddSearchDetails(){
        String searchText = "Album - " + savedAlbum.getmArtistName();
        artistName.setText(searchText);
        invalidate();
        requestLayout();
    }

    public void setOnClicks(OnClickListener openAlbum, OnClickListener removeAlbum){
        textLayout.setOnClickListener(openAlbum);
        removeButton.setOnClickListener(removeAlbum);
    }

    public void makeRecentlyPlayed(){
        removeButton.setBackgroundColor(9657693);
        removeButton.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, .05f));
        addView(removeButton);
    }

    public DBAlbum getAlbum(){
        return savedAlbum;
    }
    public LinearLayout getTextLayout(){return textLayout;}
}
