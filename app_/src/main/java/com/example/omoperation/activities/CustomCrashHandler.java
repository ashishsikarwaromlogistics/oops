package com.example.omoperation.activities;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

  class CustomCrashHandler implements Thread.UncaughtExceptionHandler {

    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public CustomCrashHandler(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            // Create a file in external storage
            File crashFile = new File(context.getExternalFilesDir(null), "crash_report.txt");
            if (!crashFile.exists()) {
                crashFile.createNewFile();
            }

            // Write crash details to the file
            FileWriter writer = new FileWriter(crashFile);
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            printWriter.close();
            writer.close();

            // Optionally send the file or handle it
            // sendCrashReport(crashFile); // Define this method if you want to upload/send the file

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Pass the exception to the default handler
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        } else {
            System.exit(1);
        }
    }
}
