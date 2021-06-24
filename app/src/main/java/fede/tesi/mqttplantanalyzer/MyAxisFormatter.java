package fede.tesi.mqttplantanalyzer;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyAxisFormatter extends ValueFormatter{
    private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.ITALIAN);
    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        long millis = TimeUnit.MINUTES.toMillis((long) value);
        return mFormat.format(new Date(millis));
    }

}
