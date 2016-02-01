/** @file PentaBoard.java
 *
 * @author marco corvi
 * @date dec 2014
 *
 * @brief board
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.pentomino;

import java.io.StringWriter;
import java.io.PrintWriter;

class PentaBoard
{
  int board[];
  String name;;

  PentaBoard( String n, int b[] )
  {
    name = n;
    board = new int[64];
    for ( int k=0; k<64; ++ k ) { board[k] = b[k]; }
  }

  static String toString( int[] b )
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter( sw );
    for ( int k=0; k<64; ++ k ) { 
      pw.format("%c", (b[k] >= 0 && b[k] <= 9)? '0' + b[k] : ( b[k] >= 10 && b[k] <= 12 )? 'A' + b[k] - 10 : '#' );
    }
    return sw.getBuffer().toString();
  }

  static int[] parseString( String str )
  {
    if ( str.length() < 64 ) return null;
    int[] bb = new int[64];
    for ( int k=0; k<64; ++ k ) { 
      char c = str.charAt( k );
      if ( c >= '0' && c <= '9' ) {
        bb[k] = (int)( c - '0' );
      } else if ( c >= 'A' && c <= 'C' ) {
        bb[k] = (int)( c - 'A' + 10 );
      } else {
        bb[k] = -1;
      }
    }
    return bb;
  }
    
    

  boolean isComplete()
  {
    for ( int k=0; k<64; ++ k ) if ( board[k] < 0 ) return false;
    return true;
  }

}
    
