/*
	BASS recording example
	Copyright (c) 2002-2024 Un4seen Developments Ltd.
*/

package com.example.rectest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import com.un4seen.bass.BASS;

public class RecTest extends Activity {
	static final int BUFSTEP = 1000000;    // memory allocation unit

	float volume = 1; // recording level adjustment
	int rchan; // recording channel
	int pchan; // playback channel
	ByteBuffer recbuf; // recording buffer

	// display error messages
	void Error(String es) {
		// get error code in current thread for display in UI thread
		String s = String.format("%s\n(error code: %d)", es, BASS.BASS_ErrorGetCode());
		runOnUiThread(() -> {
			new AlertDialog.Builder(RecTest.this)
					.setMessage(s)
					.setPositiveButton("OK", null)
					.show();
		});
	}

	BASS.RECORDPROC RecordingCallback = (handle, buffer, length, user) -> {
		// buffer the data
		try {
			recbuf.put(buffer);
		} catch (BufferOverflowException e) {
			// increase buffer size
			ByteBuffer temp;
			try {
				temp = ByteBuffer.allocateDirect(recbuf.position() + length + BUFSTEP);
			} catch (Error e2) {
				runOnUiThread(() -> {
					Error("Out of memory!");
					StopRecording();
				});
				return false;
			}
			temp.order(ByteOrder.LITTLE_ENDIAN);
			recbuf.limit(recbuf.position());
			recbuf.position(0);
			temp.put(recbuf);
			recbuf = temp;
			recbuf.put(buffer);
		}
		return true; // continue recording
	};

	void StartRecording() {
		if (pchan != 0) { // free old recording
			BASS.BASS_StreamFree(pchan);
			pchan = 0;
			findViewById(R.id.play).setEnabled(false);
			findViewById(R.id.save).setEnabled(false);
		}
		// get selected sample format
		int format = ((Spinner) findViewById(R.id.format)).getSelectedItemPosition();
		int freq = format > 3 ? 22050 : format > 1 ? 44100 : 48000;
		int chans = 1 + (format & 1);
		// allocate initial buffer and write the WAVE header
		recbuf = ByteBuffer.allocateDirect(BUFSTEP);
		recbuf.order(ByteOrder.LITTLE_ENDIAN);
		recbuf.put(new byte[]{'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'A', 'V', 'E', 'f', 'm', 't', ' ', 16, 0, 0, 0});
		recbuf.putShort((short) 1);
		recbuf.putShort((short) chans);
		recbuf.putInt(freq);
		recbuf.putInt(freq * chans * 2);
		recbuf.putShort((short) 2);
		recbuf.putShort((short) 16);
		recbuf.put(new byte[]{'d', 'a', 't', 'a', 0, 0, 0, 0});
		// start recording (paused to set VOLDSP first)
		rchan = BASS.BASS_RecordStart(freq, chans, BASS.BASS_RECORD_PAUSE, RecordingCallback, null);
		if (rchan == 0) {
			Error("Can't start recording");
			return;
		}
		BASS.BASS_ChannelSetAttribute(rchan, BASS.BASS_ATTRIB_VOLDSP, volume);
		BASS.BASS_ChannelStart(rchan); // resume recording
		((Button) findViewById(R.id.record)).setText("Stop");
		findViewById(R.id.format).setEnabled(false);
	}

	void StopRecording() {
		BASS.BASS_ChannelStop(rchan);
		rchan = 0;
		recbuf.limit(recbuf.position());
		// complete the WAVE header
		recbuf.putInt(4, recbuf.position() - 8);
		recbuf.putInt(40, recbuf.position() - 44);
		// enable "save" button
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			findViewById(R.id.save).setEnabled(true);
		// create a stream from the recording
		pchan = BASS.BASS_StreamCreateFile(recbuf, 0, recbuf.limit(), 0);
		if (pchan != 0)
			findViewById(R.id.play).setEnabled(true); // enable "play" button
		((Button) findViewById(R.id.record)).setText("Record");
		findViewById(R.id.format).setEnabled(true);
	}

	public void Record(View v) {
		if (rchan == 0)
			StartRecording();
		else
			StopRecording();
	}

	public void Play(View v) {
		BASS.BASS_ChannelPlay(pchan, true); // play the recorded data
	}

	public void Save(View v) {
		File file = new File(getExternalFilesDir(null), "bass.wav");
		try {
			FileChannel fc = new FileOutputStream(file).getChannel();
			recbuf.position(0);
			fc.write(recbuf);
			fc.close();
			new AlertDialog.Builder(RecTest.this)
					.setMessage("Saved to:\n" + file.toString())
					.setPositiveButton("OK", null)
					.show();
		} catch (IOException e) {
			Error("Can't save the file");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (Build.VERSION.SDK_INT >= 23)
			requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);

		// initialize default recording device
		if (!BASS.BASS_RecordInit(-1)) {
			Error("Can't initialize recording device");
			return;
		}
		// initialize default output device
		if (!BASS.BASS_Init(-1, 48000, 0))
			Error("Can't initialize output device");

		String[] formats = new String[]{
			"48000 Hz mono 16-bit", "48000 Hz stereo 16-bit",
			"44100 Hz mono 16-bit", "44100 Hz stereo 16-bit",
			"22050 Hz mono 16-bit", "22050 Hz stereo 16-bit"
		};
		((Spinner) findViewById(R.id.format)).setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, formats));
		// preselect native rate
		BASS.BASS_RECORDINFO info = new BASS.BASS_RECORDINFO();
		BASS.BASS_RecordGetInfo(info);
		if (info.freq == 0) info.freq = 44100; // native rate unknown
		if (info.freq <= 22050)
			((Spinner) findViewById(R.id.format)).setSelection(4);
		else if (info.freq <= 44100)
			((Spinner) findViewById(R.id.format)).setSelection(2);
		else
			((Spinner) findViewById(R.id.format)).setSelection(0);

		((SeekBar) findViewById(R.id.volume)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser)
					BASS.BASS_ChannelSetAttribute(rchan, BASS.BASS_ATTRIB_VOLDSP, volume = progress / 100.f); // set recording level adjustment
			}
		});

		// timer to update the display
		Handler timerHandler = new Handler(getMainLooper());
		Runnable timer = new Runnable() {
			public void run() {
				// update the level display
				float[] level = {0};
				if (rchan != 0 || pchan != 0) {
					BASS.BASS_ChannelGetLevelEx(rchan != 0 ? rchan : pchan, level, 0.1f, BASS.BASS_LEVEL_MONO);
					if (rchan != 0) level[0] *= volume; // apply recording level adjustment
					if (level[0] > 0) {
						level[0] = (float)(1 + 0.5 * Math.log10(level[0])); // convert to dB (40dB range)
						if (level[0] < 0) level[0] = 0;
						if (level[0] > 1) level[0] = 1;
					}
				}
				((ProgressBar) findViewById(R.id.level)).setProgress((int)(level[0] * 100));
				// update the recording/playback counter
				String text = "-";
				if (rchan != 0) { // recording
					text = String.format("%d", BASS.BASS_ChannelGetPosition(rchan, BASS.BASS_POS_BYTE));
				} else if (pchan != 0) {
					if (BASS.BASS_ChannelIsActive(pchan) != BASS.BASS_ACTIVE_STOPPED) // playing
						text = String.format("%d\n%d", BASS.BASS_ChannelGetLength(pchan, BASS.BASS_POS_BYTE), BASS.BASS_ChannelGetPosition(pchan, BASS.BASS_POS_BYTE));
					else
						text = String.format("%d", BASS.BASS_ChannelGetLength(pchan, BASS.BASS_POS_BYTE));
				}
				((TextView) findViewById(R.id.position)).setText(text);
				timerHandler.postDelayed(this, 50);
			}
		};
		timerHandler.postDelayed(timer, 50);
	}

	@Override
	public void onDestroy() {
		// release all BASS stuff
		BASS.BASS_RecordFree();
		BASS.BASS_Free();

		super.onDestroy();
	}
}