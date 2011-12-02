package org.ninehells.angrypipes;

class Position
{
	boolean valid;
	int i, j;

	Position() {
		reset();
	}

	Position (Position a) {
		set(a);
	}

	Position (int a, int b) {
		set(a, b);
	}

	void set (Position a) {
		set(a.i, a.j);
	}

	void set (int a, int b) {
		i = a;
		j = b;
		valid = true;
	}

	void reset() {
		valid = false;
	}

	boolean equals (Position a) {
		return (valid && a.valid && i==a.i && j==a.j);
	}

	boolean equals (int a, int b) {
		return (valid && i==a && j==b);
	}
}

// vim600:fdm=marker:nu:
