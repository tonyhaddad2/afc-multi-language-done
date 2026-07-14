package com.belahza.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public final class Dialer {
    private Dialer() {}
    public static void call(Context c, String number) { try { c.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)))); } catch(Exception e){ Toast.makeText(c,"Could not open dialer",Toast.LENGTH_SHORT).show(); } }
    public static void share(Context c, String text) { Intent send=new Intent(Intent.ACTION_SEND); send.setType("text/plain"); send.putExtra(Intent.EXTRA_TEXT,text); c.startActivity(Intent.createChooser(send,"Share emergency alert")); }
    public static void copy(Context c, String label, String text) { ClipboardManager cm=(ClipboardManager)c.getSystemService(Context.CLIPBOARD_SERVICE); if(cm!=null) cm.setPrimaryClip(ClipData.newPlainText(label,text)); Toast.makeText(c,"Copied",Toast.LENGTH_SHORT).show(); }
}
