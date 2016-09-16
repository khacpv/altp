package com.oic.game.ailatrieuphu.util;

/**
 * Created by kienht on 8/30/16.
 */

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundPoolManager {

    private static SoundPoolManager instance;
    private SoundPool soundPool;
    private List<Integer> sounds;
    private HashMap<Integer, SoundSampleEntity> hashMap;
    private boolean isPlaySound;

    public synchronized static SoundPoolManager getInstance() {
        return instance;
    }

    public static void CreateInstance() {
        if (instance == null) {
            instance = new SoundPoolManager();
        }
    }

    public List<Integer> getSounds() {
        return sounds;
    }

    public void setSounds(List<Integer> sounds) {
        this.sounds = sounds;
    }


    public void InitializeSoundPool(Activity activity, final ISoundPoolLoaded callback) throws Exception {
        if (sounds == null || sounds.size() == 0) {
            throw new Exception("Sounds not set");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(1)
                    .build();

        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        }


        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                SoundSampleEntity entity = getEntity(sampleId);
                if (entity != null) {
                    entity.setLoaded(status == 0);
                }

                if (sampleId == maxSampleId()) {
                    callback.onSuccess();
                }
            }
        });
        int length = sounds.size();
        hashMap = new HashMap<>();
        int index;
        for (index = 0; index < length; index++) {
            hashMap.put(sounds.get(index), new SoundSampleEntity(0, false));
        }
        index = 0;
        for (Map.Entry<Integer, SoundSampleEntity> entry : hashMap.entrySet()) {
            index++;
            int loadResId = soundPool.load(activity, entry.getKey(), index);
            entry.getValue().setSampleId(loadResId);
        }
    }

    private int maxSampleId() {
        int sampleId = 0;
        for (Map.Entry<Integer, SoundSampleEntity> entry : hashMap.entrySet()) {
            SoundSampleEntity entity = entry.getValue();
            sampleId = entity.getSampleId() > sampleId ? entity.getSampleId() : sampleId;
        }
        return sampleId;
    }

    private SoundSampleEntity getEntity(int sampleId) {
        for (Map.Entry<Integer, SoundSampleEntity> entry : hashMap.entrySet()) {
            SoundSampleEntity entity = entry.getValue();
            if (entity.getSampleId() == sampleId) {
                return entity;
            }
        }
        return null;
    }

    public boolean isPlaySound() {
        return isPlaySound;
    }

    public void setPlaySound(boolean isPlaySound) {
        this.isPlaySound = isPlaySound;
    }

    public void playSound(int resourceId) {
        if (isPlaySound()) {
            SoundSampleEntity entity = hashMap.get(resourceId);
            if (entity.getSampleId() > 0 && entity.isLoaded()) {
                int streamId = soundPool.play(entity.getSampleId(), 1f, 1f, 1, 0, 1f);
                entity.setStreamId(streamId);
            }
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
        }
    }

    public void stop() {
        if (soundPool != null) {
            for (Map.Entry<Integer, SoundSampleEntity> entry : hashMap.entrySet()) {
                SoundSampleEntity entity = entry.getValue();
                soundPool.stop(entity.getStreamId());
            }
        }
    }


    private class SoundSampleEntity {
        private int sampleId;   // for play, return by load()
        private boolean isLoaded;
        private int streamId;   // for stop, return by play()


        public SoundSampleEntity(int sampleId, boolean isLoaded) {
            this.isLoaded = isLoaded;
            this.sampleId = sampleId;
        }

        public int getSampleId() {
            return sampleId;
        }

        public void setSampleId(int sampleId) {
            this.sampleId = sampleId;
        }

        public boolean isLoaded() {
            return isLoaded;
        }

        public void setLoaded(boolean isLoaded) {
            this.isLoaded = isLoaded;
        }

        public int getStreamId() {
            return streamId;
        }

        public void setStreamId(int streamId) {
            this.streamId = streamId;
        }
    }
}

