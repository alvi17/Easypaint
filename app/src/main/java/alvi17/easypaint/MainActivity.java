package alvi17.easypaint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import adapters.Custom_Drawer_Adapter;
import adapters.DrawerItem;
import views.DrawingView;


public class MainActivity extends AppCompatActivity{
    private String selectedImagePath;
    private DrawingView paintView;
    private AtomicBoolean dialogIsDisplayed=new AtomicBoolean();
    RadioGroup fontColorGroup,downColorGroup;
    private Dialog currentDialog;
    int imagesave=0;

    SeekBar fontSeekBar,fontDownSeekBar;
    ImageView img;
    TextView edit1;
    EditText tText,dText;
    TextView edit;
    int i=0;
    int x=0;
    int y=0;
    int color;
    boolean toptextAdded,downtextAdded;
    private static final int SELECT_PICTURE = 1;
    CallbackManager callbackManager;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    Custom_Drawer_Adapter adapter;
    List<DrawerItem> dataList;
    FrameLayout fm;
    private InterstitialAd interstitial;
    boolean isadLoaded=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm=(FrameLayout)findViewById(R.id.frame);
        View view=getLayoutInflater().inflate(R.layout.another_layout, fm,false);
        fm.addView(view);
        toptextAdded=false;
        downtextAdded=false;
        edit=new TextView(this);
        edit1=new TextView(this);
        imagesave=0;
        color= Color.WHITE;


        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        dataList = new ArrayList<DrawerItem>();

        addDrawerItems();
        setupDrawer();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


//        //facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "alvi17.easypaint",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
        interstitial=new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-6508526601344465/4046023638");
        AdRequest aRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        interstitial.loadAd(aRequest);

        interstitial.setAdListener(
                new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        isadLoaded=true;

                    }
                }
        );

    }



    private void addDrawerItems() {
        dataList.add(new DrawerItem(" Main Options"));
        // adding a header to the list
        dataList.add(new DrawerItem(" Screen Portrait", R.drawable.ic_hardware_smartphone));
        dataList.add(new DrawerItem(" Screen LandScape", R.drawable.ic_hardware_tablet));
        dataList.add(new DrawerItem(" Set BackGround", R.drawable.android_back));
        dataList.add(new DrawerItem(" Load Prev Image", R.drawable.gallery));
        dataList.add(new DrawerItem(" Clear",R.drawable.clear));

        dataList.add(new DrawerItem(" Other Options")); // adding a header to the list
        dataList.add(new DrawerItem(" About", R.drawable.ic_action_about));
        dataList.add(new DrawerItem(" Help", R.drawable.ic_action_help));
        dataList.add(new DrawerItem(" Exit", R.drawable.clear));
        adapter = new Custom_Drawer_Adapter(this, R.layout.custom_drawer_item,
                dataList);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    private void showColorDialog()
    {
        currentDialog=new Dialog(this);
        currentDialog.setContentView(R.layout.color_dialog);
        currentDialog.setTitle("Choose Color");
        currentDialog.setCancelable(true);
        final SeekBar alphaSeekbar= (SeekBar)currentDialog.findViewById(R.id.alphaseekBar);
        alphaSeekbar.setProgress(255);
        final SeekBar redSeekbar=(SeekBar)currentDialog.findViewById(R.id.redseekBar);
        redSeekbar.setProgress(0);
        final SeekBar greenSeekbar=(SeekBar)currentDialog.findViewById(R.id.greenseekBar);
        greenSeekbar.setProgress(0);
        final SeekBar blueSeekbar=(SeekBar)currentDialog.findViewById(R.id.blueseekBar);
        blueSeekbar.setProgress(0);
        alphaSeekbar.setOnSeekBarChangeListener(colorSeekbarChanged);
        redSeekbar.setOnSeekBarChangeListener(colorSeekbarChanged);
        greenSeekbar.setOnSeekBarChangeListener(colorSeekbarChanged);
        blueSeekbar.setOnSeekBarChangeListener(colorSeekbarChanged);
        Button setColorButton=(Button)currentDialog.findViewById(R.id.setColorbutton);
        setColorButton.setOnClickListener(setColotButtonListener);
        dialogIsDisplayed.set(true);
        currentDialog.show();
    }
    private Bitmap takeScreenshot(){
        View rootView = findViewById(R.id.frame).getRootView();
        rootView.setDrawingCacheEnabled(true);
        Rect rectangle= new Rect();
        Window window= getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight= rectangle.top+3;

        Bitmap bmp1 = Bitmap.createBitmap(rootView.getDrawingCache(), 0,
                statusBarHeight,
                rootView.getDrawingCache().getWidth(),
                rootView.getDrawingCache().getHeight()- statusBarHeight);
        Bitmap bmp=Bitmap.createBitmap(bmp1, 0, 2*statusBarHeight, bmp1.getWidth(), bmp1.getHeight()-2*statusBarHeight);
        return bmp;
    }
    public void SelectItem(int possition) {
        paintView=(DrawingView)findViewById(R.id.paintView);
        switch (possition){
            case 1:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 2:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 3:

                showBackgroundColor();
                break;

            case 4:

                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),SELECT_PICTURE);
                fm.removeView(edit);
                fm.removeView(edit1);
                toptextAdded=false;
                downtextAdded=false;
                x=0;
                y=0;

                break;

            case 5:

                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Conformation");
                builder.setMessage("Clear Drawing?");
                builder.setPositiveButton("Clear",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        fm.removeView(edit);
                        toptextAdded=false;
                        downtextAdded=false;
                        fm.removeView(edit1);
                        paintView.clear();
                        paintView.set_BackgroundColor(color);
                        x=0;
                        y=0;
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;
            case 7:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=alvi17.easypaint"));
                startActivity(browserIntent);
                break;

            case 8:
                final Dialog dialog=new Dialog(this);
                dialog.setContentView(R.layout.help_view);
                Button ok=(Button)dialog.findViewById(R.id.helpok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                dialog.setTitle("Help -> Options");
                dialog.setCanceledOnTouchOutside(true);

                dialog.show();

                break;
            case 9:

                AlertDialog.Builder builder3=new AlertDialog.Builder(this);
                builder3.setCancelable(true);
                builder3.setTitle("Conformation").setIcon(R.drawable.clear);
                builder3.setMessage("Are you sure to Exit?");
                builder3.setPositiveButton("Exit",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                });
                builder3.setNegativeButton("Cancel", null);
                builder3.show();

                break;
        }
        mDrawerList.setItemChecked(possition, true);
        setTitle(dataList.get(possition).getItemName());
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void showBackgroundColor()
    {
        currentDialog=new Dialog(this);
        currentDialog.setContentView(R.layout.background_color);
        currentDialog.setTitle("Background Color");
        currentDialog.setCancelable(true);
//        final SeekBar backalphaSeekbar=(SeekBar)currentDialog.findViewById(R.id.backalphaseekBar);
//        backalphaSeekbar.setProgress(255);
        final SeekBar backredSeekbar=(SeekBar)currentDialog.findViewById(R.id.backredseekBar);
        backredSeekbar.setProgress(255);
        final SeekBar backgreenSeekbar=(SeekBar)currentDialog.findViewById(R.id.backgreenseekBar);
        backgreenSeekbar.setProgress(255);
        final SeekBar backblueSeekbar=(SeekBar)currentDialog.findViewById(R.id.backblueseekBar);
        backblueSeekbar.setProgress(255);
     //   backalphaSeekbar.setOnSeekBarChangeListener(backcolorSeekbarChanged);
        backredSeekbar.setOnSeekBarChangeListener(backcolorSeekbarChanged);
        backgreenSeekbar.setOnSeekBarChangeListener(backcolorSeekbarChanged);
        backblueSeekbar.setOnSeekBarChangeListener(backcolorSeekbarChanged);
        Button setbackColorButton=(Button)currentDialog.findViewById(R.id.setbackColorbutton);
        setbackColorButton.setOnClickListener(setbackColotButtonListener);
        dialogIsDisplayed.set(true);
        currentDialog.show();
    }
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Exit").setIcon(R.drawable.clear);
        alert.setMessage("Do you want to Exit?");
        alert.setPositiveButton("Exit", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isadLoaded)
                {
                    interstitial.show();
                }
                finish();
                //
            }
        });
        alert.setNegativeButton("Cancel", null);
        AlertDialog dialog=alert.create();
        dialog.show();
      //  super.onBackPressed();

    }
    private SeekBar.OnSeekBarChangeListener backcolorSeekbarChanged=new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
        //    SeekBar alphaSeekBar=(SeekBar)currentDialog.findViewById(R.id.backalphaseekBar);

            SeekBar redSeekBar=(SeekBar)currentDialog.findViewById(R.id.backredseekBar);
            SeekBar greenSeekBar=(SeekBar)currentDialog.findViewById(R.id.backgreenseekBar);
            SeekBar blueSeekBar=(SeekBar)currentDialog.findViewById(R.id.backblueseekBar);
            View colorView=(View)currentDialog.findViewById(R.id.backcolorView);
            colorView.setBackgroundColor(Color.argb(255, redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress()));


        }
    };


    private View.OnClickListener setbackColotButtonListener=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //SeekBar alphaSeekBar=(SeekBar)currentDialog.findViewById(R.id.backalphaseekBar);
            SeekBar redSeekBar=(SeekBar)currentDialog.findViewById(R.id.backredseekBar);
            SeekBar greenSeekBar=(SeekBar)currentDialog.findViewById(R.id.backgreenseekBar);
            SeekBar blueSeekBar=(SeekBar)currentDialog.findViewById(R.id.backblueseekBar);
            color=Color.argb(255, redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());
            paintView.set_BackgroundColor(color);
            if(toptextAdded==true)
            {
                fm.removeView(edit);
                toptextAdded=false;
            }
            if(downtextAdded==true)
            {
                fm.removeView(edit1);
                downtextAdded=false;
            }


            dialogIsDisplayed.set(false);
            currentDialog.dismiss();
            currentDialog=null;
        }
    };
    private SeekBar.OnSeekBarChangeListener colorSeekbarChanged=new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            SeekBar alphaSeekBar=(SeekBar)currentDialog.findViewById(R.id.alphaseekBar);

            SeekBar redSeekBar=(SeekBar)currentDialog.findViewById(R.id.redseekBar);
            SeekBar greenSeekBar=(SeekBar)currentDialog.findViewById(R.id.greenseekBar);
            SeekBar blueSeekBar=(SeekBar)currentDialog.findViewById(R.id.blueseekBar);
            View colorView=(View)currentDialog.findViewById(R.id.colorView);
            colorView.setBackgroundColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress()));

        }
    };
    private View.OnClickListener setColotButtonListener=new View.OnClickListener()
    {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            SeekBar alphaSeekBar=(SeekBar)currentDialog.findViewById(R.id.alphaseekBar);
            SeekBar redSeekBar=(SeekBar)currentDialog.findViewById(R.id.redseekBar);
            SeekBar greenSeekBar=(SeekBar)currentDialog.findViewById(R.id.greenseekBar);
            SeekBar blueSeekBar=(SeekBar)currentDialog.findViewById(R.id.blueseekBar);
            paintView.setDrawingColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress()));
            dialogIsDisplayed.set(false);
            currentDialog.dismiss();
            currentDialog=null;
        }

    };
    private void showTopTextDialog(){
        currentDialog=new Dialog(this);
        currentDialog.setContentView(R.layout.top_test_layout);
        currentDialog.setTitle("Top Text");
        tText=(EditText)currentDialog.findViewById(R.id.topText);
        SeekBar fontSeekbar=(SeekBar)currentDialog.findViewById(R.id.fontseekBar);
        fontSeekbar.setOnSeekBarChangeListener(fontSeekBarListener);
        fontSeekbar.setProgress(16);
        fontColorGroup=(RadioGroup)currentDialog.findViewById(R.id.radioGroup);
        fontColorGroup.setOnCheckedChangeListener(topChangeListener);
        Button settopText=(Button)currentDialog.findViewById(R.id.submitTop);
        settopText.setOnClickListener(setTopTextListener);
        dialogIsDisplayed.set(true);
        currentDialog.show();

    }
    private RadioGroup.OnCheckedChangeListener topChangeListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int selected=group.getCheckedRadioButtonId();
            RadioButton radio1=(RadioButton)currentDialog.findViewById(selected);
            String textColor=radio1.getText().toString();
            TextView dtext=(TextView)currentDialog.findViewById(R.id.topTextView);
            if(textColor.equals("Black"))
            {
                dtext.setTextColor(Color.BLACK);
            }
            else if(textColor.equals("White"))
            {
                dtext.setTextColor(Color.WHITE);
            }
            else if(textColor.equals("Red"))
            {
                dtext.setTextColor(Color.RED);
            }

        }


    };
    private void showDownTextDialog(){
        currentDialog=new Dialog(this);
        currentDialog.setContentView(R.layout.down_text_layout);
        currentDialog.setTitle("Bottom Text");
        dText=(EditText)currentDialog.findViewById(R.id.downText);
        SeekBar fontDownSeekbar=(SeekBar)currentDialog.findViewById(R.id.fontdownseekBar);

        fontDownSeekbar.setOnSeekBarChangeListener(fontdownSeekBarListener);
        fontDownSeekbar.setProgress(16);
        downColorGroup=(RadioGroup)currentDialog.findViewById(R.id.downradioGroup);
        downColorGroup.setOnCheckedChangeListener(downColorChangedListener);
        Button setdownText=(Button)currentDialog.findViewById(R.id.submitdown);
        setdownText.setOnClickListener(setDownTextListener);
        dialogIsDisplayed.set(true);
        currentDialog.show();
    }
    private RadioGroup.OnCheckedChangeListener downColorChangedListener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int selected=group.getCheckedRadioButtonId();
            RadioButton radio1=(RadioButton)currentDialog.findViewById(selected);
            String textColor=radio1.getText().toString();
            TextView dtext=(TextView)currentDialog.findViewById(R.id.downTextView);
            if(textColor.equals("Black"))
            {
                dtext.setTextColor(Color.BLACK);
            }
            else if(textColor.equals("White"))
            {
                dtext.setTextColor(Color.WHITE);
            }
            else if(textColor.equals("Red"))
            {
                dtext.setTextColor(Color.RED);
            }

        }


    };
    private SeekBar.OnSeekBarChangeListener fontdownSeekBarListener=new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            fontDownSeekBar=(SeekBar)currentDialog.findViewById(R.id.fontdownseekBar);
            TextView bottomText=(TextView)currentDialog.findViewById(R.id.downTextView);
            bottomText.setTextSize(fontDownSeekBar.getProgress());

        }
    };
    private SeekBar.OnSeekBarChangeListener fontSeekBarListener=new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            fontSeekBar=(SeekBar)currentDialog.findViewById(R.id.fontseekBar);
            TextView topText=(TextView)currentDialog.findViewById(R.id.topTextView);
            topText.setTextSize(fontSeekBar.getProgress());

        }
    };

    private void showLineWidthDialog(){
        currentDialog=new Dialog(this);
        currentDialog.setContentView(R.layout.width_dialog);
        currentDialog.setTitle("Set Line Width");
        currentDialog.setCancelable(true);
        SeekBar widthSeekbar=(SeekBar)currentDialog.findViewById(R.id.widthseekBar);
        widthSeekbar.setOnSeekBarChangeListener(widthSeekbarListener);
        widthSeekbar.setProgress(paintView.getLineWidth());
        Button setWidthButton=(Button)currentDialog.findViewById(R.id.widthbutton);
        setWidthButton.setOnClickListener(setLineWidthListener);
        dialogIsDisplayed.set(true);
        currentDialog.show();
    }
    private SeekBar.OnSeekBarChangeListener widthSeekbarListener=new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap=Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub

            ImageView widthImageView=(ImageView)currentDialog.findViewById(R.id.WidthimageView);
            Paint p=new Paint();
            p.setColor(paintView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30, 50, 370, 50, p);
            widthImageView.setImageBitmap(bitmap);
        }
    };
    private View.OnClickListener setDownTextListener=new View.OnClickListener() {
        //RadioGroup fontColorGroup=(RadioGroup)currentDialog.findViewById(R.id.radioGroup);
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int selected=downColorGroup.getCheckedRadioButtonId();
            RadioButton radio1=(RadioButton)currentDialog.findViewById(selected);
            String textColor=radio1.getText().toString();
            currentDialog.dismiss();
            dialogIsDisplayed.set(false);

            //edit1.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
            edit1.setText(dText.getText()+"\n");

            // llp.setMargins(left, top, right, bottom);
            if(downtextAdded==false){
                fm.addView(edit1);
                downtextAdded=true;
                y++;
            }

            if(textColor.equals("Black")){
                edit1.setTextColor(Color.BLACK);
            }
            else if(textColor.equals("Red"))
            {
                edit1.setTextColor(Color.RED);
            }
            else
            {
                edit1.setTextColor(Color.WHITE);
            }
            edit1.setTextSize(fontDownSeekBar.getProgress());


        }
    };
    private View.OnClickListener setTopTextListener=new View.OnClickListener() {
        //RadioGroup fontColorGroup=(RadioGroup)currentDialog.findViewById(R.id.radioGroup);
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int selected=fontColorGroup.getCheckedRadioButtonId();
            RadioButton radio1=(RadioButton)currentDialog.findViewById(selected);
            String textColor=radio1.getText().toString();
            currentDialog.dismiss();
            dialogIsDisplayed.set(false);

            edit.setText(tText.getText());

            if(toptextAdded==false){
                fm.addView(edit);
                toptextAdded=true;
                x++;
            }

            if(textColor.equals("Black")){
                edit.setTextColor(Color.BLACK);
            }
            else if(textColor.equals("Red"))
            {
                edit.setTextColor(Color.RED);
            }
            else
            {
                edit.setTextColor(Color.WHITE);
            }
            edit.setTextSize(fontSeekBar.getProgress());

        }
    };
    private View.OnClickListener setLineWidthListener=new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            SeekBar widthSeekBar=(SeekBar)currentDialog.findViewById(R.id.widthseekBar);
            paintView.setLineWidth(widthSeekBar.getProgress());
            dialogIsDisplayed.set(false);
            currentDialog.dismiss();
            currentDialog=null;
        }
    };
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        paintView=(DrawingView)findViewById(R.id.paintView);
        switch(item.getItemId())
        {
            case R.id.color:
                //fm.removeView(edit);
                showColorDialog();
                return true;
            case R.id.line:
                showLineWidthDialog();
                return true;
            case R.id.erase:
                paintView.setDrawingColor(paintView.get_backgroundColor());
                Toast.makeText(getApplicationContext(),"Eraser",Toast.LENGTH_LONG).show();
                return true;
            case R.id.clear:
                edit.setGravity(Gravity.CENTER_HORIZONTAL);
                edit1.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
                return true;
            case R.id.uptext:
                showTopTextDialog();
                return true;
            case R.id.downtext:
                showDownTextDialog();
                return true;
            case R.id.saveMenu:
                saveImage();
                return  true;


            case R.id.fb:
                DrawingView view=(DrawingView)findViewById(R.id.paintView);
                final Bitmap bitmap = view.getBitmap();

                if(AccessToken.getCurrentAccessToken()==null)
                {
                    currentDialog=new Dialog(this);
                    currentDialog.setContentView(R.layout.fb_login);
                    currentDialog.setTitle("Share with Facebook");
                    currentDialog.setCancelable(true);
//                    Button cancel=(Button)currentDialog.findViewById(R.id.cancel_button);
//                    cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialogIsDisplayed.set(false);
//                        currentDialog.dismiss();
//
//                    }
//                });
                    LoginButton loginButton = (LoginButton) currentDialog.findViewById(R.id.login_button);

                    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Toast.makeText(getApplicationContext(),"Login SuccessFul",Toast.LENGTH_LONG).show();

                            dialogIsDisplayed.set(false);
                            currentDialog.dismiss();
                            SharePhoto photo = new SharePhoto.Builder()
                                    .setBitmap(bitmap)
                                    .build();
                            SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(photo)
                                    .build();
                            ShareDialog.show(MainActivity.this, content);

                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException e) {
                            Toast.makeText(getApplicationContext(),"Error Connecting to Facebook",Toast.LENGTH_LONG).show();

                        }
                    });
                    dialogIsDisplayed.set(true);
                    currentDialog.show();

                }
                else
                {

                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    ShareDialog.show(MainActivity.this, content);

                }


                return true;

        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //disableAccelerometerListening();
    }
    public void saveImage()
    {
        final Bitmap bit=takeScreenshot();

        currentDialog=new Dialog(this);
        currentDialog.setContentView(R.layout.save_image);
        currentDialog.setTitle("Save Image");
        final DrawingView  dView=(DrawingView)findViewById(R.id.paintView);
        final EditText nameText=(EditText)currentDialog.findViewById(R.id.saveeditText);
        Button saveImage=(Button)currentDialog.findViewById(R.id.saveImagebutton);
        saveImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dView.saveImage(nameText.getText().toString());
            //    saveNameImage(nameText.getText().toString(), bit);
                currentDialog.dismiss();
                dialogIsDisplayed.set(false);


            }
        });
        dialogIsDisplayed.set(true);
        currentDialog.show();

    }
    public void saveNameImage(String name,Bitmap bmap)
    {
        String fileName=name;
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpg");

        Uri uri=this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try
        {
            OutputStream outStream=this.getContentResolver().openOutputStream(uri);
            Bitmap bitmap = bmap;

            //	ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Toast message=Toast.makeText(this, "Image saved as "+fileName, Toast.LENGTH_LONG);
            message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
            message.show();
        }
        catch(IOException e)
        {
            Toast message=Toast.makeText(this, "Error in saving Image", Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
            message.show();
        }
        imagesave=0;
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            //	Toast.makeText(getApplicationContext(), "Clicked",Toast.LENGTH_LONG).show();
            if (dataList.get(position).getTitle() == null) {
                SelectItem(position);
            }
        }
    }
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    //    mFaceBook.onActivityResult(this, requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);

//                Bitmap bit=Bitmap.createBitmap(BitmapFactory.decodeFile(selectedImagePath)).copy(Bitmap.Config.ARGB_8888, true);

                paintView=(DrawingView)findViewById(R.id.paintView);
                paintView.isload=true;

                paintView.paintselectedImagePath=selectedImagePath;

                Toast.makeText(getApplicationContext(), "Image Loaded", Toast.LENGTH_LONG).show();

            }
        }

        //ca-app-pub-6508526601344465/4046023638
    }



}
