package edu.uw.yw239.wehike.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by Yun on 12/3/2017.
 */

public class AccountInfo {
    public static final String CREDENTIAL_FILE_NAME = "credential";

    private static String userName = null;
    private static String authToken = null;

    public static void clearAccountInfo() {
        File file = new File(MyApplication.getContext().getFilesDir(), AccountInfo.CREDENTIAL_FILE_NAME);
        if(file.exists()) {
            file.delete();
        }

        userName = null;
        authToken = null;
    }

    public static String getCurrentUserName() {
        if (userName == null) {
            loadUserAccountInfo();
        }

        return userName;
    }

    public static String getAuthToken() {
        if (authToken == null) {
            loadUserAccountInfo();
        }

        return authToken;
    }

    private static void loadUserAccountInfo() {
        try {
            File file = new File(MyApplication.getContext().getFilesDir(), CREDENTIAL_FILE_NAME);
            if(!file.exists()){
                return;
            }

            FileInputStream fis = MyApplication.getContext().openFileInput(CREDENTIAL_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            userName = reader.readLine();
            authToken = reader.readLine();
            reader.close();
        }
        catch (Exception ex) {

        }
    }
}
