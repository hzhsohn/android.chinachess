package han.zh.chinachess;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public abstract class AbstractView extends View {

	public enum ViewType {
		NONE, MAIN_MENU, GAME, HELP_ABOUT,
	}

	public AbstractView(Context context) {
		super(context);
	}

	public abstract void onDraw(Canvas canvas);

	public abstract void release();
}
