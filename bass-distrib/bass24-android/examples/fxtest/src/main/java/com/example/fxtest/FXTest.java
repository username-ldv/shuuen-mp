/*
	BASS effects example
	Copyright (c) 2001-2024 Un4seen Developments Ltd.
*/

package com.example.fxtest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;

import java.io.File;
import java.lang.Math;

import com.un4seen.bass.BASS;

public class FXTest extends Activity {
	int chan;               // channel handle
	int fxchan;             // output stream handle
	int[] fx = new int[4];  // 3 eq bands + reverb

	// display error messages
	void Error(String es) {
		// get error code in current thread for display in UI thread
		String s = String.format("%s\n(error code: %d)", es, BASS.BASS_ErrorGetCode());
		runOnUiThread(() -> {
			new AlertDialog.Builder(FXTest.this)
					.setMessage(s)
					.setPositiveButton("OK", null)
					.show();
		});
	}

	void UpdateFX(SeekBar sb) {
		int v = sb.getProgress();
		int n = Integer.parseInt((String) sb.getTag());
		if (n < 3) { // EQ
			BASS.BASS_DX8_PARAMEQ p = new BASS.BASS_DX8_PARAMEQ();
			BASS.BASS_FXGetParameters(fx[n], p);
			p.fGain = v - 10;
			BASS.BASS_FXSetParameters(fx[n], p);
		} else if (n == 3) { // reverb
			BASS.BASS_DX8_REVERB p = new BASS.BASS_DX8_REVERB();
			BASS.BASS_FXGetParameters(fx[n], p);
			p.fReverbMix = (float) (v != 0 ? Math.log(v / 20.0) * 20 : -96);
			BASS.BASS_FXSetParameters(fx[n], p);
		} else // volume
			BASS.BASS_ChannelSetAttribute(chan, BASS.BASS_ATTRIB_VOL, v / 10.f);
	}

	void SetupFX() {
		// setup the effects
		int ch = fxchan != 0 ? fxchan : chan; // set on output stream if enabled, else file stream
		fx[0] = BASS.BASS_ChannelSetFX(ch, BASS.BASS_FX_DX8_PARAMEQ, 0);
		fx[1] = BASS.BASS_ChannelSetFX(ch, BASS.BASS_FX_DX8_PARAMEQ, 0);
		fx[2] = BASS.BASS_ChannelSetFX(ch, BASS.BASS_FX_DX8_PARAMEQ, 0);
		fx[3] = BASS.BASS_ChannelSetFX(ch, BASS.BASS_FX_DX8_REVERB, 0);
		BASS.BASS_DX8_PARAMEQ p = new BASS.BASS_DX8_PARAMEQ();
		p.fGain = 0;
		p.fBandwidth = 18;
		p.fCenter = 125;
		BASS.BASS_FXSetParameters(fx[0], p);
		p.fCenter = 1000;
		BASS.BASS_FXSetParameters(fx[1], p);
		p.fCenter = 8000;
		BASS.BASS_FXSetParameters(fx[2], p);
		UpdateFX(findViewById(R.id.eq1));
		UpdateFX(findViewById(R.id.eq2));
		UpdateFX(findViewById(R.id.eq3));
		UpdateFX(findViewById(R.id.reverb));
	}

	public void OpenClicked(View v) {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT)
				.addCategory(Intent.CATEGORY_OPENABLE)
				.setType("*/*")
				.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		try {
			startActivityForResult(i, 1);
		} catch (Exception e) {
			Error(e.getMessage());
		}
	}

	public void OutputClicked(View v) {
		// remove current effects
		BASS.BASS_FXFree(fx[0]);
		BASS.BASS_FXFree(fx[1]);
		BASS.BASS_FXFree(fx[2]);
		BASS.BASS_FXFree(fx[3]);
		if (((CheckBox) findViewById(R.id.output)).isChecked())
			fxchan = BASS.BASS_StreamCreate(0, 0, 0, BASS.STREAMPROC_DEVICE, null); // get device output stream
		else
			fxchan = 0; // stop using device output stream
		SetupFX();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent resultData) {
		if (resultCode == Activity.RESULT_OK) {
			ParcelFileDescriptor fd;
			try {
				fd = getContentResolver().openFileDescriptor(resultData.getData(), "r");
			} catch (Exception e) {
				Error(e.getMessage());
				return;
			}
			BASS.BASS_ChannelFree(chan); // free the old channel
			chan = BASS.BASS_StreamCreateFile(fd, 0, 0, BASS.BASS_SAMPLE_LOOP | BASS.BASS_SAMPLE_FLOAT);
			if (chan == 0 && BASS.BASS_ErrorGetCode() == BASS.BASS_ERROR_FILEFORM)
				chan = BASS.BASS_MusicLoad(fd, 0, 0, BASS.BASS_MUSIC_RAMPS | BASS.BASS_SAMPLE_LOOP | BASS.BASS_SAMPLE_FLOAT, 1);
			if (chan == 0) {
				((Button) findViewById(R.id.open)).setText("Open file...");
				Error("Can't play the file");
				return;
			}
			BASS.BASS_CHANNELINFO info = new BASS.BASS_CHANNELINFO();
			BASS.BASS_ChannelGetInfo(chan, info);
			((Button) findViewById(R.id.open)).setText(info.filename != null ? new File(info.filename).getName() : "?");
			if (fxchan == 0)
				SetupFX(); // set effects on file if not using output stream
			UpdateFX(findViewById(R.id.volume)); // set volume
			BASS.BASS_ChannelPlay(chan, false);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (Build.VERSION.SDK_INT >= 23)
			requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

		// initialize default output device
		if (!BASS.BASS_Init(-1, 44100, 0)) {
			Error("Can't initialize device");
			return;
		}

		SeekBar.OnSeekBarChangeListener osbcl = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				UpdateFX(seekBar);
			}
		};
		((SeekBar) findViewById(R.id.eq1)).setOnSeekBarChangeListener(osbcl);
		((SeekBar) findViewById(R.id.eq2)).setOnSeekBarChangeListener(osbcl);
		((SeekBar) findViewById(R.id.eq3)).setOnSeekBarChangeListener(osbcl);
		((SeekBar) findViewById(R.id.reverb)).setOnSeekBarChangeListener(osbcl);
		((SeekBar) findViewById(R.id.volume)).setOnSeekBarChangeListener(osbcl);
	}

	@Override
	public void onDestroy() {
		BASS.BASS_Free();

		super.onDestroy();
	}
}