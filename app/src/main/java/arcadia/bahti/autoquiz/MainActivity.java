package arcadia.bahti.autoquiz;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static MainActivity mainAct;

    private ArrayList<Quiz> quizzes;

    private TextView mTextMessage;
    private ListView listView;

    public ArrayList<Quiz> getQuizzes(){
        return quizzes;
    }

    private static TextToSpeech toSpeech;
    private int result;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homePage();
                    return true;
                case R.id.navigation_quizzes:
                    quizPage();
                    return true;
            }
            return false;
        }
    };


    public static void say(final int delay,final ArrayList<String> texts){

        try {

            new Thread(){
                public void run(){

                    for (String t : texts) {
                        toSpeech.speak(t,TextToSpeech.QUEUE_ADD,null);
                        try{
                            Thread.sleep(delay);
                        }catch (Exception e ){

                        }
                    }
                }
            }.start();

        }catch (Exception e){

        }
    }

    private void homePage(){
        listView.setVisibility(View.INVISIBLE);
        startActivity(new Intent(MainActivity.this, HomePage.class));
    }

    private void quizPage(){
        reload();
        listView.setVisibility(View.VISIBLE);
    }

    private void reload(){
        final AssetManager assetManager = getAssets();
        quizzes = new ArrayList<Quiz>();
        try {
            String[] files = assetManager.list("data");
            if (files != null) {

                for (int i=0; i<files.length; i++) {
                    // Get filename of file or directory
                    String filename = files[i];
                    //mTextMessage.setText(mTextMessage.getText()+" \n"+filename);

                    ArrayList<Question> data = new ArrayList<Question>();

                    try {
                        InputStream input = assetManager.open("data/"+filename);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        String line;

                        while ((line = reader.readLine()) != null) {

                            String a = line.substring(0,line.indexOf(":"));
                            line = line.substring(line.indexOf(":")+1);
                            String q = line;

                            data.add(new Question(q,a));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Quiz q = new Quiz(filename.substring(0,filename.indexOf(".")));
                    q.setQuestions(data);
                    quizzes.add(q);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Quiz quiz : Track.added){
            quizzes.add(quiz);
        }

        final String[] quizNames = new String[quizzes.size()];

        for (int i = 0; i < quizNames.length; i++){
            quizNames[i] = (i+1)+". "+quizzes.get(i).getQuizName();
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, quizNames);
        System.out.println("NUMEBR OF QUIZZES: "+quizzes.size()+" RAW: "+quizNames.length);


        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toSpeech.speak(quizzes.get((int)id).getQuestions().get((int)(Math.random()*quizzes.get((int)id).getQuestions().size())).getQuestion(),TextToSpeech.QUEUE_FLUSH,null);
            }
        });
    }

    public static MainActivity getSelf(){
        return mainAct;
    }

    public void addQuiz(String input,String name){
        ArrayList<Question> data = new ArrayList<>();
        while (input.contains(":")) {
            String a = input.substring(0, input.indexOf(":"));
            input = input.substring(input.indexOf(":") + 1);
            String q = input.substring(0,input.indexOf("\n"));
            input = input.substring(q.length()+1);
            data.add(new Question(q,a));
        }
        Quiz quiz = new Quiz(name);
        quiz.setQuestions(data);
        Track.added.add(quiz);
        System.out.println("ADDING QUIZ: "+name+" THAT HAS "+data.size()+"QUESTIONS");
        reload();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        findViewById(R.id.add).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startActivity(new Intent(MainActivity.this,AddQuizActivity.class));
                        break;
                }

                return false;
            }
        });

        toSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS){
                    result = toSpeech.setLanguage(Locale.US);
                    toSpeech.setPitch(0.5F);
                    toSpeech.setSpeechRate(0.85F);
                }else{
                    mTextMessage.setText("ERROR");
                }
            }
        });

        reload();

        mainAct = this;
    }

}
