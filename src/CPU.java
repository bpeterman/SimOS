import java.util.List;


public class CPU {
	int A;
	int B;
	int C;
	int D;
	int Acc;
	int lastintr;

	public CPU(int A, int B, int C, int D, int Acc, int lastintr) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.Acc = Acc;
		this.lastintr = lastintr;
	}
	
	public void add(int reg1, int reg2){
		setAcc(reg1+reg2);
	}
	
	public void sub(int reg1, int reg2){
		setAcc(reg2-reg1);
	}
	public void mul(int reg1, int reg2){
		setAcc(reg1*reg2);
	}
	public void div(int reg1, int reg2){
		setAcc(reg2/reg1);
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

	public int getLastintr() {
		return lastintr;
	}

	public void setLastintr(int lastintr) {
		this.lastintr = lastintr;
	}	
	
}
