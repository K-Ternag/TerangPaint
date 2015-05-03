package com.terang.paintboard;

import com.terang.paintboard.R;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 굵기 선택 대화상자
 *
 * @author Mike
 * @date 2011-07-01
 */
public class PenPaletteDialog extends Activity implements OnSeekBarChangeListener {

    //declare variables
    SeekBar seekbar1;
    int value;
    TextView result;
    
    //recieve intent
    Intent intent;
    int sizePen;

	public static OnPenSelectedListener mSelectedListener;

	// set constructor for when activity is first created
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.handwriting_dialog_pen);
		seekbar1 = (SeekBar) findViewById(R.id.sbBar);
		result = (TextView) findViewById(R.id.tvResult);
		this.setTitle("펜 크기를 선택 하세요");

		intent = getIntent();

		sizePen = Integer.parseInt(intent.getStringExtra("size"));
		
		seekbar1.setProgress(sizePen);
		result.setText("크기 : " + sizePen);

		// set change listener
		seekbar1.setOnSeekBarChangeListener(this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		value = progress;
		result.setText("크기 : " + value);

		PenPaletteDialog.mSelectedListener.onPenSelected(value);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}
}

