/* @file PentaView.java
 *
 * This class is adapted from the "snake" sample (with very few changes)
 *
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pentomino;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.util.DisplayMetrics;
import android.util.Log;

/**
 * PentaView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 * 
 */
public class PentaView extends SurfaceView
                       implements SurfaceHolder.Callback
{

    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawables will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */

    private Context mContext;
    private Penta mPenta;

    protected static int mTileSize = 80;
    protected static int mMiniTileSize = 20;

    protected static final int mXTileCount = 8;
    protected static final int mYTileCount = 8;

    private static int mXOffset;
    private static int mYOffset;
    private static int mMiniXOffset;
    private static int mMiniYOffset;

    int mDim;
    int mDim2;
    int mXOff2;
    int mYOff2;
    int mYOff3;

    Paint mPaint[];
    Paint mPaintBlack;
    Paint mPaintGrey;

    SurfaceHolder mHolder = null;
    MyThread mThread;
    boolean  mHasSurface;

    int mBoard[];
    int mMiniBoard[];
    PentaPiece mPiece[];
    int mIndex; // index of the current piece

    void init()
    {
      makePaints();
      makePieces();
      clearTiles();
      // mHolder = getHolder();
      getHolder().addCallback( this );
      mHasSurface = false;
      mThread = null;
    }      

    public PentaView(Context context, AttributeSet attrs, int defStyle )
    {
      super(context, attrs, defStyle);
      mContext = context;
      // Log.v("Penta", "PentaView 3");
      init();
    }

    public PentaView(Context context, AttributeSet attrs)
    {
      super(context, attrs);
      mContext = context;
      // Log.v("Penta", "PentaView 2");
      init();
    }

    public PentaView(Context context )
    {
      super(context);
      mContext = context;
      // Log.v("Penta", "PentaView 1");
      init();
    }

    synchronized void reset()
    {
      for ( int k=0; k<13; ++k ) mPiece[k].setUsed( false );
      for ( int j=0; j<64; ++j ) mBoard[j] = -1;
    }

    synchronized void display( int[] board )
    {
      // for ( int k=0; k<13; ++k ) mPiece[k].setUsed( false );
      for ( int j=0; j<64; ++j ) mMiniBoard[j] = board[j];
    }

    synchronized void setBoard( int[] board ) 
    {
      for ( int k=0; k<13; ++k ) mPiece[k].setUsed( true );
      for ( int j=0; j<64; ++j ) mBoard[j] = board[j];
    }

    public void resume()
    {
      // Log.v("Penta", "PentaView resume hasSurface " + mHasSurface );

      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( mContext );
      String str = sp.getString( "PENTA_BOARD", null );
      if ( str != null ) {
        int[] board = PentaBoard.parseString( str );
        if ( board != null ) {
          mBoard = board;
          for ( int k=0; k<64; ++k ) {
            if ( mBoard[k] >= 0 ) mPiece[mBoard[k]].setUsed( true );
          }
        }
      }
      
      if ( mThread == null ) {
        // Log.v("Penta", "PentaView create thread");
        mThread = new MyThread();
      }
      if ( mHasSurface && mThread != null ) {
        // Log.v("Penta", "PentaView start thread");
        mThread.start();
      }
    }
 
    public void pause()
    {
      String str = PentaBoard.toString( mBoard );
      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( mContext );
      SharedPreferences.Editor editor = sp.edit();
      editor.putString( "PENTA_BOARD", str );
      editor.commit();

      if ( mThread != null ) {
        // Log.v("Penta", "PentaView stop thread");
        mThread.doExit();
        mThread = null;
      }
      // mHolder = null;
    }

    public void surfaceCreated( SurfaceHolder holder )
    {
      // Log.v("Penta", "PentaView surface created ");
      if ( mHolder != holder ) {
        // Log.v("Penta", "PentaView set holder");
        mHolder = holder;
        mHolder.addCallback( this );
      }
      mHasSurface = true;
      resume();
      // if ( mThread != null ) {
      //   mThread.start();
      // }
    }

    public void surfaceDestroyed( SurfaceHolder holder )
    {
      // Log.v("Penta", "PentaView surface destroyed ");
      pause();
    }

    public void surfaceChanged( SurfaceHolder holder, int format, int w, int h )
    { 
      // Log.v("Penta", "PentaView surface changed " + w + " " + h );
      onWindowResize( w, h );
      if ( mHolder != holder ) {
        // Log.v("Penta", "PentaView set holder");
        mHolder = holder;
        mHolder.addCallback( this );
        if ( mThread != null ) {
          // TODO ???
        }
      }
    }

    class MyThread extends Thread
    {
      private boolean done;

      MyThread( ) 
      {
        super();
        done = false;
        // Log.v("Penta", "Penta thread created");
      }

      @Override
      public void run()
      {
        while ( ! done ) {
          if ( mHolder != null ) {
            Canvas canvas = null;
            // do {
              canvas = mHolder.lockCanvas();
            //   if ( canvas == null ) {
            //     try { sleep( 100 ); } catch ( InterruptedException e ) { }
            //   }
            // } while ( canvas == null );
            drawOn( canvas );
            mHolder.unlockCanvasAndPost( canvas );
          } else {
            // Log.v("Penta", "Penta thread run: holder " + ( (mHolder==null)? "null" : "non-null") );
            try { sleep( 40 ); } catch ( InterruptedException e ) { }
          }
        }
        // Log.v("Penta", "Penta thread done");
      }

      public void doExit()
      {
        done = true;
        try {
          join();
        } catch ( InterruptedException e ) {
          // Log.v("Penta", "thread doExit interrupted join");
        }
      }
    }

    /** check if can place a piece on the board
     * @param k    index of the piece
     * @param x    x coord on the board
     * @param y    y coord on the board
     * @return true if the piece has been placed on the board
     */
    boolean placeOnBoard( int k, int x, int y )
    {
      PentaPiece pp = mPiece[k];
      int ii = ( k > 0 )? 5 : 4;
      for ( int i = 0; i<ii; ++i ) {
        int xx = x + pp.x[i];
        int yy = y + pp.y[i];
        if ( xx < 0 || xx >= 8 ) return false;
        if ( yy < 0 || yy >= 8 ) return false;
        if ( mBoard[yy*8+xx] >= 0 ) return false;
      }
      for ( int i = 0; i<ii; ++i ) {
        int xx = x + pp.x[i];
        int yy = y + pp.y[i];
        mBoard[yy*8+xx] = k;
      }
      pp.setUsed( true ); // , x, y );
      mIndex = -1;
      return true;
    }

    /** remove a piece from the board
     * The removed piece becomes the active one
     */
    boolean removeFromBoard( int x, int y )
    {
      int k = mBoard[y*8+x];
      if ( k < 0 ) return false;
      if ( mPiece[k].isUsed() != true ) {
        Log.e("Penta", "removing the unused piece " + k );
      }
      mPiece[k].setUsed( false );
      for ( int i=0; i<64; ++i ) if ( mBoard[i] == k ) mBoard[i] = -1;
      mIndex = k;
      return true;
    }

    int getBoardValue( int x, int y ) { return mBoard[y*8+x]; }
      

    // -----------------------------------------------------------------

    void makePieces()
    {
      mBoard = new int[mXTileCount * mYTileCount];
      mMiniBoard = new int[mXTileCount * mYTileCount];
      for (int i=0; i<64; ++i ) {
        mBoard[i] = -1;
        mMiniBoard[i] = -1;
      }
      mPiece = new PentaPiece[13];
      for (int k=0; k<13; ++k ) mPiece[k] = new PentaPiece( k );
      mIndex = -1;
    }

    void rotateLeft() { if ( mIndex >= 0 ) mPiece[mIndex].rotateLeft(); }
    void rotateRight() { if ( mIndex >= 0 ) mPiece[mIndex].rotateRight(); }
    void flipH() { if ( mIndex >= 0 ) mPiece[mIndex].flipH(); }
    void flipV() { if ( mIndex >= 0 ) mPiece[mIndex].flipV(); }
  
    int pad = 3;
    static int Minipad = 1;

    //  w    h  dpi  t mt dim dim2
    // 768 1184 320 80 20 12    11
    // 480  800 240 50 12  8     7
    // 240  320 120 25  6  4     3
    void setSizes( int ww, int hh, int dpi )
    {
      pad = ( ww < 300 )? 1 : (ww < 600)? 2 : 3;
      // mDim = ww / (12*5); 
      // mDim2 = mDim/2-1;
      // mTileSize = (80 * ww) / 768;
      // mMiniTileSize = mTileSize / 4;
      mDim = ( ww < 300 )? 4 : (ww < 600)? 8 : ww/(12*5);
      mDim2 = ( ww < 300 )? 4 : (ww < 600)? 7 : mDim - 2;


      mTileSize = ( ww < 300 )? 20 : (ww < 600)? 48 : (80 * ww)/768;
      mMiniTileSize = ( ww < 300 )? 6 : mTileSize/4;

      // Log.v( "Penta", "Set size " + ww + " " + hh + " dpi " + dpi + " Tile size " + mTileSize + " " + mMiniTileSize );
      onWindowResize( ww, hh );
    }
    
    void onWindowResize( int w, int h )
    {

      mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
      // mYOffset = ((h - (mTileSize * mYTileCount)) / 2) + mTileSize;
      mYOffset = h - mTileSize * mYTileCount - mDim2;

      mXOff2 = 3 * mDim;

      int extraYoff = ( w < 300 )? 0 : (w < 600)? 4 : 8;

      mYOff2 = mYOffset - 12 * mDim - 2 * extraYoff;
      mYOff3 = mYOffset - 6 * mDim - extraYoff;

      // mMiniXOffset = mXOff2 + 56 * mDim - 9 * mMiniTileSize;
      mMiniXOffset = w - 9 * mMiniTileSize;
      mMiniYOffset = mYOffset - 9 * mMiniTileSize;

      // Log.v("Penta", "Dim " + mDim + " off " + mXOff2 + " " + mYOff2 );
      // Log.v("Penta", "board off " + mXOffset + " " + mYOffset );
    }

    /**
     * Resets all tiles to -1 (empty)
     * 
     */
    public void clearTiles() 
    {
      for (int x = 0; x < mXTileCount; x++) {
        for (int y = 0; y < mYTileCount; y++) {
          setTile(-1, x, y);
        }
      }
    }

    /**
     * Used to indicate that a particular tile (set with loadTile and referenced
     * by an integer) should be drawn at the given x/y coordinates during the
     * next invalidate/draw cycle.
     * 
     * @param index color index
     * @param x
     * @param y
     */
    public void setTile(int index, int x, int y) 
    {
      mBoard[y*8+x] = index;
    }
     
    synchronized public void drawOn(Canvas canvas) 
    {
      if ( canvas == null ) return;
      // synchronized( mThread ) 
      {

        canvas.drawRect( mXOffset, mYOffset, mXOffset + 9 * mTileSize, mYOffset + 9 * mTileSize, mPaintBlack );

        int delta = mTileSize/2 - pad;
        for ( int j=0; j<8; ++j ) {
          int y0 = mYOffset + j * mTileSize + pad + delta;
          for ( int i=1; i<8; ++i ) {
            if ( mBoard[j*8+i] >= 0 && mBoard[j*8+i] == mBoard[j*8+i-1] ) {
              int x0 = mXOffset + i * mTileSize + pad + delta; // center of the square
              canvas.drawRect( x0-mTileSize, y0-delta, x0, y0+delta, mPaintGrey );
            }
          }
        }
        for ( int i=0; i<8; ++i ) {
          int x0 = mXOffset + i * mTileSize + pad + delta; // center of the square
          for ( int j=1; j<8; ++j ) {
            if ( mBoard[j*8+i] >= 0 && mBoard[j*8+i] == mBoard[j*8+i-8] ) {
              int y0 = mYOffset + j * mTileSize + pad + delta;
              canvas.drawRect( x0-delta, y0-mTileSize, x0+delta, y0, mPaintGrey );
            }
          }
        }
          
        for ( int k=0; k<13; ++k ) {
          int ii = ( k == 0 )? 4 : 5;
          PentaPiece pp = mPiece[k];
          Paint paint = pp.isUsed() ? mPaintGrey : mPaint[k];

          int xoff = mXOff2 + 6 * ((k<6)? k : k-6) * mDim;
          int yoff = ( (k < 6) ? mYOff2 : mYOff3 );

          int x = xoff + pp.x[0] * mDim;
          int y = yoff + pp.y[0] * mDim;
          
          canvas.drawRect( x-2*mDim, y-2*mDim, x + 3*mDim, y + 3*mDim, ( k == mIndex )? mPaint[13] : mPaintBlack );
          canvas.drawRect( x, y, x+mDim, y+mDim, paint );

          canvas.drawRect( x+mDim2, y+mDim2, x+mDim2+3, y+mDim2 + 3, mPaintBlack );
          for ( int i=1; i<ii; ++i ) {
            x = xoff + pp.x[i] * mDim;
            y = yoff + pp.y[i] * mDim;
            canvas.drawRect( x, y, x+mDim, y+mDim, paint );
          }
        }

        for (int x = 0; x < mXTileCount; ++x ) {
          for (int y = 0; y < mYTileCount; ++ y ) {
            if (mBoard[y*8+x] >= 0) {
              canvas.drawRect( mXOffset + x * mTileSize+pad, mYOffset + y * mTileSize+pad,
                               mXOffset + (x+1) * mTileSize-pad, mYOffset + (y+1) * mTileSize-pad,
                               mPaint[ mBoard[y*8+x] ] );
            } else {
              canvas.drawRect( mXOffset + x * mTileSize+pad, mYOffset + y * mTileSize+pad,
                               mXOffset + (x+1) * mTileSize-pad, mYOffset + (y+1) * mTileSize-pad,
                    	     mPaint[ 13 ] );
            }
          }
        }

        for (int x = 0; x < mXTileCount; ++x ) {
          for (int y = 0; y < mYTileCount; ++ y ) {
            if (mMiniBoard[y*8+x] >= 0) {
              canvas.drawRect( mMiniXOffset + x * mMiniTileSize+Minipad, mMiniYOffset + y * mMiniTileSize+Minipad,
                               mMiniXOffset + (x+1) * mMiniTileSize-Minipad, mMiniYOffset + (y+1) * mMiniTileSize-Minipad,
                               mPaint[ mMiniBoard[y*8+x] ] );
            } else {
              canvas.drawRect( mMiniXOffset + x * mMiniTileSize+Minipad, mMiniYOffset + y * mMiniTileSize+Minipad,
                               mMiniXOffset + (x+1) * mMiniTileSize-Minipad, mMiniYOffset + (y+1) * mMiniTileSize-Minipad,
                    	     mPaint[ 13 ] );
            }
          }
        }
      }
    }

    int getPiece( int x, int y )
    {
      int ret = -1;
      for ( int k=0; k<13; ++k ) {
        int xoff = mXOff2 + 6 * ((k<6)? k : k-6) * mDim;
        int yoff = (k < 6 )? mYOff2 : mYOff3;
        if ( xoff - 2*mDim <= x && xoff + 3*mDim >= x && yoff-2*mDim <= y && yoff+3*mDim >= y ) {
          if ( mIndex != k && ! mPiece[k].isUsed() ) {
            mIndex = k;
          } else { 
            mIndex = -1;
          }
          ret = k;
        }
      }
      return ret;
    }

    int getBoard( int xx, int yy )
    {
      for (int x = 0; x < mXTileCount; ++x ) {
        for (int y = 0; y < mYTileCount; ++ y ) {
          if ( mXOffset + x * mTileSize+pad <= xx && mYOffset + y * mTileSize+pad <= yy &&
               mXOffset + (x+1) * mTileSize-pad >= xx && mYOffset + (y+1) * mTileSize-pad >= yy ) {
            return  y*8+x;
          }
        }
      }
      return -1;
    }


    void makePaints()
    {
      mPaintBlack = new Paint();
      mPaintBlack.setDither(true);
      mPaintBlack.setColor( 0xff000000 );
      mPaintBlack.setStyle(Paint.Style.FILL);
      mPaintBlack.setStrokeJoin(Paint.Join.ROUND);
      mPaintBlack.setStrokeCap(Paint.Cap.ROUND);
      mPaintBlack.setStrokeWidth( 1 );

      mPaintGrey = new Paint();
      mPaintGrey.setDither(true);
      mPaintGrey.setColor( 0xffcccccc );
      mPaintGrey.setStyle(Paint.Style.FILL);
      mPaintGrey.setStrokeJoin(Paint.Join.ROUND);
      mPaintGrey.setStrokeCap(Paint.Cap.ROUND);
      mPaintGrey.setStrokeWidth( 1 );

      mPaint = new Paint[14];

      for ( int k=0; k<13; ++k ) {
        mPaint[k] = new Paint();
        mPaint[k].setDither(true);
        mPaint[k].setColor( PentaPiece.colors[k] );
        mPaint[k].setStyle(Paint.Style.FILL);
        mPaint[k].setStrokeJoin(Paint.Join.ROUND);
        mPaint[k].setStrokeCap(Paint.Cap.ROUND);
        mPaint[k].setStrokeWidth( 1 );
      }

      mPaint[13] = new Paint();
      mPaint[13].setDither(true);
      mPaint[13].setColor( 0xff666666 );
      mPaint[13].setStyle(Paint.Style.FILL);
      mPaint[13].setStrokeJoin(Paint.Join.ROUND);
      mPaint[13].setStrokeCap(Paint.Cap.ROUND);
      mPaint[13].setStrokeWidth( 1 );

   }
}

