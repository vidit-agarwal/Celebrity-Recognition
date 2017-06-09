package com.hugeardor.vidit.recognisecharacter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisInDomainResult;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    private VisionServiceClient visionServiceClient = new VisionServiceRestClient("f38be5afaf0446b5b8bbc9e3ee9adb36");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        Button btnProcess = (Button)findViewById(R.id.btnProcess);

        imageView.setImageBitmap(mBitmap);

        //convert bitmap to stream

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<InputStream,String,String> recognizeCeleb = new AsyncTask<InputStream, String, String>()
                {
                  ProgressDialog mDialog = new ProgressDialog(MainActivity.this);

                    @Override
                    protected String doInBackground(InputStream... params) {
                       try{

                           publishProgress("Detecting...");
                           String model = "celebrities";

                           AnalysisInDomainResult analysisInDomainResult = visionServiceClient.analyzeImageInDomain(params[0],model);
                           //String strResult =
                             return  new Gson().toJson(analysisInDomainResult);
                           //return strResult;

                       }catch (Exception ex)
                       {

                           return null;

                       }

                    }

                    @Override
                    protected void onPreExecute()
                    {  mDialog.show();

                    }

                    @Override
                    protected  void onPostExecute(String s)
                    {
                        mDialog.dismiss();
                        Gson gson = new Gson();

                        AnalysisInDomainResult result = gson.fromJson(s, AnalysisInDomainResult.class);

                        TextView textView = (TextView)findViewById(R.id.txtDescription);

                        StringBuilder stringBuilder = new StringBuilder();

                        JsonArray detectedCelebs= result.result.getAsJsonArray("celebrities").getAsJsonArray();
                        for(JsonElement element: detectedCelebs)
                        {
                            JsonObject celeb = element.getAsJsonObject();
                            stringBuilder.append("Name : ").append(celeb.get("name").getAsString()+"\n");


                        }
                        textView.setText(stringBuilder);

                    }

                    @Override
                    protected void onProgressUpdate(String... values)
                    {
                           mDialog.setMessage(values[0]);
                    }


                };

                recognizeCeleb.execute(inputStream);



            }
        });
    }
}
