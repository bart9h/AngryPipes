package org.ninehells.angrypipes;

import java.util.Random;

class Board
{
	private byte[][] m_pipes;
	private int m_width, m_height;

	public Board(int width, int height)
	{
		m_width = width;
		m_height = height;
		m_pipes = new byte[width][height];

		Random rand = new Random();

		// TODO
		for (int j = 0; j < height; ++j)
		for (int i = 0; i < width;  ++i) {
			m_pipes[i][j] = (byte)rand.nextInt(16);
		}
	}

	public int width()  { return m_width; }
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
