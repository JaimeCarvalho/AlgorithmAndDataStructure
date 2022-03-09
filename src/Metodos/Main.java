package Metodos;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//
public class Main {

	public static void main(String[] args) {
		if (1.55 - (int)1.55 == 0.5 && (int)1.55 % 2 == 0)
			System.out.println("if" + (int)1.55);
		else
			System.out.println((int)Math.round(1.55));
		if (2.5 - (int)2.5 == 0.5 && (int)2.5 % 2 == 0)
			System.out.println("if" + (int)2.5);
		else
			System.out.println((int)Math.round(2.5));
		long pop = 395005;
		long totpop = 3893874;
		long mid = 104;
		float a = (float)(totpop/pop);
		System.out.println(a);
//		System.out.println((int)Math.round(1.2));
//		System.out.println(Math.round(1.5));
//		System.out.println(Math.round(1.7));
//		System.out.println(Math.round(2.5));

	}
}
