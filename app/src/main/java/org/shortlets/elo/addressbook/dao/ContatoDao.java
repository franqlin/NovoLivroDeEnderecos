 package org.shortlets.elo.addressbook.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class ContatoDao
{
   private static final String DATABASE_NAME = "ELO_CONTATOS_SCHEMA";
   private SQLiteDatabase database;
   private DatabaseOpenHelper databaseOpenHelper;

   public ContatoDao(Context context)
   {
      databaseOpenHelper =  new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   }

   public void open() throws SQLException
   {
      database = databaseOpenHelper.getWritableDatabase();
   }

   public void close()
   {
      if (database != null)
         database.close();
   }

   public void insertContato(String nome, String email, String telefone,
      String endereco, String nota)
   {
      ContentValues newContact = new ContentValues();
      newContact.put("nome", nome);
      newContact.put("email", email);
      newContact.put("telefone", telefone);
      newContact.put("endereco", endereco);
      newContact.put("nota", nota);

      open();
      database.insert("contato", null, newContact);
      close();
   }

   public void updateContato(long id, String nome, String email,String telefone, String endereco, String nota)
   {
      ContentValues editContact = new ContentValues();
      editContact.put("nome", nome);
      editContact.put("email", email);
      editContact.put("telefone", telefone);
      editContact.put("endereco", endereco);
      editContact.put("nota", nota);

      open();
      database.update("contato", editContact, "_id=" + id, null);
      close();
   }

   public Cursor findAll()
   {
      return database.query("contato", new String[] {"_id", "nome","endereco","telefone"},
         null, null, null, null, "nome");
   }

   public Cursor findContatoById(long id)
   {
      return database.query( "contato", null, "_id=" + id, null, null, null, null);
   }

   public void deleteContato(long id)
   {
      open();
      database.delete("contato", "_id=" + id, null);
      close();
   }

   private class DatabaseOpenHelper extends SQLiteOpenHelper
   {
      public DatabaseOpenHelper(Context context, String nome,CursorFactory factory, int version)
      {
         super(context, nome, factory, version);
      }

      @Override
      public void onCreate(SQLiteDatabase db)
      {
         String createQuery = "CREATE TABLE contato" +
            "(_id integer primary key autoincrement," +
            "nome TEXT, email TEXT, telefone TEXT," +
            "endereco TEXT, nota TEXT);";
         db.execSQL(createQuery);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion,
          int newVersion)
      {
      }
   }
}



