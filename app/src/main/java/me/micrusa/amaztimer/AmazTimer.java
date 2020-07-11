package me.micrusa.amaztimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import me.micrusa.amaztimer.activities.PrepareActivity;
import me.micrusa.amaztimer.activities.SettingsActivity;
import me.micrusa.amaztimer.button.buttonEvent;
import me.micrusa.amaztimer.button.buttonListener;
import me.micrusa.amaztimer.utils.SystemProperties;
import me.micrusa.amaztimer.utils.file;
import me.micrusa.amaztimer.utils.utils;

public class AmazTimer extends Activity {
    //Define items
    private Button plus, plus2, plus3, minus, minus2, minus3, start;
    private TextView sets, rest, work;
    //Other classes
    private buttonListener buttonListener = new buttonListener();
    //Settings
    private boolean hasResumed = false;
    private boolean hasLaunchedIntent = false;
    private file timerFile;

    private final View.OnClickListener plusMinusBtnListener = view -> plusMinusUpdates(view.getId(), false);
    private final View.OnLongClickListener plusMinusBtnLongListener = view -> plusMinusUpdates(view.getId(), true);

    private boolean plusMinusUpdates(int id, boolean longClick){
        int sets = timerFile.get(defValues.SETTINGS_SETS, defValues.DEF_SETS);
        int workTime = timerFile.get(defValues.SETTINGS_WORK, defValues.DEF_WORKTIME);
        int restTime = timerFile.get(defValues.SETTINGS_REST, defValues.DEF_RESTTIME);
        //Increase or decrease the value that user clicked
        switch(id){
            case R.id.plus:
                sets = utils.getUpdatedSets(sets, longClick ? 5 : 1, this);
                break;
            case R.id.plus2:
                workTime = utils.getUpdatedTime(workTime, longClick ? 60 : 1, this);
                break;
            case R.id.plus3:
                restTime = utils.getUpdatedTime(restTime, longClick ? 60 : 1, this);
                break;
            case R.id.minus2:
                sets = utils.getUpdatedSets(sets, longClick ? -5 : -1, this);
                break;
            case R.id.minus:
                workTime = utils.getUpdatedTime(workTime, longClick ? -60 : -1, this);
                break;
            case R.id.minus3:
                restTime = utils.getUpdatedTime(restTime, longClick ? -60 : -1, this);
                break;
            default:
                break;
        }
        utils.pushToFile(timerFile, sets, workTime, restTime);
        setTexts();
        return true;
    }


    //Much like a fragment, getView returns the content view of the page. You can set up your layout here
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils.setLang(this, new file(defValues.SETTINGS_FILE).get(defValues.SETTINGS_LANG, defValues.DEFAULT_LANG));
        setContentView(SystemProperties.isStratos3() || SystemProperties.isVerge() ? R.layout.round_amaztimer : R.layout.amaztimer);
        this.init();
        setupBtnListener();

        plus.setOnClickListener(plusMinusBtnListener);
        plus2.setOnClickListener(plusMinusBtnListener);
        plus3.setOnClickListener(plusMinusBtnListener);
        minus.setOnClickListener(plusMinusBtnListener);
        minus2.setOnClickListener(plusMinusBtnListener);
        minus3.setOnClickListener(plusMinusBtnListener);
        plus.setOnLongClickListener(plusMinusBtnLongListener);
        plus2.setOnLongClickListener(plusMinusBtnLongListener);
        plus3.setOnLongClickListener(plusMinusBtnLongListener);
        minus.setOnLongClickListener(plusMinusBtnLongListener);
        minus2.setOnLongClickListener(plusMinusBtnLongListener);
        minus3.setOnLongClickListener(plusMinusBtnLongListener);

        start.setOnClickListener(view -> launchIntent(new Intent(view.getContext(),
                new file(defValues.SETTINGS_FILE).get(defValues.SETTINGS_ENABLEPREPARE, false)
                        ? PrepareActivity.class : TimerActivity.class)));

        start.setOnLongClickListener(view -> launchIntent(new Intent(view.getContext(), SettingsActivity.class)));
    }

    private void setTexts(){
        sets.setText(String.valueOf(timerFile.get(defValues.SETTINGS_SETS, defValues.DEF_SETS)));
        work.setText(utils.formatTime(timerFile.get(defValues.SETTINGS_WORK, defValues.DEF_WORKTIME)));
        rest.setText(utils.formatTime(timerFile.get(defValues.SETTINGS_WORK, defValues.DEF_WORKTIME)));
    }

    private void init() {
        timerFile = new file(defValues.TIMER_FILE);
        plus = findViewById(R.id.plus);
        plus2 = findViewById(R.id.plus2);
        plus3 = findViewById(R.id.plus3);
        minus = findViewById(R.id.minus2);
        minus2 = findViewById(R.id.minus);
        minus3 = findViewById(R.id.minus3);
        start = findViewById(R.id.start);
        sets = findViewById(R.id.sets);
        rest = findViewById(R.id.rest);
        work = findViewById(R.id.work);
    }

    public void onStart() {
        super.onStart();
        hasLaunchedIntent = false;
        setTexts();
    }

    public void onStop() {
        super.onStop();
        buttonListener.stop();
    }

    public void onPause() {
        hasResumed = false;
        new Handler().postDelayed(() -> {
            if(hasResumed)
                return;
            buttonListener.stop();
        }, 15 * 1000);
        super.onPause();
    }

    public void onResume() {
        hasResumed = true;
        hasLaunchedIntent = false;
        setupBtnListener();
        super.onResume();
    }

    private boolean launchIntent(Intent intent){
        if(hasLaunchedIntent)
            return true;
        hasLaunchedIntent = true;
        buttonListener.stop();
        startActivity(intent);
        return true;
    }

    private void setupBtnListener(){
        buttonListener.start(this, ButtonEvent -> {
            if(ButtonEvent.getKey() == buttonEvent.KEY_DOWN)
                new Handler().post(() -> start.performClick());
            else if(ButtonEvent.getKey() == buttonEvent.KEY_CENTER)
                new Handler().post(() -> start.performLongClick());
        });
    }
}
