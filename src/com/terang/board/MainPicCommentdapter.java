package com.terang.board;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MainPicCommentdapter extends BaseAdapter {

	private Context mContext;

	private List<MainPicCommentListItem> mItems = new ArrayList<MainPicCommentListItem>();

	public MainPicCommentdapter(Context context) {
		mContext = context;
	}

	public void clear() {
		mItems.clear();
	}

	public void addItem(MainPicCommentListItem it) {
		mItems.add(it);
	}

	public void setListItems(List<MainPicCommentListItem> lit) {
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
		MainPicCommentView itemView;
		if (convertView == null) {
			itemView = new MainPicCommentView(mContext);
		} else {
			itemView = (MainPicCommentView) convertView;
		}

		// set current item data
		itemView.setContents(0, (String) mItems.get(position).getData(0));
		itemView.setContents(1, (String) mItems.get(position).getData(1));

		return itemView;
	}

}
