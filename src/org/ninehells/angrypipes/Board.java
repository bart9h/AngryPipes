package org.ninehells.angrypipes;

import java.util.ArrayList;
import java.util.Random;

import java.io.Console;

class Board
{
	public Board(int width, int height)
	{
		if (width < 2 || height < 2 || width > 4000 || height > 4000)
			throw new IllegalArgumentException("invalid board size");

		m_width = width;
		m_height = height;
		m_pipes = new byte[width][height];

		Random rand = new Random();

		/* fill board with zeros */
		for (int j = 0; j < m_height; ++j)
		for (int i = 0; i < m_width;  ++i)
			m_pipes[i][j] = 0;


		/* start with two cells connected */
		int i0 = rand.nextInt(m_width-1);
		int j0 = rand.nextInt(m_height);
		m_pipes[i0][j0] = 1;
		m_pipes[i0+1][j0] = 4;

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
			ArrayList<Integer> dirs = new ArrayList<Integer>();
			if (i < m_width-1 && m_pipes[i+1][j] != 0)
				dirs.add(0);
			if (j < m_height-1 && m_pipes[i][j+1] != 0)
				dirs.add(1);
			if (i > 0 && m_pipes[i-1][j] != 0)
				dirs.add(2);
			if (j > 0 && m_pipes[i][j-1] != 0)
				dirs.add(3);

			/* choose a random direction */
			int dir = dirs.get(rand.nextInt(dirs.size()));

			/* connect with cell in that direction */
			if (dir == 0) {
				m_pipes[i][j] = 1;
				m_pipes[i+1][j] |= 4;
			}
			else if (dir == 1) {
				m_pipes[i][j] = 2;
				m_pipes[i][j+1] |= 8;
			}
			else if (dir == 2) {
				m_pipes[i][j] = 4;
				m_pipes[i-1][j] |= 1;
			}
			else if (dir == 3) {
				m_pipes[i][j] = 8;
				m_pipes[i][j-1] |= 2;
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
			rotate(i, j);
	}

	public int width()  { return m_width;  }
	public int height() { return m_height; }

	public boolean right(int i, int j)  { return (m_pipes[i][j] & 1)==1; }
	public boolean down (int i, int j)  { return (m_pipes[i][j] & 2)==2; }
	public boolean left (int i, int j)  { return (m_pipes[i][j] & 4)==4; }
	public boolean up   (int i, int j)  { return (m_pipes[i][j] & 8)==8; }

	public void rotate (int i, int j)
	{
		if (i < 0 || i >= m_width || j < 0 || j >= m_height)
			return;

		byte b = m_pipes[i][j];
		b <<= 1;
		if (b > 0xf)
			b -= 0xf;
		m_pipes[i][j] = b;
	}


	private byte[][] m_pipes;
	private int m_width, m_height;

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
