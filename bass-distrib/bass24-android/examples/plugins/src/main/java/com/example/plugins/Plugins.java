/*
	BASS plugins example
	Copyright (c) 2005-2024 Un4seen Developments Ltd.
*/

package com.example.plugins;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;

import com.un4seen.bass.BASS;

public class Plugins extends Activity {
	int chan;                // channel handle

	Runnable timer;
	Handler timerHandler;

	// display error messages
	void Error(String es) {
		// get error code in current thread for display in UI thread
		String s = String.format("%s\n(error code: %d)", es, BASS.BASS_ErrorGetCode());
		runOnUiThread(() -> {
			new AlertDialog.Builder(Plugins.this)
					.setMessage(s)
					.setPositiveButton("OK", null)
					.show();
		});
	}

	// translate a CTYPE value to text
	String GetCTypeString(int ctype, int plugin) {
		if (plugin != 0) { // using a plugin
			BASS.BASS_PLUGININFO pinfo = BASS.BASS_PluginGetInfo(plugin); // get plugin info
			int a;
			for (a = 0; a < pinfo.formatc; a++) {
				if (pinfo.formats[a].ctype == ctype) // found a "ctype" match...
					return pinfo.formats[a].name; // return its name
			}
		}
		// check built-in stream formats...
		if (ctype == BASS.BASS_CTYPE_STREAM_OGG) return "Ogg Vorbis";
		if (ctype == BASS.BASS_CTYPE_STREAM_MP1) return "MPEG layer 1";
		if (ctype == BASS.BASS_CTYPE_STREAM_MP2) return "MPEG layer 2";
		if (ctype == BASS.BASS_CTYPE_STREAM_MP3) return "MPEG layer 3";
		if (ctype == BASS.BASS_CTYPE_STREAM_AIFF) return "Audio IFF";
		if (ctype == BASS.BASS_CTYPE_STREAM_WAV_PCM) return "PCM WAVE";
		if (ctype == BASS.BASS_CTYPE_STREAM_WAV_FLOAT) return "Floating-point WAVE";
		if ((ctype & BASS.BASS_CTYPE_STREAM_WAV) != 0) // other WAVE codec
			return "WAVE";
		return "?";
	}

	BASS.SYNCPROC blah = (handle, channel, data, user) -> {
	};

	public void OpenClicked(View v) {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT)
//		Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT)
				.addCategory(Intent.CATEGORY_OPENABLE)
				.setType("*/*")
				.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		try {
			startActivityForResult(i, 1);
		} catch (Exception e) {
			Error(e.getMessage());
		}
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
			BASS.BASS_StreamFree(chan); // free the old stream
			chan = BASS.BASS_StreamCreateFile(fd, 0, 0, BASS.BASS_SAMPLE_LOOP | BASS.BASS_SAMPLE_FLOAT);
			if (chan == 0) {
				((Button) findViewById(R.id.open)).setText("Open file...");
				((TextView) findViewById(R.id.info)).setText("");
				((TextView) findViewById(R.id.positiontext)).setText("");
				((SeekBar) findViewById(R.id.position)).setMax(0);
				Error("Can't play the file");
				return;
			}
			// display the file type
			BASS.BASS_CHANNELINFO info = new BASS.BASS_CHANNELINFO();
			BASS.BASS_ChannelGetInfo(chan, info);
			((Button) findViewById(R.id.open)).setText(info.filename != null ? new File(info.filename).getName() : "?");
			String ctype;
			if (info.ctype == BASS.BASS_CTYPE_STREAM_AM)
				ctype = (String) BASS.BASS_ChannelGetTags(chan, BASS.BASS_TAG_AM_MIME);
			else
				ctype = GetCTypeString(info.ctype, info.plugin);
			((TextView) findViewById(R.id.info)).setText(String.format("channel type = %x (%s)", info.ctype, ctype));
			// update scroller range
			long len = BASS.BASS_ChannelGetLength(chan, BASS.BASS_POS_BYTE);
			if (len == -1) len = 0; // unknown length
			((SeekBar) findViewById(R.id.position)).setMax((int) (BASS.BASS_ChannelBytes2Seconds(chan, len) * 1000));
			BASS.BASS_ChannelPlay(chan, false);
			timerHandler.removeCallbacks(timer);
			timerHandler.postDelayed(timer, 100);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (Build.VERSION.SDK_INT >= 23) {
			requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
		}

		// initialize default output device
		if (!BASS.BASS_Init(-1, 44100, 0)) {
			Error("Can't initialize device");
			return;
		}

		// look for plugins
		String plugins = "";
		String[] list = new File(getApplicationInfo().nativeLibraryDir).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("libbass.+\\.so");
			}
		});
		for (String s : list) {
			int plug = BASS.BASS_PluginLoad(s, 0);
			if (plug != 0) { // plugin loaded
				plugins += s + "\n"; // add it to the list
			}
		}
		if (plugins.isEmpty()) plugins = "no plugins - visit the BASS webpage to get some\n";
		((TextView) findViewById(R.id.plugins)).setText(plugins);

		((SeekBar) findViewById(R.id.position)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser)
					BASS.BASS_ChannelSetPosition(chan, BASS.BASS_ChannelSeconds2Bytes(chan, progress / 1000.0), BASS.BASS_POS_BYTE);
			}
		});

		// timer to update the position display
		timerHandler = new Handler(getMainLooper());
		timer = new Runnable() {
			public void run() {
				if (chan != 0) {
					double len = BASS.BASS_ChannelBytes2Seconds(chan, BASS.BASS_ChannelGetLength(chan, BASS.BASS_POS_BYTE));
					double pos = BASS.BASS_ChannelBytes2Seconds(chan, BASS.BASS_ChannelGetPosition(chan, BASS.BASS_POS_BYTE));
					((TextView) findViewById(R.id.positiontext)).setText(String.format("%d:%02d / %d:%02d",
							(int)pos / 60, (int)pos % 60, (int)len / 60, (int)len % 60));
					((SeekBar) findViewById(R.id.position)).setProgress((int)(pos * 1000));
					timerHandler.postDelayed(this, 100);
				}
			}
		};
	}

	@Override
	public void onDestroy() {
		BASS.BASS_Free();
		BASS.BASS_PluginFree(0); // unload all plugins

		super.onDestroy();
	}
}