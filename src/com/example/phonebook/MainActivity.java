package com.example.phonebook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private DataListView listview;
	private PhoneBookListAdapter adapter;
	private LinkedList<PhoneBookItem> list;
	private int size;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	ActionBar actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
        
        listview = new DataListView(getApplicationContext());
        
        new AsyncTask<Void, Integer, PhoneBookListAdapter>() {
			private Cursor cursor;
			private ProgressDialog progress;
			
			private Cursor getContractCursor() {
				// 검색할 컬럼 정하기
				String[] projection = new String[] {
					BaseColumns._ID,				//0
					Contacts.DISPLAY_NAME,		//1
					Contacts.HAS_PHONE_NUMBER,	//2
					Contacts.LOOKUP_KEY,		//3
				};

				// 쿼리 날려서 커서 얻기
				String[] selectionArgs = null;
				String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

				return getContentResolver().query(Contacts.CONTENT_URI, projection, null, selectionArgs, sortOrder);
			}
			
			private Cursor getPhoneNumberCursor(String key){
				String selection = Phone.LOOKUP_KEY +" = ?";
			    String[] selectionArgs = new String[]{key};
			    
				return getContentResolver().query(Phone.CONTENT_URI, null, selection, selectionArgs, null);
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				cursor = getContractCursor();
				size = cursor.getCount(); // 전화번호부의 갯수 세기
				adapter = new PhoneBookListAdapter(getApplicationContext());
				listview = new DataListView(getApplicationContext());
				progress = ProgressDialog.show(MainActivity.this, "Loading PhoneBook", "Please Wait...", true);
			}
			
			@Override
			protected PhoneBookListAdapter doInBackground(Void... params) {
				int count = 0;
				if (cursor.moveToFirst()) {
					do {
						count++;
						String displayName = cursor.getString(1);
						
						Drawable photo = new BitmapDrawable(
								getResources(),
								BitmapFactory.decodeStream(Contacts.openContactPhotoInputStream(
										getContentResolver(),
										Uri.withAppendedPath(
												Contacts.CONTENT_URI,
												Long.toString(cursor.getInt(cursor.getColumnIndex("_id")))
										)
									)
								)
							);
						if(photo.getIntrinsicHeight()==0)
							photo = getResources().getDrawable(android.R.drawable.gallery_thumb);
						
						String phoneNumber = "번호없음";
						
						if (cursor.getInt(2) == 1) {
							Cursor pCursor = getPhoneNumberCursor(cursor.getString(3));
							if (pCursor != null && pCursor.moveToFirst()) {
								phoneNumber = pCursor.getString(pCursor.getColumnIndex(Phone.NUMBER));
								pCursor.close();
							}
						}
//						getResources().getDrawable(android.R.drawable.ic_menu_view)
						adapter.addItem(new PhoneBookItem(photo, displayName, phoneNumber));
						Log.d("TAG",  "Name: "+((PhoneBookItem)(adapter.getItem(count-1))).getDisplayName()+" Phone: "+((PhoneBookItem)(adapter.getItem(count-1))).getPhoneNumber());
					} while (cursor.moveToNext() || count > size);
				}
				
				return adapter;
			}
			
			@Override
			protected void onPostExecute(PhoneBookListAdapter result) {
				progress.dismiss();
				listview.setAdapter(result);
				listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
						PhoneBookItem item = ((PhoneBookItem)(adapter.getItem(position))); 
						item.setBoxChecked(!item.isBoxChecked());
						adapter.notifyDataSetChanged();
						if(item.isBoxChecked()){
							if(!list.contains(item)){
								if(!list.add(item))
									Toast.makeText(getApplicationContext(), "Problem Occured", Toast.LENGTH_SHORT).show();
							}
						}
						else{
							if(list.contains(item)){
								if(!list.remove(item))
									Toast.makeText(getApplicationContext(), "Problem Occured", Toast.LENGTH_SHORT).show();
							}
						}	
						invalidateOptionsMenu();
					}
				});
				setContentView(listview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				//super.onPostExecute(result);
			}
		}.execute(); // 전화번호부 가져오기	        
		list = new LinkedList<PhoneBookItem>();
		//listview.setAdapter(adapter);
    }
    
    private void exportListToFile() throws IOException{
    	final String dirPath = Environment.getExternalStorageDirectory()+"/ExportPhoneBook";
    	final String filePath = dirPath+"/phonebook.txt";
    	     	
    	new AsyncTask<Writer, Void, Writer>(){
    		
    		private ProgressDialog progress;
    		
    		@Override
    		protected void onPreExecute() {
    			File dir = new File(dirPath);
    	    	if(!dir.exists())
    	    		dir.mkdirs();
    			Collections.sort(list, new Comparator<PhoneBookItem>() {
    				@Override
    				public int compare(PhoneBookItem lhs, PhoneBookItem rhs) {
    					return lhs.getDisplayName().compareTo(rhs.getDisplayName());
    				}
    			});
    			progress = ProgressDialog.show(MainActivity.this, "Exporting PhoneBook", "Please Wait...", true);
    			super.onPreExecute();
    		}
    		
			@Override
			protected Writer doInBackground(Writer... outs) {
				Writer out = outs[0];
				Iterator<PhoneBookItem> iterator = list.iterator();
				if (!iterator.hasNext())
					Toast.makeText(getApplicationContext(), "Nobody Selected",
							Toast.LENGTH_LONG).show();
				try {
					for (; iterator.hasNext();) {
						PhoneBookItem curItem = iterator.next();
						out.append("이름: " + curItem.getDisplayName() + "\n");
						out.append("연락처: " + curItem.getPhoneNumber() + "\n");
						out.append("\n");
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("TAG", e.getLocalizedMessage());
				}
				
				return out;
			}

			@Override
			protected void onPostExecute(Writer out) {
				progress.dismiss();
				Toast.makeText(getApplicationContext(), "Export Complete", Toast.LENGTH_SHORT).show();
				try {
					out.close();
				} catch (IOException e) {
					Log.e("TAG", e.getLocalizedMessage());
				}
				super.onPostExecute(out);
			}
    	}.execute(new BufferedWriter(new FileWriter(filePath, true)));
    }
    
    private boolean isAllChecked(){
    	Log.d("TAG", "count= "+adapter.getCount()+" size= "+list.size());
    	return adapter.getCount() == list.size();
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	if(isAllChecked())
    		getMenuInflater().inflate(R.menu.main_checked, menu);
    	else
    		getMenuInflater().inflate(R.menu.main_unchecked, menu);
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
    	
    	switch(menuItem.getItemId()){
    	case R.id.uncheck_all:
			for(int i=0;i<adapter.getCount();i++){
				PhoneBookItem item = (PhoneBookItem)adapter.getItem(i);
				item.setBoxChecked(false);
				if(list.contains(item))
					list.remove(item);

			}
			invalidateOptionsMenu();
			adapter.notifyDataSetChanged();
    		break;
    	case R.id.check_all:
			for(int i=0;i<adapter.getCount();i++){
				PhoneBookItem item = (PhoneBookItem)adapter.getItem(i);
				item.setBoxChecked(true);
				if(!list.contains(item))
					list.add(item);
			}
			invalidateOptionsMenu();
			adapter.notifyDataSetChanged();
    		break;
    	case R.id.action_export:
    		try {
				exportListToFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("TAG", e.getLocalizedMessage());
			}
    		break;
    	case R.id.action_settings:
    		Toast.makeText(getApplicationContext(), "Settings Clicked", Toast.LENGTH_SHORT).show();
    		break;
    	default:
    		throw new IllegalAccessError();
    	}
    	
    	return super.onOptionsItemSelected(menuItem);
    }
}
