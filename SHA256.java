import java.util.Scanner;

/**
 * @author VassiliKan
 *
 */
public class SHA256 {
			
	private static String msg = "";
	
	private static long modulus = 4294967296L;   // 2^32
	
	private static long H[] = new long[8];
			
	private static long K[] = {
			1116352408L, 1899447441L, 3049323471L, 3921009573L, 961987163L,  1508970993L, 2453635748L, 2870763221L,
			3624381080L, 310598401L,  607225278L,  1426881987L, 1925078388L, 2162078206L, 2614888103L, 3248222580L, 
			3835390401L, 4022224774L, 264347078L,  604807628L,  770255983L,  1249150122L, 1555081692L, 1996064986L, 
			2554220882L, 2821834349L, 2952996808L, 3210313671L, 3336571891L, 3584528711L, 113926993L,  338241895L, 
			666307205L,  773529912L,  1294757372L, 1396182291L, 1695183700L, 1986661051L, 2177026350L, 2456956037L, 
			2730485921L, 2820302411L, 3259730800L, 3345764771L, 3516065817L, 3600352804L, 4094571909L, 275423344L, 
			430227734L,  506948616L,  659060556L,  883997877L,  958139571L,  1322822218L, 1537002063L, 1747873779L, 
			1955562222L, 2024104815L, 2227730452L, 2361852424L, 2428436474L, 2756734187L, 3204031479L, 3329325298L };
	
	private static String W[] = new String [64];
	
	private static long a,b,c,d,e,f,g,h;
	
	public static void main(String[] args) {
		msg = paddingMsg();
		int nbBlock = msg.length() % 512;
		String block;
		for (int i = 0; i <= nbBlock; i++) {
			block = msg.substring(512*i, 512*(i+1));
			buildWord(block);
			setParam();
			for (int j = 0; j < 64; j++) {
				insideLoop(j);
			}
			computeH();
		}
		System.out.println(concatenate());
	}

	/*
	 * Convert the input into bytes, then add zeros until the total length of the message is a multiple of 512
	 * Total length includes : msg in bytes, a '1' after the msg, and the length of the messages in binary (at the end of everything)
	 */
	public static String paddingMsg() {
		Scanner sc = new Scanner(System.in);
		String msg = sc.nextLine();
		String msgBinary = "";
		String strLenBin;
		char msgToChar[] = msg.toCharArray();
		for (int i = 0; i < msgToChar.length; i++) {
			msgBinary += toNBits(Integer.toBinaryString(msgToChar[i]), 8);
		}
		strLenBin = Integer.toBinaryString(msgBinary.length());
		msgBinary += '1';
		while (((msgBinary.length() + strLenBin.length()) % 512) != 0) {
			msgBinary += '0';
		}
		return msgBinary += strLenBin;
	}
	
	/*
	 * Build 64 W[] words for each M {0,1} block of 512 bytes. 16 first are just 32i bytes of the msg, then 17 to 64 are build
	 * using smallSigma function 0 and 1, and previous results
	 */
	public static void buildWord(String msg) {
		for (int i = 0; i < 16; i++) {
			W[i] = msg.substring(32*i, 32*(i+1));
		}
		for (int i = 16; i < 64; i++) {
			Long tempLong = (smallSigma1(W[i-2]) + Long.parseLong(W[i-7],2) + smallSigma0(W[i-15]) + Long.parseLong(W[i-16], 2)) % modulus;
			W[i] = toNBits(Long.toBinaryString(tempLong), 32);
		}
	}
	
	/*
	 * set initial value of a,b,c,d,e,f,g,h and H[i] 0 <= i <= 7 equals to the fractional part 
	 * of the square roots of the first 8 prime numbers
	 */
	public static void setParam() {
		a = H[0] = 1779033703L;
		b = H[1] = 3144134277L;
		c = H[2] = 1013904242L;
		d = H[3] = 2773480762L;
		e = H[4] = 1359893119L;
		f = H[5] = 2600822924L;
		g = H[6] = 528734635L;
		h = H[7] = 1541459225L;
	}
	
	
	/*
	 * 
	 */
	public static void insideLoop(int j) {
		long temp1, temp2;
		temp1 = (h + bigSigma1(Long.toBinaryString(e)) + Ch(e, f, g) + K[j] + Long.parseLong(W[j], 2)) % modulus;
		temp2 = (bigSigma0(Long.toBinaryString(a)) + Maj (a, b ,c)) % modulus;
		h = g;
		g = f;
		f = e;
		e = (d + temp1) % modulus;
		d = c;
		c = b;
		b = a;
		a = (temp1 + temp2) % modulus;
	}
	
	/*
	 * Compute the new value of H 
	 */
	public static void computeH() {
		H[0] = (H[0] + a) % modulus;
		H[1] = (H[1] + b) % modulus;
		H[2] = (H[2] + c) % modulus;
		H[3] = (H[3] + d) % modulus;
		H[4] = (H[4] + e) % modulus;
		H[5] = (H[5] + f) % modulus;
		H[6] = (H[6] + g) % modulus;
		H[7] = (H[7] + h) % modulus;
	}
	
	
	public static String concatenate() {
		String digest = "";
		for (int i = 0; i < 8; i++) {
			digest += Long.toHexString(H[i]);
		}
		return digest;
	}
	
	private static long Ch(Long X, Long Y, Long Z) {
		return (X & Y) ^ (~X & Z);
	}
	
	private static long Maj(Long X, Long Y, Long Z) {
		return (X & Y) ^ (X & Z) ^ (Y & Z);
	}

	private static long bigSigma0(String X) {
		return RotR(X, 2) ^ RotR(X, 13) ^ RotR(X, 22);
	}
	
	private static long bigSigma1(String X) {
		return RotR(X, 6) ^ RotR(X, 11) ^ RotR(X, 25);
	}
	
	private static long smallSigma0(String X) {
		return RotR(X, 7) ^ RotR(X, 18) ^ ShR(X, 3);
	}

	private static long smallSigma1(String X) {
		return RotR(X, 17) ^ RotR(X, 19) ^ ShR(X, 10);
	}
	
	private static long ShR(String X, int i) {
		return Long.parseLong(X, 2) >> i;
	}

	private static long RotR(String X, int i) {
		String str = toNBits(X, 32);
		return Long.parseLong(str.substring(str.length()-i) + str.substring(0, str.length()-i), 2);
	}
	
	
	private static String toNBits(String str, int n) {
		String temp = "";
		while (str.length() + temp.length() != n) {
			temp += '0';
		}
		return temp + str;
	}
	
}