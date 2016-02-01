/** @file PentaHelp.java
 *
 * @author marco corvi
 * @date dec 2014
 *
 * @brief help
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 */
package com.pentomino;

import android.content.Context;
// import android.content.Intent;
// import android.content.ActivityNotFoundException;

import android.app.Dialog;
import android.view.View;
import android.webkit.WebView;

class PentaHelp extends Dialog
{
  private Context mContext;

  PentaHelp( Context context )
  {
    super( context );
    mContext = context;
    setContentView(R.layout.help);
    setTitle( context.getResources().getString(R.string.app_name) );
    WebView wv = (WebView) findViewById( R.id.text );
    String html = context.getResources().getString( R.string.help ).replaceAll("BR", "<br><p>");
    wv.loadData( "<html><body>" + html + "</body></html>", "text/html", null );
  }
  
}
