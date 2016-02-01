/** @file ResultDialog.java
 *
 * @author marco corvi
 * @date dec 2014
 *
 * @brief result dialog
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.pentomino;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.content.Context;

import android.view.View;

import android.widget.TextView;
import android.widget.GridView;
import android.widget.ArrayAdapter;

import java.io.StringWriter;
import java.io.PrintWriter;

class ResultDialog extends Dialog
{
  Context mContext;
  // Penta mParent;
  int[] mResult;

  ResultDialog( Context context, int[] result )
  {
    super( context );
    mContext = context;
    mResult = result;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate( savedInstanceState );
    setContentView(R.layout.result_dialog);

    GridView grid = (GridView) findViewById(R.id.result_grid);

    ArrayAdapter< String > adapter = new ArrayAdapter< String >( mContext, R.layout.message );
    for ( int k=0; k<64; ++k ) {
      if ( mResult[k] < 0 ) {
        adapter.add( "-" );
      } else {
        adapter.add( Integer.toString( mResult[k] ) );
      }
    }
    grid.setAdapter( adapter );
    grid.invalidate();

    setTitle( "Solutions by position" );
  }

}

