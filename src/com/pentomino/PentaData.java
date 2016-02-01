/** @file PentaData.java
 *
 * @author marco corvi
 * @date dec 2014
 *
 * @brief data
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.pentomino;

import java.util.ArrayList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Environment;
import android.content.Context;

import android.util.Log;

class PentaData
{
  
  static private String FILENAME = "pentomino.txt";
  private String mHomeDir;   // "/mnt/sdcard/Android/data/com.pentomino/";
  private String mFilepath;

  private Context mContext;

  ArrayList< PentaBoard > mBoards;

  PentaData( Context context )
  {
    mContext = context;
    mHomeDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.pentomino";
    // mHomeDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pentomino";
    mFilepath = mHomeDir + "/" + FILENAME;
    File dir = new File( mHomeDir );
    if ( ! dir.exists() ) {
      if ( ! dir.mkdirs() ) {
        Log.e("Penta", "Failed to make home dir " + mHomeDir );
        mFilepath = "/mnt/sdcard/" + FILENAME;
      }
    }
    // Log.v("Penta", "data load from " + FILENAME );
    mBoards = new ArrayList< PentaBoard >();

    int board[];
    board = new int[64];
    try {
      FileInputStream fr = new FileInputStream( mFilepath );
      // FileInputStream fr = mContext.openFileInput( FILENAME );
      // Log.v("Penta", "data load " + mBoards.size() + " boards to " + fr.toString() );
      BufferedReader br = new BufferedReader( new InputStreamReader( fr ) );
      String line;
      line = br.readLine();
      int line_cnt = 1;
      while ( line != null ) {
        line.trim();
        String[] vals = line.split(" ");
        String name = vals[0];
        if ( vals.length < 65 ) {
          Log.e("Penta", "SHORT LINE " + line_cnt + " <" + line + ">");
        } else {
          try { 
            for ( int k=0; k<64; ++k ) board[k] = Integer.parseInt( vals[1+k] );
            int k=0;
            for ( ; k<mBoards.size(); ++k ) {
              if ( matching( board, mBoards.get(k).board ) ) break;
            }
            if ( k == mBoards.size() ) {
              // PentaBoard bb = new PentaBoard( name, board );
              mBoards.add( new PentaBoard( name, board ) );
            }
          } catch ( NumberFormatException e ) {
            Log.e("Penta", "INVALID LINE " + line_cnt + " <" + line + ">");
          }
        }
        line = br.readLine();
        ++line_cnt;
      }
      fr.close();
    } catch ( FileNotFoundException e ) {
      Log.e("Penta", "FileNotFound " + e );
    } catch ( IOException e ) {
      Log.e("Penta", "data load exception " + e );
    }
  }

  String getNewName()
  {
    int max = 0;
    for ( PentaBoard b : mBoards ) {
      try {
        int n = Integer.parseInt( b.name );
        if ( n > max ) max = n;
      } catch ( NumberFormatException e ) {
      }
    }
    ++ max;
    return Integer.toString( max );
  }


  int size() { return mBoards.size(); }

  ArrayList< PentaBoard > getCloseBoards( int[] board ) 
  {
    ArrayList< PentaBoard > ret = new ArrayList< PentaBoard >();
    for ( PentaBoard b : mBoards ) {
      if ( coincidences( board, b.board ) >= 7 ) {
        ret.add( b );
      }
    }
    return ret;
  }

  ArrayList< PentaBoard > getMatchingBoards( int[] board )
  {
    ArrayList< PentaBoard > ret = new ArrayList< PentaBoard >();
    for ( PentaBoard b : mBoards ) {
      if ( matching( board, b.board ) ) {
        ret.add( b );
      }
    }
    return ret;
  }
    

  boolean hasName( String name )
  {
    for ( PentaBoard b : mBoards ) {
      if ( b.name.equals( name ) ) return true;
    }
    return false;
  }

  PentaBoard getName( String name )
  {
    for ( PentaBoard b : mBoards ) {
      if ( b.name.equals( name ) ) return b;
    }
    return null;
  }

  /**
   * @param bb     first board
   * @param board  second board ( must be completely filled ) 
   * @return the number of coincident pieces
   */
  private int coincidences( int[] bb, int[] board )
  {
    int nc = 0;
    int np;
    int kp[] = new int[13];
    int kmin = 65;
    int k;

    for ( k=0; k<13; ++k ) kp[k] = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( bb[j*8+i] == board[j*8+i] ) kp[ board[j*8+i] ] ++;
    }
    np = ( kp[0] == 4 )? 1 : 0;
    for ( k=1; k<13; ++k ) if ( kp[k] == 5 ) ++ np;
    if ( np > nc ) nc = np;
    
    for ( k=0; k<13; ++k ) kp[k] = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( bb[j*8+(7-i)] == board[j*8+i] ) kp[ board[j*8+i] ] ++;
    }
    np = ( kp[0] == 4 )? 1 : 0;
    for ( k=1; k<13; ++k ) if ( kp[k] == 5 ) ++ np;
    if ( np > nc ) nc = np;
    
    for ( k=0; k<13; ++k ) kp[k] = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( bb[(7-j)*8+i] == board[j*8+i] ) kp[ board[j*8+i] ] ++;
    }
    np = ( kp[0] == 4 )? 1 : 0;
    for ( k=1; k<13; ++k ) if ( kp[k] == 5 ) ++ np;
    if ( np > nc ) nc = np;
    
    for ( k=0; k<13; ++k ) kp[k] = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( bb[(7-j)*8+(7-i)] 
       == board[j*8+i] ) kp[ board[j*8+i] ] ++;
    }
    np = ( kp[0] == 4 )? 1 : 0;
    for ( k=1; k<13; ++k ) if ( kp[k] == 5 ) ++ np;
    if ( np > nc ) nc = np;
    
    for ( k=0; k<13; ++k ) kp[k] = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( bb[j*8+i] == board[i*8+j] ) kp[ board[i*8+j] ] ++;
    }
    np = ( kp[0] == 4 )? 1 : 0;
    for ( k=1; k<13; ++k ) if ( kp[k] == 5 ) ++ np;
    if ( np > nc ) nc = np;
    
    for ( k=0; k<13; ++k ) kp[k] = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( bb[j*8+(7-i)] == board[i*8+j] ) kp[ board[i*8+j] ] ++;
    }
    np = ( kp[0] == 4 )? 1 : 0;
    for ( k=1; k<13; ++k ) if ( kp[k] == 5 ) ++ np;
    if ( np > nc ) nc = np;
    
    for ( k=0; k<13; ++k ) kp[k] = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( bb[(7-j)*8+i] == board[i*8+j] ) kp[ board[i*8+j] ] ++;
    }
    np = ( kp[0] == 4 )? 1 : 0;
    for ( k=1; k<13; ++k ) if ( kp[k] == 5 ) ++ np;
    if ( np > nc ) nc = np;
    
    for ( k=0; k<13; ++k ) kp[k] = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( bb[(7-j)*8+(7-i)] == board[i*8+j] ) kp[ board[i*8+j] ] ++;
    }
    np = ( kp[0] == 4 )? 1 : 0;
    for ( k=1; k<13; ++k ) if ( kp[k] == 5 ) ++ np;
    if ( np > nc ) nc = np;

    return nc;
  }

  // @param board  matching board, it can have empty cells
  // @param bb     complete board
  private boolean matching( int[] board, int[] bb )
  {
    // straight
    int k = 0; 
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( board[j*8+i] != -1 && bb[j*8+i] != board[j*8+i] ) break;          // J, I
      ++k;
    }
    if ( k == 64 ) return true;
    
    // bb reflected horizontally
    k = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( board[j*8+i] != -1 && bb[j*8+(7-i)] != board[j*8+i] ) break;      // J, -I
      ++k;
    }
    if ( k == 64 ) return true;
    
    // bb reflected vertically
    k = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( board[j*8+i] != -1 && bb[(7-j)*8+i] != board[j*8+i] ) break;      // -J, I
      ++k;
    }
    if ( k == 64 ) return true;
    
    // bb rotated by 180 degrees
    k = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( board[j*8+i] != -1 && bb[(7-j)*8+(7-i)] != board[j*8+i] ) break;  // -J, -I
      ++k;
    }
    if ( k == 64 ) return true;
    
    k = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( board[j*8+i] != -1 && bb[i*8+j] != board[j*8+i] ) break;          // I,  J
      ++k;
    }
    if ( k == 64 ) return true;
    
    // bb rotated by 90 degrees clockwise
    k = 0; 
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( board[j*8+i] != -1 && bb[i*8+(7-j)] != board[j*8+i] ) break;      // I, -J
      ++k;
    }
    if ( k == 64 ) return true;
    
    k = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( board[j*8+i] != -1 && bb[(7-i)*8+j] != board[j*8+i] ) break;      // -I,  J
      ++k;
    }
    if ( k == 64 ) return true;

    k = 0;
    for ( int j=0; j<8; ++j) for ( int i=0; i<8; ++i ) {
      if ( board[j*8+i] != -1 && bb[(7-i)*8+(7-j)] != board[j*8+i] ) break; // -I, -J
      ++k;
    }
    if ( k == 64 ) return true;

    return false;
  }

  String check( int[] board )
  {
    for ( int i=0; i<64; ++i ) if ( board[i] < 0 ) return null;
    for ( PentaBoard b : mBoards ) {
      int d = coincidences( b.board, board );
      if ( d == 13 ) return b.name;
    }
    return null;
  }

  String store( int[] board, String name ) 
  {
    name = name.replace(" ", "_");
    PentaBoard pb = new PentaBoard( name, board );
    mBoards.add( pb );
    // save to file
    // save();
    appendToFile( pb );
    return "added board <" + name + "> size " + mBoards.size();
  }

  private void appendToFile( PentaBoard pb )
  {
    try {
      FileWriter fos = new FileWriter( mFilepath, true );
      // FileOutputStream fos = mContext.openFileOutput( FILENAME, mContext.MODE_WORLD_READABLE );
      PrintWriter pw = new PrintWriter( fos );
      pw.format( "%s", pb.name );
      for ( int k=0; k<64; ++k ) pw.format( " %d", pb.board[k] );
      pw.format("\n");
      fos.flush();
      fos.close();
    } catch ( IOException e ) {
      Log.e("Penta", "data append exception " + e );
    }
  }

  // void save( )
  // {
  //   try {
  //     FileWriter fos = new FileWriter( mFilepath );
  //     // FileOutputStream fos = mContext.openFileOutput( FILENAME, mContext.MODE_WORLD_READABLE );
  //     // Log.v("Penta", "data save " + mBoards.size() + " boards to " + fos.toString() );
  //     PrintWriter pw = new PrintWriter( fos );
  //     for ( PentaBoard b : mBoards ) {
  //       pw.format( "%s", b.name );
  //       for ( int k=0; k<64; ++k ) pw.format( " %d", b.board[k] );
  //       pw.format("\n");
  //     }
  //     fos.flush();
  //     fos.close();
  //   } catch ( IOException e ) {
  //     Log.e("Penta", "data save exception " + e );
  //   }
  // }

  int getNrBoards() { return mBoards.size(); }

}
