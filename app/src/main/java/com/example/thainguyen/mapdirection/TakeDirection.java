package com.example.thainguyen.mapdirection;

import android.os.AsyncTask;

import java.io.IOException;

public class TakeDirection extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... strings) {
        String response = "";
        DownloadURL downloadURL = new DownloadURL();
        try{
            response = downloadURL.readURl(strings[0]);
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        GetDirection getDirection = new GetDirection();
        getDirection.execute(s);
    }
}
