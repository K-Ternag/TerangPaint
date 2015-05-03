package com.terang.board;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MainPicdapter extends BaseAdapter {

	private Context mContext;

	private List<MainPicListItem> mItems = new ArrayList<MainPicListItem>();

	public MainPicdapter(Context context) {
		mContext = context;
	}

	public void clear() {
		mItems.clear();
	}

	public void addItem(MainPicListItem it) {
		mItems.add(it);
	}

	public void setListItems(List<MainPicListItem> lit) {
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
		MainPictureView itemView;
		if (convertView == null) {
			itemView = new MainPictureView(mContext);
		} else {
			itemView = (MainPictureView) convertView;
		}

		// set current item data
		itemView.setContents(0, (String) mItems.get(position).getData(0));
		itemView.setContents(1, (String) mItems.get(position).getData(1));
		itemView.setContents(2, (String) mItems.get(position).getData(2));
		itemView.setContents(3, (String) mItems.get(position).getData(3));
		itemView.setContents(4, (String) mItems.get(position).getData(4));
		itemView.setContents(5, (String) mItems.get(position).getData(5));
		itemView.setContents(6, (String) mItems.get(position).getData(6));

		return itemView;
	}

}
