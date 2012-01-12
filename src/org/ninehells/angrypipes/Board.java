package org.ninehells.angrypipes;

//{//  import

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.ninehells.angrypipes.Config;
import org.ninehells.angrypipes.Position;

//}//

class Board
{
	Board (Config config, byte[] board)
	{//
		mConfig = config;
		W = mConfig.width;
		H = mConfig.height;
		mPipes = new byte[W][H];

		if (W < 2  ||  H < 2  ||  W > 4000  ||  H > 4000)
			throw new IllegalArgumentException("Invalid board dimensions.");

		if (board.length == 0)
			randomize();
		else
			serialize(board);
	}//

	byte[] serialize()
	{//
		byte[] board = new byte[W*H];
		for (int j = 0;  j < H;  ++j)
		for (int i = 0;  i < W;  ++i)
			board[i+j*W] = mPipes[i][j];
		return board;
	}//

	boolean rotate (Position pos)
	{//
		if (locked(pos.i, pos.j))
			return false;
		else {
			if (!pos.equals(mLastRotated)) {
				undoAdd(pos, mLastRotated);

				if (mLastRotated.valid) {
					if (mConfig.auto_lock || mConfig.challenge_mode)
						mPipes[mLastRotated.i][mLastRotated.j] |= LOCKED;
				}

				if (mConfig.auto_lock && isBlocked(pos.i, pos.j)) {
					toggleLock(pos);
					undoAdd(pos, mLastRotated);
					return true;
				}
				else {
					mLastRotated.set(pos);
					if (moved(pos.i, pos.j))
						++mConfig.mistake_count;
				}
			}

			mPipes[pos.i][pos.j] |= MOVED;
			doRotate(pos.i, pos.j);
			return true;
		}
	}//

	void undo()
	{//
		if (mUndoPosition1.valid) {
			byte old = mPipes[mUndoPosition1.i][mUndoPosition1.j];
			mPipes[mUndoPosition1.i][mUndoPosition1.j] = mUndoPipe1;
			mUndoPipe1 = old;

			setCursor(mUndoPosition1.i, mUndoPosition1.j);
			mSolvedFlagIsDirty = true;;
		}

		if (mUndoPosition2.valid) {
			mLastRotated.set(mUndoPosition2);
			byte old = mPipes[mUndoPosition2.i][mUndoPosition2.j];
			mPipes[mUndoPosition2.i][mUndoPosition2.j] = mUndoPipe2;
			mUndoPipe2 = old;
		}
	}//

	boolean isSolved()
	{//
		if (mSolvedFlagIsDirty) {

			if (!mCursor.valid)
				return false;

			mFilledCount = 0;
			mBadFillFlag = false;

			byte mask = (byte)(0xff ^ FILLED);
			for (int j = 0;  j < H;  ++j)
			for (int i = 0;  i < W;  ++i)
				mPipes[i][j] &= mask;
			fill(mCursor.i, mCursor.j);

			mSolvedFlagIsDirty = false;
			mSolvedFlag = (mFilledCount == W*H);
		}

		if (mSolvedFlag)
			mGameOver = true;

		return mSolvedFlag;
	}//

	private void randomize()
	{//
		mSolvedFlagIsDirty = true;

		Random rand = new Random();

		/* fill board with zeros */
		for (int j = 0;  j < H;  ++j)
		for (int i = 0;  i < W;  ++i)
			mPipes[i][j] = 0;


		/* start with two cells connected */
		int i0 = rand.nextInt(W-1);
		int j0 = rand.nextInt(H);
		mPipes[i0][j0]   = RIGHT;
		mPipes[i0+1][j0] = LEFT;

		/* initialize the border */
		ArrayList<Integer> border = new ArrayList<Integer>();
		addBorder(border, i0+2, j0+0);
		addBorder(border, i0+1, j0+1);
		addBorder(border, i0+0, j0+1);
		addBorder(border, i0-1, j0+0);
		addBorder(border, i0+0, j0-1);
		addBorder(border, i0+1, j0-1);


		while (!border.isEmpty()) {

			/* remove a random cell from the border */
			int borderToRemove = rand.nextInt(border.size());
			int e = border.remove(borderToRemove);
			int i = int2i(e);
			int j = int2j(e);

			/* which directions can we connect? */
			ArrayList<Byte> dirs = new ArrayList<Byte>();
			boolean nc = mConfig.no_cross_mode;
			int r=pipe(i+1,j), d=pipe(i,j+1), l=pipe(i-1,j), u=pipe(i,j-1);
			if (r>0 && !(nc && (r&ALLDIRS)==(RIGHT|DOWN|UP  )))  dirs.add(RIGHT);
			if (d>0 && !(nc && (d&ALLDIRS)==(RIGHT|DOWN|LEFT)))  dirs.add(DOWN);
			if (l>0 && !(nc && (l&ALLDIRS)==(DOWN |LEFT|UP  )))  dirs.add(LEFT);
			if (u>0 && !(nc && (u&ALLDIRS)==(RIGHT|LEFT|UP  )))  dirs.add(UP);

			/* if the only possible dirs are T's, ignore */
			if (dirs.size() == 0)
				continue;

			/* choose a random direction */
			byte dir = dirs.get(rand.nextInt(dirs.size()));

			/* connect with cell in that direction */
			if (dir == RIGHT) {
				mPipes[i][j] = RIGHT;
				mPipes[(i+1)%W][j] |= LEFT;
			}
			else if (dir == DOWN) {
				mPipes[i][j] = DOWN;
				mPipes[i][(j+1)%H] |= UP;
			}
			else if (dir == LEFT) {
				mPipes[i][j] = LEFT;
				mPipes[(i+W-1)%W][j] |= RIGHT;
			}
			else if (dir == UP) {
				mPipes[i][j] = UP;
				mPipes[i][(j+H-1)%H] |= DOWN;
			}

			/* expand the border */
			addBorder(border, i+1, j+0);
			addBorder(border, i+0, j+1);
			addBorder(border, i-1, j+0);
			addBorder(border, i+0, j-1);
		}

		/* shuffle */
		for (int j = 0;  j < H;  ++j)
		for (int i = 0;  i < W;  ++i)
		for (int r = rand.nextInt(4);  r > 0;  --r)
			doRotate(i, j);
	}//

	//{// acessors
	Config config() { return mConfig; }
	boolean gameOver() { return mGameOver; }
	boolean isBadFill() { return mBadFillFlag; }
	boolean right (int i, int j)  { return (pipe(i,j) & RIGHT )!=0; }
	boolean down  (int i, int j)  { return (pipe(i,j) & DOWN  )!=0; }
	boolean left  (int i, int j)  { return (pipe(i,j) & LEFT  )!=0; }
	boolean up    (int i, int j)  { return (pipe(i,j) & UP    )!=0; }
	boolean moved (int i, int j)  { return (pipe(i,j) & MOVED )!=0; }
	boolean locked(int i, int j)  { return (pipe(i,j) & LOCKED)!=0; }
	boolean filled(int i, int j)  { return (pipe(i,j) & FILLED)!=0; }
	//}//

	void setCursor(int i, int j)
	{//
		if (!mCursor.equals(i, j)) {
			mCursor.set(i,j);
			mSolvedFlagIsDirty = true;
		}
	}//

	boolean toggleLock (Position pos)
	{//
		if (locked(pos.i, pos.j)) {
			if (mConfig.challenge_mode)
				return false;
			undoAdd(pos, new Position());
			mPipes[pos.i][pos.j] &= (byte)(0xff ^ LOCKED);
		}
		else {
			undoAdd(pos, new Position());
			mPipes[pos.i][pos.j] |= LOCKED;
		}
		return true;
	}//

	private void fill (int i, int j)
	{//
		if (!mFillStack.empty())
			throw new RuntimeException("Fill stack is not empty.");

		if (mConfig.torus_mode) {
			i = (i+W)%W;
			j = (j+H)%H;
		}
		mFillStack.push(ij2int(i,j));

		while (!mFillStack.empty()) {
			int x = mFillStack.pop();
			int ai = int2i(x);
			int aj = int2j(x);
			int originDir = int2dir(x);

			mPipes[ai][aj] |= FILLED;
			++mFilledCount;

			byte[] dirs = { RIGHT, DOWN, LEFT, UP };
			for (byte adir : dirs) {

				byte bdir = 0;
				int bi = ai, bj = aj;
				switch (adir) {
					case RIGHT:  bdir = LEFT;   bi++;  break;
					case DOWN:   bdir = UP;     bj++;  break;
					case LEFT:   bdir = RIGHT;  bi--;  break;
					case UP:     bdir = DOWN;   bj--;  break;
				}
				if (mConfig.torus_mode) {
					bi = (bi+W)%W;
					bj = (bj+H)%H;
				}

				if ((originDir != bdir) && (mPipes[ai][aj] & adir) != 0 &&
						(!mConfig.auto_lock || locked(bi,bj) || moved(bi,bj))
				) {
					int p = pipe(bi, bj);
					if (p > 0) {
						if ((p & bdir) != 0) {
							if ((p & FILLED) != 0) // loop
								mBadFillFlag = true;
							else
								mFillStack.push(ijdir2int(bi, bj, adir));
						}
						else if ((p & LOCKED) != 0) // dead end
							mBadFillFlag = true;
					}
					else
						mBadFillFlag = true;
				}
			}
		}
	}//

	void serialize(byte[] board)
	{//
		if (board.length != W*H)
			throw new IllegalArgumentException("Invalid board string size.");

		for (int j = 0;  j < H;  ++j)
		for (int i = 0;  i < W;  ++i)
			mPipes[i][j] = board[i+j*W];

		mLastRotated.reset();
	}//

	private void doRotate (int i, int j)
	{//
		byte b = mPipes[i][j];

		mPipes[i][j] = (byte)
		(
			(((b & UP   ) == UP   ) ? RIGHT : 0) |
			(((b & RIGHT) == RIGHT) ? DOWN  : 0) |
			(((b & DOWN ) == DOWN ) ? LEFT  : 0) |
			(((b & LEFT ) == LEFT ) ? UP    : 0) |
			(b & MOVED) | (b & LOCKED) | (b & FILLED)
		);

		mSolvedFlagIsDirty = true;
	}//

	private boolean isBorder (ArrayList<Integer> border, int I, int J)
	{//
		if (mConfig.torus_mode) {
			I = (I+W)%W;
			J = (J+H)%H;
		}

		for (int b = 0;  b < border.size();  ++b) {
			int e = border.get(b);
			int i = int2i(e);
			int j = int2j(e);
			if (i == I  &&  j == J)
				return true;
		}
		return false;
	}//

	private boolean setPos (Position pos, int i, int j)
	{//
		if (!mConfig.torus_mode && (i<0 || i>=W || j<0 || j>=H))
			return false;

		pos.set((i+W)%W, (j+H)%H);
		return true;
	}//

	private boolean isBlocked (int i, int j)
	{//
		return (isBlockedPositive(i,j) || isBlockedNegative(i,j));
	}//

	private boolean isBlockedPositive (int i, int j)
	{//
		Position p = new Position();

		if (right(i,j) && !(setPos(p,i+1,j+0) && left (p.i,p.j) && locked(p.i,p.j)))  return false;
		if (down (i,j) && !(setPos(p,i+0,j+1) && up   (p.i,p.j) && locked(p.i,p.j)))  return false;
		if (left (i,j) && !(setPos(p,i-1,j-0) && right(p.i,p.j) && locked(p.i,p.j)))  return false;
		if (up   (i,j) && !(setPos(p,i-0,j-1) && down (p.i,p.j) && locked(p.i,p.j)))  return false;

		return true;
	}//

	private boolean isBlockedNegative (int i, int j)
	{//
		Position p = new Position();

		if (!right(i,j) && !(!setPos(p,i+1,j+0) || !left (p.i,p.j) && locked(p.i,p.j)))  return false;
		if (!down (i,j) && !(!setPos(p,i+0,j+1) || !up   (p.i,p.j) && locked(p.i,p.j)))  return false;
		if (!left (i,j) && !(!setPos(p,i-1,j-0) || !right(p.i,p.j) && locked(p.i,p.j)))  return false;
		if (!up   (i,j) && !(!setPos(p,i-0,j-1) || !down (p.i,p.j) && locked(p.i,p.j)))  return false;

		return true;
	}//

	private void addBorder (ArrayList<Integer> border, int i, int j)
	{//
		if (isBorder(border, i, j))
			return;

		if (mConfig.torus_mode) {
			i = (i+W)%W;
			j = (j+H)%H;
		}

		if (i>=0 && j>=0 && i<W && j<H && mPipes[i][j]==0)
			border.add(ij2int(i,j));
	}//

	private void undoAdd (Position pos1, Position pos2)
	{//
		if (pos1.valid) {
			mUndoPosition1.set(pos1);
			mUndoPipe1 = mPipes[pos1.i][pos1.j];
		}

		if (pos2.valid) {
			mUndoPosition2.set(pos2);
			mUndoPipe2 = mPipes[pos2.i][pos2.j];
		}
		else {
			mUndoPosition2.reset();
		}
	}//

	private int pipe (int i, int j)
	{//
		return mConfig.torus_mode
			? mPipes [(i+W)%W] [(j+H)%H]
			: (i>=0 && j>=0 && i<W && j<H) ? mPipes[i][j] : -1;
	}//

	private int int2i     (int x)                 { return (x >> 00) & 0xfff;   }
	private int int2j     (int x)                 { return (x >> 12) & 0xfff;   }
	private int int2dir   (int x)                 { return (x >> 24) & 0xf;     }
	private int ij2int    (int i, int j)          { return i|(j<<12);           }
	private int ijdir2int (int i, int j, int dir) { return i|(j<<12)|(dir<<24); }

	private byte[][] mPipes;
	private Config mConfig;
	private Position mLastRotated = new Position();
	private Position mCursor = new Position();
	private int mFilledCount = 0;
	private Stack<Integer> mFillStack = new Stack<Integer>();
	private boolean mSolvedFlag = false;
	private boolean mSolvedFlagIsDirty = true;
	private boolean mGameOver = false;
	private boolean mBadFillFlag = false;
	private int W, H;

	private Position mUndoPosition1 = new Position();
	private Position mUndoPosition2 = new Position();
	private byte mUndoPipe1;
	private byte mUndoPipe2;

	private final byte RIGHT  = 0x01;
	private final byte DOWN   = 0x02;
	private final byte LEFT   = 0x04;
	private final byte UP     = 0x08;
	private final byte MOVED  = 0x10;
	private final byte LOCKED = 0x20;
	private final byte FILLED = 0x40;
	private final byte ALLDIRS = (RIGHT|DOWN|LEFT|UP);
}

// vim600:fdm=marker:fmr={//,}//:fdn=2:nu:
