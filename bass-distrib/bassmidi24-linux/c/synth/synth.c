/*
	BASSMIDI synth
	Copyright (c) 2011-2024 Un4seen Developments Ltd.
*/

#define GDK_VERSION_MIN_REQUIRED GDK_VERSION_3_0
#include <gtk/gtk.h>
#include <gdk/gdkkeysyms.h>
#include <glib/gthread.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <regex.h>
#include <glob.h>
#include "bass.h"
#include "bassmidi.h"

#define UIFILE "synth.ui"
GtkBuilder *builder;

GtkWidget *win;
GtkWidget *fontfilesel;

BASS_INFO info;
DWORD input;		// MIDI input device
HSTREAM stream;		// output stream
HSOUNDFONT font;	// soundfont
DWORD preset;		// current preset
BOOL drums;			// drums enabled?
BOOL preload;		// preload samples?
BOOL chans16;		// 16 MIDI channels?
BOOL activity;
DWORD velocity = 110;
DWORD octave = 4;

guint timer;

const DWORD fxtype[5] = { BASS_FX_DX8_REVERB, BASS_FX_DX8_ECHO, BASS_FX_DX8_CHORUS, BASS_FX_DX8_FLANGER, BASS_FX_DX8_DISTORTION };
HFX fx[5];			// effect handles

#define KEYS 20
const guint keys[KEYS] = {
	'Q','2','W','3','E','R','5','T','6','Y','7','U',
	'I','9','O','0','P', GDK_KEY_bracketleft, GDK_KEY_equal, GDK_KEY_bracketright
};
BOOL keysdown[KEYS];

// display error messages
void Error(const char *es)
{
	GtkWidget *dialog = gtk_message_dialog_new(GTK_WINDOW(win), GTK_DIALOG_DESTROY_WITH_PARENT,
		GTK_MESSAGE_ERROR, GTK_BUTTONS_OK, "%s\n(error code: %d)", es, BASS_ErrorGetCode());
	gtk_dialog_run(GTK_DIALOG(dialog));
	gtk_widget_destroy(dialog);
}

#define GetWidget(id) GTK_WIDGET(gtk_builder_get_object(builder,id))

void WindowDestroy(GtkWidget *obj, gpointer data)
{
	gtk_main_quit();
}

gboolean ActivityOff(gpointer data)
{
	timer = 0;
	gtk_label_set_markup(GTK_LABEL(GetWidget("activity")), "<span bgcolor=\"white\"> activity </span>");
	return FALSE;
}

gboolean ActivityOn(gpointer data)
{
	gtk_label_set_markup(GTK_LABEL(GetWidget("activity")), "<span bgcolor=\"green\"> activity </span>");
	if (timer) g_source_remove(timer);
	timer = g_timeout_add(100, ActivityOff, NULL);
	return FALSE;
}

// MIDI input function
void CALLBACK MidiInProc(DWORD handle, double time, const BYTE *buffer, DWORD length, void *user)
{
	if (chans16) // using 16 channels
		BASS_MIDI_StreamEvents(stream, BASS_MIDI_EVENTS_RAW, buffer, length); // send MIDI data to the MIDI stream
	else
		BASS_MIDI_StreamEvents(stream, (BASS_MIDI_EVENTS_RAW + 17) | BASS_MIDI_EVENTS_SYNC, buffer, length); // send MIDI data to channel 17 in the MIDI stream
	g_timeout_add(0, ActivityOn, NULL); // update UI in main thread
}

gboolean PresetUpdate(gpointer data)
{
	gtk_combo_box_set_active(GTK_COMBO_BOX(GetWidget("preset")), preset);
	return FALSE;
}

// program/preset event sync function
void CALLBACK ProgramEventSync(HSYNC handle, DWORD channel, DWORD data, void *user)
{
	preset = LOWORD(data);
	g_timeout_add(0, PresetUpdate, NULL); // update UI in main thread
	BASS_MIDI_FontCompact(0); // unload unused samples
}

void UpdatePresetList()
{
	int a;
	GtkComboBoxText *list = GTK_COMBO_BOX_TEXT(GetWidget("preset"));
	gtk_combo_box_text_remove_all(list);
	for (a = 0; a < 128; a++) {
		char text[60];
		const char *name = BASS_MIDI_FontGetPreset(font, a, drums ? 128 : 0); // get preset name
		snprintf(text, sizeof(text), "%03d: %s", a, name ? name : "");
		gtk_combo_box_text_append_text(list, text);
	}
	gtk_combo_box_set_active(GTK_COMBO_BOX(list), preset);
}

void DeviceChanged(GtkComboBox *obj, gpointer data)
{
	int sel = gtk_combo_box_get_active(obj);
	if (sel != input) {
		BASS_MIDI_InFree(input); // free current input device
		input = sel; // set new input device selection
		if (BASS_MIDI_InInit(input, MidiInProc, 0)) // successfully initialized...
			BASS_MIDI_InStart(input); // start it
		else
			Error("Can't initialize MIDI device");
	}
}

void ChansToggled(GtkToggleButton *obj, gpointer data)
{
	chans16 = gtk_toggle_button_get_active(obj); // MIDI input channels
}

void ResetClicked(GtkButton *obj, gpointer data)
{
	BASS_MIDI_StreamEvent(stream, 0, MIDI_EVENT_SYSTEM, MIDI_SYSTEM_GS); // send system reset event
	if (drums) BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_DRUMS, drums); // send drum switch event
	BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_PROGRAM, preset); // send program/preset event
}

void OctaveChanged(GtkComboBox *obj, gpointer data)
{
	octave = gtk_combo_box_get_active(obj);
	BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_NOTESOFF, 0);
}

void VelocityChanged(GtkSpinButton *obj, gpointer data)
{
	velocity = gtk_spin_button_get_value(obj);
}

gboolean FileExtensionFilter(const GtkFileFilterInfo *info, gpointer data)
{
	return !regexec((regex_t*)data, info->filename, 0, NULL, 0);
}

void OpenFontClicked(GtkButton *obj, gpointer data)
{
	int resp = gtk_dialog_run(GTK_DIALOG(fontfilesel));
	gtk_widget_hide(fontfilesel);
	if (resp == GTK_RESPONSE_ACCEPT) {
		char *file = gtk_file_chooser_get_filename(GTK_FILE_CHOOSER(fontfilesel));
		HSOUNDFONT newfont = BASS_MIDI_FontInit(file, 0);
		if (newfont) {
			BASS_MIDI_FONT sf;
			sf.font = newfont;
			sf.preset = -1; // use all presets
			sf.bank = 0; // use default bank(s)
			BASS_MIDI_StreamSetFonts(0, &sf, 1); // set default soundfont
			BASS_MIDI_StreamSetFonts(stream, &sf, 1); // apply to current stream too
			BASS_MIDI_FontFree(font); // free old soundfont
			font = newfont;
			{
				BASS_MIDI_FONTINFO i;
				BASS_MIDI_FontGetInfo(font, &i);
				gtk_button_set_label(obj, i.name ? i.name : strrchr(file, '/') + 1);
				if (i.presets == 1) { // only 1 preset, auto-select it...
					DWORD p;
					BASS_MIDI_FontGetPresets(font, &p);
					drums = (HIWORD(p) == 128); // bank 128 = drums
					preset = LOWORD(p);
					gtk_toggle_button_set_active(GTK_TOGGLE_BUTTON(GetWidget("drums")), drums);
					BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_DRUMS, drums); // send drum switch event
					BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_PROGRAM, preset); // send program/preset event
					gtk_widget_set_sensitive(GetWidget("preset"), FALSE);
					gtk_widget_set_sensitive(GetWidget("drums"), FALSE);
				} else {
					gtk_widget_set_sensitive(GetWidget("preset"), TRUE);
					gtk_widget_set_sensitive(GetWidget("drums"), TRUE);
				}
			}
			UpdatePresetList();
			if (preload) BASS_MIDI_FontLoadEx(font, preset, drums ? 128 : 0, 50, BASS_MIDI_FONTLOAD_NOWAIT | BASS_MIDI_FONTLOAD_TIME); // preload 50ms of current preset
		}
		g_free(file);
	}
}

void PresetChanged(GtkComboBox *obj, gpointer data)
{
	int sel = gtk_combo_box_get_active(obj);
	if (sel >= 0) {
		preset = sel;
		BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_PROGRAM, preset); // send program/preset event
		BASS_MIDI_FontCompact(0); // unload unused samples
		if (preload) BASS_MIDI_FontLoadEx(font, preset, drums ? 128 : 0, 50, BASS_MIDI_FONTLOAD_NOWAIT | BASS_MIDI_FONTLOAD_TIME); // preload 50ms of current preset
	}
}

void DrumsToggled(GtkToggleButton *obj, gpointer data)
{
	drums = gtk_toggle_button_get_active(obj);
	BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_DRUMS, drums); // send drum switch event
	BASS_MIDI_StreamEvents(stream, BASS_MIDI_EVENTS_FLUSH, 0, 0); // process pending events before GetEvent
	preset = BASS_MIDI_StreamGetEvent(stream, 16, MIDI_EVENT_PROGRAM); // preset is reset in drum switch
	UpdatePresetList();
	BASS_MIDI_FontCompact(0); // unload unused samples
	if (preload) BASS_MIDI_FontLoadEx(font, preset, drums ? 128 : 0, 50, BASS_MIDI_FONTLOAD_NOWAIT | BASS_MIDI_FONTLOAD_TIME); // preload 50ms of current preset
}

void PreloadToggled(GtkToggleButton *obj, gpointer data)
{
	preload = gtk_toggle_button_get_active(obj);
	if (preload) BASS_MIDI_FontLoadEx(font, preset, drums ? 128 : 0, 50, BASS_MIDI_FONTLOAD_NOWAIT | BASS_MIDI_FONTLOAD_TIME); // preload 50ms of current preset
}

void InterpolationToggled(GtkToggleButton *obj, gpointer data)
{
	if (gtk_toggle_button_get_active(obj)) {
		const gchar *objname = gtk_buildable_get_name(GTK_BUILDABLE(obj));
		int src = atoi(objname + 3);
		BASS_ChannelSetAttribute(stream, BASS_ATTRIB_MIDI_SRC, src); // set interpolation points
	}
}

void ReverbChanged(GtkComboBox *obj, gpointer data)
{
	// send GS reverb type macro
	int sel = gtk_combo_box_get_active(obj);
	BASS_MIDI_StreamEvent(stream, 0, MIDI_EVENT_REVERB_MACRO, sel ? 0x8000 + sel - 1 : 0);
}

void ReverbLevelChanged(GtkRange *range, gpointer data)
{
	// reverb level
	int value = gtk_range_get_value(range);
	BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_REVERB, value);
}

void ChorusChanged(GtkComboBox *obj, gpointer data)
{
	// send GS chorus type macro
	int sel = gtk_combo_box_get_active(obj);
	BASS_MIDI_StreamEvent(stream, 0, MIDI_EVENT_CHORUS_MACRO, sel ? 0x8000 + sel - 1 : 0);
}

void ChorusLevelChanged(GtkRange *range, gpointer data)
{
	// chorus level
	int value = gtk_range_get_value(range);
	BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_CHORUS, value);
}

void FXToggled(GtkToggleButton *obj, gpointer data)
{
	// toggle effects
	const gchar *objname = gtk_buildable_get_name(GTK_BUILDABLE(obj));
	int n = atoi(objname + 2);
	if (fx[n]) {
		BASS_ChannelRemoveFX(stream, fx[n]);
		fx[n] = 0;
	} else
		fx[n] = BASS_ChannelSetFX(stream, fxtype[n], n);
}

gboolean TimerProc(gpointer data)
{
	// display loaded sample data
	char text[40];
	DWORD loaded = 0;
	BASS_MIDI_FONTINFO i;
	if (BASS_MIDI_FontGetInfo(font, &i))
		loaded = (i.samload + 1023) / 1024;
	sprintf(text, "Loaded: %u KB", loaded);
	gtk_label_set_text(GTK_LABEL(GetWidget("loaded")), text);
	return TRUE;
}

gboolean KeyHandler(GtkWidget *grab_widget, GdkEventKey *event, gpointer data)
{
	int key, kv = event->keyval;
	if (kv >= 'a' && kv <= 'z') kv -= 0x20;
	for (key = 0; key < KEYS; key++) {
		if (kv == keys[key]) {
			if (event->type == GDK_KEY_RELEASE || !keysdown[key]) {
				keysdown[key] = (event->type == GDK_KEY_PRESS);
				BASS_MIDI_StreamEvent(stream, 16, MIDI_EVENT_NOTE, MAKEWORD(octave * 12 + key, keysdown[key] ? velocity : 0)); // send note on/off event
			}
			break;
		}
	}
	return FALSE;
}

int main(int argc, char* argv[])
{
#if !GLIB_CHECK_VERSION(2,32,0)
	g_thread_init(NULL);
#endif
	gdk_threads_init();
	gtk_init(&argc, &argv);

	// check the correct BASS was loaded
	if (HIWORD(BASS_GetVersion()) != BASSVERSION) {
		Error("An incorrect version of BASS was loaded");
		return 0;
	}

	// initialize default output device
	if (!BASS_Init(-1, 44100, 0, NULL, NULL)) {
		Error("Can't initialize output device");
		return 0;
	}

	// initialize GUI
	builder = gtk_builder_new();
	if (!gtk_builder_add_from_file(builder, UIFILE, NULL)) {
		char path[PATH_MAX];
		readlink("/proc/self/exe", path, sizeof(path));
		strcpy(strrchr(path, '/') + 1, UIFILE);
		if (!gtk_builder_add_from_file(builder, path, NULL)) {
			Error("Can't load UI");
			return 0;
		}
	}
	win = GetWidget("window1");
	gtk_builder_connect_signals(builder, NULL);

	{ // setup file selector
		GtkFileFilter *filter;
		regex_t *fregex;
		fontfilesel = gtk_file_chooser_dialog_new("Open Soundfont", GTK_WINDOW(win), GTK_FILE_CHOOSER_ACTION_OPEN,
			GTK_STOCK_CANCEL, GTK_RESPONSE_CANCEL, GTK_STOCK_OPEN, GTK_RESPONSE_ACCEPT, NULL);
		filter = gtk_file_filter_new();
		gtk_file_filter_set_name(filter, "Soundfonts (sf2/sf2pack/sf3/sfz)");
		fregex = malloc(sizeof(*fregex));
		regcomp(fregex, "\\.(sf2|sf2pack|sf3|sfz)$", REG_ICASE | REG_NOSUB | REG_EXTENDED);
		gtk_file_filter_add_custom(filter, GTK_FILE_FILTER_FILENAME, FileExtensionFilter, fregex, NULL);
		gtk_file_chooser_add_filter(GTK_FILE_CHOOSER(fontfilesel), filter);
		filter = gtk_file_filter_new();
		gtk_file_filter_set_name(filter, "All files");
		gtk_file_filter_add_pattern(filter, "*");
		gtk_file_chooser_add_filter(GTK_FILE_CHOOSER(fontfilesel), filter);
	}

	stream = BASS_MIDI_StreamCreate(17, BASS_MIDI_ASYNC, 1); // create the MIDI stream with async processing and 16 MIDI channels for device input + 1 for keyboard input
	BASS_ChannelSetAttribute(stream, BASS_ATTRIB_BUFFER, 0); // no buffering for minimum latency
	BASS_ChannelSetSync(stream, BASS_SYNC_MIDI_EVENT | BASS_SYNC_MIXTIME, MIDI_EVENT_PROGRAM, ProgramEventSync, 0); // catch program/preset changes
	BASS_MIDI_StreamEvent(stream, 0, MIDI_EVENT_SYSTEM, MIDI_SYSTEM_GS); // send GS system reset event
	BASS_ChannelPlay(stream, 0); // start it
	{ // enumerate available input devices
		GtkComboBoxText *list = GTK_COMBO_BOX_TEXT(GetWidget("device"));
		BASS_MIDI_DEVICEINFO di;
		int dev;
		for (dev = 0; BASS_MIDI_InGetDeviceInfo(dev, &di); dev++)
			gtk_combo_box_text_append_text(list, di.name);
		if (dev) { // got some, try to initialize one
			int a;
			for (a = 0; a < dev; a++) {
				if (BASS_MIDI_InInit(a, MidiInProc, 0)) { // succeeded, start it
					input = a;
					BASS_MIDI_InStart(input);
					gtk_combo_box_set_active(GTK_COMBO_BOX(list), input);
					break;
				}
			}
			if (a == dev) Error("Can't initialize MIDI device");
		} else {
			gtk_combo_box_text_append_text(list, "no devices");
			gtk_combo_box_set_active(GTK_COMBO_BOX(list), 0);
			gtk_widget_set_sensitive(GTK_WIDGET(list), FALSE);
			gtk_widget_set_sensitive(GetWidget("chans1"), FALSE);
			gtk_widget_set_sensitive(GetWidget("chans16"), FALSE);
		}
	}
	gtk_combo_box_set_active(GTK_COMBO_BOX(GetWidget("octave")), octave);
	UpdatePresetList();

	// load optional plugins for packed soundfonts (others may be used too)
	BASS_PluginLoad("libbassflac.so", 0);
	BASS_PluginLoad("libbasswv.so", 0);
	BASS_PluginLoad("libbassopus.so", 0);

	g_timeout_add(1000, TimerProc, NULL);
	g_signal_connect(win, "key-press-event", G_CALLBACK(KeyHandler), NULL);
	g_signal_connect(win, "key-release-event", G_CALLBACK(KeyHandler), NULL);

	gdk_threads_enter();
	gtk_main();
	gdk_threads_leave();

	gtk_widget_destroy(fontfilesel);

	// release everything
	BASS_MIDI_InFree(input);
	BASS_Free();
	BASS_PluginFree(0);

	return 0;
}
