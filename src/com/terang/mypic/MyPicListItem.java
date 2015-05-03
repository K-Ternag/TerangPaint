package com.terang.mypic;

import android.net.Uri;


public class MyPicListItem {


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
	public MyPicListItem(String itemId, String[] obj) {
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
	public MyPicListItem(String memoId, String memoDate, String memoText, String uri_handwriting)
	{
		mId = memoId;
		mData = new String[3];
		mData[0] = memoDate;
		mData[1] = memoText;
		mData[2] = uri_handwriting;
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
	public int compareTo(MyPicListItem other) {
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
