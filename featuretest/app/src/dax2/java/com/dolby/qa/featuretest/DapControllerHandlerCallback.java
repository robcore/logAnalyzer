package com.dolby.qa.featuretest;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dolby.dax.DolbyAudioEffect;
import com.dolby.dax.DsParams;

import java.util.Arrays;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.dolby.qa.featuretest.Constants.GLOBAL_SESSION_ID_NUM;
import static com.dolby.qa.featuretest.Constants.MIDDLE_PRIORITY_NUM;
import static com.dolby.qa.featuretest.Constants.MSG_UPDATE_DAP_FEATURE_TYPE_TV;
import static com.dolby.qa.featuretest.Constants.MSG_UPDATE_DAP_FEATURE_VALUE_TV;
import static com.dolby.qa.featuretest.Constants.MSG_UPDATE_DAP_PROFILE_TV;
import static com.dolby.qa.featuretest.Constants.MSG_UPDATE_DAP_STATUS_TV;
import static com.dolby.qa.featuretest.Constants.MSG_UPDATE_PLAY_CONTENT_TV;
import static com.dolby.qa.featuretest.Constants.MSG_UPDATE_RESET_TV;
import static com.dolby.qa.featuretest.Constants.MSG_UPDATE_UI_ARG1_DEFAULT;
import static com.dolby.qa.featuretest.Constants.MSG_UPDATE_UI_ARG1_VISIBLE;
import static com.dolby.qa.featuretest.MainActivity.mDAP;
import static com.dolby.qa.featuretest.MainActivity.mTrack;
import static com.dolby.qa.featuretest.MainActivity.playOrPause;
import static com.dolby.qa.featuretest.MainActivity.releaseResource;
import static com.dolby.qa.featuretest.MainActivity.restartPlayback;
import static com.dolby.qa.featuretest.MainActivity.stopPlayback;

public class DapControllerHandlerCallback implements Handler.Callback{
    private Handler mUpdateUIHandlerInMainUi;

    public DapControllerHandlerCallback(Handler mUpdateUIHandlerInMainUi) {
        this.mUpdateUIHandlerInMainUi = mUpdateUIHandlerInMainUi;
    }

    @Override
    public boolean handleMessage(Message msg) {
        int messageType = msg.what;
        switch (messageType) {
            case Constants.MSG_PLAY_CONTENT:
                String mContentShowInPlayContentTV = String.format(Locale.getDefault(), "play content : %s", msg.obj);
                packageIntentForUIUpdate(MSG_UPDATE_PLAY_CONTENT_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mContentShowInPlayContentTV);
                String mFileName = (String) msg.obj;
                playOrPause(mFileName);
                /* create a dap instance every time app playback content */
                if (mDAP != null) {
                    mDAP.release();
                    mDAP=null;
                }
                if (mTrack != null){
                    mTrack.release();
                    mTrack = null;
                }
//                        mDAP = new DolbyAudioEffect(5, mSession);
                mDAP = new DolbyAudioEffect(MIDDLE_PRIORITY_NUM, GLOBAL_SESSION_ID_NUM);
                break;
            case Constants.MSG_STOP_PLAYBACK:
                stopPlayback();
                break;
            case Constants.MSG_RESTART_PLAYBACK:
                restartPlayback();
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_STATUS:
                String mContentShowInDapStatusTV = String.format(Locale.getDefault(), "set ds status : %b", msg.arg1 != 0);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_STATUS_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mContentShowInDapStatusTV);
                mDAP.setDsOn(msg.arg1 != 0);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_PROFILE:
                String mContentShowInDapProfileTV = String.format(Locale.getDefault(), "set profile id: %d", msg.arg1);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_PROFILE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mContentShowInDapProfileTV);
                mDAP.setProfile(msg.arg1);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_RESET_PROFILE:
                if (msg.arg1 == Constants.ID_RESET_ALL_PROFILE) {
                    String mContentShowInDapFeatureTypeTV = String.format(Locale.getDefault(), "reset all profile : %d", msg.arg1);
                    packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mContentShowInDapFeatureTypeTV);

                    for (int index = 0; index < ConstantdDax2.PROFILE_NUM; index++) {
                        if (mDAP.isProfileSpecificSettingsModified(index)) {
                            mDAP.resetProfileSpecificSettings(index);
                        }
                    }
                } else {
                    String mContentShowInDapFeatureTypeTV = String.format(Locale.getDefault(), "reset profile id: %d", msg.arg1);
                    packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mContentShowInDapFeatureTypeTV);
                    mDAP.resetProfileSpecificSettings(msg.arg1);
                }
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_RESET_UNIVERSAL:
                String mContentShowInDapFeatureTypeTV = String.format(Locale.getDefault(), "reset universal parameter %d", msg.arg1);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mContentShowInDapFeatureTypeTV);
                mDAP.resetUniversalSettings();
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_PROFILE_DE:
                String mdeTV = String.format(Locale.getDefault(), "change de : %b", msg.arg1 != 0);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mdeTV);
                mDAP.setDialogEnhancementEnabled(msg.arg1 != 0);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_PROFILE_IEQ:
                String mieqTV = String.format(Locale.getDefault(), "change ieq : %d", msg.arg1);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mieqTV);
                mDAP.setIeqPreset(msg.arg1);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_PROFILE_HV:
                String mhvTV = String.format(Locale.getDefault(), "change headphone virtualizer : %b", msg.arg1 != 0);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mhvTV);
                mDAP.setDolbyHeadphoneVirtualizerEnabled(msg.arg1 != 0);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_PROFILE_VSV:
                String mvsvTV = String.format(Locale.getDefault(), "change speaker virtualizer : %b", msg.arg1 != 0);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mvsvTV);
                if (!mDAP.isMonoSpeaker()){
                    mDAP.setDolbyVirtualSpeakerVirtualizerEnabled(msg.arg1 != 0);
                }else{
                    try{
                        mDAP.setDolbyVirtualSpeakerVirtualizerEnabled(msg.arg1 != 0);
                    }catch (UnsupportedOperationException e){
                        e.printStackTrace();
                        Log.d(TAG,"virtual speaker virtualizer can not be operated for mono speaker endpoint!");
                    }
                }
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_UNIVERSAL_VL:
                String mvlTV = String.format(Locale.getDefault(), "change vl : %b", msg.arg1 != 0);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mvlTV);
                int[] vlValue = {msg.arg1};
                mDAP.setUniversalParameter(DsParams.DolbyVolumeLevelerEnable.toInt(), vlValue);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_UNIVERSAL_BE:
                String mbeTV = String.format(Locale.getDefault(), "change be : %b", msg.arg1 != 0);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mbeTV);
                int[] beValue = {msg.arg1};
                mDAP.setUniversalParameter(DsParams.BassEnable.toInt(), beValue);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_UNIVERSAL_GEQ:
                String mgeqonTV = String.format(Locale.getDefault(), "change geq status : %b", msg.arg1 != 0);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mgeqonTV);
                int[] geqValue = {msg.arg1};
                mDAP.setUniversalParameter(DsParams.GraphicEqualizerEnable.toInt(), geqValue);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_UNIVERSAL_GEBG:
                String mgeqbgTV = String.format(Locale.getDefault(), "change geq band gain : %s", msg.arg1);
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mgeqbgTV);
                int[] geqbgValue = (int[]) msg.obj;
                String mgeqbgValueTV = String.format(Locale.getDefault(), "geq band gain : %s", Arrays.toString(geqbgValue));
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_VALUE_TV,MSG_UPDATE_UI_ARG1_VISIBLE,mgeqbgValueTV);
                mDAP.setUniversalParameter(DsParams.GraphicEqualizerBandGains.toInt(), geqbgValue);
                break;
            case Constants.MSG_CHANGE_DAP_FEATURE_DAP_FEATURE_TYPE:
                String mTypeTV = "feature type :" + DsParams.FromInt(msg.arg1).toString();
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_TYPE_TV,MSG_UPDATE_UI_ARG1_DEFAULT,mTypeTV);
                int[] featureValue = (int[]) msg.obj;
                String mValueTV = String.format(Locale.getDefault(), "feature value : %s",Arrays.toString(featureValue));
                packageIntentForUIUpdate(MSG_UPDATE_DAP_FEATURE_VALUE_TV,MSG_UPDATE_UI_ARG1_VISIBLE,mValueTV);
                mDAP.setParameter(msg.arg1, featureValue);
                break;
            case Constants.MSG_RECORD_LOG:
                //show_dap_feature_type_tv.setText(String.format(Locale.getDefault(),"record log : %d",msg.arg1));
                break;
            case Constants.MSG_RELEASE_RESOURCE:
                releaseResource();
                packageIntentForUIUpdate(MSG_UPDATE_RESET_TV,MSG_UPDATE_UI_ARG1_DEFAULT,null);
                break;
            default:
                break;
        }
        return false;
    }

    private void packageIntentForUIUpdate(int msgType, int msgArg1, Object msgObj){
        Message msg= mUpdateUIHandlerInMainUi.obtainMessage();
        msg.what=msgType;
        msg.arg1= msgArg1;
        msg.obj=msgObj;
        mUpdateUIHandlerInMainUi.sendMessage(msg);
    }
}
