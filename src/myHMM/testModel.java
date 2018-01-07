package myHMM;

public class testModel {
	
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
		System.out.println("max Prob:"+delta[M-1][maxIndex]);
		System.out.print(maxIndex);
		for (int i = M-1; i >= 1; i--) {
			maxIndex = psi[i][maxIndex];
			System.out.print(maxIndex);
		}
		return input;
	}
	
	public static void main(String[] args) {
		testModel model = new testModel();
		model.segment("我爱中华人民共和国。");
	}
}
