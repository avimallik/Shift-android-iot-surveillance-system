package com.armavi.shift.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeOfDetection {


    Date TIME = new Date();
    Date DATE = new Date();

    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss a");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");

    //Clock
    String timeTemp = sdfTime.format(TIME);
    //Date
    String timeDateTemp = sdfDate.format(DATE);
}
