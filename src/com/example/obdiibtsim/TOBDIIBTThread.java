package com.example.obdiibtsim;

import java.util.ArrayList;

import android.os.Handler;

public class TOBDIIBTThread extends Thread{

	private ArrayList<PidInfo> pidList;
	private boolean isSocketOpened;
	private final Handler mHandler;
	private String[] result;
	private int a;

	public TOBDIIBTThread(ArrayList<PidInfo> tusingList, Handler mHandler2) {
		this.pidList = tusingList;
		this.isSocketOpened = true;
		mHandler = mHandler2;
		result = new String[tusingList.size()];
		a = 0;
	}

	public void setSocket(boolean socket) {
		this.isSocketOpened = socket;
	}

	@Override
	public void run() {
		while(isSocketOpened) {
			for(PidInfo pid : pidList) {
				result[pidList.indexOf(pid)] = pid.getName() + " result" + Integer.toString(a);
			}
			a++;
			mHandler.obtainMessage(1, result).sendToTarget();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
