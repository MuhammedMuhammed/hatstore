package com.sound.hatstore;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class InstallingUpdatingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        SharedPreferences sharedPreference = context.getSharedPreferences("HatApksManager", Context.MODE_PRIVATE);
        Uri uri = Uri.parse(sharedPreference.getString("uri",null));
        String destination = sharedPreference.getString("destination",null);
        String Provider_Path = sharedPreference.getString("Provider_Path",null);
        String App_Install_Path = sharedPreference.getString("App_Install_Path",null);
        Intent install =new Intent(Intent.ACTION_VIEW);
       String action =intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + Provider_Path, new File(destination));

                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                   install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    install.setData(contentUri);

                } else {
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(
                            uri,
                            App_Install_Path
                    );

                }
                context.startActivity(install);

        }
    }
}