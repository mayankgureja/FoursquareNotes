package com.example.foursquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	public static String OAUTH_URL = "https://foursquare.com/oauth2/authenticate";
	public static String OAUTH_ACCESS_TOKEN_URL = "https://foursquare.com/oauth2/access_token";
	public static final String CLIENT_ID = "2A1BTCE3WGMTR1EXOLJQJKHBHIISQTQDWEOGFAYFUPYRBDJA";
	public static final String CLIENT_SECRET = "RWOATISVMDT3MG0JSTY3GMVK4VZSLKQRAHQYKLNFIFRNUDSV";
	public static String CALLBACK_URL = "http://localhost";
	private static String ACCESS_TOKEN = "";
	public static String tokenURL;

	public static boolean flag = true;

	public static ArrayList<String> IDs = new ArrayList<String>();
	public static ArrayList<String> Locations = new ArrayList<String>();
	public static ArrayList<String> Shouts = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.connectButton).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						flag = true;
						Toast.makeText(MainActivity.this,
								"Authenticating...",
								Toast.LENGTH_LONG).show();
						openWebView();
					}
				});
	}

	public void fillListview()
	{
		try
		{
			String[] ID_String = new String[IDs.size()];
			String[] Location_String = new String[Locations.size()];
			String[] Shout_String = new String[Shouts.size()];

			ID_String = IDs.toArray(ID_String);
			Location_String = Locations.toArray(Location_String);
			Shout_String = Shouts.toArray(Shout_String);

			final String[] ID_List = ID_String;

			ListView list = (ListView) findViewById(R.id.listView1);
			System.out.println(list);
			CheckinsArrayAdapter adapter = new CheckinsArrayAdapter(this,
					Location_String, Shout_String);
			list.setAdapter(adapter);

			final Vibrator vibe = (Vibrator) MainActivity.this
					.getSystemService(Context.VIBRATOR_SERVICE);
			list.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3)
				{
					vibe.vibrate(30);
					System.out.println(ID_List[arg2]); // Checkin ID, Do intents
														// here
					Intent cm = new Intent(MainActivity.this, CloudMine.class);
					cm.putExtra("checkin", ID_List[arg2]);
					startActivity(cm);
				}
			});
		}
		catch (Exception ex)
		{
			Log.e("Foursquare", "ERROR in fillListView: " + ex);
		}
	}

	public void openWebView()
	{
		WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setVisibility(View.VISIBLE);
		setContentView(webview);

		String url = OAUTH_URL + "?client_id=" + CLIENT_ID
				+ "&response_type=code" + "&redirect_uri=" + CALLBACK_URL;

		webview.setWebViewClient(new WebViewClient()
		{
			public void onPageStarted(WebView view, String url,
					android.graphics.Bitmap favicon)
			{
				if (url.contains("?code=")
						&& url.toLowerCase(Locale.getDefault()).startsWith(
								CALLBACK_URL))
				{
					view.setVisibility(View.INVISIBLE);		
					setContentView(R.layout.activity_main);
					handleCallback(url);
				}
			}
		});

		webview.loadUrl(url);
	}

	public void handleCallback(String url)
	{
		if (flag) // Only do once
		{
			flag = false;
			String accessCode = url.substring(url.indexOf("code")).replace(
					"code=", "");

			tokenURL = OAUTH_ACCESS_TOKEN_URL + "?client_id=" + CLIENT_ID
					+ "&client_secret=" + CLIENT_SECRET
					+ "&grant_type=authorization_code" + "&redirect_uri="
					+ CALLBACK_URL + "&code=" + accessCode;
			new GetAccessToken(this).execute();
		}
	}

	private class GetAccessToken extends AsyncTask<String, Void, String>
	{
		private ProgressDialog mDialog;
		private Context context;

		public GetAccessToken(Activity activity)
		{
			context = activity;
			mDialog = new ProgressDialog(context);
		}

		@Override
		protected String doInBackground(String... params)
		{
			try
			{
				try
				{
					getAccessToken(tokenURL);
					getCheckins();
				}
				catch (Exception e)
				{
					System.out.println(e.toString());
				}
			}
			catch (Exception e)
			{
				Log.e("Foursquare", "ERROR in AsyncTask: " + e.toString());
				e.printStackTrace();
			}
			return "";
		}

		protected void onPreExecute()
		{
			mDialog.setMessage("Loading...");
			mDialog.setCancelable(false);
			mDialog.show();
		}

		protected void onPostExecute(String str)
		{
			Log.i("Foursquare", "onPostExecute launched");
			fillListview();
			if (mDialog.isShowing())
				mDialog.dismiss();
		}
	}

	public void getAccessToken(String tokenURL)
	{
		try
		{
			URL url = new URL(tokenURL);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);

			urlConnection.connect();

			String response = streamToString(urlConnection.getInputStream());

			JSONObject jsonObj = (JSONObject) new JSONTokener(response)
					.nextValue();

			ACCESS_TOKEN = jsonObj.getString("access_token");
			System.out.println("ACCESS_TOKEN: " + ACCESS_TOKEN);

		}
		catch (Exception ex)
		{
			Log.e("Foursquare", "ERROR in getAccessToken: " + ex.toString());
		}
	}

	private static String streamToString(InputStream is) throws IOException
	{
		String str = "";

		if (is != null)
		{
			StringBuilder sb = new StringBuilder();
			String line;

			try
			{
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null)
				{
					sb.append(line);
				}

				reader.close();
			}
			finally
			{
				is.close();
			}

			str = sb.toString();
		}

		return str;
	}

	public void getCheckins()
	{
		try
		{
			URL url = new URL(
					"https://api.foursquare.com/v2/users/self/checkins?oauth_token="
							+ ACCESS_TOKEN);

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);

			urlConnection.connect();

			String response = streamToString(urlConnection.getInputStream());

			JSONObject jsonObj = (JSONObject) new JSONTokener(response)
					.nextValue();

			JSONArray checkins = (JSONArray) jsonObj.getJSONObject("response")
					.getJSONObject("checkins").getJSONArray("items");

			int length = checkins.length();

			if (length > 0)
			{
				IDs.clear();
				Locations.clear();
				Shouts.clear();
				for (int i = 0; i < length; i++)
				{
					JSONObject item = (JSONObject) checkins.get(i);
					IDs.add(item.getString("id"));
					Locations
							.add(item.getJSONObject("venue").getString("name"));
					Shouts.add(item.getString("shout"));

				}
			}
		}
		catch (Exception ex)
		{
			Log.e("Foursquare", "ERROR in getCheckins: " + ex);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
