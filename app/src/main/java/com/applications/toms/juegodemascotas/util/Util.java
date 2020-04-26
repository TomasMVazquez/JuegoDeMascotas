package com.applications.toms.juegodemascotas.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.view.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    private static final String TAG = "Util";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public static Boolean isOnline(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static String getExtension(Uri uri){
        String fileName = uri.getLastPathSegment();
        Integer startExtension = fileName.indexOf(".");
        String extension = fileName.substring(startExtension);
        return extension;
    }

    public static void printHash(Context context) {
        try {

            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(),
                            PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.v("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //CHECK VERSION FOR MAPS
    public static boolean isServicesOk (Context context){
        Log.d(TAG, "isServicesOk: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (available == ConnectionResult.SUCCESS){
            //Everything is find and user can do maps request
            Log.d(TAG, "isServicesOk: Google Play services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //Error occurred but can be resolved
            Log.d(TAG, "isServicesOk: an error occured but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(context, "You cannot make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static String googleMapsApiKey(Context context){
        String apiKey = context.getString(R.string.google_maps_key);
        if(apiKey.isEmpty()){
            return null;
        }
        return apiKey;
    }

    //Esconder Soft Keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}

