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

package it.cnr.iit.ck.probes;

import it.cnr.iit.ck.controllers.AudioController;
import it.cnr.iit.ck.model.AudioInfo;

/**
 * This probe monitors several information related to the {@link android.media.AudioManager}.
 * Specifically, it monitors the following information:
 *  - ringer mode
 *  - alarm volume
 *  - music volume
 *  - notification volume
 *  - ring volume
 *  - if a Bluetooth Sco is connected
 *  - if a microphone is connected
 *  - if the music is active
 *  - if the speaker is on
 *  - if a pair of headset is connected
 *
 */
@SuppressWarnings("unused")
class AudioProbe extends ContinuousProbe {

    @Override
    public void init() {}

    @Override
    public void onFirstRun() {}

    @Override
    void onStop() {}

    @Override
    public void exec() {

        AudioInfo audioInfo = AudioController.getAudioInfo(getContext());

        if(audioInfo != null){
            logOnFile(true, audioInfo);
        }
    }
}
