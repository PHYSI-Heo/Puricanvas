package com.physi.pac.setter.list;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.physi.pac.setter.R;

public class ImageHolder extends RecyclerView.ViewHolder {

    ImageView ivShowImg, ivFileType;
    ProgressBar pbLoading;
    RelativeLayout item;

    public ImageHolder(@NonNull View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.rl_img_item);

        ivShowImg = itemView.findViewById(R.id.iv_show_image);
        ivFileType = itemView.findViewById(R.id.iv_file_type);

        pbLoading = itemView.findViewById(R.id.pb_loading);
    }
}
