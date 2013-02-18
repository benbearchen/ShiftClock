package org.bxmy.shiftclock.shiftduty;

import java.util.ArrayList;

public class ShiftDuty {

    private static ShiftDuty sShiftDuty;

    private ArrayList<Duty> mDuties = new ArrayList<Duty>();

    public static synchronized ShiftDuty getInstance() {
        if (sShiftDuty == null) {
            sShiftDuty = new ShiftDuty();
            sShiftDuty.load();
        }

        return sShiftDuty;
    }

    private ShiftDuty() {
    }

    public String[] getDutyNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Duty duty : mDuties) {
            names.add(duty.getName());
        }

        return (String[]) names.toArray();
    }

    public Duty[] getDuties() {
        return (Duty[]) mDuties.toArray(new Duty[0]);
    }

    private void load() {
        addTestDuties();
    }

    private void addTestDuties() {
        Duty duty = new Duty();
        duty.setName("白班");
        duty.setStartSecondsInDay(3600 * 9 + 1800);
        duty.setDurationSeconds(3600 * 9);
        mDuties.add(duty);
    }
}
