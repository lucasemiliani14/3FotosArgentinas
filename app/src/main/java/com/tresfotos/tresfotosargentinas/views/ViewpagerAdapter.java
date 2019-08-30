package com.tresfotos.tresfotosargentinas.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.tresfotos.tresfotosargentinas.R;
import com.tresfotos.tresfotosargentinas.model.pojo.Palabra;

public class ViewpagerAdapter  extends PagerAdapter {

    private Context context;
    private Palabra palabra;

    public ViewpagerAdapter(Context context, Palabra palabra) {
        this.context = context;
        this.palabra = palabra;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_fotos, null);
        ImageView imageView = view.findViewById(R.id.image_of_viewpager);
        imageView.setImageResource(context.getResources().getIdentifier(getImageAtPosition(position), "drawable", context.getPackageName()));
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    private String getImageAtPosition(int position){
        switch (position){
            case 0:
                return palabra.getDrawableName1();
            case 1:
                return palabra.getDrawableName2();
            case 2:
                return palabra.getDrawableName3();
                default:
                    return null;
        }
    }
}
