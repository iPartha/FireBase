package com.example.ipartha.firebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * Created by iPartha on 26-03-2017.
 */

public class AlertMsg {

    private String mAlertMsgTitle;
    private String mAlertMsg;
    private String mAlertMsgPos;
    private String mAlertMsgNeg;
    Context context;

    public AlertMsg(Context context, String title, String msg, String posMsg, String negMsg){

        this.context = context;
        this.mAlertMsgTitle = title;
        this.mAlertMsg = msg;
        this.mAlertMsgPos = posMsg;
        this.mAlertMsgNeg = negMsg;
    }

    public void showAlertMsg() {
        do {

            if (this.mAlertMsg.isEmpty() && this.mAlertMsgTitle.isEmpty()) {
                break;
            }

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this.context);
            builder1.setMessage(this.mAlertMsg);
            if (this.mAlertMsgTitle !=null && !(this.mAlertMsgTitle.isEmpty())) {
                builder1.setTitle(this.mAlertMsgTitle);
            }
            builder1.setCancelable(true);

            if (this.mAlertMsgPos !=null && !(this.mAlertMsgPos.isEmpty())) {

                builder1.setPositiveButton(
                        this.mAlertMsgPos,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
            }

            if (this.mAlertMsgNeg !=null && !(this.mAlertMsgNeg.isEmpty())) {
                builder1.setNegativeButton(
                        mAlertMsgNeg,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
            }


            AlertDialog alert11 = builder1.create();
            alert11.show();

        }while (false);

    }

    public static void showToast(Context context, String msg){

        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    public static void showToast(Context context, String msg, final int duration){

        Toast.makeText(context, msg, duration).show();

    }

}
