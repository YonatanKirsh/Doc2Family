package com.kirsh.doc2family.views;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.kirsh.doc2family.R;

import java.security.Policy;

public class ConfirmDialog {

    public static void show(Context context, DialogInterface.OnClickListener dialogClickListener){
        show(context, dialogClickListener, context.getString(R.string.are_you_sure));
    }

    public static void show(Context context, DialogInterface.OnClickListener dialogClickListener, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm)
                .setMessage(message)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }
}
