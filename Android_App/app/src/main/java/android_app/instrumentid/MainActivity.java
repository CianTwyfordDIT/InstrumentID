package android_app.instrumentid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
{
    Button uploadFile;
    Intent fileIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverConnection();

        uploadFile = (Button) findViewById(R.id.uploadFile);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                return;
            }
        }
        enableButton();
    }

    private void enableButton()
    {
        uploadFile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("audio/*"); // File types allowed to be selected
                startActivityForResult(fileIntent, 10); // Specify which activity returning from

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
        {
            enableButton();
        }
        else
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    String getUploadURL()
    {
        String IPAddress = "192.168.0.25";
        String port = "5000";
        String postURL = "http://"+IPAddress+":"+port+"/"+"uploadFile";

        return postURL;
    }

    String getURL()
    {
        String IPAddress = "192.168.0.25";
        String port = "5000";
        String postURL = "http://"+IPAddress+":"+port+"/";

        return postURL;
    }

    void serverConnection()
    {
        String postURL = getURL();
        String postText="";

        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        RequestBody post_Text = RequestBody.create(mediaType, postText);

        postRequest(postURL, post_Text);
    }

    void postRequest(String postURL, RequestBody requestBody)
    {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postURL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {

                // Cancel the post on failure.
                call.cancel();

                e.printStackTrace();

                runOnUiThread(new Runnable()
                {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run()
                    {
                        TextView responseText = findViewById(R.id.response);
                        responseText.setText("Failed To Connect To Flask Server");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextView responseText = findViewById(R.id.response);
                        try
                        {
                            responseText.setText(response.body().string());
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 10 && resultCode == RESULT_OK)
        {
            String path = data.getData().getPath();

            System.out.println(path);

            File file = new File("/sdcard/InstrumentID/Data/WavFiles/00353774.wav");
            String contentType = getMimeType(path);

            RequestBody fileBody = RequestBody.create(MediaType.parse(contentType), file);

            String fileName = file.getName();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("type", contentType)
                    .addFormDataPart("uploadFile", fileName, fileBody)
                    .build();

            String postURL = getUploadURL();

            postRequest(postURL, requestBody);
        }
    }

    private String getMimeType(String path)
    {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}