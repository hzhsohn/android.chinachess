package han.zh.chinachess;

import android.graphics.Bitmap;
import chinachess.mid.MID_IPiece;
import chinachess.mid.MID_ChessBoard.PiecePosition;
import chinachess.mid.MID_IPiece.PieceColor;
import chinachess.mid.MID_IPiece.PieceStatus;
import chinachess.mid.MID_IPiece.PieceType;

public class Chess {

	private MID_IPiece midPiece; // mid chess object
	private Bitmap bmp;

	private boolean isSelected = false; // is selected

	public Chess(MID_IPiece midPiece, Bitmap bmp) {
		this.midPiece = midPiece;
		this.bmp = bmp;
	}

	/**
	 * get position
	 * 
	 * @return
	 */
	public PiecePosition getPosition() {
		return midPiece.getPiecePosition();
	}

	public MID_IPiece getMidPiece() {
		return midPiece;
	}

	public PieceStatus getStatus() {
		return midPiece.getPieceStatus();
	}

	public void setStatus(PieceStatus status) {
		midPiece.setPieceStatus(status);
	}

	public PieceColor getColor() {
		return midPiece.getPieceColor();
	}

	public PieceType getType() {
		return midPiece.getPieceType();
	}

	public Bitmap getBmp() {
		return this.bmp;
	}

	public void setSelected(boolean value) {
		this.isSelected = value;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Chess:type = " + midPiece.getPieceType() + " color = " + midPiece.getPieceColor() + " position= x =" + midPiece.getPiecePosition().x + " y=" + midPiece.getPiecePosition().y;
	}
}
