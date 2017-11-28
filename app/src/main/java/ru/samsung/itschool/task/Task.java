package ru.samsung.itschool.task;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

public class Task {
      public static void showDialog(Context context, String message)
      {

    	  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
    	  dlgAlert.setMessage(message);
    	  dlgAlert.setTitle("Task");
    	  dlgAlert.setPositiveButton("OK", null);
    	  dlgAlert.setCancelable(true);
    	  dlgAlert.setPositiveButton("Ok",
    			    new DialogInterface.OnClickListener() {
    			        public void onClick(DialogInterface dialog, int which) {

    			        }
    			    });
    	  dlgAlert.create().show();
      }

      public static void showToast(Context context, String message)
      {

    	  Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
    	  toast.setGravity(Gravity.CENTER, 0, 0);
    	  toast.show();
      }

}
