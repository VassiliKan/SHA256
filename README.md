# Note : the attached file sha256.java is an implementation of the sha256 algorithm, described below. 

All the methods in my implementation come from operations and functions of this document, except the method toNBits. Explanation : Since java cannot easily add two hexadecimal or binary numbers, I had to use the long datatype (also because that's more efficient to use it than converting all the time). However, when I had to convert a number from long to binary, the conversion result wasn't automatically a 32 bits word, a specific condition that is required by the algorithm. Indeed, the result of the Rotr operation on the string "00000000001101010101000101010101" is totally different than if you used "1101010101000101010101", even if these numbers are equal in base 10.
This method is also usefull in buildWords and paddingMsg methods.

## General description of SHA-256 algorithm (from CRIPTOGRAFIA MAII - FIB pdf)

SHA-256 (secure hash algorithm, FIPS 182-2) is a cryptographic hash function with digest length of 256
bits. It is a keyless hash function; that is, an MDC (Manipulation Detection Code).
A message is processed by blocks of 512 = 16 × 32 bits, each block requiring 64 rounds.

# Basic operations

• Boolean operations AND, XOR and OR, denoted by ^,  and _, respectively.
• Bitwise complement, denoted by ¯.
• Integer addition modulo 2^32, denoted by A + B.
  Each of them operates on 32-bit words. For the last operation, binary words are interpreted as
integers written in base 2.
• RotR(A, n) denotes the circular right shift of n bits of the binary word A.
• ShR(A, n) denotes the right shift of n bits of the binary word A.
• AkB denotes the concatenation of the binary words A and B.

# Functions and constants
The algorithm uses the functions:

	- ch(X, Y,Z) = (X ^ Y )  (X ^ Z),
	- maj(X, Y,Z) = (X ^ Y )  (X ^ Z)  (Y ^ Z),
	- bigSigma0(X) = RotR(X, 2)  RotR(X, 13)  RotR(X, 22),
	- bigSigma1(X) = RotR(X, 6)  RotR(X, 11)  RotR(X, 25),
	- smallSigma0(X) = RotR(X, 7)  RotR(X, 18)  ShR(X, 3),
	- smallSigma1(X) = RotR(X, 17)  RotR(X, 19)  ShR(X, 10),

and the 64 binary words Ki given by the 32 first bits of the fractional parts of the cube roots of the first
64 prime numbers:

	0x428a2f98 	0x71374491 	0xb5c0fbcf 	0xe9b5dba5 	0x3956c25b 	0x59f111f1 
	0x923f82a4 	0xab1c5ed5	0xd807aa98 	0x12835b01 	0x243185be 	0x550c7dc3 
	0x72be5d74 	0x80deb1fe 	0x9bdc06a7 	0xc19bf174	0xe49b69c1 	0xefbe4786 
	0x0fc19dc6 	0x240ca1cc 	0x2de92c6f 	0x4a7484aa 	0x5cb0a9dc 	0x76f988da
	0x983e5152 	0xa831c66d 	0xb00327c8 	0xbf597fc7 	0xc6e00bf3 	0xd5a79147 
	0x06ca6351 	0x14292967	0x27b70a85 	0x2e1b2138 	0x4d2c6dfc 	0x53380d13 
	0x650a7354 	0x766a0abb 	0x81c2c92e 	0x92722c85	0xa2bfe8a1 	0xa81a664b 
	0xc24b8b70 	0xc76c51a3 	0xd192e819 	0xd6990624 	0xf40e3585 	0x106aa070
	0x19a4c116 	0x1e376c08 	0x2748774c 	0x34b0bcb5 	0x391c0cb3 	0x4ed8aa4a 
	0x5b9cca4f 	0x682e6ff3	0x748f82ee 	0x78a5636f 	0x84c87814 	0x8cc70208 
	0x90befffa 	0xa4506ceb 	0xbef9a3f7 	0xc67178f2


# Padding
To ensure that the message1 has length multiple of 512 bits:
	• first, a bit 1 is appended,
	• next, k bits 0 are appended, with k being the smallest positive integer such that l + 1 + k  448
mod 512, where l is the length in bits of the initial message,
	• finally, the length l < 264 of the initial message is represented with exactly 64 bits, and these bits
are added at the end of the message.
The message shall always be padded, even if the initial length is already a multiple of 512.

# Block decomposition

For each block M 2 {0, 1}512, 64 words of 32 bits each are constructed as follows:
	• the first 16 are obtained by splitting M in 32-bit blocks
				M = W1 || W2 ··· || W15 || W16
	• the remaining 48 are obtained with the formula:
	  		Wi = smallSigma1(Wi−2) + Wi−7 + smallSigma0(Wi−15) +Wi−16

# Hash computation

• First, eight variables are set to their initial values, given by the first 32 bits of the fractional part
of the square roots of the first 8 prime numbers:

	H1 = 0x6a09e667    H2 = 0xbb67ae85    H3 = 0x3c6ef372    H4 = 0xa54ff53a   
	H5 = 0x510e527f     H6 = 0x9b05688c    H7 = 0x1f83d9ab    H8 = 0x5be0cd19

• Next, the blocks M(1), M(2), . . . ,M(N) are processed one at a time:

For t = 1 to N
	- construct the 64 blocks Wi from M(t), as explained above
	- set
		(a, b, c, d, e, f, g, h) = (H(t−1)1 ,H(t−1)2 ,H(t−1)3 ,H(t−1)4 ,H(t−1)5 , H(t−1)6 ,H(t−1)7 ,H(t−1)8 )
	- do 64 rounds consisting of:
				T1 = h + 1(e) + Ch(e, f, g) + Ki +Wi
				T2 = bigSigma0(a) +Maj(a, b, c)
				h = g
				g = f
				f = e
				e = d + T1
				d = c
				c = b
				b = a
				a = T1 + T2

1We assume that the length of the message can be represented by a 64-bit integer.


	- compute the new value of H(t)j
					H(t)1 = H(t−1)1 + a
					H(t)2 = H(t−1)2 + b
					H(t)3 = H(t−1)3 + c
					H(t)4 = H(t−1)4 + d
					H(t)5 = H(t−1)5 + e
					H(t)6 = H(t−1)6 + f
					H(t)7 = H(t−1)7 + g
					H(t)8 = H(t−1)8 + h




End for.

• The hash of the message is the concatenation of the variables HN
i after the last block has been processed :
		H = H(N)1|| H(N)2 || H(N)3 || H(N)4 || H(N)5 || H(N)6 || H(N)7 || H(N)8 
    
    
Execution time for the string "abc" :      9283100 ns.

Execution time for a 4020 length string : 95829800 ns.
    
