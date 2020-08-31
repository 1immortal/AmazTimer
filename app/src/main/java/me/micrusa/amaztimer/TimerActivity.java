package me.micrusa.amaztimer;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pixplicity.easyprefs.library.Prefs;

import me.micrusa.amaztimer.TCX.Constants;
import me.micrusa.amaztimer.button.buttonEvent;
import me.micrusa.amaztimer.button.buttonListener;
import me.micrusa.amaztimer.utils.handlers.chronoHandler;
import me.micrusa.amaztimer.utils.handlers.hrZoneHandler;
import me.micrusa.amaztimer.utils.handlers.timeHandler;
import me.micrusa.amaztimer.utils.handlers.timerHandler;
import me.micrusa.amaztimer.utils.heartrate.hrSensor;
import me.micrusa.amaztimer.utils.utils;

public class TimerActivity extends AppCompatActivity {
    private TextView time, status, intervaltime, heartrate, sets;
    private Chronometer elapsedtime;
    private Button cancel, finishset;
    private View hrZoneView;

    private timerHandler timerHandler;
    private timeHandler timeHandler;
    public hrZoneHandler hrZoneHandler;
    private chronoHandler chronoHandler;

    private boolean hasResumed;
    private boolean isWorking;
    private boolean hasFinished;
    private int currSet;
    
    public static boolean isRunning;

    private me.micrusa.amaztimer.button.buttonListener buttonListener = new buttonListener();

    private void init(){
        utils.setupLang(this);
        setContentView(R.layout.activity_timer);
        time = findViewById(R.id.time);
        status = findViewById(R.id.status);
        sets = findViewById(R.id.sets);
        intervaltime = findViewById(R.id.intervaltime);
        heartrate = findViewById(R.id.heartrate);
        elapsedtime = findViewById(R.id.totaltime);
        cancel = findViewById(R.id.cancel);
        finishset = findViewById(R.id.finishset);
        hrZoneView = findViewById(R.id.hrzoneView);
        hrZoneHandler = new hrZoneHandler(hrZoneView);
        setupBtnListener();
        //Setup onClickListeners
        cancel.setOnLongClickListener(view -> {
            utils.vibrate(defValues.HAPTIC_VIBRATION, view.getContext());
            endActivity();
            return true;
        });
        finishset.setOnClickListener(view -> {
            utils.vibrate(defValues.HAPTIC_VIBRATION, view.getContext());
            updateStatus(!isWorking);
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        startTimer();
    }

    private void startTimer(){
        isRunning = true;
        //Setup time
        timeHandler = new timeHandler(time);
        //Setup total time elapsed chrono
        elapsedtime.setBase(SystemClock.elapsedRealtime());
        elapsedtime.start();

        hrSensor.initialize(hr -> {
            heartrate.setText(String.valueOf(hr));
            hrZoneHandler.addHrValue(hr);
        });
        if(Prefs.getBoolean(defValues.KEY_HRTOGGLE, true))
            hrSensor.getInstance().registerListener(this); //Register if hr enabled

        currSet = utils.isModeManualSets() ? 0 : Prefs.getInt(defValues.KEY_SETS, defValues.DEF_SETS) + 1;
        updateStatus(true);
    }

    private boolean endActivity(){
        if(hasFinished) return true;
        hasFinished = true;
        isRunning = false;
        if(Prefs.getBoolean(defValues.KEY_HRTOGGLE, true))
            hrSensor.getInstance().unregisterListener(this); //Unregister if hr enabled
        stopHandlers(true);
        finish();
        return true;
    }

    private void updateStatus(boolean working){
        stopHandlers(false);
        isWorking = working;
        isRunning = true;
        if(working && (utils.isModeManualSets() ? ++currSet : --currSet) == 0) endActivity();
        hrSensor.getInstance().newLap(working ? Constants.STATUS_ACTIVE : Constants.STATUS_RESTING);
        sets.setText(String.valueOf(currSet));
        status.setBackground(ContextCompat.getDrawable(this, working ? R.color.work : R.color.rest));
        status.setText(getResources().getString(working ? R.string.work : R.string.rest));
        if(utils.getMode() >= (working ? 1 : 2))
            chronoHandler = new chronoHandler(intervaltime);
        else
            timerHandler = new timerHandler(intervaltime
                    , working ? Prefs.getInt(defValues.KEY_WORK, defValues.DEF_WORKTIME) : Prefs.getInt(defValues.KEY_REST, defValues.DEF_RESTTIME)
                    , () -> updateStatus(!working), this);
    }
    
    private void stopHandlers(boolean isEnd){
        if(chronoHandler != null) chronoHandler.stop();
        if(timerHandler != null) timerHandler.stop();
        if(isEnd && timeHandler != null) timeHandler.stop();
    }

    //Destroy, pause, resume and button stuff
    public void onDestroy() {
        super.onDestroy();
        buttonListener.stop();
        endActivity();
    }
    public void onPause() {
        this.hasResumed = false;
        buttonListener.stop();
        new Handler().postDelayed(() -> {
            if(!hasResumed) endActivity();
        }, 9 * 1000);
        super.onPause();
    }
    public void onResume() {
        hasResumed = true;
        setupBtnListener();
        super.onResume();
    }
    public void onStop(){
        super.onStop();
        buttonListener.stop();
        new Handler().postDelayed(() -> {
            if(!hasResumed) endActivity();
        }, 9 * 1000);
    }
    public void onStart(){
        super.onStart();
        hasResumed = true;
        setupBtnListener();
    }
    private void setupBtnListener(){
        Handler handler = new Handler();
        buttonListener.start(this, ButtonEvent -> {
            if(ButtonEvent.getKey() == buttonEvent.KEY_DOWN)
                handler.post(() -> cancel.performLongClick());
            else if(ButtonEvent.getKey() == buttonEvent.KEY_UP)
                handler.post(() -> finishset.performClick());
        });
    }
}