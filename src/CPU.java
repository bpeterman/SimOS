public class CPU {
	int A;
	int B;
	int C;
	int D;
	int Acc;

	public CPU(int A, int B, int C, int D, int Acc) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.Acc = Acc;
	}

	@Override
	public String toString() {
		return "CPU [A=" + A + ", B=" + B + ", C=" + C + ", D=" + D + ", Acc="
				+ Acc + "]";
	}

	public int getRegCon(char reg) {
		if (reg == 'A')
			return getA();
		else if (reg == 'B')
			return getB();
		else if (reg == 'C')
			return getC();
		else if (reg == 'D')
			return getD();
		else
			return 0;
	}

	public void add(char reg1, char reg2) {
		setAcc((getRegCon(reg1) + getRegCon(reg2)) + getAcc());
	}

	public void sub(char reg1, char reg2) {
		setAcc((getRegCon(reg2) - getRegCon(reg1)) + getAcc());
	}

	public void mul(char reg1, char reg2) {
		setAcc((getRegCon(reg1) * getRegCon(reg2)) + getAcc());
	}

	public void div(char reg1, char reg2) {
		if (getRegCon(reg1) != 0 && getRegCon(reg1) != 0)
			setAcc((getRegCon(reg2) / getRegCon(reg1)) + getAcc());

	}

	public void sto(int val) {
		setAcc(val);
	}

	public void rcl(char reg1) {
		int regval = getAcc();
		if (reg1 == 'A')
			setA(regval);
		else if (reg1 == 'B')
			setB(regval);
		else if (reg1 == 'C')
			setC(regval);
		else if (reg1 == 'D')
			setD(regval);
	}

	public int getA() {
		return A;
	}

	public void setA(int a) {
		A = a;
	}

	public int getB() {
		return B;
	}

	public void setB(int b) {
		B = b;
	}

	public int getC() {
		return C;
	}

	public void setC(int c) {
		C = c;
	}

	public int getD() {
		return D;
	}

	public void setD(int d) {
		D = d;
	}

	public int getAcc() {
		return Acc;
	}

	public void setAcc(int acc) {
		Acc = acc;
	}

}
