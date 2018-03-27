package com.example.prem.findimage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prem.findimage.dataobjects.Image;
import com.example.prem.findimage.dataobjects.ImageFactory;

import java.util.ArrayList;
import java.util.List;

import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by Prem on 09-Mar-18.
 */

/**
 * This activity is used to display Image
 * Also it is has features like
 * Zoom
 * Next
 * Prev
 */
public class ImageViewActivity extends AppCompatActivity {
    private PhotoDraweeView photoDraweeView;
    private TextView info_title,info_url;
    private List<Image> images;
    private FloatingActionButton nextButton,prevButton;
    private int position;
    private Image image;
    private LinearLayout layout;
    private ImageButton info_btn;
    private ImageFactory imageFactory;
    public ImageViewActivity(){
        imageFactory = ImageFactory.getImageFactory();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        initViews();
        Intent intent = getIntent();
        //images = (ArrayList<Image>)intent.getSerializableExtra("image");
        images = imageFactory.getImageList();
        position = intent.getIntExtra("position",0);
        image = images.get(position);
        setImage(image);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Set ic_next image
                 */
                if(position < images.size()-1){
                    position++;
                    image = images.get(position);
                    setImage(image);
                }
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Set ic_previous image
                 */
                if(position > 0){
                    position--;
                    image = images.get(position);
                    setImage(image);
                }else{
                    Toast.makeText(getApplicationContext(),"Reached first image",Toast.LENGTH_SHORT).show();
                }
            }
        });
        info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Information button to see the title and url
                 */
                if(layout.getVisibility() == View.GONE){
                    layout.setVisibility(View.VISIBLE);
                }else{
                    layout.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * Initialize all the views
     */
    public void initViews(){
        photoDraweeView = findViewById(R.id.view_image);
        nextButton = findViewById(R.id.btn_next_image);
        prevButton = findViewById(R.id.btn_prev_image);
        layout = findViewById(R.id.info_layout);
        info_btn = findViewById(R.id.info_btn);
        info_url = findViewById(R.id.info_url);
        info_title = findViewById(R.id.info_title);
    }

    /**
     * Sets image to imageview and also sets title and url
     * @param image
     */
    public void setImage(Image image){
        String url = "https://i.imgur.com/"+image.getId()+"h.jpg";
        info_title.setText("Title: "+image.getTitle());
        info_url.setText("Url: "+url);
        photoDraweeView.setPhotoUri(Uri.parse(url));
    }
}
