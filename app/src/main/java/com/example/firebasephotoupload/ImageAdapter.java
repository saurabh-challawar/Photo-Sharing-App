package com.example.firebasephotoupload;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<PhotoUpload> mUploads;
    private ProgressBar progressBarItem;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context, List<PhotoUpload> uploads){
        mContext=context;
        mUploads=uploads;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        PhotoUpload photoUploadCurr = mUploads.get(position);
        holder.textViewName.setText(photoUploadCurr.getFileName());
        holder.viewEmail.setText(photoUploadCurr.getUser());
        //holder.imageView.setImageURI(Uri.parse(photoUploadCurr.getImageUri()));
        Picasso.get()
                .load(photoUploadCurr.getImageUri())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView textViewName, viewEmail;
        public ImageView imageView;


        @RequiresApi(api = Build.VERSION_CODES.M)
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName=itemView.findViewById(R.id.textItemName);
            imageView=itemView.findViewById(R.id.imageItemView);
            viewEmail=itemView.findViewById(R.id.viewEmail);
//            progressBarItem=itemView.findViewById(R.id.progressBarItem);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                int postion = getAdapterPosition();
                if(postion != RecyclerView.NO_POSITION){
                    mListener.onItemClick(postion);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Choose");
            MenuItem doWhatever = menu.add(Menu.NONE,1,1,"Do whatever");
            MenuItem delete = menu.add(Menu.NONE,2,2,"Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null){
                int postion = getAdapterPosition();
                if(postion != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            mListener.onWhateverClick(postion);
                            return true;
                        case 2:
                            mListener.onDeleteClick(postion);
                            return true;
                    }

                }
            }
            return false;
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);

        void onWhateverClick(int position);

        void onDeleteClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }
}
