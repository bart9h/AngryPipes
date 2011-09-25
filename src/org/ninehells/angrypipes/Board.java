package org.ninehells.angrypipes;

import java.util.ArrayList;
import java.util.Random;

import java.io.Console;

class Board
{
	public Board (int width, int height, byte[] board)
	{
		if (width < 2 || height < 2 || width > 4000 || height > 4000)
			throw new IllegalArgumentException("Invalid board dimensions.");

		mWidth = width;
		mHeight = height;
		mPipes = new byte[width][height];

		if (board.length == 0)
			randomize();
		else
			serialize(board);
	}

	public byte[] serialize()
	{
		byte[] board = new byte[mWidth*mHeight];
		for (int j = 0; j < mHeight; ++j)
		for (int i = 0; i < mWidth;  ++i)
			board[i+j*mWidth] = mPipes[i][j];
		return board;
	}

	public void rotate (int i, int j)
	{
		if (i < 0 || i >= mWidth || j < 0 || j >= mHeight)
			return;

		if (i != mLastRotatedI  ||  j != mLastRotatedJ) {
			if (mLastRotatedI != -1)
				mPipes[mLastRotatedI][mLastRotatedJ] |= FIXED;
			mLastRotatedI = i;
			mLastRotatedJ = j;
		}

		doRotate(i, j);
	}

	public int width()  { return mWidth;  }
	public int height() { return mHeight; }
	public boolean right(int i, int j)  { return (mPipes[i][j] & RIGHT)==RIGHT; }
	public boolean down (int i, int j)  { return (mPipes[i][j] & DOWN )==DOWN ; }
	public boolean left (int i, int j)  { return (mPipes[i][j] & LEFT )==LEFT ; }
	public boolean up   (int i, int j)  { return (mPipes[i][j] & UP   )==UP   ; }
	public boolean fixed(int i, int j)  {
		return ((mPipes[i][j] & FIXED)==FIXED)
			|| (i==mLastRotatedI && j==mLastRotatedJ);
	}


	private void randomize()
	{
		Random rand = new Random();

		/* fill board with zeros */
		for (int j = 0; j < mHeight; ++j)
		for (int i = 0; i < mWidth;  ++i)
			mPipes[i][j] = 0;


		/* start with two cells connected */
		int i0 = rand.nextInt(mWidth-1);
		int j0 = rand.nextInt(mHeight);
		mPipes[i0][j0]   = RIGHT;
		mPipes[i0+1][j0] = LEFT;

		/* initialize the border */
		ArrayList<Integer> border = new ArrayList<Integer>();
		if (i0 > 0)
			border.add((i0-1) | ((j0+0)<<12));
		if (j0 > 0) {
			border.add((i0+0) | ((j0-1)<<12));
			border.add((i0+1) | ((j0-1)<<12));
		}
		if (i0 < mWidth-2)
			border.add((i0+2) | ((j0+0)<<12));
		if (j0 < mHeight-1) {
			border.add((i0+1) | ((j0+1)<<12));
			border.add((i0+0) | ((j0+1)<<12));
		}


		while (!border.isEmpty()) {

			/* remove a random cell from the border */
			int borderToRemove = rand.nextInt(border.size());
			int e = border.remove(borderToRemove);
			int i = e & 0xfff;
			int j = e >> 12;

			/* which directions can we go? */
			ArrayList<Byte> dirs = new ArrayList<Byte>();
			if (i < mWidth -1 && mPipes[i+1][j] != 0)
				dirs.add(RIGHT);
			if (j < mHeight-1 && mPipes[i][j+1] != 0)
				dirs.add(DOWN);
			if (i > 0 && mPipes[i-1][j] != 0)
				dirs.add(LEFT);
			if (j > 0 && mPipes[i][j-1] != 0)
				dirs.add(UP);

			/* choose a random direction */
			byte dir = dirs.get(rand.nextInt(dirs.size()));

			/* connect with cell in that direction */
			if (dir == RIGHT) {
				mPipes[i][j] = RIGHT;
				mPipes[i+1][j] |= LEFT;
			}
			else if (dir == DOWN) {
				mPipes[i][j] = DOWN;
				mPipes[i][j+1] |= UP;
			}
			else if (dir == LEFT) {
				mPipes[i][j] = LEFT;
				mPipes[i-1][j] |= RIGHT;
			}
			else if (dir == UP) {
				mPipes[i][j] = UP;
				mPipes[i][j-1] |= DOWN;
			}

			/* expand the border */
			if (i < mWidth -1 && mPipes[i+1][j] == 0 && !isBorder(border, i+1, j))
				border.add((i+1) | ((j+0)<<12));
			if (j < mHeight-1 && mPipes[i][j+1] == 0 && !isBorder(border, i, j+1))
				border.add((i+0) | ((j+1)<<12));
			if (i > 0 && mPipes[i-1][j] == 0 && !isBorder(border, i-1, j))
				border.add((i-1) | ((j+0)<<12));
			if (j > 0 && mPipes[i][j-1] == 0 && !isBorder(border, i, j-1))
				border.add((i+0) | ((j-1)<<12));
		}

		/* shuffle */
		for (int j = 0; j < mHeight; ++j)
		for (int i = 0; i < mHeight; ++i)
		for (int r = rand.nextInt(4); r > 0; --r)
			doRotate(i, j);
	}

	private void serialize(byte[] board)
	{
		if (board.length != mWidth*mHeight)
			throw new IllegalArgumentException("Invalid board string size.");

		for (int j = 0; j < mHeight; ++j)
		for (int i = 0; i < mWidth;  ++i)
			mPipes[i][j] = board[i+j*mWidth];
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
	}

	private boolean isBorder (ArrayList<Integer> border, int I, int J)
	{
		for (int b = 0; b < border.size(); ++b) {
			int e = border.get(b);
			int i = e & 0xfff;
			int j = e >> 12;
			if (i==I && j==J)
				return true;
		}
		return false;
	}

	private byte[][] mPipes;
	private int mWidth, mHeight;
	private int mLastRotatedI = -1, mLastRotatedJ = -1;

	private final byte RIGHT = 0x01;
	private final byte DOWN  = 0x02;
	private final byte LEFT  = 0x04;
	private final byte UP    = 0x08;
	private final byte FIXED = 0x10;
}

// vim600:fdm=syntax:fdn=2:nu:
