package com.example.gcs.faster5;


/**
 * Created by Kien on 07/12/2016.
 */
public class ListQuestion {
    String question, ans1, ans2, ans3, ans4;
    Integer stt,idAnsCorrect;
    public ListQuestion(){

    }

    public ListQuestion(Integer stt, String question, String ans1, String ans2, String ans3, String ans4, Integer idAnsCorrect) {
        this.stt = stt;
        this.question = question;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
        this.ans4 = ans4;
        this.idAnsCorrect = idAnsCorrect;
    }

    public Integer getStt() {
        return stt;
    }

    public void setStt(Integer stt) {
        this.stt = stt;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAns1() {
        return ans1;
    }

    public void setAns1(String ans1) {
        this.ans1 = ans1;
    }

    public String getAns2() {
        return ans2;
    }

    public void setAns2(String ans2) {
        this.ans2 = ans2;
    }

    public String getAns3() {
        return ans3;
    }

    public void setAns3(String ans3) {
        this.ans3 = ans3;
    }

    public String getAns4() {
        return ans4;
    }

    public void setAns4(String ans4) {
        this.ans4 = ans4;
    }


    public Integer getIdAnsCorrect() {
        return idAnsCorrect;
    }

    public void setIdAnsCorrect(Integer idAnsCorrect) {
        this.idAnsCorrect = idAnsCorrect;
    }
}
