package myHMM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ReadTrain {
	private final static String trainPath = "icwb2-data/training/pku_training.utf8"; //存储的训练集的地址
	
	/**
	 * 下面这一块定义了状态的值对应关系
	 */
	public final static int B = 0;
	public final static int E = 1;
	public final static int M = 2;
	public final static int S = 3;
	
	public double[][] matrixA; //状态转移概率矩阵
	public int N; //状态个数
	
	@SuppressWarnings("rawtypes")
	public HashMap[] matrixB; //观测概率矩阵
	
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
		matrixA = new double [N][N];
		matrixB = new HashMap [N];
		for (int i = 0; i < N; i++) {
			matrixB[i] = new HashMap<String, Double>();
		}
		pi = new double [N];
		for (int i = 0; i < N; i++) {
			pi[i] = 0.0;
			for (int j = 0; j < N; j++) {
				matrixA[i][j] = 0.0;
			}
		}
		this.readFile();
	}
	
	/**
	 * 读取训练集
	 */
	private void readFile() {
		InputStreamReader isr;
		BufferedReader br;
		int count = 0;
		try {
			isr = new InputStreamReader(new FileInputStream(ReadTrain.trainPath), "utf-8");
			br = new BufferedReader(isr);
			String line;
			while(true) {
				line = br.readLine();
				count++;
				if (count > 10) {
					break;
				}
				if (line == null) {
					break;
				}
				String[] lines = line.split(" ");
				boolean isFirst = true; //判断是否为首个字段
				for (int i = 0; i < lines.length; i++) {
					if (lines[i].length() != 0) {
						//System.out.println(lines[i]+" "+lines[i].length());
						if (isFirst) {
							if (lines[i].length() == 1) {
								pi[S] += 1.0;
							} else {
								pi[B] += 1.0;
							}
							isFirst = false;
						}
					}	
				}
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
