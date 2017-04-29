/**
 * Nasa Space Apps Challenge
 * 2017, Timisoara
 * 
 * UV-Sense Android app for "Let's go to the Beach" UV crowd monitoring tool
 * 
 * Radu Motisan
 * radu@uradmonitor.com
 * 
 * www.uradmonitor.com
 */

package com.magnasci.uvsense;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	TextView tvLabel , tvValue;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RelativeLayout panel = new RelativeLayout(this);
		panel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		panel.setBackgroundColor(Color.rgb(0x24,0x24, 0x24));
		setContentView(panel);
		
		ImageView topLogo = new ImageView(this);
		topLogo.setId(1);;
		topLogo.setImageResource(R.drawable.app_logo);
		RelativeLayout.LayoutParams lpv = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpv.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		panel.addView(topLogo, lpv);
		
		ImageView bottom = new ImageView(this);
		bottom.setId(2);
		bottom.setImageResource(R.drawable.bottom);
		lpv = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpv.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		panel.addView(bottom, lpv);
		
		
		LinearLayout content = new LinearLayout(this);
		content.setOrientation(LinearLayout.HORIZONTAL);
		content.setGravity(Gravity.CENTER_VERTICAL);
		content.setPadding(10, 10, 10, 10);
		lpv = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpv.addRule(RelativeLayout.BELOW, 1);
		lpv.addRule(RelativeLayout.ABOVE, 2);
		panel.addView(content, lpv);
		
		ImageView sun = new ImageView(this);
		sun.setImageResource(R.drawable.sun);
		content.addView(sun);
		
		
		LinearLayout vertical = new LinearLayout(this);
		vertical.setGravity(Gravity.CENTER_HORIZONTAL);
		vertical.setOrientation(LinearLayout.VERTICAL);
		content.addView(vertical, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		tvValue = new TextView(this);
		tvValue.setGravity(Gravity.CENTER_HORIZONTAL);
		tvValue.setText("2.3");
		tvValue.setTextColor(Color.WHITE);
		tvValue.setTextSize(40);
		vertical.addView(tvValue);
		
		TextView tv = new TextView(this);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setText("mW/cm²");
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(30);
		vertical.addView(tv);
		
		tvLabel = new TextView(this);
		tvLabel.setGravity(Gravity.CENTER_HORIZONTAL);
		tvLabel.setText("");
		tvLabel.setTextColor(Color.WHITE);
		tvLabel.setTextSize(20);
		vertical.addView(tvLabel);
   	  	
		Thread t = new Thread() {
			public void run() {
				while(true) {
					try {
						
							String value = getTextUrl("http://192.168.43.8");
							
							if (value != null) {
								int adc = Integer.parseInt(value);
								float vol = (float) (adc * 3.3 / 1024.0);
								// based on ML8511 intensity graph
								float uv = (float) ((vol - 0.99) * (15 - 0) / (2.8 - 0.99) + 0);
								
								if (uv < 0) uv = 0;
								String out = String.format("%.02f", uv);
								changeText(tvValue, out , 0);
								
								if (uv < 3) changeText(tvLabel, "Low", Color.GREEN);
								else if (uv < 6) changeText(tvLabel, "Moderate", Color.YELLOW);
								else if (uv < 8) changeText(tvLabel, "High", Color.rgb(0xFF, 0x66, 0x00));
								else if (uv < 11) changeText(tvLabel, "Very High", Color.RED);
								else changeText(tvLabel, "Extreme", Color.rgb(0x96, 0x00, 0xFF));
								
							}
							
							Thread.sleep(200);
						
					} catch (Exception e) { Log.e("ERROR", ""+ e.getMessage()); }
				}
				
			};
		};
		t.start();
		
	}
	
	
	public static String getTextUrl(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }

	void changeText(final TextView tv, final String text, final int color) {
		tv.post(new Runnable() {
			public void run() {
				if (text != null) tv.setText(text);
				if (color != 0) tv.setTextColor(color);
			}
		 });
	}
	
	
}
