/** @file PentaPiece.java
 *
 * @author marco corvi
 * @date dec 2014
 *
 * @brief piece
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.pentomino;


class PentaPiece
{
  int index;
  int color;
  int x[];
  int y[];
  private boolean used; // whether the piece is on the board

  static int colors[] = {
    0xffff0000, 0xffff3300, 0xffff0033,
    0xff0000ff, 0xff3333cc, 0xff0066ff,
    0xffffff00, 0xffcccc33, 0xffffff66,
    0xff00ff00, 0xff33cc33, 0xff00ff66, 0xff66ff00 };

  boolean isUsed() { return used; }

  // k piece index
  PentaPiece( int ii )
  {
    index = ii;
    color = colors[index];
    used = false;
    x = new int[5]; // x1 = new int[5]; x2 = new int[5];
    y = new int[5]; // y1 = new int[5]; y2 = new int[5];

    for ( int k=0; k<5; ++k ) {
      x[k] = y[k] = 0;
    }
    switch ( index ) {
      case 0:
                             // [0] [1]
        x[1] = 1; y[1] = 0;  // [3] [2]
        x[2] = 1; y[2] = 1;
        x[3] = 0; y[3] = 1;
        break;
      case 1:
                             // [3] [0] [1]
        x[1] = 1;  y[1] = 0; // [4]     [2]
        x[2] = 1;  y[2] = 1;
        x[3] = -1; y[3] = 0;
        x[4] = -1; y[4] = 1;
        break;
      case 2:
                            // [0] [1] [2]
        x[1] = 1; y[1] = 0; // [3]
        x[2] = 2; y[2] = 0; // [4]
        x[3] = 0; y[3] = 1;
        x[4] = 0; y[4] = 2;
        break;
      case 3:
                             // [2] [0] [1]
        x[1] = 1;  y[1] = 0; //     [3]
        x[2] = -1; y[2] = 0; //     [4]
        x[3] = 0; y[3] = 1;
        x[4] = 0; y[4] = 2;
        break;
      case 4:
                            // [3] [0] [1] [2]
        x[1] = 1; y[1] = 0; //     [4]
        x[2] = 2; y[2] = 0;
        x[3] = -1; y[3] = 0;
        x[4] = 0; y[4] = 1;
        break;
      case 5:               //     [3]
                            // [2] [0] [1] 
        x[1] = 1; y[1] = 0; //     [4]
        x[2] = -1; y[2] = 0;
        x[3] = 0; y[3] = -1;
        x[4] = 0; y[4] = 1;
        break;
      case 6:
                            // [2] [0] [1] 
        x[1] = 1; y[1] = 0; // [3] [4]
        x[2] = -1; y[2] = 0;
        x[3] = -1; y[3] = 1;
        x[4] = 0; y[4] = 1;
        break;
      case 7:
                            //     [0] [1] [2]
        x[1] = 1; y[1] = 0; // [3] [4]
        x[2] = 2; y[2] = 0;
        x[3] = -1; y[3] = 1;
        x[4] = 0; y[4] = 1;
        break;
      case 8:                //     [1] [2]
                             //     [0]
        x[1] = 0; y[1] = -1; // [3] [4]
        x[2] = 1; y[2] = -1;
        x[3] = 0; y[3] = 1;
        x[4] = -1; y[4] = 1;
        break;
      case 9:                //     [1] [2]
                             // [3] [0]
        x[1] = 0; y[1] = -1; // [4]
        x[2] = 1; y[2] = -1;
        x[3] = -1; y[3] = 0;
        x[4] = -1; y[4] = 1;
        break;
      case 10:               //     [1] [2]
                             // [3] [0]
        x[1] = 0; y[1] = -1; //     [4]
        x[2] = 1; y[2] = -1;
        x[3] = -1; y[3] = 0;
        x[4] = 0; y[4] = 1;
        break;
      case 11:
                            // [3] [0] [1] [2]
        x[1] = 1; y[1] = 0; // [4]
        x[2] = 2; y[2] = 0;
        x[3] = -1; y[3] = 0;
        x[4] = -1; y[4] = 1;
        break;
      case 12:
                            // [4] [3] [0] [1] [2]
        x[1] = 1; y[1] = 0; 
        x[2] = 2; y[2] = 0;
        x[3] = -1; y[3] = 0;
        x[4] = -2; y[4] = 0;
        break;
    }
  }

  void rotateLeft()
  {
    if ( used ) return;
    for ( int k=0; k<5; ++k ) {
      int t = x[k];
      x[k] = y[k];
      y[k] = -t;
    }
  }

  void rotateRight()
  {
    if ( used ) return;
    for ( int k=0; k<5; ++k ) {
      int t = x[k];
      x[k] = -y[k];
      y[k] = t;
    }
  }

  void flipH()
  {
    if ( used ) return;
    for ( int k=0; k<5; ++k ) {
      x[k] = -x[k];
    }
  }

  void flipV()
  {
    if ( used ) return;
    for ( int k=0; k<5; ++k ) {
      y[k] = -y[k];
    }
  }

  void setUsed( boolean u ) { used = u; }

}
