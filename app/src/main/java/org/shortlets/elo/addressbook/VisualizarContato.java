package org.shortlets.elo.addressbook;

import org.shortlets.elo.addressbook.dao.ContatoDao;
import com.example.eloaddressbook.R;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts.Intents;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class VisualizarContato extends Activity 
{
   private long rowID; 
   private TextView nomeTextView; 
   private TextView telefoneTextView; 
   private TextView emailTextView; 
   private TextView enderecoTextView; 
   private TextView notaTextView; 

   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_view_contact);

      nomeTextView = (TextView) findViewById(R.id.nomeTextView);
      telefoneTextView = (TextView) findViewById(R.id.telefoneTextView);
      emailTextView = (TextView) findViewById(R.id.emailTextView);
      enderecoTextView = (TextView) findViewById(R.id.enderecoTextView);
      notaTextView = (TextView) findViewById(R.id.cidadeTextView);
      
      Bundle extras = getIntent().getExtras();
      rowID = extras.getLong(ListaContatosActivity.ROW_ID);
    
   } 

   @Override
   protected void onResume()
   {
      super.onResume();     
      new CarregarContato().execute(rowID);
   }

   private class CarregarContato extends AsyncTask<Long, Object, Cursor> 
   {
      ContatoDao contatoDao = 
         new ContatoDao(VisualizarContato.this);

      @Override
      protected Cursor doInBackground(Long... params)
      {
         contatoDao.open();
         return contatoDao.findContatoById(params[0]);
      }
      @Override
      protected void onPostExecute(Cursor result)
      {
         super.onPostExecute(result);
   
         result.moveToFirst(); 
         int nomeIndex = result.getColumnIndex("nome");
         int telefoneIndex = result.getColumnIndex("telefone");
         int emailIndex = result.getColumnIndex("email");
         int enderecoIndex = result.getColumnIndex("endereco");
         int notaIndex = result.getColumnIndex("nota");
   
         nomeTextView.setText(result.getString(nomeIndex));
         telefoneTextView.setText(result.getString(telefoneIndex));
         emailTextView.setText(result.getString(emailIndex));
         enderecoTextView.setText(result.getString(enderecoIndex));
         notaTextView.setText(result.getString(notaIndex));
   
         result.close(); 
         contatoDao.close(); 
      }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.view_contact, menu);
      return true;
   }
   
   public  void goMap(View v){
	   String format = "geo:0,0?q="+ enderecoTextView.getText().toString();
       Uri uri = Uri.parse(format); 
       Intent intent = new Intent(Intent.ACTION_VIEW, uri);
       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       this.startActivity(intent);
   }
   public void mailTo(View v){
	  Log.i("Enviar email email", "");

      String[] TO = {emailTextView.getText().toString()};
      String[] CC = {"mcmohd@gmail.com"};
      Intent emailIntent = new Intent(Intent.ACTION_SEND);
      emailIntent.setData(Uri.parse("mailto:"));
      emailIntent.setType("text/plain");
      emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
      emailIntent.putExtra(Intent.EXTRA_CC, CC);
      //emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
      //emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

      try {
         startActivity(Intent.createChooser(emailIntent, "Enviando mail..."));
         finish();
      } catch (android.content.ActivityNotFoundException ex) {
         Toast.makeText(this, 
         "Erro ao enviar email", Toast.LENGTH_SHORT).show();
      }
   }
	  

   @SuppressWarnings("deprecation")
public void  goSave(View v){
	    Intent intent = new Intent(Intents.Insert.ACTION);
	    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
	    intent.putExtra(Intents.Insert.PHONE, telefoneTextView.getText());
	    intent.putExtra(Intents.Insert.NAME, nomeTextView.getText());
	    intent.putExtra(Intents.Insert.EMAIL,emailTextView.getText());
	    intent.putExtra(Intents.Insert.NOTES,notaTextView.getText());
	    intent.putExtra(Intents.Insert.POSTAL,enderecoTextView.getText());
	    intent.putExtra(Intents.Insert.EMAIL_TYPE, CommonDataKinds.Email.TYPE_WORK);
	    startActivity(intent);
	    
   }
   
   public void goPhone(View v){
	   try {
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel:"+telefoneTextView.getText().toString()));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException e) {
	         e.printStackTrace();
	    }
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId()) 
      {
         case R.id.editItem:
            Intent addContato =
               new Intent(this, InserirAtualizarContatos.class);
            addContato.putExtra(ListaContatosActivity.ROW_ID, rowID);
            addContato.putExtra("nome", nomeTextView.getText());
            addContato.putExtra("telefone", telefoneTextView.getText());
            addContato.putExtra("email", emailTextView.getText());
            addContato.putExtra("endereco", enderecoTextView.getText());
            addContato.putExtra("nota", notaTextView.getText());
            startActivity(addContato);
            return true;
         case R.id.deleteItem:
            deleteContato();
            return true;
         default:
            return super.onOptionsItemSelected(item);
      } 
   } 
   
  
   private void deleteContato()
   {
 
      AlertDialog.Builder builder = 
         new AlertDialog.Builder(VisualizarContato.this);

      builder.setTitle(R.string.confirmTitle); 
      builder.setMessage(R.string.confirmMessage); 

      builder.setPositiveButton(R.string.button_delete,
         new DialogInterface.OnClickListener()
         {
            @Override
            public void onClick(DialogInterface dialog, int button)
            {
               final ContatoDao databaseConnector = 
                  new ContatoDao(VisualizarContato.this);
               
               AsyncTask<Long, Object, Object> deleteTask =
                  new AsyncTask<Long, Object, Object>()
                  {
                     @Override
                     protected Object doInBackground(Long... params)
                     {
                        databaseConnector.deleteContato(params[0]); 
                        return null;
                     } 

                     @Override
                     protected void onPostExecute(Object result)
                     {
                        finish(); 
                     } 
                  }; 
               deleteTask.execute(new Long[] { rowID });               
            } 
         } 
      ); 
      
      builder.setNegativeButton(R.string.button_cancel, null);
      builder.show(); 
   } 
} 

