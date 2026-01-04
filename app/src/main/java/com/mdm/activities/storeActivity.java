package com.mdm.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import com.emanuelef.remote_capture.R;
import com.emanuelef.remote_capture.Utils;
import com.emanuelef.remote_capture.activities.PasswordManager;
import com.emanuelef.remote_capture.activities.picker;

public class storeActivity extends Activity {

    private ListView listView;
    private TextView emptyView;
    private StoreItemAdapter adapter;
    public static ItemsManager itemsManager;
    public static ConfigManager configManager;

    private ImportExportManager importExportManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setTheme(this);
        setTitle("חנות");
        setContentView(R.layout.store_main);
        
        //for now to avoid install user files placed in the folder with the good name...
        utils.deleteTempDir(getExternalFilesDir(""),false);
        configManager = new ConfigManager(this);
        itemsManager = new ItemsManager(this, configManager);
        importExportManager = new ImportExportManager(this, itemsManager);
        
        Dialogs.loadPriorityFromPrefs(this,true);
        
        listView = (ListView) findViewById(R.id.main_list_view);
        emptyView = (TextView) findViewById(R.id.empty_view);
        
        adapter = new StoreItemAdapter(this, itemsManager.getAllItemsvisible(), itemsManager);
        listView.setAdapter(adapter);

        listView.setEmptyView(emptyView);
    }
    public static String pickedfilepath="";
    public static String pickeddirpath="";
    protected void onResume() {
        super.onResume();
        if(pickedfilepath!=null&&!pickedfilepath.equals("")){
            String uri=pickedfilepath;
            pickedfilepath="";
            Dialogs.showImportModeDialog(this, itemsManager, adapter, uri, importExportManager);
        }else if(pickeddirpath!=null&&!pickeddirpath.equals("")){
            String uri=pickeddirpath;
            pickeddirpath="";
            Dialogs.showExportDialog(storeActivity.this, importExportManager,uri);
        }
        refreshData(false);
    }
    public static ProgressDialog progressDialog;
    boolean refreshinfocon=false;
    private synchronized void refreshData(final boolean refreshinfo) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_button_background);
        progressDialog.setMessage("טוען עדכונים לאפליקציות...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(refreshinfocon)return;
        final ConnectivityManager cm=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean connected=false;
        for(Network ne:cm.getAllNetworks()){
            NetworkInfo ni=cm.getNetworkInfo(ne);
            if(ni.isConnected()&&ni.getType()!=cm.TYPE_VPN){
                connected=true;
                break;
            }

        }
        if(connected){
            NetworkInfo ni=cm.getNetworkInfo(cm.getActiveNetwork());
            //LogUtil.logToFile("def==ln="+cm.getAllNetworks().length+"ei="+ni.getExtraInfo()+"re="+ni.getReason()+"st="+ni.getSubtypeName()+"t="+ni.getTypeName()+"ds="+ni.getDetailedState().name()+"s="+ni.getState().name());
            
            for(Network ne:cm.getAllNetworks()){
                 ni=cm.getNetworkInfo(ne);
                //LogUtil.logToFile("ln="+cm.getAllNetworks().length+"ei="+ni.getExtraInfo()+"re="+ni.getReason()+"st="+ni.getSubtypeName()+"t="+ni.getTypeName()+"ds="+ni.getDetailedState().name()+"s="+ni.getState().name());
                NetworkCapabilities nc=cm.getNetworkCapabilities(ne);
                //LogUtil.logToFile("cvpn="+nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)+"tvpn="+nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN)+"up="+nc.getLinkUpstreamBandwidthKbps()+"do="+nc.getLinkDownstreamBandwidthKbps()+(Build.VERSION.SDK_INT>=29?("stren="+nc.getSignalStrength()):""));
            }
        if(refreshinfo){
            refreshinfocon=true;
        Toast.makeText(this, "מתחיל בדיקת עדכונים...", Toast.LENGTH_SHORT).show();
        }}else if(refreshinfo){
            Toast.makeText(this, "התחבר לאינטרנט ונסה שוב...", Toast.LENGTH_SHORT).show();
        }
        itemsManager.refreshAllItems(refreshinfocon, configManager.loadConfig(), new ItemsManager.RefreshCompleteListener() {
                public void onComplete() {
                    // עדכון ה-UI על ה-Main Thread
                    runOnUiThread(new Runnable() {
                            public void run() {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                adapter.updateData(itemsManager.getAllItemsvisible());
                                
                                if(refreshinfocon){
                                    refreshinfocon=false;
                                Toast.makeText(storeActivity.this, "בדיקת עדכונים הסתיימה.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu); 
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_launch_settings) {
           // startActivity(new Intent(MainActivity.this, StoreSettingsActivity.class));
            Intent settingsIntent = new Intent(this, storeSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.menu_add_item_pn) {
            PasswordManager.requestPasswordAndSave(new Runnable(){
                    @Override
                    public void run() {
            Dialogs.showAddItemDialog(storeActivity.this, com.mdm.activities.StoreItem.ItemSourceType.MANUAL, itemsManager, adapter);
            
             } }, this);
            return true;
        } else if (id == R.id.menu_add_item_link) {
            PasswordManager.requestPasswordAndSave(new Runnable(){
                    @Override
                    public void run() {
                        Dialogs.showAddItemDialog(storeActivity.this, com.mdm.activities.StoreItem.ItemSourceType.CUSTOM_LINK, itemsManager, adapter);
            //Dialogs.showAddAppDialog(this, itemsManager, adapter);
            } }, this);
            return true;
        } else if (id == R.id.menu_import_json) {
            PasswordManager.requestPasswordAndSave(new Runnable(){
                    @Override
                    public void run() {
            // פעולה זו תפתח דיאלוג לבחירת סוג ייבוא (קובץ / קישור)
                        Dialogs.showImportSelectionDialog(storeActivity.this, itemsManager, adapter, importExportManager); 
            } }, this);
            return true;
          
        } else if (id == R.id.menu_export_json) {
            PasswordManager.requestPasswordAndSave(new Runnable(){
                    @Override
                    public void run() {
                        startActivity(new Intent(storeActivity.this,picker.class).putExtra("from","storeseldir"));
            } }, this);
            return true;
        } else if (id == R.id.menu_refresh) {
            refreshData(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
   /*
    private static final int PICK_FILE_REQUEST_CODE = 42;
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                
                Dialogs.showImportModeDialog(this, itemsManager, adapter, uri, importExportManager);
            }
        }
    }*/
}
