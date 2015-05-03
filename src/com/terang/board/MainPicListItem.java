package com.terang.board;

import android.net.Uri;


public class MainPicListItem {


	/**
	 * Data array
	 */
	private String[] mData;

	/**
	 * Item ID
	 */
	private String mId;

	/**
	 * True if this item is selectable
	 */
	private boolean mSelectable = true;

	/**
	 * Initialize with icon and data array
	 *
	 * @param obj
	 *
	 */
	public MainPicListItem(String itemId, String[] obj) {
		mId = itemId;
		mData = obj;
	}

	/**
	 * Initialize with strings
	 *
	 *
	 * @param obj01 - memo input_date
	 * @param obj02 - memo memoStr
	 * @param obj03 - memo picture_id
	 *
	 */
	public MainPicListItem(String memoId, String memoTitle, String memoUser, String memoDate,  String uri_handwriting
			, String cntLike, String cntView, String cntReply)
	{
		mId = memoId;
		mData = new String[7];
		mData[0] = memoTitle;
		mData[1] = memoUser;
		mData[2] = memoDate;
		mData[3] = uri_handwriting;
		mData[4] = cntLike;
		mData[5] = cntView;
		mData[6] = cntReply;
	}

	/**
	 * True if this item is selectable
	 */
	public boolean isSelectable() {
		return mSelectable;
	}

	/**
	 * Set selectable flag
	 */
	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}

	public String getId() {
		return mId;
	}

	public void setId(String itemId) {
		mId = itemId;
	}


	/**
	 * Get data array
	 *
	 * @return
	 */
	public String[] getData() {
		return mData;
	}

	/**
	 * Get data
	 */
	public String getData(int index) {
		if (mData == null || index >= mData.length) {
			return null;
		}

		return mData[index];
	}

	/**
	 * Set data array
	 *
	 * @param obj
	 */
	public void setData(String[] obj) {
		mData = obj;
	}


	/**
	 * Compare with the input object
	 *
	 * @param other
	 * @return
	 */
	public int compareTo(MainPicListItem other) {
		if (mData != null) {
			Object[] otherData = other.getData();
			if (mData.length == otherData.length) {
				for (int i = 0; i < mData.length; i++) {
					if (!mData[i].equals(otherData[i])) {
						return -1;
					}
				}
			} else {
				return -1;
			}
		} else {
			throw new IllegalArgumentException();
		}

		return 0;
	}

}
