/*
	BASS spectrum analyser example
	Copyright (c) 2002-2024 Un4seen Developments Ltd.
*/

package com.example.spectrum;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.View;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.un4seen.bass.BASS;

public class Spectrum extends Activity {
	int chan;  // channel handle
	BASS.BASS_CHANNELINFO info = new BASS.BASS_CHANNELINFO();

	Runnable timer;
	Handler timerHandler;
	SpectrumView specview;

	// display error messages
	void Error(String es) {
		// get error code in current thread for display in UI thread
		String s = String.format("%s\n(error code: %d)", es, BASS.BASS_ErrorGetCode());
		runOnUiThread(() -> {
			new AlertDialog.Builder(Spectrum.this)
					.setMessage(s)
					.setPositiveButton("OK", null)
					.show();
		});
	}

	class SpectrumView extends View {
		private final int refreshrate = 20;
		private int[] palette;
		private int specmode;	// spectrum mode
		private int specpos;	// marker pos for 3D mode
		private int width, height;
		private int dwidth, dheight;
		int[] specbuf;

		public SpectrumView(Context context) {
			super(context);

			// setup palette
			palette = new int[256];
			for (int a = 1; a < 128; a++) {
				palette[a] = Color.rgb(2 * a, 256 - 2 * a, 0);
			}
			for (int a = 0; a < 32; a++) {
				palette[128 + a] = Color.rgb(0, 0, 8 * a);
				palette[128 + 32 + a] = Color.rgb(8 * a, 0, 255);
				palette[128 + 64 + a] = Color.rgb(255, 8 * a, 8 * (31 - a));
				palette[128 + 96 + a] = Color.rgb(255, 255, 8 * a);
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			dwidth = w;
			dheight = h;
			width = Math.min(368, dwidth);
			height = Math.min(127, dheight);
			specbuf = new int[height * width];
			specpos = 0;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (chan == 0) {
					Intent i = new Intent(Intent.ACTION_GET_CONTENT)
							.addCategory(Intent.CATEGORY_OPENABLE)
							.setType("*/*")
							.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
					try {
						startActivityForResult(i, 1);
					} catch (Exception e) {
						Error(e.getMessage());
					}
				} else {
					specmode = (specmode + 1) % 4; // change spectrum mode
					Arrays.fill(specbuf, 0); // clear display
				}
				return true;
			}
			return false;
		}

		@Override
		@SuppressWarnings("deprecation")
		protected void onDraw(Canvas canvas) {
			if (chan == 0) {
				Paint p = new Paint();
				p.setColor(Color.WHITE);
				p.setTextAlign(Align.CENTER);
				p.setTextSize(Math.min(dwidth / 20, dheight / 16));
				canvas.drawText("touch to open a file", dwidth / 2, dheight / 2, p);
				return;
			}
			if (specmode == 3) { // waveform
				Arrays.fill(specbuf, 0);
				ByteBuffer bbuf = ByteBuffer.allocateDirect(info.chans * width * 4); // allocate buffer for data
				bbuf.order(null); // little-endian byte order
				BASS.BASS_ChannelGetData(chan, bbuf, info.chans * width * 4); // get the sample data
				float[] pcm = new float[info.chans * width]; // allocate a "float" array for the sample data
				bbuf.asFloatBuffer().get(pcm); // get the data from the buffer into the array
				for (int c = 0; c < info.chans; c++) {
					for (int x = 0, y = 0; x < width; x++) {
						int v = (int) ((1 - pcm[x * info.chans + c]) * height / 2); // invert and scale to fit display
						if (v < 0) v = 0;
						else if (v >= height) v = height;
						if (x == 0) y = v;
						do { // draw line from previous sample...
							if (y < v) y++;
							else if (y > v) y--;
							specbuf[y * width + x] = palette[(c & 1) != 0 ? 127 : 1]; // left=green, right=red (could add more colours to palette for more chans)
						} while (y != v);
					}
				}
			} else {
				ByteBuffer bbuf = ByteBuffer.allocateDirect(512 * 4); // allocate a buffer for the FFT data
				bbuf.order(null); // little-endian byte order
				BASS.BASS_ChannelGetData(chan, bbuf, BASS.BASS_DATA_FFT1024); // get the FFT data
				float[] fft = new float[512]; // allocate a "float" array for the FFT data
				bbuf.asFloatBuffer().get(fft); // get the data from the buffer into the array
				if (specmode == 0) { // "normal" FFT
					Arrays.fill(specbuf, 0);
					for (int x = 0, y, y1 = 0; x < width / 2; x++) {
						if (true) {
							y = (int) (Math.sqrt(fft[x + 1]) * 3 * height - 4); // scale it (sqrt to make low values more visible)
						} else {
							y = (int) (fft[x + 1] * 10 * height); // scale it (linearly)
						}
						if (y > height) y = height; // cap it
						if (x > 0) { // interpolate from previous to make the display smoother
							y1 = (y + y1) / 2;
							while (--y1 >= 0)
								specbuf[(height - 1 - y1) * width + x * 2 - 1] = palette[y1 * 127 / height + 1];
						}
						y1 = y;
						while (--y >= 0)
							specbuf[(height - 1 - y) * width + x * 2] = palette[y * 127 / height + 1]; // draw level
					}
				} else if (specmode == 1) { // logarithmic, combine bins
					Arrays.fill(specbuf, 0);
					final int BANDS = 28;
					for (int x = 0, b0 = 0; x < BANDS; x++) {
						float peak = 0;
						int b1 = (int) Math.pow(2, x * 9.0 / (BANDS - 1));
						if (b1 <= b0) b1 = b0 + 1; // make sure it uses at least 1 FFT bin
						if (b1 > 511) b1 = 511;
						for (; b0 < b1; b0++)
							if (peak < fft[1 + b0]) peak = fft[1 + b0];
						int y = (int) (Math.sqrt(peak) * 3 * height - 4); // scale it (sqrt to make low values more visible)
						if (y > height) y = height; // cap it
						while (--y >= 0) {
							int s = (height - 1 - y) * width + x * (width / BANDS);
							Arrays.fill(specbuf, s, s + (width / BANDS) * 9 / 10, palette[y * 127 / height + 1]); // draw bar
						}
					}
				} else { // "3D"
					for (int x = 0; x < height; x++) {
						int y = (int) (Math.sqrt(fft[x + 1]) * 3 * 127); // scale it (sqrt to make low values more visible)
						if (y > 127) y = 127; // cap it
						specbuf[(height - 1 - x) * width + specpos] = palette[128 + y]; // plot it
					}
					// move marker onto next position
					specpos = (specpos + 1) % width;
					for (int x = 0; x < height; x++) specbuf[x * width + specpos] = Color.WHITE;
				}
			}
			// update the display
			canvas.drawBitmap(Bitmap.createBitmap(specbuf, width, height, Bitmap.Config.ARGB_8888),
					new Rect(0, 0, width, height), new Rect(0, 0, dwidth, dheight), null);
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
			chan = BASS.BASS_StreamCreateFile(fd, 0, 0, BASS.BASS_SAMPLE_LOOP | BASS.BASS_SAMPLE_FLOAT);
			if (chan == 0 && BASS.BASS_ErrorGetCode() == BASS.BASS_ERROR_FILEFORM)
				chan = BASS.BASS_MusicLoad(fd, 0, 0, BASS.BASS_MUSIC_RAMPS | BASS.BASS_SAMPLE_LOOP | BASS.BASS_SAMPLE_FLOAT, 1);
			if (chan == 0) {
				Error("Can't play the file");
				return;
			}
			BASS.BASS_ChannelGetInfo(chan, info);
			BASS.BASS_ChannelPlay(chan, false);
			timerHandler.postDelayed(timer, 1000 / specview.refreshrate);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		specview = new SpectrumView(this);
		setContentView(specview);

		if (Build.VERSION.SDK_INT >= 23)
			requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

		// initialize default output device
		if (!BASS.BASS_Init(-1, 44100, 0)) {
			Error("Can't initialize device");
			return;
		}

		// timer to update the display
		timerHandler = new Handler(getMainLooper());
		timer = new Runnable() {
			public void run() {
				specview.invalidate();
				timerHandler.postDelayed(this, 1000 / specview.refreshrate);
			}
		};
	}

	@Override
	public void onDestroy() {
		BASS.BASS_Free();

		super.onDestroy();
	}
}