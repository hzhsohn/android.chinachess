package han.zh.chinachess;


import han.zh.chinachess.AbstractView.ViewType;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class ControlView extends View {
	private static final String TAG = ControlView.class.getSimpleName();

	private Context context;
	private ViewType currentViewType;
	private AbstractView currentView;

	private static ControlView self;

	/**
	 * @param context
	 */
	private ControlView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		currentViewType = ViewType.NONE;
	}

	public static ControlView getInstace(Context context) {
		if (self == null) {
			self = new ControlView(context);
		}

		return self;
	}

	public void release() {
		if (currentView != null) {
			currentView.release();
			currentView = null;
		}

		self = null;
	}

	/**
	 * switch content view
	 * 
	 * @param type
	 */
	public boolean switchContentView(ViewType type) {
		if ((type == ViewType.NONE) || (currentViewType == type)) {
			Log.e(TAG, "view type error");
			return false;
		}

		if (this.currentView != null) {
			currentView.release();
			currentView = null;
			System.gc();
		}

		currentViewType = type;
		switch (currentViewType) {
		case MAIN_MENU:
			currentView = new MainMenuView(context, this);
			break;
		case GAME:
			currentView = new GameView(context, this);
			break;
		case HELP_ABOUT:
			currentView = new HelpView(context);
			break;
		default:
			break;
		}

		this.updateCanvas();
		return true;
	}

	/**
	 * get current view type
	 * 
	 * @return
	 */
	public ViewType getCurrentViewType() {
		return this.currentViewType;
	}

	/**
	 * get current view object
	 * 
	 * @return
	 */
	public AbstractView getCurrentView() {
		return this.currentView;
	}

	public void updateCanvas() {
		this.postInvalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (currentView != null) {
			currentView.onDraw(canvas);
		} else {
			Log.e(TAG, "target view is null");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// Log.i(TAG, "on key down");
		if (currentView != null) {
			if (this.currentViewType == ViewType.HELP_ABOUT) {
				this.switchContentView(ViewType.MAIN_MENU);
			} else {
				currentView.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (currentView != null) {
			if (this.currentViewType == ViewType.HELP_ABOUT) {
				this.switchContentView(ViewType.MAIN_MENU);
			} else {
				currentView.onKeyUp(keyCode, event);
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (currentView != null) {
			if (this.currentViewType == ViewType.HELP_ABOUT) {
				this.switchContentView(ViewType.MAIN_MENU);
			} else {
				currentView.onTouchEvent(event);
			}
		}
		return super.onTouchEvent(event);
	}

}
