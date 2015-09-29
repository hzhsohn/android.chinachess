package han.zh.chinachess;

import han.zh.chinachess.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class HelpView extends AbstractView {

	private Paint paint;
	private Rect rect;

	private int strStartX;
	private int strStartY;
	private int strOffsetY;

	private String quitPrompt;
	private String contents[];
	private MainActivity activity;

	public HelpView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		activity = (MainActivity) context;

		rect = new Rect(10, 10, MainActivity.getScreenWidth() - 10, MainActivity.getScreenHeight() - 10);
		strStartX = rect.left + 10;
		strStartY = rect.top + 30;
		strOffsetY = 20;
		paint = new Paint();

		Resources res = activity.getResources();
		contents = new String[] { res.getString(R.string.help_line0), res.getString(R.string.help_line1), res.getString(R.string.help_line2), res.getString(R.string.help_line3), res.getString(R.string.help_line4), res.getString(R.string.help_line5), res.getString(R.string.help_line6), res.getString(R.string.help_line7), res.getString(R.string.help_line8), res.getString(R.string.help_line9), res.getString(R.string.help_line10), };

		quitPrompt = res.getString(R.string.quit_prompt);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		paint.setColor(Color.LTGRAY);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(rect, paint);

		int index;
		// paint.setTypeface(Typeface.SERIF);
		paint.setColor(Color.GREEN);
		for (index = 0; index < contents.length; index++) {
			canvas.drawText(contents[index], strStartX, strStartY + index * strOffsetY, paint);
		}

		paint.setColor(Color.RED);
		canvas.drawText(quitPrompt, strStartX, strStartY + (index + 2) * strOffsetY, paint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see chinachess.app.AbstractView#release()
	 */
	@Override
	public void release() {
		// TODO Auto-generated method stub
	}
}
