/** @file BoardListDialog.java
 *
 * @author marco corvi
 * @date dec 2014
 *
 * @brief BoardListDialog 
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
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

class BoardListDialog extends Dialog
                      implements OnItemClickListener
{
  Context mContext;
  Penta mParent;
  ArrayList< PentaBoard > mBoards;

  BoardListDialog( Context context, Penta parent, ArrayList<PentaBoard> pbs )
  {
    super( context );
    mContext = context;
    mParent  = parent;
    mBoards  = pbs;
  }

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate( savedInstanceState );
    setContentView(R.layout.board_list_dialog);
    ArrayAdapter dataAdapter = new ArrayAdapter<String>( mContext, R.layout.item );

    ListView list = (ListView) findViewById(R.id.list);
    list.setOnItemClickListener( this );
    list.setDividerHeight( 2 );

    setTitle( "Nr. of solutions: " + mBoards.size() );
    for ( PentaBoard pb : mBoards ) {
      dataAdapter.add( pb.name );
    }
    list.setAdapter( dataAdapter );
    list.invalidate();
  }

  @Override 
  public void onItemClick(AdapterView<?> parent, View view, int position, long id)
  {
    CharSequence item = ((TextView) view).getText();
    String value = item.toString();
    for ( PentaBoard pb : mBoards ) {
      if ( pb.name.equals( value ) ) {
        mParent.display( pb.board );
        dismiss();
        return;
      }
    }
  }
 


}
