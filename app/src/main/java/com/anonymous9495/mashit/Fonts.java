package com.anonymous9495.mashit;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by anjali desai on 22-09-2017.
 */

public class Fonts {
    Context context;

    public Fonts(Context mCon){
        context = mCon;
    }

    public Typeface getCinzelBoldFont(){
        Typeface myType = Typeface.createFromAsset(context.getAssets(),"fonts/Cinzel-Bold.ttf");
        return myType;
    }

    public Typeface getCinzelFont(){
        Typeface myType = Typeface.createFromAsset(context.getAssets(),"fonts/Cinzel-Regular.ttf");
        return myType;
    }

    public Typeface getCourgetteFont(){
        Typeface myType = Typeface.createFromAsset(context.getAssets(),"fonts/Courgette-Regular.ttf");
        return myType;
    }

    public Typeface getBreeSerifFont(){
        Typeface myType = Typeface.createFromAsset(context.getAssets(),"fonts/BreeSerif-Regular.ttf");
        return myType;
    }

    public Typeface getKaushanFont(){
        Typeface myType = Typeface.createFromAsset(context.getAssets(),"fonts/KaushanScript-Regular.ttf");
        return myType;
    }

    public Typeface getLobsterFont(){
        Typeface myType = Typeface.createFromAsset(context.getAssets(),"fonts/Lobster-Regular.ttf");
        return myType;
    }


}
