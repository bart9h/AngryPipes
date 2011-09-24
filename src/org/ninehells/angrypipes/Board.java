package org.ninehells.angrypipes;

import java.util.ArrayList;
import java.util.Random;

class Board
{
	private byte[][] m_pipes;
	private int m_width, m_height;

	public Board(int width, int height)
	{
		if (width < 2 || height < 2 || width > 4000 || height > 4000)
			throw new IllegalArgumentException("invalid board size");

		m_width = width;
		m_height = height;
		m_pipes = new byte[width][height];

		Random rand = new Random();
		int i, j;

		for (j = 0; j < height; ++j)
		for (i = 0; i < width;  ++i)
			m_pipes[i][j] = 0;

		/* start with two cells connected */
		i = rand.nextInt(width-1);
		j = rand.nextInt(height);
		m_pipes[i][j] = 1;
		m_pipes[i+1][j] = 4;

		/* initial border */
		ArrayList<Integer> border = new ArrayList<Integer>();
		if (i > 0)
			border.add((i-1) | ((j+0)<<12));
		if (j > 0) {
			border.add((i+0) | ((j-1)<<12));
			border.add((i+1) | ((j-1)<<12));
		}
		if (i < width-2)
			border.add((i+2) | ((j+0)<<12));
		if (j < height-1) {
			border.add((i+1) | ((j+1)<<12));
			border.add((i+0) | ((j+1)<<12));
		}

		/* while there are elemtns in the border */
		ArrayList<Integer> dirs = new ArrayList<Integer>();
		while (!border.isEmpty()) {

			/* remove a random cell from the border */
			int e = border.remove(rand.nextInt(border.size()));
			i = e & 0xfff;
			j = e >> 12;

			/* which directions can we go? */
			if (i < width-1 && m_pipes[i+1][j] != 0)
				dirs.add(0);
			if (j < height-1 && m_pipes[i][j+1] != 0)
				dirs.add(1);
			if (i > 0 && m_pipes[i-1][j] != 0)
				dirs.add(2);
			if (j > 0 && m_pipes[i][j-1] != 0)
				dirs.add(3);

			/* choose a random direction */
			int dir = dirs.get(rand.nextInt(dirs.size()));
			dirs.clear();

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
			if (i < width-1 && m_pipes[i+1][j] == 0)
				border.add((i+1) | ((j+0)<<12));
			if (j < height-1 && m_pipes[i][j+1] == 0)
				border.add((i+0) | ((j+1)<<12));
			if (i > 0 && m_pipes[i-1][j] == 0)
				border.add((i-1) | ((j+0)<<12));
			if (j > 0 && m_pipes[i][j-1] == 0)
				border.add((i+0) | ((j-1)<<12));
		}
	}

	public int width()  { return m_width;  }
	public int height() { return m_height; }

	public boolean right(int i, int j)  { return (m_pipes[i][j] & 1)==1; }
	public boolean down (int i, int j)  { return (m_pipes[i][j] & 2)==2; }
	public boolean left (int i, int j)  { return (m_pipes[i][j] & 4)==4; }
	public boolean up   (int i, int j)  { return (m_pipes[i][j] & 8)==8; }

	public void rotate (int i, int j)
	{
		byte b = m_pipes[i][j];
		b <<= 1;
		if (b > 0xf)
			b -= 0xf;
		m_pipes[i][j] = b;
	}
}
