package me.micrusa.amaztimer;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import clc.sliteplugin.flowboard.AbstractPlugin;
import clc.sliteplugin.flowboard.ISpringBoardHostStub;
import me.micrusa.app.amazwidgets.R;

public class widget extends AbstractPlugin {

    private static final String TAG = "AmazTimer";
    //As AbstractPlugin is not an Activity or Service, we can't just use "this" as a context or getApplicationContext, so Context is global to allow easier access
    private Context mContext;
    //These get set up later
    private View mView;
    private boolean mHasActive = false;
    private ISpringBoardHostStub mHost = null;
    private int v;
    //Setup objects
    private Button plus, plus2, plus3, minus, minus2, minus3, start;
    private TextView sets, rest, work, time, hr, rSets, status;
    private ConstraintLayout L1, L2;
    //Classes
    private utils utils = new utils();
    //Default values
    private defValues defValues = new defValues();


    //Much like a fragment, getView returns the content view of the page. You can set up your layout here
    @Override
    public View getView(final Context paramContext) {
        Log.d(TAG, "getView()" + paramContext.getPackageName() + " AmazTimer");
        this.mContext = paramContext;
        this.mView = LayoutInflater.from(paramContext).inflate(R.layout.amaztimer, null);
        final View gView = this.mView;
        final file file = new file("amaztimer", mView.getContext());
        this.init();
        //Text default values
        sets.setText(String.valueOf(file.get("sets", defValues.sets)));
        work.setText(utils.sToMinS(file.get("work", defValues.workTime)));
        rest.setText(utils.sToMinS(file.get("rest", defValues.restTime)));
        //Plus and minus buttons
        plus.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                v = file.get("sets", defValues.sets) + 1;
                if(v>defValues.maxSets){
                    v = defValues.maxSets;
                    utils.vibrate(defValues.sVibration, gView.getContext());
                }
                file.set("sets", v);
                sets.setText(String.valueOf(v));
            }
        });
        minus.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                v = file.get("sets", defValues.sets) - 1;
                if(v<defValues.minSets){
                    v = defValues.minSets;
                    utils.vibrate(defValues.sVibration, gView.getContext());
                }
                file.set("sets", v);
                sets.setText(String.valueOf(v));
            }
        });
        //Work
        plus2.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                v = file.get("work", defValues.workTime) + 1;
                if(v>defValues.maxTime){
                    v = defValues.maxTime;
                    utils.vibrate(defValues.sVibration, gView.getContext());
                }
                file.set("work", v);
                work.setText(utils.sToMinS(v));
            }
        });
        minus2.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                v = file.get("work", defValues.workTime) - 1;
                if(v<defValues.minTime){
                    v = defValues.minTime;
                    utils.vibrate(defValues.sVibration, gView.getContext());
                }
                file.set("work", v);
                work.setText(utils.sToMinS(v));
            }
        });
        //Rest
        plus3.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                v = file.get("rest", defValues.restTime) + 1;
                if(v>defValues.maxTime){
                    v = defValues.maxTime;
                    utils.vibrate(defValues.sVibration, gView.getContext());
                }
                file.set("rest", v);
                rest.setText(utils.sToMinS(v));
            }
        });
        minus3.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                v = file.get("rest", defValues.restTime) - 1;
                if(v<defValues.minTime){
                    v = defValues.minTime;
                    utils.vibrate(defValues.sVibration, gView.getContext());
                }
                file.set("rest", v);
                rest.setText(utils.sToMinS(v));
            }
        });

        //Start button
        start.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                //Move to second layout with timer's stuff and set all texts
                L1.setVisibility(View.GONE);
                L2.setVisibility(View.VISIBLE);
                L2.setBackgroundColor(gView.getResources().getColor(R.color.yellow));
                rSets.setText(String.valueOf(file.get("sets", defValues.sets)));
                status.setText(gView.getResources().getString(R.string.prepare));
                //Start hrSensor listener
                final hrSensor hrSensor = new hrSensor(gView.getContext(), hr);
                hrSensor.registerListener();
                final CountDownTimer PrepareTimer = new CountDownTimer(5 * 1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        v = (int) l / 1000;
                        time.setText(utils.sToMinS(v));
                        if(v<4){
                            if(v==1){
                                utils.vibrate(defValues.lVibration, gView.getContext());}
                            if(v!=1){
                                utils.vibrate(defValues.sVibration, gView.getContext());}
                        }

                    }

                    @Override
                    public void onFinish() {
                        startTimer(gView, gView.getResources().getString(R.string.work), gView.getResources().getString(R.string.rest), file.get("work", defValues.workTime), file.get("rest", defValues.restTime), hrSensor); }
                };
                PrepareTimer.start();

            }
        });
        return this.mView;
    }

    private void init(){
        //Buttons
        plus = this.mView.findViewById(R.id.plus);
        plus2 = this.mView.findViewById(R.id.plus2);
        plus3 = this.mView.findViewById(R.id.plus3);
        minus = this.mView.findViewById(R.id.minus2);
        minus2 = this.mView.findViewById(R.id.minus);
        minus3 = this.mView.findViewById(R.id.minus3);
        start = this.mView.findViewById(R.id.start);
        //TextViews
        sets = this.mView.findViewById(R.id.sets);
        rest = this.mView.findViewById(R.id.rest);
        work = this.mView.findViewById(R.id.work);
        time = this.mView.findViewById(R.id.time);
        hr = this.mView.findViewById(R.id.heartbeat);
        rSets = this.mView.findViewById(R.id.remSets);
        status = this.mView.findViewById(R.id.status);
        //Layouts
        L1 = this.mView.findViewById(R.id.startScreen);
        L2 = this.mView.findViewById(R.id.timerScreen);
    }

    private void startTimer(final View c, final String sWork, final String sRest, final int work, final int rest, final hrSensor hrSensor){
        this.init();
        status.setText(sWork);
        L2.setBackgroundColor(c.getResources().getColor(R.color.red));
        if(!this.mHasActive){
            return;
        }
        CountDownTimer Timer = new CountDownTimer(work * 1000, 1000) {
            @Override
            public void onTick(long l) {
                v = (int) l / 1000;
                time.setText(utils.sToMinS(v));
                if(v<4){
                    if(v==1){
                        utils.vibrate(defValues.lVibration, c.getContext());}
                    if(v!=1){
                        utils.vibrate(defValues.sVibration, c.getContext());}
                }
            }

            @Override
            public void onFinish() {
                restTimer(c, sWork, sRest, work, rest, hrSensor);
            }
        };
        Timer.start();
    }

    private void restTimer(final View c, final String sWork, final String sRest, final int work, final int rest, final hrSensor hrSensor){
        this.init();
        status.setText(sRest);
        L2.setBackgroundColor(c.getResources().getColor(R.color.green));
        if(!this.mHasActive){
            return;
        }
        CountDownTimer Timer = new CountDownTimer(rest * 1000, 1000) {
            @Override
            public void onTick(long l) {
                v = (int) l / 1000;
                time.setText(utils.sToMinS(v));
                if (v < 4) {
                    if (v != 1) {
                        utils.vibrate(defValues.sVibration, c.getContext());
                    } else{
                        utils.vibrate(defValues.lVibration, c.getContext());}
                }
            }

            @Override
            public void onFinish() {
                if(Integer.parseInt(rSets.getText().toString())!=1){
                    rSets.setText(String.valueOf(Integer.parseInt(rSets.getText().toString()) - 1));
                    startTimer(c, sWork, sRest, work, rest, hrSensor);
                }else{
                    //Unregister hrSensor listener and make visible initial screen again
                    hrSensor.unregisterListener();
                    L1.setVisibility(View.VISIBLE);
                    L2.setVisibility(View.GONE);
                }
            }
        };
        Timer.start();
    }

    //Return the icon for this page, used when the page is disabled in the app list. In this case, the launcher icon is used
    @Override
    public Bitmap getWidgetIcon(Context paramContext) {
        return ((BitmapDrawable) this.mContext.getResources().getDrawable(R.drawable.add)).getBitmap();
    }

    //Return the launcher intent for this page. This might be used for the launcher as well when the page is disabled?
    @Override
    public Intent getWidgetIntent() {
        return new Intent();
    }

    //Return the title for this page, used when the page is disabled in the app list. In this case, the app name is used
    @Override
    public String getWidgetTitle(Context paramContext) {
        return "AmazTimer";
    }

    //Called when the page is shown
    @Override
    public void onActive(Bundle paramBundle) {
        super.onActive(paramBundle);
        //Check if the view is already inflated (reloading)
        if ((!this.mHasActive) && (this.mView != null)) {
            //It is, simply refresh
            refreshView();
        }
        //Store active state
        this.mHasActive = true;
    }

    private void refreshView() {
    }

    //Returns the springboard host
    public ISpringBoardHostStub getHost() {
        return this.mHost;
    }

    //Called when the page is loading and being bound to the host
    @Override
    public void onBindHost(ISpringBoardHostStub paramISpringBoardHostStub) {
        Log.d(TAG, "onBindHost");
        //Store host
        this.mHost = paramISpringBoardHostStub;
    }

    //Called when the page is destroyed completely (in app mode). Same as the onDestroy method of an activity
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Called when the page becomes inactive (the user has scrolled away)
    @Override
    public void onInactive(Bundle paramBundle) {
        super.onInactive(paramBundle);
        //Store active state
        this.mHasActive = false;
    }

    //Called when the page is paused (in app mode)
    @Override
    public void onPause() {
        super.onPause();
        this.mHasActive = false;
    }
    //Not sure what this does, can't find it being used anywhere. Best leave it alone
    @Override
    public void onReceiveDataFromProvider(int paramInt, Bundle paramBundle) {
        super.onReceiveDataFromProvider(paramInt, paramBundle);
    }

    //Called when the page is shown again (in app mode)
    @Override
    public void onResume() {
        super.onResume();
        //Check if view already loaded
        if ((!this.mHasActive) && (this.mView != null)) {
            //It is, simply refresh
            this.mHasActive = true;
            refreshView();
        }
        //Store active state
        this.mHasActive = true;
    }

    //Called when the page is stopped (in app mode)
    @Override
    public void onStop() {
        super.onStop();
        this.mHasActive = false;
    }
}