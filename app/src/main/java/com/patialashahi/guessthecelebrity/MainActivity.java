package com.patialashahi.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebURLS = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    int locationOfCorrect = 0;
    String[] answers = new String[4];
    Button button;
    Button button1 ;
    Button button2 ;
    Button button3 ;
    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrect))){
            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Incorrect! It was" + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        CreateQuestion();
    }


    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try{URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap myBitMap = BitmapFactory.decodeStream(inputStream);
            return myBitMap;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;

        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int data = inputStreamReader.read();
                while (data!= -1){
                    char current = (char)data;
                    result += current;
                    data = inputStreamReader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        imageView = (ImageView)findViewById(R.id.imageView);
        DownloadTask task = new DownloadTask();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult  = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()){
                celebURLS.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()){
                celebNames.add(m.group(1));
            }



        }catch (Exception e){
            e.printStackTrace();
        }
        CreateQuestion();
    }
    public void CreateQuestion(){

        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLS.size());
        ImageDownloader imageTask = new ImageDownloader();
        Bitmap celebImage;
        try {
            celebImage = imageTask.execute(celebURLS.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);
            locationOfCorrect = random.nextInt(4);
            int incorrectAnswerLocation;
            for (int i=0;i<4;i++){
                if(i==locationOfCorrect){
                    answers[i] = celebNames.get(chosenCeleb);
                }else{
                    incorrectAnswerLocation = random.nextInt(celebURLS.size());
                    while(incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = random.nextInt(celebURLS.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswerLocation);

                }
            }
            button.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
