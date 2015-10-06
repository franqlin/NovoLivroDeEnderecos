package org.shortlets.elo.addressbook;


import java.util.Locale;

import org.shortlets.elo.addressbook.dao.ContatoDao;
import org.shortlets.elo.service.location.GPSUtil;


import com.example.eloaddressbook.R;

import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ListaContatosActivity extends ListActivity
{
   public static final String ROW_ID = "row_id";
   private ListView contactListView;
   private CursorAdapter contactAdapter;
   private LocationManager locationMangaer=null;
   private LocationListener locationListener=null;
   private static final String TAG = "Debug";



   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      contactListView = getListView();
      contactListView.setOnItemClickListener(viewContactListener);
      ColorDrawable devidrColor = new ColorDrawable(
              this.getResources().getColor(R.color.translucent_bosta));
      contactListView.setDivider(devidrColor);
      contactListView.setDividerHeight(1);
      String[] from = new String[] { "nome","endereco","telefone"};
      int[] to = new int[] { R.id.nomeLista,R.id.enderecoLista,R.id.telefoneLista };
      contactAdapter = new SimpleCursorAdapter(ListaContatosActivity.this, R.layout.contact_list_item, null, from, to);

      setListAdapter(contactAdapter);

   }

   @Override
   protected void onResume()
   {
      super.onResume();
       new GetContactsTask().execute((Object[]) null);
    }

   @Override
   protected void onStop()
   {
      Cursor cursor = contactAdapter.getCursor();

      if (cursor != null)
         cursor.deactivate();

      contactAdapter.changeCursor(null);
      super.onStop();
   }

   private class GetContactsTask extends AsyncTask<Object, Object, Cursor>
   {
      ContatoDao databaseConnector =  new ContatoDao(ListaContatosActivity.this);
      @Override
      protected Cursor doInBackground(Object... params)
      {
         databaseConnector.open();
         return databaseConnector.findAll();
      }
      @Override
      protected void onPostExecute(Cursor result)
      {
         contactAdapter.changeCursor(result);
         databaseConnector.close();
      }
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.add_contact, menu);
      return true;
   }


   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {

      switch (item.getItemId()) {
	case R.id.addContactItem:
		  Intent addNewContact =  new Intent(ListaContatosActivity.this, InserirAtualizarContatos.class);
	      startActivity(addNewContact);
	      return true;
	case R.id.ondeEstou:

		if (displayGpsStatus()) {
			Log.v(TAG, "click:: buscar endereco");
			locationListener = new GPSLocationListener();
			locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,locationListener);
			   String format = "geo:0,0?";
		       Uri uri = Uri.parse(format);
			   Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		       this.startActivity(intent);

			} else {
			alertaGPS("Gps", " GPS: OFF");
		}


	       return true;
	default:
		return super.onOptionsItemSelected(item);
	}

   }
  	protected void alertaGPS(String titulo, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("GPS desabilitado")
				.setCancelable(false)
				.setTitle("Gps Status")
				.setPositiveButton("Gps On",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent myIntent = new Intent(
										Settings.ACTION_SECURITY_SETTINGS);
								startActivity(myIntent);
								dialog.cancel();
							}
						})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}



  	private class GPSLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
                Toast.makeText(getBaseContext(),"Localizacao atual: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(),Toast.LENGTH_SHORT).show();
        }

		@Override
		public void onProviderDisabled(String arg0) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	}



	private Boolean displayGpsStatus() {
		ContentResolver contentResolver = getBaseContext().getContentResolver();
		return Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
	}

   OnItemClickListener viewContactListener = new OnItemClickListener()
   {
      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
      {
         Intent viewContact = new Intent(ListaContatosActivity.this, VisualizarContato.class);
         viewContact.putExtra(ROW_ID, arg3);
         startActivity(viewContact);
      }
   };
}
