package chinachess.mid;

import chinachess.mid.MID_ChessBoard.PiecePosition;

class MID_Piece implements MID_IPiece {

	private PieceColor color;
	private PieceType type;
	private PiecePosition position;
	private PieceStatus status;

	public MID_Piece(PieceColor color, PieceType type) {
		this.color = color;
		this.type = type;
		this.status = PieceStatus.LIVE;
		this.position = new PiecePosition();
	}

	public MID_Piece(PieceColor color, PieceType type, PiecePosition position) {
		this(color, type);
		this.position = position;
	}

	@Override
	public PieceColor getPieceColor() {
		return this.color;
	}

	@Override
	public PiecePosition getPiecePosition() {
		return this.position;
	}

	@Override
	public PieceStatus getPieceStatus() {
		return this.status;
	}

	@Override
	public PieceType getPieceType() {
		return this.type;
	}

	@Override
	public boolean isValidMove(PiecePosition toPosition) {
		return false;
	}

	public boolean setPieceStatus(PieceStatus status) {
		this.status = status;
		return true;
	}

	void setPieceColor(PieceColor color) {
		this.color = color;
	}

	void setPieceType(PieceType type) {
		this.type = type;
	}

	boolean setPiecePosition(PiecePosition position) {
		this.position.x = position.x;
		this.position.y = position.y;
		return true;
	}

	boolean setPiecePosition(int col, int row) {
		this.position.x = col;
		this.position.y = row;

		return true;
	}
}
