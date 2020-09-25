package com.physi.pac.setter.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.physi.pac.setter.R;

/**
 * Created by Heo on 2018-02-09.
 */

public class NotifyDialog {

    public void show(Context context, String title, String message,
                     String btnText, DialogInterface.OnClickListener clickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(btnText, clickListener)
                .setPositiveButton(android.R.string.cancel, null)
                .setCancelable(false).create().show();
    }

    public void show(Context context, String title, String message,
                     String btnText1, DialogInterface.OnClickListener clickListener1,
                     String btnText2, DialogInterface.OnClickListener clickListener2)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.cancel, null)
                .setNegativeButton(btnText1, clickListener1)
                .setPositiveButton(btnText2, clickListener2)
                .setCancelable(false).create().show();
    }


    public void show(Context context, int title, int message,
                     String btnText, DialogInterface.OnClickListener clickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, clickListener)
                .setCancelable(false).create().show();
    }

    public void show(Context context, String title, int layoutResId,
                     String btnText, DialogInterface.OnClickListener clickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layoutResId)
                .setPositiveButton(btnText, clickListener)
                .setCancelable(false).create().show();
    }

    public void show(Context context, String title, View view)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(android.R.string.cancel, null)
                .setCancelable(false).create().show();
    }

    public void show(Context context, View view,
                     String btnText, DialogInterface.OnClickListener clickListener)
    {
        new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton(btnText, clickListener)
                .setPositiveButton(android.R.string.cancel, null)
                .setCancelable(false).create().show();
    }
}
