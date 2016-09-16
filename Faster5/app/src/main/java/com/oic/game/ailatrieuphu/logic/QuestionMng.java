package com.oic.game.ailatrieuphu.logic;

import com.oic.game.ailatrieuphu.model.Question;

import java.util.ArrayList;

/**
 * Created by Kien on 07/12/2016.
 */
public class QuestionMng {
    public static ArrayList<Question> listQuestion;
    public static int sTopicId;

    public static ArrayList<Question> getQuestion() {
//        if (listQuestion == null || listQuestion.isEmpty()) {
//            listQuestion = new ArrayList<Question>();
//
//            switch (sTopicId) {
//                case 1:
//                    listQuestion.add(new Question(1, "1+2+3 = ?football", "1", "2", "3", "6", 3));
//                    listQuestion.add(new Question(2, "1+2+4 = ?football", "1", "2", "7", "6", 2));
//                    listQuestion.add(new Question(3, "1+5+3 = ?football", "1", "9", "3", "6", 1));
//                    listQuestion.add(new Question(4, "1+6+3 = ?football", "10", "2", "3", "6", 0));
//                    listQuestion.add(new Question(5, "9+2+3 = ?football", "14", "2", "3", "6", 0));
//                    break;
//                case 2:
//                    listQuestion.add(new Question(1, "1+2+3 = ?art", "1", "2", "3", "6", 3));
//                    listQuestion.add(new Question(2, "1+2+4 = ?art", "1", "2", "7", "6", 2));
//                    listQuestion.add(new Question(3, "1+5+3 = ?art", "1", "9", "3", "6", 1));
//                    listQuestion.add(new Question(4, "1+6+3 = ?art", "10", "2", "3", "6", 0));
//                    listQuestion.add(new Question(5, "9+2+3 = ?art", "14", "2", "3", "6", 0));
//                    break;
//            }
//        }
        return listQuestion;
    }
}