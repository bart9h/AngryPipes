package org.ninehells.angrypipes;

import java.util.ArrayList;
import java.util.Random;

import java.io.Console;

class Board
{
	public Board (int width, int height, byte[] board)
	{
		if (width < 2 || height < 2 || width > 4000 || height > 4000)
			throw new IllegalArgumentException("invalid board size");

		m_width = width;
		m_height = height;
		m_pipes = new byte[width][height];
		m_lastRotatedI = m_lastRotatedJ = -1;

		if (board == null)
			randomize();
		else
			serialize(board);
	}

	private void serialize(byte[] board)
	{
		for (int j = 0; j < m_height; ++j)
		for (int i = 0; i < m_width;  ++i)
			m_pipes[i][j] = board[i+j*m_width];
	}

	public byte[] serialize()
	{
		byte[] board = new byte[m_width*m_height];
		for (int j = 0; j < m_height; ++j)
		for (int i = 0; i < m_width;  ++i)
			board[i+j*m_width] = m_pipes[i][j];
		return board;
	}

	void randomize()
	{
		Random rand = new Random();

		/* fill board with zeros */
		for (int j = 0; j < m_height; ++j)
		for (int i = 0; i < m_width;  ++i)
			m_pipes[i][j] = 0;


		/* start with two cells connected */
		int i0 = rand.nextInt(m_width-1);
		int j0 = rand.nextInt(m_height);
		m_pipes[i0][j0]   = RIGHT;
		m_pipes[i0+1][j0] = LEFT;

		/* initialize the border */
		ArrayList<Integer> border = new ArrayList<Integer>();
		if (i0 > 0)
			border.add((i0-1) | ((j0+0)<<12));
		if (j0 > 0) {
			border.add((i0+0) | ((j0-1)<<12));
			border.add((i0+1) | ((j0-1)<<12));
		}
		if (i0 < m_width-2)
			border.add((i0+2) | ((j0+0)<<12));
		if (j0 < m_height-1) {
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
			if (i < m_width -1 && m_pipes[i+1][j] != 0)
				dirs.add(RIGHT);
			if (j < m_height-1 && m_pipes[i][j+1] != 0)
				dirs.add(DOWN);
			if (i > 0 && m_pipes[i-1][j] != 0)
				dirs.add(LEFT);
			if (j > 0 && m_pipes[i][j-1] != 0)
				dirs.add(UP);

			/* choose a random direction */
			byte dir = dirs.get(rand.nextInt(dirs.size()));

			/* connect with cell in that direction */
			if (dir == RIGHT) {
				m_pipes[i][j] = RIGHT;
				m_pipes[i+1][j] |= LEFT;
			}
			else if (dir == DOWN) {
				m_pipes[i][j] = DOWN;
				m_pipes[i][j+1] |= UP;
			}
			else if (dir == LEFT) {
				m_pipes[i][j] = LEFT;
				m_pipes[i-1][j] |= RIGHT;
			}
			else if (dir == UP) {
				m_pipes[i][j] = UP;
				m_pipes[i][j-1] |= DOWN;
			}

			/* expand the border */
			if (i < m_width -1 && m_pipes[i+1][j] == 0 && !isBorder(border, i+1, j))
				border.add((i+1) | ((j+0)<<12));
			if (j < m_height-1 && m_pipes[i][j+1] == 0 && !isBorder(border, i, j+1))
				border.add((i+0) | ((j+1)<<12));
			if (i > 0 && m_pipes[i-1][j] == 0 && !isBorder(border, i-1, j))
				border.add((i-1) | ((j+0)<<12));
			if (j > 0 && m_pipes[i][j-1] == 0 && !isBorder(border, i, j-1))
				border.add((i+0) | ((j-1)<<12));
		}

		/* shuffle */
		for (int j = 0; j < m_height; ++j)
		for (int i = 0; i < m_height; ++i)
		for (int r = rand.nextInt(4); r > 0; --r)
			doRotate(i, j);
	}

	public int width()  { return m_width;  }
	public int height() { return m_height; }

	public boolean right(int i, int j)  { return (m_pipes[i][j] & RIGHT)==RIGHT; }
	public boolean down (int i, int j)  { return (m_pipes[i][j] & DOWN )==DOWN ; }
	public boolean left (int i, int j)  { return (m_pipes[i][j] & LEFT )==LEFT ; }
	public boolean up   (int i, int j)  { return (m_pipes[i][j] & UP   )==UP   ; }
	public boolean fixed(int i, int j)  {
		return ((m_pipes[i][j] & FIXED)==FIXED)
			|| (i==m_lastRotatedI && j==m_lastRotatedJ);
	}

	public void rotate (int i, int j)
	{
		if (i < 0 || i >= m_width || j < 0 || j >= m_height)
			return;

		if (i != m_lastRotatedI  ||  j != m_lastRotatedJ) {
			if (m_lastRotatedI != -1)
				m_pipes[m_lastRotatedI][m_lastRotatedJ] |= FIXED;
			m_lastRotatedI = i;
			m_lastRotatedJ = j;
		}

		doRotate(i, j);
	}

	private void doRotate (int i, int j)
	{
		byte b = m_pipes[i][j];

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

		m_pipes[i][j] = b;
	}


	private byte[][] m_pipes;
	private int m_width, m_height;
	private int m_lastRotatedI, m_lastRotatedJ;

	private final byte RIGHT = 0x01;
	private final byte DOWN  = 0x02;
	private final byte LEFT  = 0x04;
	private final byte UP    = 0x08;
	private final byte FIXED = 0x10;

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
}

// vim600:fdm=syntax:fdn=2:
