package com.example.aprokopenko.triphelper;

import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testFindMaxSpeed() throws Exception{
        final float case1 = CalculationUtils.findMaxSpeed(10,5);
        final float case2 = CalculationUtils.findMaxSpeed(5,10);
        final float case3 = CalculationUtils.findMaxSpeed(0,0);

        assertTrue(case1==10);
        assertTrue(case2==10);
        assertTrue(case3==0);
    }

    @Test
    public void testGetDistanceMultiplier(){
        float measurementMultiplier;
        CalculationUtils.setMeasurementMultiplier(0);
        measurementMultiplier = CalculationUtils.getMeasurementUnitMultiplier();
        assertTrue(measurementMultiplier == ConstantValues.KMH_MULTIPLIER);

        CalculationUtils.setMeasurementMultiplier(1);
        measurementMultiplier = CalculationUtils.getMeasurementUnitMultiplier();
        assertTrue(measurementMultiplier == ConstantValues.MPH_MULTIPLIER);

        CalculationUtils.setMeasurementMultiplier(2);
        measurementMultiplier = CalculationUtils.getMeasurementUnitMultiplier();
        assertTrue(measurementMultiplier == ConstantValues.KNOTS_MULTIPLIER);

        CalculationUtils.setMeasurementMultiplier(3);
        measurementMultiplier = CalculationUtils.getMeasurementUnitMultiplier();
        assertTrue(measurementMultiplier == ConstantValues.KMH_MULTIPLIER);

        CalculationUtils.setMeasurementMultiplier(4);
        measurementMultiplier = CalculationUtils.getMeasurementUnitMultiplier();
        assertTrue(measurementMultiplier == ConstantValues.KMH_MULTIPLIER);

        CalculationUtils.setMeasurementMultiplier(-1);
        measurementMultiplier = CalculationUtils.getMeasurementUnitMultiplier();
        assertTrue(measurementMultiplier == ConstantValues.KMH_MULTIPLIER);
    }

    @Test
    public void testGetHoursFromMills(){

    }
}