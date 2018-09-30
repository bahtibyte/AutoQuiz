package arcadia.bahti.autoquiz;

import android.content.Intent;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;

public class HomePage extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;


    private boolean shouldExit = false;
    private boolean listeningForAnswer = false;
    private Quiz listeningQuiz = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_quizzes:
                    startActivity(new Intent(HomePage.this, MainActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new SpeechListener());

        findViewById(R.id.button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechIntent);
                        break;
                }

                return false;
            }
        });
    }

    private void analyzeInput(String voiceIn)
    {
        String s = voiceIn.toLowerCase();
        ArrayList<String> triggerWords = new ArrayList<String>();
        triggerWords.add("text me on");
        triggerWords.add("test me on");
        triggerWords.add("quiz me on");
        triggerWords.add("quiz on");
        triggerWords.add("quest me on");
        System.out.println(s);

        int foundInd = -1;

        for (int i = 0; i < triggerWords.size(); i++)
        {
            if (s.contains(triggerWords.get(i)))
            {
                foundInd = i;
            }
        }

        if (s.contains("start"))
        {
            int ind = s.indexOf("start");
            int ind2 = s.indexOf("quiz");

            String subject = "";
            int quizNum = 0;

            try
            {
                if (ind2 - ind == 6)
                {
                    quizNum = Integer.parseInt(s.substring(ind2 + 5));
                    System.out.println(quizNum);
                }
                else
                {
                    String str = s.substring(ind + 6);

                    int space = str.indexOf(" ");
                    subject = str.substring(ind, space);
                    quizNum = Integer.parseInt(str.substring(space + 6));
                    System.out.println(subject);
                    System.out.println(quizNum);
                }
            }
            catch (Exception E)
            {
                System.out.println("Error4");
            }

            if (quizNum == 0)
                System.out.println("Error5");
            else
                runQuiz(subject, quizNum);
        }

        else if (foundInd > -1)
        {
            String trig = triggerWords.get(foundInd);
            int on = trig.indexOf("on");
            String subject = "";
            try
            {
                subject = s.substring(on + 3) ;
            }
            catch (Exception E)
            {
                System.out.println("Error6");
            }

            if (subject.equals(""))
                System.out.println("Error7");
            else
                runQuiz(subject);
        }


    }

    private void runQuiz(String subject){
        System.out.println("INVOKING RUN QUIZ: SUBJECT="+subject);

        Quiz quiz = null;
        for (Quiz q : MainActivity.getSelf().getQuizzes()){
            if(q.getQuizName().toLowerCase().equals(subject)){
                quiz = q;
            }
        }

        ArrayList<String> toSay = new ArrayList<String>();

        System.out.println("Listening For Answer: "+listeningForAnswer+"! Current Quiz: "+(listeningQuiz == null ? "NULL" : listeningQuiz.getQuizName()));

        if (!listeningForAnswer) {

            if (quiz == null) {
                toSay.add(subject+" is not in your library.");
                toSay.add("Please add it");
                MainActivity.say(150,toSay);
            } else {
                shouldExit = false;
                listeningForAnswer = true;
                listeningQuiz = quiz;
                toSay.add("Starting "+listeningQuiz.getQuizName()+" quiz!");
                int ran =(int)( Math.random() * listeningQuiz.getQuestions().size());
                System.out.println("RAW: "+listeningQuiz.getQuestions().size()+" RAN: "+ran);
                listeningQuiz.setCurrentQuestion(listeningQuiz.getQuestions().get(ran));
                toSay.add(listeningQuiz.getCurrentQuestion().getQuestion());


                MainActivity.say(250,toSay);
            }
        }else {

            ArrayList<String> triggerWords = new ArrayList<String>();
            triggerWords.add("GREAT JOB");
            triggerWords.add("AMAZING");
            triggerWords.add("OUTSTANDING");
            triggerWords.add("GOOD JOB");
            triggerWords.add("NOT BAD");

            if (subject.toLowerCase().equals(listeningQuiz.getCurrentQuestion().getAnswer().toLowerCase())){
                toSay.add(triggerWords.get((int)(Math.random()*triggerWords.size()))+"!");
                toSay.add("The answer is "+listeningQuiz.getCurrentQuestion().getAnswer());
                MainActivity.say(500,toSay);
            }else {
                toSay.add("You said, "+subject+"!");
                toSay.add("The answer is, "+listeningQuiz.getCurrentQuestion().getAnswer());

                MainActivity.say(500,toSay);

            }

            listeningForAnswer = true;

            int ran =(int)( Math.random() * listeningQuiz.getQuestions().size());
            System.out.println("1RAW: "+listeningQuiz.getQuestions().size()+" RAN: "+ran);
            listeningQuiz.setCurrentQuestion(listeningQuiz.getQuestions().get(ran));
            toSay.add("Next question, "+listeningQuiz.getCurrentQuestion().getQuestion());


        }

        System.out.println("Listening For Answer: "+listeningForAnswer+"! Current Quiz: "+(listeningQuiz == null ? "NULL" : listeningQuiz.getQuizName()));

    }

    private void runQuiz(String subject, int num){
        System.out.println("YOU CHOOSE THIS SUBJECT: "+subject);
    }

    private class SpeechListener implements RecognitionListener {

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if (matches != null){
                String userInput = matches.get(0);
                System.out.println("YOU SAID: "+userInput);

                if (userInput.contains("repeat")){

                    ArrayList<String> tosay = new ArrayList<>();
                    tosay.add("Repeating the question!");
                    tosay.add(listeningQuiz.getCurrentQuestion().getQuestion());
                    MainActivity.say(500,tosay);
                    return;
                }


                ArrayList<String> triggers = new ArrayList<>();
                triggers.add("exit");
                triggers.add("stop");
                triggers.add("restart");



                for (String s : triggers){
                    if (userInput.toLowerCase().equals(s.toLowerCase())){
                        shouldExit = false;
                        listeningForAnswer = false;

                        ArrayList<String> tosay = new ArrayList<>();
                        tosay.add("Exited out of the "+listeningQuiz.getQuizName()+" quiz!");
                        tosay.add("Select new quiz to continue or add your own quiz");
                        MainActivity.say(500,tosay);
                        listeningQuiz = null;
                        return;
                    }
                }

                if (!shouldExit && !listeningForAnswer && listeningQuiz == null) {
                    System.out.println("RUNNING FAIZANS CODe");
                    analyzeInput(userInput);
                }
                else {
                    System.out.println("RUNNING MINE  CODe");
                    runQuiz(userInput);
                }
            }
        }

        public void onPartialResults(Bundle bundle) {}

        @Override
        public void onEvent(int i, Bundle bundle) {}

        @Override
        public void onReadyForSpeech(Bundle bundle) {}

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float v) {}

        @Override
        public void onBufferReceived(byte[] bytes) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int i) {}

    }
}
