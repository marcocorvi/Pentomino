/** @file Penta.java
 *
 * @author marco corvi
 * @date dec 2014
 *
 * @brief main class
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.pentomino;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.Window;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import android.view.Display;
import android.util.DisplayMetrics;

import android.view.Menu;
import android.view.MenuItem;

// import android.util.Log;
import android.util.DisplayMetrics;

public class Penta extends Activity
                   implements View.OnClickListener
                   , View.OnTouchListener
                   , OnEditorActionListener
{

  private PentaView mView = null;
  private PentaData mData = null;

  private Button mBtLeft;
  private Button mBtHFlip;
  private Button mBtVFlip;
  private Button mBtRight;
  // private Button mBtCheck;
  // private Button mBtReset;
  // private Button mBtSave;
  private Button mBtView;
  // private Button mBtList;

  private EditText mETname;

  int mXOff1, mXOff2, mYOff1, mYOff2;


  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main);

    mETname = (EditText) findViewById( R.id.name );
    mETname.setOnEditorActionListener( this );

    mView = (PentaView) findViewById( R.id.penta_view );

    mBtLeft  = (Button) findViewById( R.id.btn_left );
    mBtRight = (Button) findViewById( R.id.btn_right );
    mBtHFlip = (Button) findViewById( R.id.btn_hflip );
    mBtVFlip = (Button) findViewById( R.id.btn_vflip );
    // mBtCheck = (Button) findViewById( R.id.btn_check );
    // mBtReset = (Button) findViewById( R.id.btn_reset );
    // mBtSave  = (Button) findViewById( R.id.btn_save );
    mBtView  = (Button) findViewById( R.id.btn_view );
    // mBtList  = (Button) findViewById( R.id.btn_list );

    mBtLeft.setOnClickListener( this );
    mBtHFlip.setOnClickListener( this );
    mBtVFlip.setOnClickListener( this );
    mBtRight.setOnClickListener( this );
    // mBtCheck.setOnClickListener( this );
    // mBtReset.setOnClickListener( this );
    // mBtSave.setOnClickListener( this );
    mBtView.setOnClickListener( this );
    // mBtList.setOnClickListener( this );

    int width  = getResources().getDisplayMetrics().widthPixels;
    int height = getResources().getDisplayMetrics().heightPixels;
    mXOff1 = width/8;
    mXOff2 = width/(5*13);
    mYOff1 = height - width;
    mYOff2 = height - width - 10 * mXOff2;

    mData = new PentaData( this );
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics( dm );
    int hh = dm.heightPixels;
    int ww = dm.widthPixels;
    int dpi = dm.densityDpi;
    // Log.v("Penta", "metrics " + ww + " " + hh + " " + dpi );

    mView.setSizes( ww, hh, dpi );
    // mView.init( );
    mView.setOnTouchListener( this );

    setTheTitle();
  }

  @Override
  public boolean onEditorAction( TextView view, int code, KeyEvent event )
  {
    if ( event == null ) {
      if ( code == EditorInfo.IME_ACTION_DONE ) {
      } else if ( code == EditorInfo.IME_ACTION_NEXT ) {
      } else {
        return false; // not consumed
      }
    } else {  
      if ( code == EditorInfo.IME_NULL ) {
        if ( event.getAction() == KeyEvent.ACTION_DOWN ) {
        } else {
          return true; // consume when key is released
        }
      } else {
        return false;
      }
    }
    handleEnterKey();
    return false;
  }

  private void setTheTitle()
  {
    setTitle( "PENTOMINO - " + mData.getNrBoards() );
  }

  private MenuItem mMIhelp;
  private MenuItem mMIreset;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) 
  {
    super.onCreateOptionsMenu( menu );

    mMIreset = menu.add( R.string.btn_reset );
    mMIhelp  = menu.add( R.string.btn_help );

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) 
  {
    if ( item == mMIhelp ) {
      (new PentaHelp( this ) ).show();
    } else if ( item == mMIreset ) {
      askReset();
    }
    return true;
  }



  void display( int[] board )
  {
    mView.display( board );
  }

  // @Override
  // public void onStop()
  // {
  //   super.onStop();
  // }

  private boolean isEmpty( int[] b )
  {
    int cnt = 0;
    for ( int k=0; k<64; ++k ) if ( b[k] < 0 ) ++cnt;
    return (cnt == 64);
  }

  private boolean isComplete( int[] b )
  {
    for ( int k=0; k<64; ++k ) if ( b[k] < 0 ) return false;
    return true;
  }

  public void onClick(View view)
  {
    Button b = (Button)view;

    if ( b == mBtLeft ) {
      mView.rotateLeft();
    } else if ( b == mBtRight ) {
      mView.rotateRight();
    } else if ( b == mBtHFlip ) {
      mView.flipH();
    } else if ( b == mBtVFlip ) {
      mView.flipV();
    // } else if ( b == mBtCheck ) {
    //   String name = mData.check( mView.mBoard );
    //   if ( name == null ) {
    //     mETname.setText( "" );
    //     Toast.makeText( this, "Not found", Toast.LENGTH_SHORT ).show();
    //   } else {
    //     mETname.setText( name );
    //   }
    // } else if ( b == mBtReset ) {
    //   askReset();
    // } else if ( b == mBtSave ) {
    //   if ( ! isComplete( mView.mBoard ) ) {
    //     Toast.makeText( this, "board is not complete", Toast.LENGTH_SHORT ).show();
    //   } else {
    //     String name = mData.check( mView.mBoard );
    //     if ( name == null ) {
    //       String new_name = mETname.getText().toString();
    //       if ( new_name == null || new_name.length() == 0 ) {
    //         mETname.setError( getResources().getString( R.string.no_name ) );
    //       } else {
    //         if ( mData.hasName( new_name ) ) {
    //           mETname.setError( getResources().getString( R.string.name_exists ) );
    //         } else {
    //           String msg = mData.store( mView.mBoard, new_name );
    //           Toast.makeText( this, msg, Toast.LENGTH_SHORT ).show();
    //           setTheTitle();
    //         }
    //       }
    //     }
    //   }
    } else if ( b == mBtView ) {
      handleBtView( );
    // } else if ( b == mBtList ) {
    //   // list all the boards
    //   (new BoardListDialog( this, this, mData.mBoards )).show();
    }
  }


  private void handleEnterKey()
  {
    if ( isEmpty( mView.mBoard ) ) {
      String new_name = mETname.getText().toString();
      if ( new_name == null || new_name.length() == 0 ) {
        mETname.setError( getResources().getString( R.string.no_name ) );
      } else {
        PentaBoard pb = mData.getName( new_name );
        if ( pb == null ) {
          mETname.setError( getResources().getString( R.string.no_solution ) );
        } else {
          mView.setBoard( pb.board );
        }
      }
    }
  }


  private void handleBtView( )
  {
    if ( isEmpty( mView.mBoard ) ) {
      // get active piece
      int index = mView.mIndex;
      if ( index >= 0 && index < 13 ) {
        int result[] = new int[64];
        for ( int k=0; k<64; ++k ) {
          if ( mView.placeOnBoard( index, k%8, k/8 ) ) {
            ArrayList<PentaBoard> matches = mData.getMatchingBoards( mView.mBoard );
            result[k] = matches.size();
            mView.removeFromBoard( k%8, k/8 );
          } else {
            result[k] = -1;
          }
        }
        mView.reset();
        (new ResultDialog(this, result)).show();
      }
    } else if ( ! isComplete( mView.mBoard ) ) {
        // Toast.makeText( this, "board is not complete", Toast.LENGTH_SHORT ).show();
        ArrayList<PentaBoard> matches = mData.getMatchingBoards( mView.mBoard );
        if ( matches.size() == 0 ) { 
          Toast.makeText( this, R.string.no_match, Toast.LENGTH_SHORT ).show();
        } else {
          (new BoardListDialog( this, this, matches )).show();
        }
    } else {
        ArrayList< PentaBoard > pbs = mData.getCloseBoards( mView.mBoard );
        if ( pbs.size() == 0 ) {
          Toast.makeText( this, R.string.no_close, Toast.LENGTH_SHORT ).show();
        } else {
          (new BoardListDialog( this, this, pbs )).show();
        }
    }
  }

  private void askReset()
  {
    new PentaAlertDialog( this, getResources(), getResources().getString( R.string.ask_reset ),
      new DialogInterface.OnClickListener() {
        @Override
        public void onClick( DialogInterface dialog, int btn ) {
          mView.reset();
        }
    } );
  }


  @Override
  public boolean onTouch( View v, MotionEvent ev )
  { 
    int x = (int)ev.getX();
    int y = (int)ev.getY();
    int action = ev.getAction() & MotionEvent.ACTION_MASK;
   
    if ( action == MotionEvent.ACTION_DOWN ) {
      int piece = mView.getPiece( x, y );
      int board = mView.getBoard( x, y );
      // Log.v("Penta", "x " + x + " y " + y + " piece " + piece + " board " + board );
      if ( board >= 0 ) {
        x = board % 8;
        y = board / 8;
        if ( mView.getBoardValue(x, y) >= 0 ) {
          mView.removeFromBoard( x, y );
        } else {
          int k = mView.mIndex;
          if ( k >= 0 ) {
            if ( mView.placeOnBoard( k, x, y ) && isComplete( mView.mBoard ) ) {
              String name = mData.check( mView.mBoard );
              if ( name == null ) {
                name = mData.getNewName();
                Toast.makeText( this, String.format( getResources().getString( R.string.new_solution ), name ),
                                Toast.LENGTH_LONG ).show();
                mData.store( mView.mBoard, name );
                setTheTitle();
              } 
              mETname.setText( name );
            }
          }
        }
      }
    }

    return true;
  }

  @Override
  public void onResume()
  {
    super.onResume();
    if ( mView != null ) mView.resume();
  }

  @Override
  public void onPause()
  {
    super.onPause();
    // mData.save();
    if ( mView != null ) mView.pause();
  }

  // @Override
  // public void onBackPressed()
  // {
  //   if ( mView != null ) mView.pause();
  // }

}
