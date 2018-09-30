package arcadia.bahti.autoquiz;

/**
 * Created by bahti on 9/30/2018.
 */

public class Question
{
    String question;
    String answer;

    public Question(String qu, String ans)
    {
        question = qu;
        answer = ans;
    }

    public Question()
    {
        question = "";
        answer = "";
    }

    public String getQuestion()
    {
        return question;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setQuestion(String q)
    {
        question = q;
    }

    public void setAnswer(String a)
    {
        answer = a;
    }

    public String toString()
    {
        return question + " : " + answer;
    }
}