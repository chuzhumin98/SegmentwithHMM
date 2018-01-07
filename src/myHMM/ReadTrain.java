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
			matrixB[i] = new HashMap<Character, Double>();
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
	@SuppressWarnings("unchecked")
	private void readFile() {
		InputStreamReader isr;
		BufferedReader br;
		int count = 0; //总行数
		int countUseful = 0; //有效行数
		try {
			isr = new InputStreamReader(new FileInputStream(ReadTrain.trainPath), "utf-8");
			br = new BufferedReader(isr);
			String line;
			while(true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				count++;
				String[] lines = line.split(" ");
				boolean isFirst = true; //判断是否为首个字段
				int predOne = -1; //初始化前一个状态为-1，表示无前一个状态
				for (int i = 0; i < lines.length; i++) {
					if (lines[i].length() != 0) {
						//System.out.println(lines[i]+" "+lines[i].length());
						//下面配置PI向量
						if (isFirst) {
							countUseful++;
							if (lines[i].length() == 1) {
								pi[S] += 1.0;
							} else {
								pi[B] += 1.0;
							}
							isFirst = false;
						}
						
						//下面配置A和B矩阵
						if (lines[i].length() == 1) {
							if (predOne != -1) {
								this.matrixA[predOne][S] += 1.0;	
							}
							predOne = S;
							
							Character thisKey = lines[i].charAt(0); 
							if (this.matrixB[S].containsKey(thisKey)) {
								double predValue = (double) this.matrixB[S].get(thisKey);
								this.matrixB[S].put(thisKey, predValue+1);
							} else {
								this.matrixB[S].put(thisKey, 1.0);
							}
						} else {
							if (predOne != -1) {
								this.matrixA[predOne][B] += 1.0;
							}
							predOne = E;
							if (lines[i].length() == 2) {
								this.matrixA[B][E] += 1.0;
							} else {
								this.matrixA[B][M] += 1.0;
								this.matrixA[M][M] += lines[i].length() - 3.0;
								this.matrixA[M][E] += 1.0;
							}
							
							for(int j = 0; j < lines[i].length(); j++) {
								int index = E;
								if (j == 0) {
									index = B;
								} else if (j != lines[i].length() - 1) {
									index = M;
								}
								Character thisKey = lines[i].charAt(j); 
								if (this.matrixB[index].containsKey(thisKey)) {
									double predValue = (double) this.matrixB[index].get(thisKey);
									this.matrixB[index].put(thisKey, predValue+1);
								} else {
									this.matrixB[index].put(thisKey, 1.0);
								}
							}
						}
					}	
				}
			}
			System.out.println("lines:"+count);
			System.out.println("useful lines:"+countUseful);
			
			for (int i = 0; i < N; i++) {
				pi[i] /= (double)countUseful;
			}
			this.normalMatrix(matrixA);

			this.toStringPI();
			this.toStringA();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 输出PI向量
	 */
	public void toStringPI() {
		String output = "";
		output += "pi array:\n[";
		for (int i = 0; i < N; i++) {
			if (i == 0) {
				output += " "+pi[i];
			} else {
				output += ", "+pi[i];
			}
		}
		output += " ]";
		System.out.println(output);
	}
	
	/**
	 * 输出A矩阵
	 */
	public void toStringA() {
		String output = "matrix A:\n";
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				output += this.matrixA[i][j] + " ";
			}
			output += "\n";
		}
		System.out.println(output);
	}

	/**
	 * 对矩阵做归一化处理
	 * 
	 * @param M
	 */
	public void normalMatrix(double[][] M) {
		for (int i = 0; i < M.length; i++) {
			double sums = 0.0;
			for (int j = 0; j < M[i].length; j++) {
				sums += M[i][j];
			}
			if (sums < 1e-5) {
				System.err.println("the denerator is zero!");
				for (int j = 0; j < M[i].length; j++) {
					M[i][j] = 1.0 / (double)M[i].length;
				}
			} else {
				for (int j = 0; j < M[i].length; j++) {
					M[i][j] /= sums;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		ReadTrain train = ReadTrain.getInstance();
	}
}
