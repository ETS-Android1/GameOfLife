package com.zmachsoft.gameoflife;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import fi.iki.kuitsi.listtest.MyTagHandler;

/**
 * Some Game UI utils
 *
 * @author Master
 */
public class GameUIUtils {

    /**
     * Display a text (HTML or not) in the game generic text dialog
     *
     * @param masterActivity
     * @param title
     * @param text
     * @param isHtml
     */
    public static void displayTextDialog(Activity masterActivity, String title, String text, boolean isHtml) {
        LayoutInflater inflater = LayoutInflater.from(masterActivity);
        View view = inflater.inflate(R.layout.generic_text_dialog, null);

        // HTML text or not ?
        TextView textview = (TextView) view.findViewById(R.id.generic_textmsg);
        if (isHtml)
            textview.setText(Html.fromHtml(text, null, new MyTagHandler()));
        else
            textview.setText(text);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(masterActivity);
        alertDialog.setTitle(title);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("OK", null);
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

}
