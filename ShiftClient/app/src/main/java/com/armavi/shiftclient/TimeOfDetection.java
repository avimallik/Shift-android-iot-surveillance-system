package com.armavi.shiftclient;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeOfDetection {


    Date dateTemp = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");

    //Clock
    String timeTemp = sdf.format(dateTemp);

    //Date
    String timeDateTemp = sdfDate.format(dateTemp);
}
