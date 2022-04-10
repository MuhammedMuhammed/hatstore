package com.sound.hatstore.helpers;

import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.sound.hatstore.BuildConfig;
import com.sound.hatstore.InstallingUpdatingReceiver;
import com.sound.hatstore.MainActivity;
import com.sound.hatstore.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.security.auth.PrivateCredentialPermission;

public class ApkManager {
    private String filename;
    private String filePathName;
    private String MIMI_TYPE;
    private String Provider_Path;
    private String App_Install_Path;
    private Context context;
    private String url;
    ApkManager(Context context,String url, String filename, String filePathName, String MIMI_TYPE, String Provider_Path, String App_Install_Path)
    {
        this.url =url;
        this.filename=filename;
        this.filePathName=filePathName;
        this.MIMI_TYPE=MIMI_TYPE;
        this.Provider_Path=Provider_Path;
        this.App_Install_Path=App_Install_Path;
        this.context =context;
    }

    void enquequeDownload()
    {
        String destination = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()+"/";
        destination+=filename;
        Uri uri =Uri.parse(filePathName+destination);
        File file=new File(destination);
        if(file.exists())file.delete();

        DownloadManager downloadManager =(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(downloadUri);
        request.setMimeType(MIMI_TYPE);
        request.setTitle("File Download");
        request.setDescription("downloading");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);

        //set destination
        request.setDestinationUri(uri);
        // Enqueue a new download and same the referenceId
        long id= downloadManager.enqueue(request);

        showInstallOption(destination,uri);
        Toast.makeText(context, "downloading", Toast.LENGTH_LONG)
                .show();
    }
    public static boolean installPackage(final Context context, final String url)
            throws IOException {
        //Use an async task to run the install package method
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
                    PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                            PackageInstaller.SessionParams.MODE_FULL_INSTALL);

                    // set params
                    int sessionId = packageInstaller.createSession(params);
                    PackageInstaller.Session session = packageInstaller.openSession(sessionId);
                    OutputStream out = session.openWrite("COSU", 0, -1);
                    //get the input stream from the url
                    HttpURLConnection apkConn = (HttpURLConnection) new URL(url).openConnection();
                    InputStream in = apkConn.getInputStream();
                    byte[] buffer = new byte[65536];
                    int c;
                    while ((c = in.read(buffer)) != -1) {
                        out.write(buffer, 0, c);
                    }
                    session.fsync(out);
                    in.close();
                    out.close();
                    //you can replace this intent with whatever intent you want to be run when the applicaiton is finished installing
                    //I assume you have an activity called InstallComplete
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("info", "somedata");  // for extra data if needed..
                    Random generator = new Random();
                    PendingIntent i = PendingIntent.getActivity(context, generator.nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    session.commit(i.getIntentSender());
                } catch (Exception ex){
                    Log.e("AppStore","Error when installing application. Error is " + ex.getMessage());
                }

                return null;
            }
        };
        task.execute(null,null);
        return true;
    }
    private void showInstallOption(String destination, Uri uri) {
        SharedPreferences sharedPreference = context.getSharedPreferences("HatApksManager", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor=sharedPreference.edit();
       editor.putString("uri",uri.toString());
        editor.putString("destination",destination);
        editor.putString("Provider_Path",Provider_Path);
        editor.putString("App_Install_Path",App_Install_Path);
        editor.apply();
        // set BroadcastReceiver to install app when .apk is downloaded
        IntentFilter filter = new IntentFilter();
        filter.addAction("downloaded");
        BroadcastReceiver br =new InstallingUpdatingReceiver();

         context.registerReceiver(br,filter);
        Intent broadcastedIntent=new Intent(context, InstallingUpdatingReceiver.class);
        broadcastedIntent.putExtra("VALUE", 100500);
        context.sendBroadcast(broadcastedIntent);

    }
}
