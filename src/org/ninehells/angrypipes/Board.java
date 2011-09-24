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

		m_rand = new Random();
		m_border = new ArrayList<Integer>();
		randomize();
		//shuffle();
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


	private byte[][] m_pipes;
	private int m_width, m_height;
	private Random m_rand;
	private ArrayList<Integer> m_border;

	private void randomize()
	{
		seed();
		//while (grow()) {}
	}

	private void seed()
	{
		int i, j;

		for (j = 0; j < m_height; ++j)
		for (i = 0; i < m_width;  ++i)
			m_pipes[i][j] = 0;

		/* start with two cells connected */
		i = m_rand.nextInt(m_width-1);
		j = m_rand.nextInt(m_height);
		m_pipes[i][j] = 1;
		m_pipes[i+1][j] = 4;

		/* initial border */
		if (i > 0)
			m_border.add((i-1) | ((j+0)<<12));
		if (j > 0) {
			m_border.add((i+0) | ((j-1)<<12));
			m_border.add((i+1) | ((j-1)<<12));
		}
		if (i < m_width-2)
			m_border.add((i+2) | ((j+0)<<12));
		if (j < m_height-1) {
			m_border.add((i+1) | ((j+1)<<12));
			m_border.add((i+0) | ((j+1)<<12));
		}
		printBorda();
	}

	public void printBorda()
	{
		System.out.printf("border ");
		for(int b = 0; b < m_border.size(); ++b) {
			int e = m_border.get(b);
			int i = e & 0xfff;
			int j = e >> 12;
			System.out.printf(" %d=(%d,%d)", b, i, j);
		}
		System.out.printf("\n");
	}

	public boolean grow()
	{
		System.out.println("====== grow ======");
		printBorda();

		if (m_border.isEmpty())
			return false;

		/* remove a random cell from the border */
		int borderToRemove = m_rand.nextInt(m_border.size());
		int e = m_border.remove(borderToRemove);
		int i = e & 0xfff;
		int j = e >> 12;
		System.out.printf("borderToRemove=%s, e=%06x, i=%d, j=%d\n", borderToRemove, e, i, j);

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
		System.out.printf("dirs ");
		for(int b = 0; b < dirs.size(); ++b) {
			System.out.printf(" %d", dirs.get(b));
		}
		System.out.printf("\n");

		/* choose a random direction */
		int dir = dirs.get(m_rand.nextInt(dirs.size()));
		System.out.printf("dir=%d\n", dir);

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
		if (i < m_width-1 && m_pipes[i+1][j] == 0)
			m_border.add((i+1) | ((j+0)<<12));
		if (j < m_height-1 && m_pipes[i][j+1] == 0)
			m_border.add((i+0) | ((j+1)<<12));
		if (i > 0 && m_pipes[i-1][j] == 0)
			m_border.add((i-1) | ((j+0)<<12));
		if (j > 0 && m_pipes[i][j-1] == 0)
			m_border.add((i+0) | ((j-1)<<12));
		printBorda();

		return true;
	}

	private void shuffle()
	{
		for (int j = 0; j < m_height; ++j)
		for (int i = 0; i < m_height; ++i)
		for (int r = m_rand.nextInt(4); r > 0; --r)
			rotate(i, j);
	}

	public boolean isBorder (int I, int J)
	{
		for (int b = 0; b < m_border.size(); ++b) {
			int e = m_border.get(b);
			int i = e & 0xfff;
			int j = e >> 12;
			if (i==I && j==J)
				return true;
		}
		return false;
	}
}

// vim600:fdm=syntax:fdn=2:
