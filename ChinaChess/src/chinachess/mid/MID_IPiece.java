package chinachess.mid;

import chinachess.mid.MID_ChessBoard.PiecePosition;

public interface MID_IPiece {

	public enum PieceType {
		KING, // 帅
		CAR, // 俥
		HORSE, // 马
		CANON, // 炮
		BISHOP, // 士
		ELEPHANT, // 象
		PAWN, // 卒
	}

	public enum PieceStatus {
		DEAD, LIVE,
	}

	public enum PieceColor {
		BLACK, RED,
	}

	public PiecePosition getPiecePosition();

	public PieceColor getPieceColor();

	public PieceType getPieceType();

	public PieceStatus getPieceStatus();

	public boolean setPieceStatus(PieceStatus status);

	public boolean isValidMove(PiecePosition _toPosition);
}