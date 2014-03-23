package com.example.obdiibtsim;

import java.util.ArrayList;

import OBDII.OBDIIBase;
import android.os.Handler;

public class OBDIIBTThread extends Thread{

	private ArrayList<OBDIIBase> usingList;
	private boolean isSocketOpened;
	private final Handler mHandler;
	private String[] result;
	
	public void setSocket(boolean socket) {
		this.isSocketOpened = socket;
	}
	
	public OBDIIBTThread(ArrayList<OBDIIBase> list, Handler handler) {
		this.usingList = list;
		this.isSocketOpened = true;
		result = new String[usingList.size()];
		mHandler = handler;
	}
	
	@Override
	public void run() {
		while(isSocketOpened) {
			for(OBDIIBase carData : usingList) {
				System.out.println(carData.getResult());
				result[usingList.indexOf(carData)] = carData.getResult();
			}
//			for(int i=0; i<usingList.size(); i++) {
//				result[i] = usingList.get(i).getResult();
//				System.out.println("result : " + result[i]);
//			}
			mHandler.obtainMessage(1, result).sendToTarget();
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
}
