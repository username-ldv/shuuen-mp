/*
	BASS internet radio example
	Copyright (c) 2002-2025 Un4seen Developments Ltd.
*/

package com.example.netradio;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.un4seen.bass.BASS;

public class NetRadio extends Activity {
	int req; // request number/counter
	int chan; // stream handle

	static final String[] urls = { // preset stream URLs
		"http://stream-dc1.radioparadise.com/rp_192m.ogg", "http://www.radioparadise.com/m3u/mp3-32.m3u",
		"http://somafm.com/secretagent.pls", "http://somafm.com/secretagent32.pls",
		"http://somafm.com/suburbsofgoa.pls", "http://somafm.com/suburbsofgoa32.pls",
		"http://www.bassdrive.com/bassdrive.m3u", "http://www.bassdrive.com/bassdrive3.m3u",
		"http://sc6.radiocaroline.net:8040/listen.pls", "http://sc2.radiocaroline.net:8010/listen.pls"
	};

	// HLS definitions (copied from BASSHLS.Java)
	static final int BASS_SYNC_HLS_SEGMENT = 0x10300;
	static final int BASS_TAG_HLS_EXTINF = 0x14000;

	Runnable timer;
	Handler timerHandler;
	final Object lock = new Object();

	// display error messages
	void Error(String es) {
		// get error code in current thread for display in UI thread
		String s = String.format("%s\n(error code: %d)", es, BASS.BASS_ErrorGetCode());
		runOnUiThread(() -> {
			new AlertDialog.Builder(NetRadio.this)
					.setMessage(s)
					.setPositiveButton("OK", null)
					.show();
		});
	}

	// update stream title from metadata
	void DoMeta() {
		String meta = (String) BASS.BASS_ChannelGetTags(chan, BASS.BASS_TAG_META);
		if (meta != null) { // got Shoutcast metadata
			int ti = meta.indexOf("StreamTitle='");
			if (ti >= 0) {
				String title = meta.substring(ti + 13, meta.indexOf("';", ti + 13));
				((TextView) findViewById(R.id.status1)).setText(title);
			}
		} else {
			String[] ogg = (String[]) BASS.BASS_ChannelGetTags(chan, BASS.BASS_TAG_OGG);
			if (ogg != null) { // got Icecast/OGG tags
				String artist = null, title = null;
				for (String s : ogg) {
					if (s.regionMatches(true, 0, "artist=", 0, 7))
						artist = s.substring(7);
					else if (s.regionMatches(true, 0, "title=", 0, 6))
						title = s.substring(6);
				}
				if (title != null) {
					if (artist != null)
						((TextView) findViewById(R.id.status1)).setText(title + " - " + title);
					else
						((TextView) findViewById(R.id.status1)).setText(title);
				}
			} else {
				meta = (String) BASS.BASS_ChannelGetTags(chan, BASS_TAG_HLS_EXTINF);
				if (meta != null) { // got HLS segment info
					int i = meta.indexOf(',');
					if (i > 0)
						((TextView) findViewById(R.id.status1)).setText(meta.substring(i + 1));
				}
			}
		}
	}

	BASS.SYNCPROC MetaSync = (handle, channel, data, user) -> {
		runOnUiThread(() -> {
			DoMeta();
		});
	};

	BASS.SYNCPROC StallSync = (handle, channel, data, user) -> {
		if (data == 0) // stalled
			timerHandler.postDelayed(timer, 50); // start buffer monitoring
	};

	BASS.SYNCPROC FreeSync = (handle, channel, data, user) -> {
		chan = 0;
		runOnUiThread(() -> {
			((TextView) findViewById(R.id.status2)).setText("not playing");
			((TextView) findViewById(R.id.status1)).setText("");
			((TextView) findViewById(R.id.status3)).setText("");
		});
	};

	BASS.DOWNLOADPROC StatusProc = (buffer, length, user) -> {
		if ((Integer)user != req) return; // not the current request
		if (buffer != null && length == 0) { // got HTTP/ICY tags
			String[] s;
			try {
				CharsetDecoder dec = Charset.forName("ISO-8859-1").newDecoder();
				ByteBuffer temp = ByteBuffer.allocate(buffer.limit()); // CharsetDecoder doesn't like a direct buffer?
				temp.put(buffer);
				temp.position(0);
				s = dec.decode(temp).toString().split("\0"); // convert buffer to string array
			} catch (Exception e) {
				return;
			}
			runOnUiThread(() -> {
				((TextView) findViewById(R.id.status3)).setText(s[0]); // display status
			});
		}
	};

	public class OpenURL implements Runnable {
		String url;

		OpenURL(String p) {
			url = p;
		}

		public void run() {
			int r;
			synchronized (lock) { // make sure only 1 thread at a time can do the following
				if (chan != 0)
					BASS.BASS_StreamFree(chan); // close old stream
				else
					BASS.BASS_StreamCancel(req); // cancel last request in case it's pending
				r = ++req; // increment the request counter for this request
			}
			runOnUiThread(() -> {
				((TextView) findViewById(R.id.status2)).setText("connecting...");
				((TextView) findViewById(R.id.status1)).setText("");
				((TextView) findViewById(R.id.status3)).setText("");
			});
			int newchan = BASS.BASS_StreamCreateURL(url, 0, BASS.BASS_STREAM_BLOCK | BASS.BASS_STREAM_STATUS | BASS.BASS_STREAM_AUTOFREE | BASS.BASS_SAMPLE_FLOAT, StatusProc, r); // open URL
			synchronized (lock) {
				if (r != req) { // there is a newer request, discard this one
					if (newchan != 0) BASS.BASS_StreamFree(newchan);
					return;
				}
				chan = newchan; // this is now the current stream
			}
			if (newchan == 0) { // failed to open
				runOnUiThread(() -> {
					((TextView) findViewById(R.id.status2)).setText("not playing");
				});
				Error("Can't play the stream");
			} else {
				// only needed the DOWNLOADPROC to receive HTTP/ICY tags, so disable it now
				BASS.BASS_ChannelSetAttributeDOWNLOADPROC(newchan, null, null);
				// set syncs for stream title updates
				BASS.BASS_ChannelSetSync(newchan, BASS.BASS_SYNC_META, 0, MetaSync, 0); // Shoutcast
				BASS.BASS_ChannelSetSync(newchan, BASS.BASS_SYNC_OGG_CHANGE, 0, MetaSync, 0); // Icecast/OGG
				BASS.BASS_ChannelSetSync(newchan, BASS_SYNC_HLS_SEGMENT, 0, MetaSync, 0); // HLS
				// set sync for stalling/buffering
				BASS.BASS_ChannelSetSync(newchan, BASS.BASS_SYNC_STALL, 0, StallSync, 0);
				// set sync for end of stream (when freed due to AUTOFREE)
				BASS.BASS_ChannelSetSync(newchan, BASS.BASS_SYNC_FREE, 0, FreeSync, 0);
				// play it!
				BASS.BASS_ChannelPlay(newchan, false);
				// start buffer monitoring (and display stream info when done)
				timerHandler.postDelayed(timer, 50);
			}
		}
	}

	public void Play(View v) {
		String proxy = null;
		if (!((CheckBox) findViewById(R.id.proxydirect)).isChecked())
			proxy = ((EditText) findViewById(R.id.proxy)).getText().toString();
		BASS.BASS_SetConfigPtr(BASS.BASS_CONFIG_NET_PROXY, proxy); // set proxy server
		String url;
		if (v.getId() == R.id.opencustom)
			url = ((EditText) findViewById(R.id.custom)).getText().toString();
		else
			url = urls[Integer.parseInt((String) v.getTag())];
		new Thread(new OpenURL(url)).start();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// initialize output device
		if (!BASS.BASS_Init(-1, 44100, 0)) {
			Error("Can't initialize device");
			return;
		}

		BASS.BASS_SetConfig(BASS.BASS_CONFIG_NET_PLAYLIST, 1); // enable playlist processing

		BASS.BASS_PluginLoad("bassflac", 0); // load BASSFLAC (if present) for FLAC support
		BASS.BASS_PluginLoad("bassopus", 0); // load BASSOPUS (if present) for OPUS support
		BASS.BASS_PluginLoad("basshls", 0); // load BASSHLS (if present) for HLS support

		timerHandler = new Handler(getMainLooper());
		timer = new Runnable() {
			public void run() {
				// monitor buffering progress
				int active = BASS.BASS_ChannelIsActive(chan);
				if (active == BASS.BASS_ACTIVE_STALLED) {
					((TextView) findViewById(R.id.status2)).setText(String.format("buffering... %d%%",
							100 - (int) BASS.BASS_StreamGetFilePosition(chan, BASS.BASS_FILEPOS_BUFFERING)));
					timerHandler.postDelayed(this, 50);
				} else if (active != 0) {
					((TextView) findViewById(R.id.status2)).setText("playing");
					// get the stream name and URL
					String[] icy = (String[]) BASS.BASS_ChannelGetTags(chan, BASS.BASS_TAG_ICY);
					if (icy == null)
						icy = (String[]) BASS.BASS_ChannelGetTags(chan, BASS.BASS_TAG_HTTP); // no ICY tags, try HTTP
					if (icy != null) {
						for (String s : icy) {
							if (s.regionMatches(true, 0, "icy-name:", 0, 9))
								((TextView) findViewById(R.id.status2)).setText(s.substring(9));
							else if (s.regionMatches(true, 0, "icy-url:", 0, 8))
								((TextView) findViewById(R.id.status3)).setText(s.substring(8));
						}
					}
					// get the stream title
					DoMeta();
				}
			}
		};
	}

	@Override
	public void onDestroy() {
		BASS.BASS_Free();
		BASS.BASS_PluginFree(0);

		super.onDestroy();
	}
}