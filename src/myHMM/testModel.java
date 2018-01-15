package myHMM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class testModel {
	private final static String testPath = "icwb2-data/testing/pku_test.utf8"; //存储的训练集的地址
	
	/**
	 * 针对输入的句子进行分词处理
	 * 输出分词只有的结果，中间用空格隔开
	 * 
	 * @param input
	 * @return
	 */
	public String segment(String input) {
		ReadTrain train = ReadTrain.getInstance();
		int N = train.N;
		int M = input.length();
		double[][] delta = new double [M][N]; //用来存储出现概率的矩阵
		int[][] psi = new int [M][N]; //用来存储上一步位置的矩阵
		for (int i = 0; i < M; i++) {
			Character word = input.charAt(i);
			double[] prob = train.getSqecifyB(word);
			if (i == 0) { //初始的一层
				for (int j = 0; j < N; j++) {
					delta[i][j] = train.pi[j] * prob[j];
				}
			} else {
				for (int j = 0; j < N; j++) {
					double maxValue = Double.MIN_VALUE;
					int index = 0;
					for (int k = 0; k < N; k++) {
						double nowValue = delta[i-1][k] * train.matrixA[k][j];
						if (nowValue > maxValue) {
							maxValue = nowValue;
							index = k;
						}
					}
					psi[i][j] = index;
					delta[i][j] = maxValue * prob[j];
				}
			}
		}
		int maxIndex = ReadTrain.E;
		if (delta[M-1][ReadTrain.S] > delta[M-1][ReadTrain.E]) {
			maxIndex = ReadTrain.S;
		}
		int[] result = new int [M];
		result[M-1] = maxIndex;
		
		System.out.println("max Prob:"+delta[M-1][maxIndex]);
		System.out.print(maxIndex);
		for (int i = M-1; i >= 1; i--) {
			maxIndex = psi[i][maxIndex];
			result[i-1] = maxIndex;
			System.out.print(maxIndex);
		}
		System.out.println();
		String splitWords = "";
		for (int i = 0; i < M; i++) {
			splitWords += input.charAt(i);
			if (result[i] == ReadTrain.E || result[i] == ReadTrain.S) {
				splitWords += " ";
			}
		}
		return splitWords;
	}
	
	public void doTest() throws FileNotFoundException, UnsupportedEncodingException {
		InputStreamReader isr = null;
		BufferedReader br;
		String outPath = "output/HMM.txt";
		PrintWriter out = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outPath),"GB2312"))); //输出文件
		
		try {
			isr = new InputStreamReader(new FileInputStream(testModel.testPath), "utf-8");
		} catch (UnsupportedEncodingException | FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		br = new BufferedReader(isr);
		String line;
		while(true) {
			try {
				line = br.readLine();
				if (line == null || line.length() == 0) {
					break;
				}
				String splits = this.segment(line);
				out.println(splits);
				System.out.println(splits);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		testModel model = new testModel();
		model.doTest();
		/**
		String words = "解决好经济和社会发展中一系列关乎全局的重大问题";
		String splits = model.segment(words);
		System.out.println("原语句："+words);
		System.out.println("分词后："+splits);
		
		words = "邓小平理论以及根据这一理论形成的“一个中心、两个基本点”的基本路线和党在社会主义初级阶段的政治、经济、文化建设的基本纲领";
		splits = model.segment(words);
		System.out.println("原语句："+words);
		System.out.println("分词后："+splits);
		*/
	}
}
