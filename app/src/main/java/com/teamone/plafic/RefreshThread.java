package com.teamone.plafic;

import android.util.Log;

public class RefreshThread extends Thread {
    MainActivity m_parent;
    int number = 0;

    public RefreshThread(MainActivity parent) {
        m_parent = parent;
    }

    @Override
    public void run() {
        while (true) {
            m_parent.showNoti(String.valueOf(number), m_parent.notiBuilder, m_parent.notiManager);
            number++;
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("REFRESH_TH", "Thread interrupted.");
            }
        }
    }
}
