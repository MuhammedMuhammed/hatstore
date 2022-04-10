package com.sound.hatstore.helpers;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sound.hatstore.MainActivity;
import com.sound.hatstore.R;


import java.lang.reflect.Field;
import java.util.List;

public class ListAdapter<T> extends BaseAdapter {
    private final int resourceLayout;
    private final Context context;
    private List<T> apksList;
    ApkManager apkManager;
    public ListAdapter(@NonNull Context context, int resource,List<T> apksList) {

        this.context =context;
        this.resourceLayout = resource;
        this.apksList = apksList;
    }

    @Override
    public int getCount() {
        return apksList.size();
    }

    @Override
    public T getItem(int position) {
        return apksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return apksList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View item = convertView;
        if(item==null)
        {
            LayoutInflater itemInflater = LayoutInflater.from(context);
            item = itemInflater.inflate(resourceLayout,null);

        }
        final T tItem =getItem(position);
        TextView nameview = item.findViewById(R.id.appName);
        ImageView imageview = item.findViewById(R.id.appIcon);
        Class tItemClass =tItem.getClass();
        try {
            Field tItemFieldName = tItemClass.getDeclaredField("name");
            Field tItemFieldImgUrl = tItemClass.getDeclaredField("url");
            tItemFieldName.setAccessible(true);
            tItemFieldImgUrl.setAccessible(true);
            String name =tItemFieldName.get(tItem).toString();
            nameview.setText(name);
            imageview.setImageURI(Uri.parse(tItemFieldImgUrl.get(tItem).toString()));
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Field tItemFieldName = null;
                    try {
                        tItemFieldName = tItemClass.getDeclaredField("name");

                    Field tItemFieldImgUrl = tItemClass.getDeclaredField("url");
                    tItemFieldName.setAccessible(true);
                    tItemFieldImgUrl.setAccessible(true);
                    String name =tItemFieldName.get(tItem).toString();
                  apkManager = new ApkManager(context,tItemFieldImgUrl.get(tItem).toString(), name,"file://", "application/vnd.android.package-archive", ".provider", "\"application/vnd.android.package-archive\"");

                        if(MainActivity.gotpermission)
                        {
apkManager.enquequeDownload();
                        }
                        else{
                            Toast.makeText(context,context.getString(R.string.denied),Toast.LENGTH_LONG).show();
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }  }
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
            return  item;
    }


}
