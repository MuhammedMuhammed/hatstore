package com.sound.hatstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.sound.hatstore.helpers.ListAdapter;
import com.sound.hatstore.models.Apks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
String[] permissions=new String[]{
        Manifest.permission.WRITE_EXTERNAL_STORAGE
};
    public static boolean gotpermission=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView apks= findViewById(R.id.apks);
        List<Apks> apksList = new ArrayList<Apks>();
        apksList.add(
             new Apks("app-release.apk","http://192.168.1.3:5421/apk/app-release.apk","com.sample.sample"));
        ListAdapter<Apks> arrayAdapter =new ListAdapter<Apks>(this,R.layout.list_item,apksList);
        apks.setAdapter(arrayAdapter);
        checkSelfPermissionCompat(this,permissions);

    }
    public static void  checkSelfPermissionCompat(Activity context, String[] permission)
    {
        ArrayList<String> neededRequests=new ArrayList<>();
        for (String s : permission) {
            int granted = ContextCompat.checkSelfPermission(context, s);
            if (granted == PackageManager.PERMISSION_DENIED) {
                neededRequests.add(s);
                gotpermission=true;
            }else{
                gotpermission=true;

            }
        }

        Object[] leftArrays=  neededRequests.toArray();
        int i= 0x1;
        for (Object p : leftArrays) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(context,p.toString()))
            {

                ActivityCompat.requestPermissions(context,new String[]{p.toString()},i++);

            }

        }

    }
    void showSnackbar(
             String msg,
            Integer length,
             CharSequence actionMessage,
             Function<View,Void> function
) {
        LayoutInflater i = LayoutInflater.from(this);
        View v = i.inflate(R.layout.snackbar,null);
        LinearLayout sn = v.findViewById(R.id.snack);
        Snackbar make = Snackbar.make(this,sn, msg, length);
        make.setAction(actionMessage, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function.apply(v);
            }
        });
        make.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0x1)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                gotpermission=true;
                showSnackbar("Permission Granted",Snackbar.LENGTH_LONG,"",v->{

                    return null;
                });
            }else{
                gotpermission=false;
                showSnackbar("Permission Denied ,Stopped downloading",Snackbar.LENGTH_LONG,"",v->{
                return null;
                });
                return;

            }
        }
    }
}