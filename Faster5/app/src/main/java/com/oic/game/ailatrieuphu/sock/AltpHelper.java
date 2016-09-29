package com.oic.game.ailatrieuphu.sock;

import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oic.game.ailatrieuphu.model.GameOverMessage;
import com.oic.game.ailatrieuphu.model.Question;
import com.oic.game.ailatrieuphu.model.Room;
import com.oic.game.ailatrieuphu.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FRAMGIA\pham.van.khac on 8/11/16.
 */
public class AltpHelper {

    private SockAltp mSockAltp;

    public AltpHelper(SockAltp sockAltp) {
        this.mSockAltp = sockAltp;
    }

    /**
     * login user
     */
    public void login(User user) {
        if (user == null) {
            Log.e("TAG", "can not login with a NULL user");
            return;
        }
        try {
            Gson gson = new Gson();
            String json = String.format("{user:%s}", gson.toJson(user));
            Log.e("TAG", String.format("user JSON: %s", json));
            JSONObject data = new JSONObject(json);
            mSockAltp.send("login", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * login success
     *
     * @return empty user object if login failed
     */
    public User loginCallback(Object... args) {
        User result = new User();
        if (args.length == 0) {
            Log.e("TAG", "login failed");
            return result;
        }
        JSONObject data = (JSONObject) args[0];
        try {
            boolean success = data.getBoolean("success");
            if (!success) {
                Log.e("TAG", "login failed");
                return null;
            }
            Log.e("TAG", "login success");
            return new Gson().fromJson(data.get("user").toString(), User.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void search(User user) {
        try {
            Gson gson = new Gson();
            String json = String.format("{user:%s}", gson.toJson(user));
            JSONObject data = new JSONObject(json);
            mSockAltp.send("search", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Pair<Room, ArrayList<User>> searchCallback(Object... args) {
        Pair<Room, ArrayList<User>> result = new Pair<>(new Room(), new ArrayList<User>());
        if (args.length == 0) {
            return result;
        }
        JSONObject data = (JSONObject) args[0];

        try {
            Room room = new Gson().fromJson(data.getString("room"), Room.class);
            Log.e("TAG", "searchCallback: " + room.roomId);
            ArrayList<User> dummyUserList = new ArrayList<>();
            JSONArray dummyUsers = data.getJSONArray("dummyUsers");
            for (int i = 0; i < dummyUsers.length(); i++) {
                User dUser = new User();
                JSONObject dummyUser = dummyUsers.getJSONObject(i);
                dUser.id = dummyUser.getString("id");
                dUser.name = dummyUser.getString("name");
                dUser.avatar = dummyUser.getString("avatar");

                dummyUserList.add(dUser);
            }

            result = new Pair<>(room, dummyUserList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void play(User user, Room room) {
        try {
            Gson gson = new Gson();
            String json =
                    String.format("{user:%s, room: %s}", gson.toJson(user), gson.toJson(room));
            Log.e("TAG", "play: " + json);
            JSONObject data = new JSONObject(json);
            mSockAltp.send("play", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return null if user is not ready
     */
    public Question playCallbackQuestion(Object... args) {
        JSONObject data = (JSONObject) args[0];
        try {
            return new Gson().fromJson(data.getString("question"), Question.class);
        } catch (JSONException e) {
            Log.e("TAG", "user has not ready");
        }
        return null;
    }

    /**
     * @return all players are ready
     */
    public boolean playCallbackReady(Object... args) {
        JSONObject data = (JSONObject) args[0];
        return data.optBoolean("notReady", false);
    }

    /**
     * @return 3, 2, 1, 0 or -1
     */
    public int playCallbackCount(Object... args) {
        JSONObject data = (JSONObject) args[0];
        return data.optInt("count", -1);
    }

    public void answer(User user, Room room, int answerIndex) {
        try {
            Gson gson = new Gson();
            String json = String.format("{user:%s, room: %s, answerIndex: %s}", gson.toJson(user),
                    gson.toJson(room), answerIndex);
            JSONObject data = new JSONObject(json);
            mSockAltp.send("answer", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Pair<Integer, ArrayList<User>> answerCallback(Object... args) {
        Pair<Integer, ArrayList<User>> result = new Pair<>(-1, new ArrayList<User>());
        JSONObject data = (JSONObject) args[0];
        if (data.optBoolean("notAllAnswered", false)) {
            Log.e("TAG", "waiting for other answer");
            return result;
        }


        try {
            int answerRight = data.getInt("answerRight");
            JSONArray answerUsers = data.getJSONArray("answerUsers");
            ArrayList<User> answerUserList = new ArrayList<>();
            for (int i = 0; i < answerUsers.length(); i++) {
                User userAnswer = new Gson().fromJson(answerUsers.get(i).toString(), User.class);
                answerUserList.add(userAnswer);
            }
            result = new Pair<>(answerRight, answerUserList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void getNextQuestion(User user, Room room) {
        try {
            Gson gson = new Gson();
            String json =
                    String.format("{user:%s, room: %s}", gson.toJson(user), gson.toJson(room));
            JSONObject data = new JSONObject(json);
            mSockAltp.send("answerNext", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Pair<Room,Question> answerNextCallback(Object... args) {
        Question question = new Question();
        Room room = new Room();
        JSONObject data = (JSONObject) args[0];
        Log.e("TAG", "answerNextCallback:" + data.toString());
        try {
            question = new Gson().fromJson(data.getJSONObject("question").toString(), Question.class);
            room = new Gson().fromJson(data.getJSONObject("room").toString(), Room.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Pair<>(room,question);
    }

    /*
    * @return list users with score
    * */
    public ArrayList<User> gameOverCallback(Object... args) {
        ArrayList<User> answerUserList = new ArrayList<>();
        JSONObject data = (JSONObject) args[0];
        try {
            JSONArray users = data.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                User userAnswer = new Gson().fromJson(users.get(i).toString(), User.class);
                answerUserList.add(userAnswer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answerUserList;
    }

    /**
     * get game over message with win, lose, draw
     * @param args from server
     * @return messages object
     */
    public GameOverMessage gameOverCallbackGetMessages(Object... args){
        GameOverMessage messages = new GameOverMessage();
        JSONObject data = (JSONObject) args[0];
        try {
            JSONObject messagesJson = data.getJSONObject("messages");
            messages = new Gson().fromJson(messagesJson.toString(),GameOverMessage.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public boolean gameOverCallbackGetLastQuestion(Object... args) {
        JSONObject data = (JSONObject) args[0];
        boolean isLastQuestion = false;
        if (data.has("lastQuestion")) {
            try {
                isLastQuestion = data.getBoolean("lastQuestion");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isLastQuestion;
    }

    /**
     * fire when timeout or disconnect
     *
     * @see AltpHelper#gameOverCallback(Object...)
     */
    public void quit(User user, Room room, boolean isPlaying) {
        try {
            Gson gson = new Gson();
            String json =
                    String.format("{user:%s, room:%s, isPlay:%s}", gson.toJson(user), gson.toJson(room), gson.toJson(isPlaying));
            JSONObject data = new JSONObject(json);
            mSockAltp.send("quit", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * fire when an user has loss connection
     */
    public ArrayList<User> quitCallback(Object... args) {
        ArrayList<User> userQuit = new ArrayList<>();
        JSONObject data = (JSONObject) args[0];

        try {
            JSONArray users = data.getJSONArray("users");
            for (int i = 0; i < users.length(); i++) {
                User userAnswer = new Gson().fromJson(users.get(i).toString(), User.class);
                userQuit.add(userAnswer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userQuit;
    }

    /**
     * fire when an user has loss connection
     * @return user quit id
     */
    public String quitCallbackGetUserQuitId(Object... args) {
        JSONObject data = (JSONObject) args[0];
        try {
            return data.getString("quitUserId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
