package arcadia.bahti.autoquiz;

import java.util.ArrayList;

/**
 * Created by bahti on 9/30/2018.
 */

public class Quiz {

    private String quizName;

    private ArrayList<Question> questions;
    private Question currentQuestion;

    public Question getCurrentQuestion(){
        return currentQuestion;
    }

    public void setCurrentQuestion(Question q){
        this.currentQuestion =q;
    }

    public Quiz(String name){
        this.questions = new ArrayList<Question>();
        this.quizName = name;
    }

    public String getQuizName(){
        return quizName;
    }

    public ArrayList<Question> getQuestions(){
        return questions;
    }

    public void setQuestions(ArrayList<Question> q){
        this.questions = q;
    }

}
