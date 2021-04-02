package com.sangsolutions.sang.Adapter.CheckImageAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sangsolutions.sang.R;
import com.sangsolutions.sang.Tools;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class CheckImageAdapter extends RecyclerView.Adapter<CheckImageAdapter.ViewHolder> {

    List<String>list;
    Context context;
    OnClickListener listener;
    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {

        void onImageClickListener( String s, int position);
    }
    public CheckImageAdapter(Context context, List<String> list) {
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public CheckImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CheckImageAdapter.ViewHolder holder, int position) {
        Picasso.get().load(list.get(position)).resize(200,350).centerCrop().into(holder.photo);
        Log.d("PicassoPicasso",list.get(position));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete?");
                builder.setMessage("Do you want to delete this image?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            }
        });
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("list_pos",list.get(position));
                listener.onImageClickListener(list.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
