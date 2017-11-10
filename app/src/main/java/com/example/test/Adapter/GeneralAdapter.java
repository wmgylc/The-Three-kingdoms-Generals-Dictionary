package com.example.test.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.DataBase.General;
import com.example.test.R;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by admin on 2017/11/2.
 */

public class GeneralAdapter extends RecyclerView.Adapter<GeneralAdapter.ViewHolder> {

    public interface onItemClickListener {
        void onClick(View view, int position, int id);
        void onLongClick(View view, int position, int id);
    }

    private Context mContext;

    private List<General> mGeneralList;

    private onItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView info;
        TextView country;
        ImageView image;
        ImageView concerned;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            country = view.findViewById(R.id.country);
            info = view.findViewById(R.id.info);
            image = view.findViewById(R.id.general_image);
            concerned = view.findViewById(R.id.concerned);

        }
    }

    public GeneralAdapter(List<General> generalList) {
        mGeneralList = generalList;
    }

    public void setAdapterData(List<General> goodsList)  {
        mGeneralList = goodsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.general_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        if (mContext == null) {
            mContext = parent.getContext();
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final General general = mGeneralList.get(position);
        holder.name.setText(general.getName());
        holder.info.setText(general.getInfo());
        holder.country.setText(general.getCountry());
        if (general.getImagePath() != null) {
            //Log.d("TAG", "position:" + position + "&&" + "notnull" + general.getName());
            String path=general.getImagePath();
//            byte[] byteArray= Base64.decode(path, Base64.DEFAULT);
//            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
//            Bitmap bitmap=BitmapFactory.decodeStream(byteArrayInputStream);
//            holder.image.setImageBitmap(bitmap);

            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            holder.image.setImageBitmap(bitmap);
        } else {
            holder.image.setImageResource(R.drawable.circle);
        }

        if (general.getConcerned() != 0) {
            //Log.d("TAG", "onBind" + position);
            holder.concerned.setImageResource(R.drawable.star_on);
        } else {
            holder.concerned.setImageDrawable(null);
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(holder.itemView, position, general.getid());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                //不能依靠position来获得good的id值
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onLongClick(holder.itemView, position, general.getid());
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mGeneralList.size();
    }

}
