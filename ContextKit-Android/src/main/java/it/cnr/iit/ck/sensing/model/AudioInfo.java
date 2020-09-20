/*
 *   Copyright (c) 2017. Mattia Campana, mattia.campana@iit.cnr.it, Franca Delmastro, franca.delmastro@gmail.com
 *
 *   This file is part of ContextKit.
 *
 *   ContextKit (CK) is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ContextKit (CK) is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ContextKit (CK).  If not, see <http://www.gnu.org/licenses/>.
 */

package it.cnr.iit.ck.sensing.model;

import android.media.AudioManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.cnr.iit.ck.commons.FeaturesUtils;
import it.cnr.iit.ck.logs.FileLogger;

public class AudioInfo implements SensorData {

    public static List<String> FEATURES_HEADER = Arrays.asList("audio_ringer_mode_silent",
            "audio_ringer_mode_vibrate", "audio_ringer_mode_normal", "alarm_volume", "music_volume",
            "notification_volume", "ringer_volume", "microphone_mute", "music_active", "speaker_on",
            "headset_on");

    public static List<String> LOG_HEADER = Arrays.asList("ringer_mode", "alarm_volume",
            "music_volume", "notification_volume", "ringer_volume", "microphone_mute",
            "music_active", "speaker_on", "headset_on");

    private static final int MAX_VOLUME_VALUE = 100;
    private static final List<Integer> RINGER_MODES = Arrays.asList(AudioManager.RINGER_MODE_SILENT,
            AudioManager.RINGER_MODE_VIBRATE, AudioManager.RINGER_MODE_NORMAL);

    public Integer ringerMode;
    public Float alarmVolume, musicVolume, notificationVolume, ringVolume;
    public Boolean microphoneMute, musicActive, speakerOn, headsetOn;

    public AudioInfo(){}

    public AudioInfo(int ringerMode, float alarmVolume, float musicVolume, float notificationVolume,
                     float ringVolume, boolean microphoneMute,  boolean musicActive,
                     boolean speakerOn, boolean headsetOn){

        this.ringerMode = ringerMode;
        this.alarmVolume = alarmVolume/MAX_VOLUME_VALUE;
        this.musicVolume = musicVolume/MAX_VOLUME_VALUE;
        this.notificationVolume = notificationVolume/MAX_VOLUME_VALUE;
        this.ringVolume = ringVolume/MAX_VOLUME_VALUE;
        this.microphoneMute = microphoneMute;
        this.musicActive = musicActive;
        this.speakerOn = speakerOn;
        this.headsetOn = headsetOn;
    }

    @Override
    public String getDataToLog() {
        return StringUtils.join(Arrays.asList(ringerMode, alarmVolume, musicVolume,
                notificationVolume, ringVolume, microphoneMute, musicActive, speakerOn,
                headsetOn), FileLogger.SEP);
    }

    @Override
    public String getLogHeader() {
        return StringUtils.join(LOG_HEADER, FileLogger.SEP);
    }

    @Override
    public List<Double> getFeatures() {
        List<Double> features = FeaturesUtils.oneHotVector(ringerMode, RINGER_MODES);
        Collections.addAll(features,
                alarmVolume != null ? (double) alarmVolume : null,
                musicVolume != null ? (double) musicVolume : null,
                musicVolume != null ? (double) notificationVolume : null,
                ringVolume != null ? (double) ringVolume : null,
                microphoneMute != null ? FeaturesUtils.binarize(microphoneMute) : null,
                musicActive != null ? FeaturesUtils.binarize(musicActive) : null,
                speakerOn != null ? FeaturesUtils.binarize(speakerOn) : null,
                headsetOn != null ? FeaturesUtils.binarize(headsetOn) : null
        );

        return features;
    }
}
