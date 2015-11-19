package com.openenergymonitor.emonpi.android;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new EmonPiControlFragment()).commit();
		}
		
		context = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * EmonPi control fragment.
	 */
	public static class EmonPiControlFragment extends Fragment implements OnClickListener, IMqttActionListener {

		private EditText server;
		boolean connected = false;
		MqttAndroidClient client;
		
		public EmonPiControlFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			Button connect = (Button) rootView.findViewById(R.id.button1);
			Button disconnect = (Button) rootView.findViewById(R.id.button2);
			Button on= (Button) rootView.findViewById(R.id.button3);
			Button off = (Button) rootView.findViewById(R.id.button4);
			server = (EditText) rootView.findViewById(R.id.editText1);
			
			
			connect.setOnClickListener(this);
			disconnect.setOnClickListener(this);
			on.setOnClickListener(this);
			off.setOnClickListener(this);
			
			return rootView;
		}

		@Override
		public void onClick(View v) {			
			try {
				if (v.getId() == R.id.button1) {
					String s = "tcp://" + server.getText() + ":1883";
					client = new MqttAndroidClient(context, s, "emonPi-android");
					client.connect(new MqttConnectOptions(),this);
					Toast.makeText(context, "Connecting to "+ s, Toast.LENGTH_LONG).show();
				} else if (v.getId() == R.id.button2) {
					if (connected) client.disconnect();
					connected = false;
				} else if (v.getId() == R.id.button3) {
					if (connected) client.publish("lwrf", "1 1".getBytes(), 1, false);
				} else if (v.getId() == R.id.button4) {
					if (connected) client.publish("lwrf", "1 0".getBytes(), 1, false);
				}
			} catch (Exception e) {
				if (v.getId() == R.id.button1) connected = false;
				Toast.makeText(context, ((Button) v).getText() + " failed " + e, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onFailure(IMqttToken arg0, Throwable arg1) {
			Toast.makeText(context, "Failed",  Toast.LENGTH_LONG).show();
			connected = false;
		}

		@Override
		public void onSuccess(IMqttToken arg0) {
			Toast.makeText(context, "Success",  Toast.LENGTH_LONG).show();
			connected = true;
		}
	}
}
