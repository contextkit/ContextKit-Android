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

package it.cnr.iit.ck.controllers;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;

import it.cnr.iit.ck.model.AudioInfo;

public class AudioController {

    @SuppressWarnings("all")
    public static AudioInfo getAudioInfo(Context context){

        boolean headsetOn = false;

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if(audioManager == null) return null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            for(AudioDeviceInfo device : audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)){
                if(device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                        device.getType() == AudioDeviceInfo.TYPE_DOCK ||
                        device.getType() == AudioDeviceInfo.TYPE_USB_HEADSET ||
                        device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                        device.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET){

                    headsetOn = true;
                    break;
                }
            }
        }else{
            headsetOn = audioManager.isWiredHeadsetOn();
        }

        float alarmVolume = (float)audioManager.getStreamVolume(AudioManager.STREAM_ALARM) /
                (float)audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        float musicVolume = (float)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) /
                (float)audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        float notifVolume = (float)audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) /
                (float)audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);

        float ringVolume = (float)audioManager.getStreamVolume(AudioManager.STREAM_RING) /
                (float)audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

        return new AudioInfo(audioManager.getRingerMode(), alarmVolume, musicVolume, notifVolume,
                ringVolume, audioManager.isBluetoothScoOn(), audioManager.isMicrophoneMute(),
                audioManager.isMusicActive(), audioManager.isSpeakerphoneOn(), headsetOn);
    }
}
