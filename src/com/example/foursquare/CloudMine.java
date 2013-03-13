package com.example.foursquare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.CMFile;
import com.cloudmine.api.CMObject;
import com.cloudmine.api.CMUser;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.rest.CMStore;
import com.cloudmine.api.rest.callbacks.CMObjectResponseCallback;
import com.cloudmine.api.rest.callbacks.CreationResponseCallback;
import com.cloudmine.api.rest.callbacks.FileCreationResponseCallback;
import com.cloudmine.api.rest.callbacks.FileLoadCallback;
import com.cloudmine.api.rest.callbacks.LoginResponseCallback;
import com.cloudmine.api.rest.callbacks.ObjectModificationResponseCallback;
import com.cloudmine.api.rest.response.CMObjectResponse;
import com.cloudmine.api.rest.response.CreationResponse;
import com.cloudmine.api.rest.response.FileCreationResponse;
import com.cloudmine.api.rest.response.FileLoadResponse;
import com.cloudmine.api.rest.response.LoginResponse;
import com.cloudmine.api.rest.response.ObjectModificationResponse;
import com.cloudmine.api.rest.response.code.CMResponseCode;

public class CloudMine extends Activity
{
	// Find this in your developer console
	private static final String APP_ID = "21356c4df1944204ba460b1ccbd83606";
	// Find this in your developer console
	private static final String API_KEY = "849101e359f3407e8a8d8a876e99b798";

	public String checkinId = new String();
	public String searchQuery = new String();
	public String filePath = new String();

	CMFile file;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cloudmine);
		initializeCredentials();

		final String checkinId = getIntent().getStringExtra("checkin");

		// checkinId = "123456";
		// String temp = "[checkinId = \"123456\"]";

		searchQuery = "[checkinId = " + "\"" + checkinId + "\"]";

		display(searchQuery);

		loadImage();

		// try
		// {
		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		// ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		// imageView.setImageBitmap(bitmap);
		// }
		// catch (Exception e)
		// {
		// System.out.println(e.toString());
		// }

		// Add Note
		findViewById(R.id.add).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				create(checkinId);
				Toast.makeText(getApplicationContext(), "Note created",
						Toast.LENGTH_SHORT).show();
				// display(searchQuery);

			}
		});

		findViewById(R.id.picture).setOnClickListener(
				new View.OnClickListener()
				{

					@Override
					public void onClick(View arg0)
					{
						Intent photoPickerIntent = new Intent(
								Intent.ACTION_PICK);
						photoPickerIntent.setType("image/*");
						startActivityForResult(photoPickerIntent, 100);
						Toast.makeText(getApplicationContext(),
								"Picture Added", Toast.LENGTH_SHORT).show();

					}
				});

		// Edit Note--Need to add load Images
		findViewById(R.id.done).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				//
				display(searchQuery);
				update(searchQuery);
				Toast.makeText(getApplicationContext(), "Update completed ",
						Toast.LENGTH_SHORT).show();
				display(searchQuery);

			}
		});

		findViewById(R.id.delete).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				delete(searchQuery);
				// deleteImage();
				Toast.makeText(getApplicationContext(), "Note deleted",
						Toast.LENGTH_SHORT).show();
				// display(searchQuery);

			}
		});

	}

	public void initializeCredentials()
	{
		CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
	}

	public void create(String id)
	{
		// Add objects

		SimpleCMObject o = new SimpleCMObject();
		o.add("checkinId", id);
		resetFields(o, o.getObjectId());

	}

	public void display(String searchQuery)
	{

		// use the default store for the application
		CMStore store = CMStore.getStore();

		store.loadApplicationObjectsSearch(searchQuery,
				new CMObjectResponseCallback()
				{
					public void onCompletion(CMObjectResponse response)
					{
						for (CMObject object : response.getObjects())
						{
							// only cs275 courses are returned
							SimpleCMObject notes = (SimpleCMObject) object;
							displayFields(object);
							// Toast.makeText(getApplicationContext(),
							// ("Found notes on: " + notes.getString("notes")),
							// Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	public void getAll()
	{
		// Search for all objects

		CMStore store = CMStore.getStore();
		store.loadAllApplicationObjects(new CMObjectResponseCallback()
		{
			public void onCompletion(CMObjectResponse response)
			{
				for (CMObject object : response.getObjects())
				{
					SimpleCMObject note = (SimpleCMObject) object;
					// displayFields(object);
					System.out.println("Retrieved notes: "
							+ note.getString("checkinId") + ": "
							+ note.getString("notes"));
				}
			}
		});
	}

	public void update(String searchQuery)
	{

		CMStore store = CMStore.getStore();

		store.loadApplicationObjectsSearch(searchQuery,
				new CMObjectResponseCallback()
				{
					public void onCompletion(CMObjectResponse response)
					{
						for (CMObject object : response.getObjects())
						{
							SimpleCMObject john = (SimpleCMObject) object;

							resetFields(john, john.getObjectId());

						}
					}

					public void onFailure(Throwable e, String msg)
					{
						System.out.println("We failed: " + e.getMessage());
					}
				});
	}

	public void displayFields(CMObject object)
	{
		SimpleCMObject note = (SimpleCMObject) object;
		System.out.println("Found notes: " + note.getString("title"));

		EditText notes = (EditText) findViewById(R.id.notes);
		notes.setText(note.getString("notes"));

		EditText title = (EditText) findViewById(R.id.title);
		title.setText(note.getString("title"));

		EditText price = (EditText) findViewById(R.id.price);
		price.setText(note.getString("price"));

		EditText rating = (EditText) findViewById(R.id.rating);
		rating.setText(note.getString("rating"));

	}

	public void resetFields(SimpleCMObject o, final String objectId)
	{
		EditText UpdatedNotes = (EditText) findViewById(R.id.notes);

		EditText Updatedprice = (EditText) findViewById(R.id.price);

		EditText Updatedtitle = (EditText) findViewById(R.id.title);

		EditText Updatedrating = (EditText) findViewById(R.id.rating);

		// UpdatedNotes.sett
		o.add("notes", UpdatedNotes.getText().toString());
		o.add("title", Updatedtitle.getText().toString());
		o.add("price", Updatedprice.getText().toString());
		o.add("rating", Updatedrating.getText().toString());

		o.save(new ObjectModificationResponseCallback()
		{
			public void onCompletion(ObjectModificationResponse response)
			{
				System.out.println("Response was a success? "
						+ response.wasSuccess());
				System.out.println("We: " + response.getKeyResponse(objectId)
						+ " the simple object");
			}

			public void onFailure(Throwable e, String msg)
			{
				System.out.println("We failed: " + e.getMessage());
			}
		});
	}

	public void delete(String searchQuery)
	{
		// Delete object

		CMStore store = CMStore.getStore();
		// String searchQuery = "[number = \"275\", dept=\"cs\"]";

		store.loadApplicationObjectsSearch(searchQuery,
				new CMObjectResponseCallback()
				{
					public void onCompletion(CMObjectResponse response)
					{
						for (CMObject object : response.getObjects())
						{
							// only cs275 courses are returned
							SimpleCMObject course = (SimpleCMObject) object;
							System.out.println("retrieved notes: "
									+ course.getString("notes"));
							// object is a CMObject that has already been saved
							object.delete(new ObjectModificationResponseCallback()
							{
								public void onCompletion(
										ObjectModificationResponse response)
								{
									System.out.println("Response was: "
											+ response.wasSuccess());
								}

								public void onFailure(Throwable e, String msg)
								{
									System.out.println("We failed: "
											+ e.getMessage());
								}
							});
						}
					}
				});
	}

	public void createImage()
	{
		File myFile = new File(filePath);

		try
		{
			file = new CMFile(new FileInputStream(myFile), checkinId,
					"image/jpeg");
			file.save(new FileCreationResponseCallback()
			{
				public void onCompletion(FileCreationResponse response)
				{
					System.out.println("Save success? " + response.wasSuccess());
					System.out.println("name of saved file? "
							+ response.getfileId());
				}
			});
		}
		catch (Exception e)
		{

		}
	}

	public void loadImage()
	{

		CMStore store = CMStore.getStore();

		store.loadApplicationFile("739e1b0a3128404c9e4edc2d3ef8fb80",
				new FileLoadCallback("739e1b0a3128404c9e4edc2d3ef8fb80")
				{
					public void onCompletion(FileLoadResponse loadResponse)
					{
						System.out.println("Did we load the file? "
								+ loadResponse.wasSuccess());
						file = loadResponse.getFile();
						System.out.println("Loaded: " + file.getObjectId());
						Bitmap bitmap = BitmapFactory.decodeStream(file
								.getFileContentStream());
						ImageView imageView = (ImageView) findViewById(R.id.imageView1);
						imageView.setImageBitmap(bitmap);
					}
				});
	}

	public void deleteImage()
	{
		CMStore store = CMStore.getStore();

		store.deleteApplicationFile(checkinId,
				new ObjectModificationResponseCallback()
				{
					public void onCompletion(ObjectModificationResponse response)
					{
						System.out.println("Did we delete it? "
								+ response.wasDeleted(file.getObjectId()));
					}
				});

		// or call it directly on CMStore
		store.deleteUserFile(file.getObjectId(),
				new ObjectModificationResponseCallback()
				{
					public void onCompletion(ObjectModificationResponse response)
					{
						System.out.println("Did we delete it? "
								+ response.wasDeleted(file.getObjectId()));
					}
				});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent)
	{
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch (requestCode)
		{
		case 100:
			if (resultCode == RESULT_OK)
			{
				Uri selectedImage = imageReturnedIntent.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				filePath = cursor.getString(columnIndex);
				cursor.close();

				try
				{
					Bitmap bitmap = decodeUri(Uri.parse("file://" + filePath));
					ImageView imageView = (ImageView) findViewById(R.id.imageView1);
					imageView.setImageBitmap(bitmap);
					createImage();
				}
				catch (FileNotFoundException e)
				{
					System.out.println(e.toString());
				}
			}
		}
	}

	/* Down sample images to fit in ImageView */
	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException
	{
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 200;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true)
		{
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
			{
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o2);

	}

	public void createUser(String userName, String password)
	{

		final CMUser user = new CMUser(userName, password);

		user.createUser(new CreationResponseCallback()
		{
			public void onCompletion(CreationResponse response)
			{
				System.out.println("was user created? " + response.wasSuccess());
				boolean userAlreadyExists = response.getResponseCode().equals(
						CMResponseCode.EMAIL_ALREADY_EXISTS);
				if (userAlreadyExists)
				{
					System.out
							.println("User with that e-mail address already exists; user was not created");
				}
				else
				{
					// configure the store with the user so that it can perform
					// user-centric operations
					CMStore.getStore().setUser(user);
				}
				// Logging in the current user
				userLogin(user);

			}
		});
	}

	public void userLogin(final CMUser user)
	{

		user.login(new LoginResponseCallback()
		{
			public void onCompletion(LoginResponse response)
			{
				if (response.wasSuccess())
				{
					System.out.println("Was a success!");
					System.out.println("Now our user has a session token? "
							+ user.isLoggedIn());

					// configure the store with the user so that it can perform
					// user-centric operations
					CMStore.getStore().setUser(user);
				}
				else
				{
					System.out.println("We failed to log in because of: "
							+ response.getResponseCode());
				}
			}

			public void onFailure(Throwable t, String msg)
			{
				System.out
						.println("The call was never made because of: " + msg);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}