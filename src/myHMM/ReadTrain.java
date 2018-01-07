package myHMM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class ReadTrain {
	private final static String trainPath = "icwb2-data/training/pku_training.utf8"; //存储的训练集的地址
	
	public double[][] A; //状态转移概率矩阵
	public int N; //状态个数
	
	@SuppressWarnings("rawtypes")
	public HashMap[] B; //观测概率矩阵
	
	public double[] pi; //初值矩阵
	
	private static ReadTrain myTrain = null;
	
	/**
	 * 获取该类的单子
	 * 
	 * @return
	 */
	public static ReadTrain getInstance() {
		if (myTrain == null) {
			myTrain = new ReadTrain();
		}
		return myTrain;
	}
	
	/**
	 * 构造函数，产生一些基础的矩阵配置
	 */
	private ReadTrain() {
		N = 4; //定义了4种状态，分别为Begin,End,Middle,Single
		A = new double [N][N];
		B = new HashMap [N];
		for (int i = 0; i < N; i++) {
			B[i] = new HashMap<String, Double>();
		}
		pi = new double [N];
		this.readFile();
	}
	
	/**
	 * 读取训练集
	 */
	private void readFile() {
		InputStreamReader isr;
		BufferedReader br;
		try {
			isr = new InputStreamReader(new FileInputStream(ReadTrain.trainPath), "utf-8");
			br = new BufferedReader(isr);
			String line;
			while(true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ReadTrain train = ReadTrain.getInstance();
	}
}
