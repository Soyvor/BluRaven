package com.exuberant.ah2019.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.exuberant.ah2019.R;
import com.exuberant.ah2019.fragments.CameraFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;

import static io.fotoapparat.result.transformer.ResolutionTransformersKt.scaled;

public class DisplayPictureAdapter extends RecyclerView.Adapter<DisplayPictureAdapter.DisplayPictureViewHolder> {

    List<PhotoResult> photoResultList;

    public DisplayPictureAdapter(List<PhotoResult> photoResultList) {
        this.photoResultList = photoResultList;
    }

    @NonNull
    @Override
    public DisplayPictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_picture, parent, false);
        return new DisplayPictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DisplayPictureViewHolder holder, int position) {
        final PhotoResult photoResult = photoResultList.get(position);
        photoResult
                .toBitmap(scaled(0.25f))
                .whenDone(new WhenDoneListener<BitmapPhoto>() {
                    @Override
                    public void whenDone(@Nullable BitmapPhoto bitmapPhoto) {
                        if (bitmapPhoto == null) {
                            return;
                        }
                        Glide.with(holder.itemView.getContext())
                                .load(bitmapPhoto.bitmap)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(holder.image);
                    }
                });

        holder.removePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraFragment.getPictureRemoveInterface().removePicture(photoResult);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoResultList.size();
    }


    class DisplayPictureViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        FloatingActionButton removePictureButton;

        DisplayPictureViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_picture);
            removePictureButton = itemView.findViewById(R.id.fab_remove_picture);
        }
    }

}
