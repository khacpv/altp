package logic;

import com.oic.game.ailatrieuphu.ui.activity.PlayScreen;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Random;

/**
 * Created by kienht on 9/21/16.
 */
public class RandomPeopleTest {

    @Test
    public void testRandomQuestion() {
        Assert.assertTrue(true);
    }

    @Test
    public void test50Percent() {
        PlayScreen activity = new PlayScreen();
        for (int i = 0; i < 1000; i++) {
            int rightAnswer = i % 3;
            int result = activity.getFifty(rightAnswer);
            Assert.assertNotEquals(result, rightAnswer);
            Assert.assertTrue(result > -1);
            Assert.assertTrue(result < 3);
        }
    }

    @Test
    public void testPeople() {
        PlayScreen activity = new PlayScreen();
        for (int i = 0; i < 1000; i++) {
            int rightAnswer = i % 3;    // 0...3
            boolean is50_50 = i % 2 == 0;
            int level = i % 14 + 1;     // 1...15
            int rdIdxFifty = 0;

            switch (rightAnswer) {
                case 0:
                    rdIdxFifty = 1;
                    break;
                case 1:
                    rdIdxFifty = 2;
                    break;
                case 2:
                    rdIdxFifty = 3;
                    break;
                case 3:
                    rdIdxFifty = 0;
                    break;
            }

            List<Integer> result = activity.audienceSuggest(level, rightAnswer, is50_50);
            if (!is50_50) {
                Assert.assertTrue(result.size() == 4);
                System.out.println("i: " + i + " rdIdxFifty: " + rdIdxFifty + "  rightAnswer: " + rightAnswer);
                System.out.println(result.get(rightAnswer) + "       " + result.get(rdIdxFifty));
                // kiem tra dieu kien cho 50-50
                Assert.assertTrue(result.get(rightAnswer) > result.get(rdIdxFifty));
               Assert.assertTrue((result.get(rightAnswer) + result.get(rdIdxFifty)) == 100);
            } else {
                Assert.assertTrue(result.size() == 4);

                if (level < 5) {
                    // kiem tra cau tra loi dung co phan tram lon nhat
                    for (int answerIndex = 0; answerIndex < 4; answerIndex++) {
                        if (answerIndex != rightAnswer) {
                            Assert.assertTrue(result.get(rightAnswer) > result.get(answerIndex));
                        }
                    }
                } else {
                    int nearMaxPercent = 0;
                    for (int answerIndex = 0; answerIndex < 4; answerIndex++) {
                        if (answerIndex != rightAnswer) {
                            nearMaxPercent = Math.max(nearMaxPercent, result.get(answerIndex));
                        }
                    }
                    int deltaPercent = 5;
                    int rightAnswerPercentValue = result.get(rightAnswer);

                    // kiem tra cau tra loi gan nhat khong qua 5% so voi cau tra loi dung
                    Assert.assertTrue(Math.abs(rightAnswerPercentValue - nearMaxPercent) <= deltaPercent);
                }
            }

        }
    }

}
