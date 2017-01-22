package views;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class DrawingView extends View{
	private static final float TOUCH_TOLERANCE=10;
	public Bitmap bitmap,bit,bitmap1;
	public Canvas bitmapCanvas;
	public Paint paintScreen;
	public Paint paintLine;
	 public boolean isload;
	 Matrix matrix;
	private static final int SELECT_PICTURE = 1;
	public HashMap<Integer,Path> pathMap;
	public HashMap<Integer,Point> previousPointMap;
	public String paintselectedImagePath="";
	int i=0;
	int j=0;
	int color;
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		
		paintScreen=new Paint();
		paintScreen.setColor(Color.BLUE);
		paintScreen.setStyle(Style.FILL);
		isload=false;
		paintLine=new Paint();
		paintLine.setAntiAlias(true);
		paintLine.setStyle(Style.STROKE);
		paintLine.setColor(Color.BLACK);
		color=Color.WHITE;
		paintLine.setStrokeWidth(2);
		paintLine.setStrokeCap(Paint.Cap.ROUND);
		pathMap=new HashMap<Integer,Path>();
		previousPointMap=new HashMap<Integer,Point>();
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		if(isload==false){

	 	 bitmap=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
		  bit=bitmap.copy(Bitmap.Config.ARGB_8888, true);
		  bit.eraseColor(Color.WHITE);
		  
		}

		matrix=new Matrix();
		matrix.setScale(1.5f, 1.5f);
		
		bitmapCanvas=new Canvas(bit);
		
		
	}
	public void clear()
	{
		pathMap.clear();
		previousPointMap.clear();
		bit.eraseColor(Color.WHITE);
		if(i==1)
		 bit=Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
		invalidate();
	}

	
	public void setDrawingColor(int color)
	{

		paintLine.setColor(color);
		
	}
	public int getDrawingColor()
	{
		return paintLine.getColor();
	}
	public void setLineWidth(int width)
	{
		
		paintLine.setStrokeWidth(width);
	}
	public int getLineWidth()
	{
		return (int) paintLine.getStrokeWidth();
	}
	
	
	 @Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
	
			if(isload==true){
				Bitmap b=BitmapFactory.decodeFile(paintselectedImagePath);
				if(b!=null) {
					bit = Bitmap.createScaledBitmap(b, getWidth(), getHeight(), true).copy(Bitmap.Config.ARGB_8888, true);

					i = 1;
					isload = false;
				}
				else {
					Toast.makeText(this.getContext(),"There was a problem loading your image." + "\nPlease select from previously saved image or from Gallery.",Toast.LENGTH_LONG).show();
				}
			}
			
			if(i==1)
				canvas.drawBitmap(bit,0,0,paintScreen);
			else
				canvas.drawBitmap(bit,0,0,paintScreen);
				
		for(Integer key:pathMap.keySet()){
			canvas.drawPath(pathMap.get(key), paintLine);
			
		}

		invalidate();
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		bitmapCanvas=new Canvas(bit);
		int action=event.getActionMasked();
		int actionIndex=event.getActionIndex();
		if(action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_POINTER_DOWN)
		{
			touchStarted(event.getX(actionIndex),event.getY(actionIndex),event.getPointerId(actionIndex));
			
		}
		else if(action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_POINTER_UP)
		{
			touchEnded(event.getPointerId(actionIndex));
		}
		else
		{
			touchMoved(event);
		}
		invalidate();
		return true;
	}
	private void touchStarted(float x,float y,int lineId)
	{
	    Path path;
		
		Point point;
		if(pathMap.containsKey(lineId))
		{
			path=pathMap.get(lineId);
			if(isload==false)
				path.reset();
			point=previousPointMap.get(lineId);
		}
		else
		{
			path=new Path();
			pathMap.put(lineId, path);
			point=new Point();
			previousPointMap.put(lineId, point);
		}
		path.moveTo(x, y);
		point.x=(int)x;
		point.y=(int)y;
		invalidate();
	}
	private void touchMoved(MotionEvent event)
	{
		for(int i=0;i<event.getPointerCount();i++)
		{
			int pointerId=event.getPointerId(i);
			int pointerIndex=event.findPointerIndex(pointerId);
			
			if(pathMap.containsKey(pointerId)){
				float newX=event.getX(pointerIndex);
				float newY=event.getY(pointerIndex);
				Path path=pathMap.get(pointerId);
				Point point=previousPointMap.get(pointerId);
				float deltaX=Math.abs(newX-point.x);
				float deltaY=Math.abs(newY-point.y);
				if(deltaX>=TOUCH_TOLERANCE ||deltaY>=TOUCH_TOLERANCE)
				{
					path.quadTo(point.x, point.y,(newX+point.x)/2,(newY+point.y)/2);
					point.x=(int)newX;
					point.y=(int)newY;
				}
			}
		}
		invalidate();
	}
	
	private void touchEnded(int lineId)
	{
		bitmapCanvas=new Canvas(bit);
		Path path=pathMap.get(lineId);
		bitmapCanvas.drawPath(path, paintLine);
	    invalidate();
	    if(isload==false)
	    	path.reset();
		
	}

	
	public void set_BackgroundColor(int color)
	{
		if(isload==false){
		bit.eraseColor(color);
			this.color=color;
		}
		else
		{
			Toast.makeText(getContext(),"Cannot Set background on a Loaded Image",Toast.LENGTH_LONG).show();
		}
	}

	public int get_backgroundColor()
	{
		return this.color;

	}

	public void saveImage(String name)
	{
		String fileName=name;
		ContentValues values=new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
		values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpg");

		Uri uri=getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		try
		{
			OutputStream outStream=getContext().getContentResolver().openOutputStream(uri);
			bit.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
			Toast message=Toast.makeText(getContext(), "Image saved as "+fileName, Toast.LENGTH_LONG);
			message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
			message.show();
		}
		catch(IOException e)
		{
			Toast message=Toast.makeText(getContext(), "Error in saving Image", Toast.LENGTH_SHORT);
			message.setGravity(Gravity.CENTER, message.getXOffset()/2, message.getYOffset()/2);
			message.show();
		}
	}
	
	public Bitmap getBitmap()
	{

		return bit;

	}
}
