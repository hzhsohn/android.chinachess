package han.zh.chinachess;

import han.zh.chinachess.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class MainMenuView extends AbstractView {
	private Bitmap logoBmp = null;
	private Bitmap pointerBmp = null;
	private Rect bmpSrc = null;
	private Rect bmpDst = null;
	
	/*
	 * menu item id
	 */
	private static final int ITEM_NEW_GAME = 0;
	private static final int ITEM_LOAD_GAME = 1;
	private static final int ITEM_GAME_SETTING = 2;
	private static final int ITEM_GAME_HELP = 3;
	private static final int ITEM_GAME_EXIT = 4;
	
	private static final int REFERENCE_POINTER_START_X = 165;
	private static final int REFERENCE_POINTER_START_Y = 155;
	private static final int REFERENCE_POINTER_OFFSET_Y = 42;
	
	private static final int REFERENCE_BUTTON_START_X1 = 204;
	private static final int REFERENCE_BUTTON_START_X2 = 222;
	private static final int REFERENCE_BUTTON_OFFSET_X1 = 76;
	private static final int REFERENCE_BUTTON_OFFSET_X2 = 38;
	
	private static final int REFERENCE_BUTTON_START_Y = 168;
	private static final int REFERENCE_BUTTON_OFFSET_Y = 41;
	
	private static final int REFERENCE_BUTTON_HEIGHT = 20;
	
	private int buttonStartX1;
	private int buttonStartX2;
	private int buttonOffsetX1;
	private int buttonOffsetX2;
	private int buttonStartY;
	private int buttonOffsetY;
	
	private int buttonHeight;
	
	private int pointerStartX;
	private int pointerStartY;
	private int pointerOffsetY;
	
	private int pointBmpWidth;
	private int pointBmpHeight;
	
	private int logoBmpWidth;
	private int logoBmpHeight;
	
	private int curItemIndex = ITEM_NEW_GAME;
	
	private MainActivity activity;
	private ControlView parentView;

	/**
	 * @param context
	 */
	public MainMenuView(Context context, ControlView parentView) {
		super(context);
		// TODO Auto-generated constructor stub
		activity = (MainActivity)context;
		this.parentView = parentView;
		
		float scaleW = MainActivity.getScaleWidth();
		float scaleH = MainActivity.getScaleHeight();
		
		buttonStartX1 = (int)(REFERENCE_BUTTON_START_X1 * scaleW);
		buttonStartX2 = (int)(REFERENCE_BUTTON_START_X2 * scaleW);
		buttonOffsetX1 = (int)(REFERENCE_BUTTON_OFFSET_X1 * scaleW);
		buttonOffsetX2 = (int)(REFERENCE_BUTTON_OFFSET_X2 * scaleW);
		
		buttonStartY = (int)(REFERENCE_BUTTON_START_Y * scaleH);
		buttonOffsetY = (int)(REFERENCE_BUTTON_OFFSET_Y * scaleH);
		
		buttonHeight = (int)(REFERENCE_BUTTON_HEIGHT * scaleH);
		
		pointerStartX = (int)(REFERENCE_POINTER_START_X * scaleW);
		pointerStartY = (int)(REFERENCE_POINTER_START_Y * scaleH);
		pointerOffsetY = (int)(REFERENCE_POINTER_OFFSET_Y * scaleH);
		
		logoBmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo);
		pointerBmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.pointer);

		logoBmpWidth = logoBmp.getWidth();
		logoBmpHeight = logoBmp.getHeight();
		
		pointBmpWidth = pointerBmp.getWidth();
		pointBmpHeight = pointerBmp.getHeight();
		bmpSrc = new Rect();
		bmpDst = new Rect();
		
		curItemIndex = ITEM_NEW_GAME;
	}

	/* (non-Javadoc)
	 * @see chinachess.app.AbstractView#onDraw(android.graphics.Canvas)
	 */
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		bmpSrc.set(0, 0, logoBmpWidth, logoBmpHeight);
		bmpDst.set(0, 0, MainActivity.getScreenWidth(), MainActivity.getScreenHeight());
		canvas.drawBitmap(logoBmp, bmpSrc, bmpDst, null);
		
		//draw pointer bitmap
		bmpSrc.set(0, 0, pointBmpWidth, pointBmpHeight);
		bmpDst.set(pointerStartX, 
				pointerStartY + pointerOffsetY * curItemIndex, 
				pointerStartX + pointBmpWidth, 
				pointerStartY + pointBmpHeight + pointerOffsetY * curItemIndex);
		canvas.drawBitmap(pointerBmp, bmpSrc, bmpDst, null);
	}

	/* (non-Javadoc)
	 * @see chinachess.app.AbstractView#onKeyDown(int)
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "Main menu on key down");
		if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
			curItemIndex++;
			if(curItemIndex > ITEM_GAME_EXIT)
				curItemIndex = ITEM_NEW_GAME;
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
			curItemIndex--;
			if(curItemIndex < 0)
				curItemIndex = ITEM_GAME_EXIT;
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
			switch(curItemIndex){
			case ITEM_NEW_GAME:
				activity.startGame();
				return true;
			case ITEM_LOAD_GAME:
				activity.openLoadDialog();
				return true;
			case ITEM_GAME_SETTING:
				activity.openSettingDialog();
				return true;
			case ITEM_GAME_HELP:
				activity.openHelpView();
				break;
			case ITEM_GAME_EXIT:
				activity.exitGame();
				return true;
			default:
				break;
			}
		}
		
		parentView.updateCanvas();	//update canvas
		return true;
	}

	/* (non-Javadoc)
	 * @see chinachess.app.AbstractView#release()
	 */
	public void release() {
		// TODO Auto-generated method stub
		logoBmp = null;
		pointerBmp = null;
		bmpSrc = null;
		bmpDst = null;
	}

	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int touchX = (int)event.getX();
		int touchY = (int)event.getY();
		//Log.i(TAG, "touch X = " + touchX + " Y = " + touchY + " event action " + event.getAction());
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			int y;
			int index = -1;
			if((touchX > buttonStartX1) && (touchX < buttonStartX1 + buttonOffsetX1))
			{
				for(int i = 0; i < 3; i++)
				{
					y = buttonStartY + i * buttonOffsetY;
					if((touchY > y - 8) && (touchY < y + buttonHeight + 8))
					{
						index = i;
						break;
					}
				}
			}
			if((touchX > buttonStartX2) && (touchX < buttonStartX2 + buttonOffsetX2))
			{
				for(int i = 3; i < 5; i++)
				{
					y = buttonStartY + i * buttonOffsetY;
					if((touchY > y - 8) && (touchY < y + buttonHeight + 8))
					{
						index = i;
						break;
					}
				}
			}
			//Log.i(TAG, "index = " + index);
			switch(index)
			{
			case ITEM_NEW_GAME:
				activity.startGame();
				break;
			case ITEM_LOAD_GAME:
				activity.openLoadDialog();
				break;
			case ITEM_GAME_SETTING:
				activity.openSettingDialog();
				break;
			case ITEM_GAME_HELP:
				activity.openHelpView();
				break;
			case ITEM_GAME_EXIT:
				activity.exitGame();
				return false;
			default:
				break;
			}
			
			if(index != -1)
			{
				curItemIndex = index;
				parentView.updateCanvas();	//update canvas
			}
		}
		return false;
	}
}
