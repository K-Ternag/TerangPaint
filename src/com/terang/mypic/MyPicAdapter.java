package com.terang.mypic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyPicAdapter extends BaseAdapter {

	private Context mContext;

	private List<MyPicListItem> mItems = new ArrayList<MyPicListItem>();

	public MyPicAdapter(Context context) {
		mContext = context;
	}

	public void clear() {
		mItems.clear();
	}

	public void addItem(MyPicListItem it) {
		mItems.add(it);
	}

	public void setListItems(List<MyPicListItem> lit) {
		mItems = lit;
	}

	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int position) {
		return mItems.get(position);
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isSelectable(int position) {
		try {
			return mItems.get(position).isSelectable();
		} catch (IndexOutOfBoundsException ex) {
			return false;
		}
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		MyPictureView itemView;
		if (convertView == null) {
			itemView = new MyPictureView(mContext);
		} else {
			itemView = (MyPictureView) convertView;
		}

		// set current item data
		itemView.setContents(0, (String) mItems.get(position).getData(0));
		itemView.setContents(1, (String) mItems.get(position).getData(1));
		itemView.setContents(2, (String) mItems.get(position).getData(2));

		return itemView;
	}

}
