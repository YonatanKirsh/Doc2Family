package com.kirsh.doc2family.views;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import java.security.Policy;

public class ConfirmDialog {

    private static final String TITLE = "Confirm";
    private static final String MESSAGE = "Are you sure?";
    private static final String POSITIVE_BUTTON_TEXT = "Yes";
    private static final String NEGATIVE_BUTTON_TEXT = "No";

    public static void show(Context context, DialogInterface.OnClickListener dialogClickListener){
        show(context, dialogClickListener, MESSAGE);
    }

    public static void show(Context context, DialogInterface.OnClickListener dialogClickListener, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(TITLE)
                .setMessage(message)
                .setPositiveButton(POSITIVE_BUTTON_TEXT, dialogClickListener)
                .setNegativeButton(NEGATIVE_BUTTON_TEXT, dialogClickListener).show();
    }
}
