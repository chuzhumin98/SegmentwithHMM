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
	
	public static void main(String[] args) {
		testModel model = new testModel();
		String words = "解决好经济和社会发展中一系列关乎全局的重大问题";
		String splits = model.segment(words);
		System.out.println("原语句："+words);
		System.out.println("分词后："+splits);
		
		words = "邓小平理论以及根据这一理论形成的“一个中心、两个基本点”的基本路线和党在社会主义初级阶段的政治、经济、文化建设的基本纲领";
		splits = model.segment(words);
		System.out.println("原语句："+words);
		System.out.println("分词后："+splits);
	}
}
