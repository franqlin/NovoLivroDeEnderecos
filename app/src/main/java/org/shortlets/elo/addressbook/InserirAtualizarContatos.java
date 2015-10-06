package org.shortlets.elo.addressbook;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.shortlets.elo.addressbook.dao.ContatoDao;
import org.shortlets.elo.service.location.GPSUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eloaddressbook.R;


public class InserirAtualizarContatos extends Activity
{
   private long rowID;

   private EditText nomeEditText;
   private EditText telefoneEditText;
   private EditText emailEditText;
   private EditText enderecoEditText;
   private EditText notaEditText;

   private LocationManager locationMangaer=null;
   private LocationListener locationListener=null;
   private ProgressBar pb =null;
   private static final String TAG = "Debug";
   private Boolean isGPSAtivo = false;



   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_contact);

      nomeEditText = (EditText) findViewById(R.id.nomeEditText);
      emailEditText = (EditText) findViewById(R.id.emailEditText);
      telefoneEditText = (EditText) findViewById(R.id.telefoneEditText);
      enderecoEditText = (EditText) findViewById(R.id.enderecoEditText);
      notaEditText = (EditText) findViewById(R.id.notaEditText);

	   pb = (ProgressBar) findViewById(R.id.progressBar1);
	   pb.setVisibility(View.INVISIBLE);
	   locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      Bundle extras = getIntent().getExtras();

      if (extras != null)
      {
         rowID = extras.getLong("row_id");
         nomeEditText.setText(extras.getString("nome"));
         emailEditText.setText(extras.getString("email"));
         telefoneEditText.setText(extras.getString("telefone"));
         enderecoEditText.setText(extras.getString("endereco"));
         notaEditText.setText(extras.getString("nota"));
      }

      Button botaoSalvar =
         (Button) findViewById(R.id.saveContactButton);
      botaoSalvar.setOnClickListener(eventoBotaoSalvar);
      Button botaoBuscar =  (Button) findViewById(R.id.buscarEndereco);
      botaoBuscar.setOnClickListener(eventoBuscarEndereco);


   }

   OnClickListener eventoBuscarEndereco = new OnClickListener()
   {

	@Override
	public void onClick(View v) {

		isGPSAtivo = displayGpsStatus();
		if (isGPSAtivo) {
			Log.v(TAG, "click:: buscar endereco");
			enderecoEditText.setText("... carregando endereço!");
			pb.setVisibility(View.VISIBLE);
			locationListener = new GPSLocationListener();
			locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,locationListener);

			} else {
			alertaGPS("Gps", " GPS: OFF");
		}


	}
   };
	private Boolean displayGpsStatus() {
		ContentResolver contentResolver = getBaseContext().getContentResolver();
		return Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
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
             	pb.setVisibility(View.INVISIBLE);
                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
    		    String endereco=GPSUtil.getEndereco(geocoder, location);
    		    Toast.makeText(getBaseContext(),"Localizacao atual: Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(),Toast.LENGTH_SHORT).show();
    		    enderecoEditText.setText(endereco);
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

   OnClickListener eventoBotaoSalvar = new OnClickListener()
   {
      @Override
      public void onClick(View v)
      {
         if (nomeEditText.getText().length() != 0)
         {
            AsyncTask<Object, Object, Object> saveContactTask =
               new AsyncTask<Object, Object, Object>()
               {
                  @Override
                  protected Object doInBackground(Object... params)
                  {
                     salvarContato();
                     return null;
                  }

                  @Override
                  protected void onPostExecute(Object result)
                  {
                     finish();
                  }
               };
            saveContactTask.execute((Object[]) null);
         }
         else
         {

            AlertDialog.Builder builder =   new AlertDialog.Builder(InserirAtualizarContatos.this);
            builder.setTitle(R.string.errorTitle);
            builder.setMessage(R.string.errorMessage);
            builder.setPositiveButton(R.string.errorButton, null);
            builder.show();
         }
      }
   };


   private void salvarContato()
   {

      ContatoDao databaseConnector = new ContatoDao(this);

      if (getIntent().getExtras() == null)
      {

         databaseConnector.insertContato(
            nomeEditText.getText().toString(),
            emailEditText.getText().toString(),
            telefoneEditText.getText().toString(),
            enderecoEditText.getText().toString(),
            notaEditText.getText().toString());
      }
      else
      {
         databaseConnector.updateContato(rowID,
            nomeEditText.getText().toString(),
            emailEditText.getText().toString(),
            telefoneEditText.getText().toString(),
            enderecoEditText.getText().toString(),
            notaEditText.getText().toString());
      }
   }

}

