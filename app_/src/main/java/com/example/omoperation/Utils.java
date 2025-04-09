package com.example.omoperation;

import static com.google.gson.internal.$Gson$Types.arrayOf;
import static java.text.NumberFormat.getCurrencyInstance;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Utils {
//
    public static boolean isNetworkConnected(Context context) {
       boolean b=false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        b= cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        if(!b){
            Toast.makeText(context, "No,Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return b;
    }
    public static String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
    public static Bitmap getBitmapToBase64(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return   BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
    public static String getRupeesSymbol() {
        return "₹";
    }
    public static boolean haveInternet(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
       Toast.makeText(ctx,"No,Internet Connection",0).show();
        // Toast.makeText(ctx,"No,Internet Connection",0).show();
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected() && cm.isActiveNetworkMetered();

    }
    public static String GetTodayDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",Locale.US);
       return    df.format(c);

    }

    public static String GetCurrentTime(){
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static void ShowToast(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static boolean isMockSettingsON(Context context) {
        List<PackageInfo> packList =context. getPackageManager().getInstalledPackages(0);
        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);
            if (  (packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                String appName = packInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                if(appName.toLowerCase(Locale.ROOT).contains("fake") || appName.toLowerCase(Locale.ROOT).contains("mock") || appName.toLowerCase(Locale.ROOT).contains("faker")){
                   return true;
                }
                    Log.d("ashishsik" + Integer.toString(i), appName);
            }
        }
        return false;
    }
    static void getInstalledApps(Context context,boolean getSysPackages) {


    }




   public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_locaction_updates";

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    static String getLocationTitle(Context context) {
        return
               "Location Update "+ DateFormat.getDateTimeInstance().format(new Date());
    }
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static String ChangedFormat(String mydate,String inputformt,String outputformat){
        String date=mydate;//"Mar 10, 2016 6:30:00 PM";
        // SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
        SimpleDateFormat spf=new SimpleDateFormat(inputformt,Locale.ENGLISH);
        Date newDate= null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat(outputformat,Locale.ENGLISH);
        date = spf.format(newDate);
        System.out.println(date);
        return date;
    }

    public static void   GiveIndianFormat(int amount){
        Format format =  getCurrencyInstance(new Locale("en", "in"));
        String counting =format.format(34989384);
    }

    public static void showDialog(Context con,String title, String msg, int icon) {
     try {
         new android.app.AlertDialog.Builder(con)
                 .setTitle(title)
                 .setIcon(icon)
                 .setMessage(msg)
                 .setCancelable(false)
                 .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                 .show();
     }catch (Exception e){
         String name="1234567890";
         name=name.substring(1,name.length()-1);
     }

    }
    public static void showCopyDialog(Context con,String title, String msg, int icon) {
        new android.app.AlertDialog.Builder(con)
                .setTitle(title)
                .setIcon(icon)
                .setMessage("You can copy data to press copy button")
                .setCancelable(false)
                .setPositiveButton("Copy", (dialog, id) -> {
                    ClipboardManager clipboard = (ClipboardManager)con. getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied", msg);
                    clipboard.setPrimaryClip(clip);
                    dialog.dismiss();
                })
                .show();
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int check_null_Int(String a){
        try {
            return Integer.parseInt(a);
        }
        catch (Exception e){
            return 0;
        }

    }


   static DecimalFormat numberFormat = new DecimalFormat("#.00");
    public static Double check_null_Double(String a){

        try {
            return  Double.parseDouble(numberFormat.format(Double.parseDouble(a)));
        }
        catch (Exception e){
            return 0.0;
        }

    }

    public static void copyData(Context context,String data){
        new android.app.AlertDialog.Builder(context)
                .setTitle("Error Send")
                .setIcon( R.drawable.ic_error_outline_red_24dp)
                .setMessage("data")
                .setCancelable(false)
                .setPositiveButton("Copy", (dialog, id) -> {
                    ClipboardManager clipboard = (ClipboardManager)context. getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", data);
                    clipboard.setPrimaryClip(clip);
                    dialog.dismiss();
                })
                .setNeutralButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                })

                .show();
    }//0000
    public static String transformNumber(long number) {
// Define the mapping
        int[] mapping = {6, 9, 5, 7, 0, 3, 1, 4, 2, 8};
//455645967
// Convert the number to a string to iterate through each digit
        String numberStr = Long.toString(number);
        StringBuilder transformedNumberStr = new StringBuilder();

// Iterate through each digit of the number and apply the mapping
        for (int i = 0; i < numberStr.length(); i++) {
            char digitChar = numberStr.charAt(i);
            int digit = Character.getNumericValue(digitChar);
// Apply the mapping if the digit is within the mapping array bounds
            if (digit >= 0 && digit < mapping.length) {
                transformedNumberStr.append(mapping[digit]);
            } else {
// If the digit is not in the mapping, keep it unchanged
                transformedNumberStr.append(digit);
            }
        }

// Convert the transformed number back to a long
      //  return Long.parseLong(transformedNumberStr.toString())+"";
        return transformedNumberStr.toString();
    }
    public static String revertTransform(String transformedNumber_s) {
      //  long transformedNumber =Long.parseLong(transformedNumber_s);
        int[] mapping = {6, 9, 5, 7, 0, 3, 1, 4, 2, 8};
        String transformedNumberStr = transformedNumber_s;
        StringBuilder originalNumberStr = new StringBuilder();
        for (int i = 0; i < transformedNumberStr.length(); i++) {
            char digitChar = transformedNumberStr.charAt(i);
            int digit = Character.getNumericValue(digitChar);
            int originalDigit = -1;
            for (int j = 0; j < mapping.length; j++) {
                if (mapping[j] == digit) {
                    originalDigit = j;
                    break;
                }
            }
            if (originalDigit == -1) {
                originalNumberStr.append(digit);
            } else {
                originalNumberStr.append(originalDigit);
            }
        }

      //  return Long.parseLong(originalNumberStr.toString())+"";
        return (originalNumberStr.toString());
    }
   /* public static String revertTransform(String transformedNumber_s) {
        long transformedNumber =Long.parseLong(transformedNumber_s);
        int[] mapping = {6, 9, 5, 7, 0, 3, 1, 4, 2, 8};
        String transformedNumberStr = Long.toString(transformedNumber);
        StringBuilder originalNumberStr = new StringBuilder();
        for (int i = 0; i < transformedNumberStr.length(); i++) {
            char digitChar = transformedNumberStr.charAt(i);
            int digit = Character.getNumericValue(digitChar);
            int originalDigit = -1;
            for (int j = 0; j < mapping.length; j++) {
                if (mapping[j] == digit) {
                    originalDigit = j;
                    break;
                }
            }
            if (originalDigit == -1) {
                originalNumberStr.append(digit);
            } else {
                originalNumberStr.append(originalDigit);
            }
        }

        //  return Long.parseLong(originalNumberStr.toString())+"";
        return (originalNumberStr.toString());
    }*/
   public static boolean arePermissionsGranted(Context context,String[] permissions) {
       for (String permission : permissions) {
           if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
               return false;
           }
       }
       return true;
   }
    public static String getDeviceID(Context context) {
        String deviceId = "12345678"; // Default value

        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android Q and above, use ANDROID_ID
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                // Below Android Q, use getDeviceId()
                if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    deviceId = telephonyManager.getDeviceId();
                } else {
                    // Request the permission or handle it gracefully
                    Log.e("getDeviceID", "READ_PHONE_STATE permission not granted");
                }
            }
        } catch (Exception e) {
            Log.e("getDeviceID", "Error while retrieving device ID", e);
        }

        return deviceId;
    }


    public static HashMap<String, String> getheaders(){
        HashMap<String, String> map =new  HashMap<String, String>();
        map.put("Accept","application/json");
        map.put(Constants.JWTTOKEN,OmOperation.getPreferences(Constants.JWTTOKEN, ""));
        map.put("EMP_CODE",OmOperation.getPreferences(Constants.EMP_CODE, ""));
      return map;
    }


    public static HashMap<String, String> getheaders2(){
        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIxMDAzOSIsIm5hbWUiOiJLYXNoaW5hdGggVGhhbGthciIsImp0aSI6ImM4YTk0YTEwLTk4NzgtNGZlMy04MzdmLWEyZDRjNzFmMmVmZiIsImV4cCI6MTczNTgwNDU1NywiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3Q6NDQzNjgvIiwiYXVkIjoiaHR0cHM6Ly9sb2NhbGhvc3Q6NDQzNjgvIn0.d0amPI5N3yknZhVoOW4qhFS7lWbSOODKxqty1Gd9ueI";

        HashMap<String, String> map =new  HashMap<String, String>();
        map.put("Accept","application/json");
        map.put("Host","nicmapi.mhlprs.com");
        map.put("Authorization",token);
     //   map.put("EMP_CODE",OmOperation.getPreferences(Constants.EMP_CODE, ""));
      return map;
    }


    public static String getUUID(Context context) {
        String uuid = Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 + Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE + Build.USER.length() % 10 + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return UUID.nameUUIDFromBytes(uuid.getBytes()).toString();
    }


    public static Double safedouble(String value){
       try {
           return Double.parseDouble(value);
       }catch (Exception e){}
       return 0.0;
    }

    public static void   ShowImageDailog(Context context,String path){
        Dialog dialog=new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.image_preview);
        TouchImageView imagepreview=dialog.findViewById(R.id.imagepreview);
        ImageView img_cross=dialog.findViewById(R.id.img_cross);
        Glide.with(context)
                .load(path)
                .placeholder(R.drawable.no_image_available)
                .into(imagepreview);
        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        float radius = width / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        return output;
    }
    public static String getDeviceIMEI(Context context) {

        String deviceUniqueIdentifier = null;
       try{
           TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
           if (null != tm) {
               deviceUniqueIdentifier = tm.getDeviceId();
           }
           if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
               deviceUniqueIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
           }
           return deviceUniqueIdentifier;
       }
       catch (Exception e){
           return "1234567";
        }

    }
    public static String getCurrentTimestamp()  {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

}
