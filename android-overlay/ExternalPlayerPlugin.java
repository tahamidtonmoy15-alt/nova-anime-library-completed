package com.hda.anime;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;

import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;

@CapacitorPlugin(name = "ExternalPlayer")
public class ExternalPlayerPlugin extends Plugin {

    private File dir() {
        File d = new File(getContext().getCacheDir(), "videos");
        d.mkdirs();
        return d;
    }

    private String safe(String n) {
        return n.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    @PluginMethod
    public void check(PluginCall call) {
        File f = new File(dir(), safe(call.getData().optString("name")));
        long size = call.getData().optLong("size", -1);
        JSObject r = new JSObject();
        r.put("exists", f.exists() && f.length() == size);
        call.resolve(r);
    }

    @PluginMethod
    public void write(PluginCall call) {
        try {
            File f = new File(dir(), safe(call.getData().optString("name")));
            boolean append = call.getData().optBoolean("append", false);
            byte[] b = Base64.decode(call.getData().optString("data"), Base64.DEFAULT);
            try (FileOutputStream o = new FileOutputStream(f, append)) {
                o.write(b);
            }
            call.resolve();
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }

    // Opens Android's "Open with" chooser (MX Player, VLC, Mi Video, etc.).
    @PluginMethod
    public void open(PluginCall call) {
        try {
            File f = new File(dir(), safe(call.getData().optString("name")));
            Uri u = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", f);
            String mime = call.getData().optString("mime", "video/*");
            if (mime.isEmpty()) mime = "video/*";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(u, mime);
            i.putExtra("title", call.getData().optString("title"));
            i.setClipData(ClipData.newRawUri("", u));
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().startActivity(i);
            call.resolve();
        } catch (ActivityNotFoundException e) {
            call.reject("No compatible video player was found.");
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }

    @PluginMethod
    public void share(PluginCall call) {
        try {
            File f = new File(dir(), safe(call.getData().optString("name")));
            Uri u = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", f);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType(call.getData().optString("mime", "*/*"));
            i.putExtra(Intent.EXTRA_STREAM, u);
            i.setClipData(ClipData.newRawUri("", u));
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getActivity().startActivity(Intent.createChooser(i, "Save / Share"));
            call.resolve();
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }
}
