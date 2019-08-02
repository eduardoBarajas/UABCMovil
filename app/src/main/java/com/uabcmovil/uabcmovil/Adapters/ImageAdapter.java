package com.uabcmovil.uabcmovil.Adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.uabcmovil.uabcmovil.R;

public class ImageAdapter extends BaseAdapter {

    private Context context;

    public ImageAdapter(Context context){ this.context = context; }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            if(metrics.densityDpi>=200 && metrics.densityDpi <300)
                imageView.setLayoutParams(new ViewGroup.LayoutParams(70, 80));
            if(metrics.densityDpi>=300 && metrics.densityDpi <400)
                imageView.setLayoutParams(new ViewGroup.LayoutParams(75, 85));
            if(metrics.densityDpi>=400)
                imageView.setLayoutParams(new ViewGroup.LayoutParams(150, 160));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {R.drawable.pc_icon,R.drawable.pc_icon,R.drawable.pc_icon,R.drawable.pc_icon,R.drawable.pc_icon,
            R.drawable.pc_icon,R.drawable.pc_icon,R.drawable.pc_icon,R.drawable.pc_icon};

    public void setSelected(int posicion){
        int lastIndex = -1;
        for(int i=0; i<mThumbIds.length; i++){
            if(mThumbIds[i] == R.drawable.pc_icon_selected){
                lastIndex = i;
                break;
            }
        }
        if(lastIndex == -1){
            mThumbIds[posicion] = R.drawable.pc_icon_selected;
        }else{
            mThumbIds[lastIndex] = R.drawable.pc_icon;
            mThumbIds[posicion] = R.drawable.pc_icon_selected;
        }
    }

    public void restartAdapter(){
        for(int i=0; i<mThumbIds.length;i++){
            mThumbIds[i] = R.drawable.pc_icon;
        }
    }
}
