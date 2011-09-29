package org.ninehells.angrypipes;

import java.util.ArrayList;
import java.util.Random;
import java.io.Console;

import org.ninehells.angrypipes.Config;

class Board
{
	public Board (Config config, byte[] board)
	{
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
	}

	public byte[] serialize()
	{
		byte[] board = new byte[W*H];
		for (int j = 0;  j < H;  ++j)
		for (int i = 0;  i < W;  ++i)
			board[i+j*W] = mPipes[i][j];
		return board;
	}

	public void rotate (int i, int j)
	{
		if (i < 0  ||  i >= mConfig.width  ||  j < 0  ||  j >= mConfig.height)
			return;

		if (i != mLastRotatedI  ||  j != mLastRotatedJ) {
			if (mLastRotatedI != -1)
				mPipes[mLastRotatedI][mLastRotatedJ] |= FIXED;
			mLastRotatedI = i;
			mLastRotatedJ = j;
		}

		doRotate(i, j);
	}

	public boolean isSolved()
	{
		if (mSolvedFlagIsDirty) {

			mFilledCount = 0;

			byte mask = RIGHT|DOWN|LEFT|UP|FIXED;
			for (int j = 0;  j < H;  ++j)
			for (int i = 0;  i < W;  ++i)
				mPipes[i][j] &= mask;
			fill(W>>1, H>>1);

			mSolvedFlagIsDirty = false;
			mSolvedFlag = (mFilledCount == W*H);
		}

		return mSolvedFlag;
	}

	public void randomize()
	{
		mLastRotatedI = -1;
		mLastRotatedJ = -1;
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
			int i = e & 0xfff;
			int j = e >> 12;

			/* which directions can we connect? */
			ArrayList<Byte> dirs = new ArrayList<Byte>();
			if (pipe(i+1,j) > 0)
				dirs.add(RIGHT);
			if (pipe(i,j+1) > 0)
				dirs.add(DOWN);
			if (pipe(i-1,j) > 0)
				dirs.add(LEFT);
			if (pipe(i,j-1) > 0)
				dirs.add(UP);

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
	}

	public Config config() { return mConfig; }
	public int width()  { return mConfig.width;  }
	public int height() { return mConfig.height; }
	public boolean right(int i, int j)  { return (mPipes[i][j] & RIGHT)!=0; }
	public boolean down (int i, int j)  { return (mPipes[i][j] & DOWN )!=0; }
	public boolean left (int i, int j)  { return (mPipes[i][j] & LEFT )!=0; }
	public boolean up   (int i, int j)  { return (mPipes[i][j] & UP   )!=0; }
	public boolean fixed(int i, int j)  {
		return ((mPipes[i][j] & FIXED)==FIXED)
			|| (i == mLastRotatedI  &&  j == mLastRotatedJ);
	}


	private int pipe (int i, int j)
	{
		return mConfig.torus_mode
			? mPipes [(i+W)%W] [(j+H)%H]
			: (i>=0 && j>=0 && i<W && j<H) ? mPipes[i][j] : -1;
	}

	private void fill (int i, int j)
	{
		if (mConfig.torus_mode) {
			i = (i+W)%W;
			j = (j+H)%H;
		}

		if ((mPipes[i][j] & FILLED)!=0)
			return;

		mPipes[i][j] |= FILLED;
		++mFilledCount;

		if ((mPipes[i][j] & RIGHT) != 0) {
			int p = pipe(i+1, j);
			if (p > 0  &&  (p & LEFT) != 0)
				fill(i+1, j);
		}
		if ((mPipes[i][j] & DOWN) != 0) {
			int p = pipe(i, j+1);
			if (p > 0  &&  (p & UP) != 0)
				fill(i, j+1);
		}
		if ((mPipes[i][j] & LEFT) != 0) {
			int p = pipe(i-1, j);
			if (p > 0  &&  (p & RIGHT) != 0)
				fill(i-1, j);
		}
		if ((mPipes[i][j] & UP) != 0) {
			int p = pipe(i, j-1);
			if (p > 0  &&  (p & DOWN) != 0)
				fill(i, j-1);
		}
	}

	private void serialize(byte[] board)
	{
		if (board.length != W*H)
			throw new IllegalArgumentException("Invalid board string size.");

		for (int j = 0;  j < H;  ++j)
		for (int i = 0;  i < W;  ++i)
			mPipes[i][j] = board[i+j*W];
	}

	private void doRotate (int i, int j)
	{
		byte b = mPipes[i][j];

		byte loMask = (RIGHT | DOWN | LEFT | UP);
		byte hiMask = (FIXED);

		/* save the higher bits, so they won't rotate */
		byte hi = (byte)(b & hiMask);
		b &= loMask;

		/* rotate the lower bits */
		b <<= 1;
		if (b >  loMask)
			b -= loMask;

		/* restore the higher bits */
		b |= hi;

		mPipes[i][j] = b;
		mSolvedFlagIsDirty = true;
	}

	private boolean isBorder (ArrayList<Integer> border, int I, int J)
	{
		if (mConfig.torus_mode) {
			I = (I+W)%W;
			J = (J+H)%H;
		}

		for (int b = 0;  b < border.size();  ++b) {
			int e = border.get(b);
			int i = e & 0xfff;
			int j = e >> 12;
			if (i == I  &&  j == J)
				return true;
		}
		return false;
	}

	private void addBorder (ArrayList<Integer> border, int i, int j)
	{
		if (isBorder(border, i, j))
			return;

		if (mConfig.torus_mode) {
			i = (i+W)%W;
			j = (j+H)%H;
		}

		if (i>=0 && j>=0 && i<W && j<H && mPipes[i][j]==0)
			border.add(i|j<<12);
	}

	private byte[][] mPipes;
	private Config mConfig;
	private int mLastRotatedI = -1, mLastRotatedJ = -1;
	private int mFilledCount = 0;
	private boolean mSolvedFlag = false;
	private boolean mSolvedFlagIsDirty = true;
	private int W, H;

	private final byte RIGHT  = 0x01;
	private final byte DOWN   = 0x02;
	private final byte LEFT   = 0x04;
	private final byte UP     = 0x08;
	private final byte FIXED  = 0x10;
	private final byte FILLED = 0x20;
}

// vim600:fdm=syntax:fdn=2:nu:
