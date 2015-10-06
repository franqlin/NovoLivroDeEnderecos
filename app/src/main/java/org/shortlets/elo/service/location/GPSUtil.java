package org.shortlets.elo.service.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

public class GPSUtil extends Service implements LocationListener {

	private final Context contexto;

	private Location location; 
	protected LocationManager locationManager;
	public GPSUtil(Context context) {
		this.contexto = context;
	}
   public String  getEndereco(){
	   Geocoder geocoder = new Geocoder(contexto, Locale.getDefault());
	   List <Address> listaEndereco = null;
	   try {
           listaEndereco = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
           } catch (IOException e) {
               e.printStackTrace();
       }
           if (listaEndereco != null && listaEndereco.size() > 0) {
               Address endereco = listaEndereco.get(0);
               return  endereco.getMaxAddressLineIndex() > 0 ? endereco.getAddressLine(0) : ""+endereco.getLocality()+endereco.getCountryName();
           } else {
             return "nao encontrado";
           }
	   
   }
   
   public static String  getEndereco(Geocoder geocoder,Location l){
	   List <Address> listaEndereco = null;
	   try {
           listaEndereco = geocoder.getFromLocation(l.getLatitude(),l.getLongitude(), 1);
           } catch (IOException e) {
               e.printStackTrace();
       }
           if (listaEndereco != null && listaEndereco.size() > 0) {
               Address endereco = listaEndereco.get(0);
               return  endereco.getMaxAddressLineIndex() > 0 ? endereco.getAddressLine(0) : ""+endereco.getLocality()+endereco.getCountryName();
           } else {
             return "nao encontrado";
           }
	   
   }
   
	public static  Boolean displayGpsStatus(final Activity activity) {
		ContentResolver contentResolver = activity.getContentResolver();
		return Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
	}

     
	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
