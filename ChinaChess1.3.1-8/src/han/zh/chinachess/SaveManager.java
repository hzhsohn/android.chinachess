package han.zh.chinachess;



import han.zh.chinachess.R;
import han.zh.chinachess.AbstractView.ViewType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import chinachess.mid.MID_ChessBoard;
import chinachess.mid.MID_IPiece.PieceStatus;

public class SaveManager implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
	private static final String TAG = SaveManager.class.getSimpleName();

	private static final String POSITION_X = "_POSITION_X";
	private static final String POSITION_Y = "_POSITION_Y";
	private static final String STATUS = "_STATUS";

	private enum DIALOG_TYPE {
		DIALOG_SAVE_GAME, DIALOG_LOAD_GAME
	}

	private Properties properties;
	private MainActivity activity;
	private EditText editView;
	private TextView txtView;
	private ListView listView;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private ArrayList<String> items;
	private ArrayAdapter<String> adapter;
	private File sysDir;

	private static SaveManager self = null;

	private DIALOG_TYPE dialogType = DIALOG_TYPE.DIALOG_SAVE_GAME;

	private SaveManager(MainActivity activity) {
		this.activity = activity;

		properties = new Properties();
		sysDir = activity.getFilesDir();
		// Log.d(TAG, sysDir.toString());

		LayoutInflater factory = LayoutInflater.from(activity);
		View dialogView = factory.inflate(R.layout.save_dialog, null);

		builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.save_manager);
		builder.setNeutralButton(R.string.bt_str_delete, this);
		builder.setPositiveButton(R.string.bt_str_ok, this);
		builder.setNegativeButton(R.string.bt_str_cancel, null);
		builder.setView(dialogView);
		dialog = builder.create();

		txtView = (TextView) dialogView.findViewById(R.id.txtView);
		editView = (EditText) dialogView.findViewById(R.id.etFileName);

		listView = (ListView) dialogView.findViewById(R.id.listView);
		// items.clear();
		items = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	public static SaveManager getInstace(MainActivity activity) {
		if (self == null) {
			self = new SaveManager(activity);
		}
		return self;
	}

	public void release() {
		self = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		editView.setText(items.get(arg2));
	}

	@Override
	public void onClick(DialogInterface dialog, int arg1) {
		boolean result = false;
		String fileName = editView.getText().toString();

		if (DialogInterface.BUTTON_NEUTRAL == arg1) {
			if (fileName == null || fileName.equals("")) {
				return;
			}
			result = this.activity.deleteFile(fileName);
			if (result) {
				items.remove(fileName);
				adapter.notifyDataSetChanged();
				Toast.makeText(activity, R.string.info_delete_ok, Toast.LENGTH_LONG).show();
			} else {
				Log.e(TAG, "delete file error");
				Toast.makeText(activity, R.string.info_delete_fail, Toast.LENGTH_LONG).show();
			}
			return;
		}

		if (dialogType == DIALOG_TYPE.DIALOG_SAVE_GAME) {
			if (ControlView.getInstace(null).getCurrentViewType() != ViewType.GAME) {
				return;
			}
			if (fileName == null || fileName.equals("")) {
				Toast.makeText(activity, R.string.info_save_select, Toast.LENGTH_LONG).show();
				return;
			}

			result = ((GameView) ControlView.getInstace(null).getCurrentView()).saveChessBoard(this, fileName);
			if (result)
				Toast.makeText(activity, R.string.save_ok, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(activity, R.string.save_fail, Toast.LENGTH_SHORT).show();
		} else if (dialogType == DIALOG_TYPE.DIALOG_LOAD_GAME) {
			if (fileName == null || fileName.equals("")) {
				Toast.makeText(activity, R.string.info_load_select, Toast.LENGTH_LONG).show();
				return;
			}
			if (ControlView.getInstace(null).getCurrentViewType() != ViewType.GAME) {
				this.activity.startGame();
			}

			result = ((GameView) ControlView.getInstace(null).getCurrentView()).loadChessBoard(this, fileName);
			if (result)
				Toast.makeText(activity, R.string.load_ok, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(activity, R.string.load_fail, Toast.LENGTH_SHORT).show();
		}
	}

	private void getFiles() {
		items.clear();
		String[] files = sysDir.list();
		for (int i = 0; i < files.length; i++) {
			items.add(files[i]);
		}
		adapter.notifyDataSetChanged();
	}

	public void openSaveDialog() {
		dialogType = DIALOG_TYPE.DIALOG_SAVE_GAME;

		getFiles();
		if (items.size() == 0) {
			txtView.setText(R.string.txt_save_info1);
		} else {
			txtView.setText(R.string.txt_save_info2);
		}
		editView.setText("");
		editView.setEnabled(true);

		dialog.show();
	}

	public void openLoadDialog() {
		dialogType = DIALOG_TYPE.DIALOG_LOAD_GAME;

		getFiles();
		if (items.size() == 0) {
			txtView.setText(R.string.txt_no_save_file);
		} else {
			txtView.setText(R.string.txt_input_info1);
		}
		editView.setText("");
		editView.setEnabled(false);

		dialog.show();
	}

	public void saveData(Chess[] chesses, String fileName) throws FileNotFoundException, IOException {
		this.properties.clear();
		for (int index = 0; index < MID_ChessBoard.MAX_CHESS_NUM; index++) {
			saveOneChess(chesses[index], index);
		}

		FileOutputStream out = null;
		try {
			out = activity.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
			properties.store(out, "");
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
			throw e;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				throw e;
			}
		}
	}

	public void loadData(Chess[] chesses, String fileName) throws FileNotFoundException, IOException {
		this.properties.clear();
		FileInputStream in = null;
		try {
			in = activity.openFileInput(fileName);
			properties.load(in);
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
			throw e;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			throw e;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				throw e;
			}
		}

		for (int index = 0; index < MID_ChessBoard.MAX_CHESS_NUM; index++) {
			loadOneChess(chesses[index], index);
		}
	}

	private void saveOneChess(Chess chess, int index) {
		properties.put(index + POSITION_X, String.valueOf(chess.getPosition().x));
		properties.put(index + POSITION_Y, String.valueOf(chess.getPosition().y));
		if (chess.getStatus() == PieceStatus.DEAD) {
			properties.put(index + STATUS, "0");
		} else {
			properties.put(index + STATUS, "1");
		}
	}

	private void loadOneChess(Chess chess, int index) {
		chess.getPosition().x = Integer.parseInt((String) properties.get(index + POSITION_X));
		chess.getPosition().y = Integer.parseInt((String) properties.get(index + POSITION_Y));
		if (Integer.parseInt((String) properties.get(index + STATUS)) == 0) {
			chess.setStatus(PieceStatus.DEAD);
		} else {
			chess.setStatus(PieceStatus.LIVE);
		}
	}
}
