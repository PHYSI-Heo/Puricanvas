package com.physi.pac.setter.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.physi.pac.setter.R;
import com.physi.pac.setter.data.IMGInfo;
import com.physi.pac.setter.http.HttpPacket;
import com.physi.pac.setter.utils.SwipeAndDragHelper;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> implements SwipeAndDragHelper.OnActionCompletionContract {

    private ItemTouchHelper touchHelper;
    private List<IMGInfo> filePaths = new LinkedList<>();
    private Context context;
    private String deviceID;

    public ImageAdapter(Context context, String deviceID){
        this.context = context;
        this.deviceID = deviceID;
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_image_item, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageHolder holder, final int position) {
        IMGInfo info = filePaths.get(position);
        // http://192.168.1.12:3000/res/default/sample1.jpg

        if(info.getLocalFilePath() == null){
            showThumbnailImage(holder, info);
        }else{
            showLocalImage(holder, info);
        }

        holder.item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                touchHelper.startDrag(holder);
                return false;
            }
        });
    }

    private void showLocalImage(final ImageHolder holder, IMGInfo info){
        holder.pbLoading.setVisibility(View.GONE);
        Glide.with(context)
                .asBitmap()
                .load(Uri.fromFile(new File(info.getLocalFilePath())))
                .into(holder.ivShowImg);
    }

    private void showThumbnailImage(final ImageHolder holder, IMGInfo info){
        String thumbnail = HttpPacket.BASEURL + "/res/";
        thumbnail += info.getUsfState().equals("0") ? "default/" : deviceID + "/";
        thumbnail += info.getFileName();

        if(info.getFileName().endsWith(".jpg") || info.getFileName().endsWith(".png")){
            holder.ivFileType.setImageResource(R.drawable.ic_img_type);
        }else{
            holder.ivFileType.setImageResource(R.drawable.ic_video_type);
            thumbnail = thumbnail.substring(0, thumbnail.lastIndexOf(".")) + ".png";
        }

        Glide.with(context)
                .asBitmap()
                .load(thumbnail)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        holder.pbLoading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.ivShowImg);
    }

    @Override
    public int getItemCount() {
        return filePaths.size();
    }

    public void setItems(List<IMGInfo> filePaths){
        this.filePaths = filePaths;
        notifyDataSetChanged();
    }


    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Collections.swap(filePaths, oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        filePaths.remove(position);
        notifyItemRemoved(position);
    }
}
