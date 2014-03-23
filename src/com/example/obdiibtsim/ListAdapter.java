package com.example.obdiibtsim;

import java.util.ArrayList;

import OBDII.OBDIIBase;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ListAdapter extends BaseAdapter{
	//private Context mContext;
	private ArrayList<PidInfo> pidList;
	private LayoutInflater inflater;
	public String str1;
	public String str2;

	public ListAdapter(Context context, ArrayList<PidInfo> list) {
		//this.mContext = context;
		this.pidList = list;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void inputPid(PidInfo info) {
		this.pidList.add(info);
	}

	public int getCount() {
		return pidList.size();
	}

	public Object getItem(int position) {
		return pidList.get(position).getName();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int pos = position;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.pidlist, parent, false);
		}
		
		CheckBox cb = (CheckBox)convertView.findViewById(R.id.checkBox);
		cb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				System.out.println("선택함!!!!!!");
				PidInfo selectedPInfo = pidList.get(pos);
				selectedPInfo.setCheck(!selectedPInfo.isChecked());
				System.out.println("변경 내용" + !selectedPInfo.isChecked() + "->" + selectedPInfo.isChecked());						
			}			
		});
		String cbTxt = pidList.get(pos).getName() + "(" + pidList.get(pos).getValue() + ")" ;
		cb.setChecked(pidList.get(pos).isChecked());
		cb.setText(cbTxt);
		
		return convertView;
	}
}
