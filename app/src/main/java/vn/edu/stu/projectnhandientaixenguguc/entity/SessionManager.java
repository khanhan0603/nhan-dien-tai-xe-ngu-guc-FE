package vn.edu.stu.projectnhandientaixenguguc.entity;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        prefs.edit().remove("TOKEN").apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        return prefs.getString("TOKEN", null);
    }
}