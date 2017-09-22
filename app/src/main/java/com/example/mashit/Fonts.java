package com.example.mashit;

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

    public Typeface getCourgetteFont(){
        Typeface myType = Typeface.createFromAsset(context.getAssets(),"fonts/Courgette-Regular.ttf");
        return myType;
    }
}
