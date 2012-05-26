/**
 *   Copyright 2012 Francesco Balducci
 *
 *   This file is part of FakeDawn.
 *
 *   FakeDawn is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   FakeDawn is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with FakeDawn.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.balau.fakedawn;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * @author francesco
 *
 */
public class Preferences extends Activity implements OnClickListener {

	private static int REQUEST_PICK_SOUND = 0;
	private Uri m_soundUri = null;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.preferences);

		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		tp.setIs24HourView(true);
		tp.setAddStatesFromChildren(true);

		Button saveButton = (Button) findViewById(R.id.buttonSave);
		saveButton.setOnClickListener(this);
		Button discardButton = (Button) findViewById(R.id.buttonDiscard);
		discardButton.setOnClickListener(this);
		Button soundButton = (Button) findViewById(R.id.buttonSound);
		soundButton.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		SharedPreferences pref = getApplicationContext().getSharedPreferences("main", MODE_PRIVATE);

		TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
		tp.setCurrentHour(pref.getInt("hour", 8));
		tp.setCurrentMinute(pref.getInt("minute", 0));

		CheckBox cb;

		cb = (CheckBox) findViewById(R.id.checkBoxAlarmEnabled);
		cb.setChecked(pref.getBoolean("enabled", true));
		cb.requestFocus();

		cb = (CheckBox) findViewById(R.id.checkBoxMondays);
		cb.setChecked(pref.getBoolean("mondays", true));
		cb = (CheckBox) findViewById(R.id.checkBoxTuesdays);
		cb.setChecked(pref.getBoolean("tuesdays", true));
		cb = (CheckBox) findViewById(R.id.checkBoxWednesdays);
		cb.setChecked(pref.getBoolean("wednesdays", true));
		cb = (CheckBox) findViewById(R.id.checkBoxThursdays);
		cb.setChecked(pref.getBoolean("thursdays", true));
		cb = (CheckBox) findViewById(R.id.checkBoxFridays);
		cb.setChecked(pref.getBoolean("fridays", true));
		cb = (CheckBox) findViewById(R.id.checkBoxSaturdays);
		cb.setChecked(pref.getBoolean("saturdays", false));
		cb = (CheckBox) findViewById(R.id.checkBoxSundays);
		cb.setChecked(pref.getBoolean("sundays", false));

		TextView tv = (TextView) findViewById(R.id.editTextMinutes);
		tv.setText(String.format("%d",pref.getInt("duration", 15)));

		String sound = pref.getString("sound", "");
		if(sound.isEmpty())
		{
			m_soundUri = null;
		}
		else
		{
			m_soundUri = Uri.parse(sound);
		}
		updateSoundButtonText();

		Log.d("FakeDawn", "Preferences loaded.");
	}

	public void onClick(View v) {
		if(v.getId() == R.id.buttonSave)
		{
			SharedPreferences pref = getApplicationContext().getSharedPreferences("main", MODE_PRIVATE);
			SharedPreferences.Editor editor = pref.edit();

			TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
			tp.clearFocus();
			editor.putInt("hour", tp.getCurrentHour());
			editor.putInt("minute", tp.getCurrentMinute());

			CheckBox cb;

			cb = (CheckBox) findViewById(R.id.checkBoxAlarmEnabled);
			editor.putBoolean("enabled", cb.isChecked());

			cb = (CheckBox) findViewById(R.id.checkBoxMondays);
			editor.putBoolean("mondays", cb.isChecked());
			cb = (CheckBox) findViewById(R.id.checkBoxTuesdays);
			editor.putBoolean("tuesdays", cb.isChecked());
			cb = (CheckBox) findViewById(R.id.checkBoxWednesdays);
			editor.putBoolean("wednesdays", cb.isChecked());
			cb = (CheckBox) findViewById(R.id.checkBoxThursdays);
			editor.putBoolean("thursdays", cb.isChecked());
			cb = (CheckBox) findViewById(R.id.checkBoxFridays);
			editor.putBoolean("fridays", cb.isChecked());
			cb = (CheckBox) findViewById(R.id.checkBoxSaturdays);
			editor.putBoolean("saturdays", cb.isChecked());
			cb = (CheckBox) findViewById(R.id.checkBoxSundays);
			editor.putBoolean("sundays", cb.isChecked());

			TextView tv = (TextView) findViewById(R.id.editTextMinutes);
			editor.putInt("duration", Integer.parseInt(tv.getText().toString()));
			if(m_soundUri == null)
			{
				editor.putString("sound", "");
			}
			else
			{
				editor.putString("sound", m_soundUri.toString());
			}

			editor.putBoolean("enabled", true);
			editor.commit();

			Intent updateAlarm = new Intent(getApplicationContext(), Alarm.class);
			getApplicationContext().startService(updateAlarm);
			Log.d("FakeDawn", "Preferences saved.");
			finish();
		}
		else if(v.getId() == R.id.buttonDiscard)
		{
			finish();
		}
		else if(v.getId() == R.id.buttonSound)
		{
			Intent pickSound = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			pickSound.putExtra(
					RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,
					true);
			pickSound.putExtra(
					RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
					false);
			pickSound.putExtra(
					RingtoneManager.EXTRA_RINGTONE_TYPE,
					RingtoneManager.TYPE_ALL);
			pickSound.putExtra(
					RingtoneManager.EXTRA_RINGTONE_TITLE,
					"Pick Alarm Sound");
			if(m_soundUri != null)
			{
				pickSound.putExtra(
						RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
						m_soundUri);
			}
			startActivityForResult(pickSound, REQUEST_PICK_SOUND);
		}
	}

	private void updateSoundButtonText()
	{
		Button soundButton = (Button) findViewById(R.id.buttonSound);
		if(m_soundUri == null)
		{
			soundButton.setText("Silent");
		}
		else
		{
			String soundTitle = RingtoneManager.getRingtone(this, m_soundUri).getTitle(this);	
			soundButton.setText(soundTitle);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_PICK_SOUND)
		{
			if(resultCode == RESULT_OK)
			{
				m_soundUri = (Uri) data.getParcelableExtra(
						RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				updateSoundButtonText();
			}
		}
	}

}
