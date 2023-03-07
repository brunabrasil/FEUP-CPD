import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

class MatrixProduct {
	private static FileWriter writer;
	private static FileWriter writer2;
	
	public MatrixProduct() {
		try {
			writer = new FileWriter("ResultsEX1.txt");
			writer2 = new FileWriter("ResultsEX2_line.txt");
		} catch (IOException e) {
			e.printStackTrace();
	 	}
	}

	public static void OnMult(int m_ar, int m_br) throws IOException {
		if (writer == null) {
			writer = new FileWriter("ResultsEX1.txt");
		}
		int numIteracoes = 10;
		long Time1, Time2;

		String st; // not used. keeping here just in case
		double temp;
		int i, j, k;

		double[] pha = new double[m_ar*m_ar];
		double[] phb = new double[m_ar*m_ar];
		double[] phc = new double[m_ar*m_ar];

		for(i=0; i<m_ar; i++)
			for(j=0; j<m_ar; j++)
				pha[i*m_ar + j] = (double)1.0;

		for(i=0; i<m_br; i++)
			for(j=0; j<m_br; j++)
				phb[i*m_br + j] = (double)(i+1);

		for (int count = 1; count <= numIteracoes; count++) {
			Time1 = System.currentTimeMillis();

			for(i=0; i<m_ar; i++){
				for(j=0; j<m_br; j++){
					temp = 0;
					for( k=0; k<m_ar; k++) {   
						temp += pha[i*m_ar+k] * phb[k*m_br+j];
					}
					phc[i*m_ar+j]=temp;
				}
			}

			Time2 = System.currentTimeMillis();

			//writer.write(String.format("Time: %3.3f seconds\n", (double)(Time2-Time1)/1000));
			if (count==1) {
				writer.write(String.format("Matrix size: %d x %d\n\n", m_ar, m_ar));
				writer.write("Result matrix: \n");
				for (i = 0; i < 1; i++) {
					for (j = 0; j < Math.min(10, m_br); j++) {
						writer.write(phc[j] + " ");
					}
				}
				writer.write("\n\n");
			}
			writer.write(String.format("Time: %3.3f seconds\n", (double)(Time2-Time1)/1000));
		}
	}

	public static void OnMultLine(int m_ar, int m_br) throws IOException {
		if (writer2 == null) {
			writer2 = new FileWriter("ResultsEX2_line.txt");
		}

		int numIteracoes = 10;
		long Time1, Time2;

		String st; // not used. keeping here just in case
		double temp;
		int i, j, k;

		double[] pha = new double[m_ar*m_ar];
		double[] phb = new double[m_ar*m_ar];
		double[] phc = new double[m_ar*m_ar];

		for(i=0; i<m_ar; i++)
			for(j=0; j<m_ar; j++)
				pha[i*m_ar + j] = (double)1.0;

		for(i=0; i<m_br; i++)
			for(j=0; j<m_br; j++)
				phb[i*m_br + j] = (double)(i+1);
		
		for(i=0; i<m_ar; i++)
			for(j=0; j<m_ar; j++)
				phc[i*m_ar + j] = (double)0;

			
		for (int count = 1; count <= numIteracoes; count++) {
			Time1 = System.currentTimeMillis();

			for(i=0; i<m_ar; i++) {
				for(k=0; k<m_ar; k++) {	
					for(j=0; j<m_br; j++) {
						phc[i*m_ar+j] += pha[i*m_ar+k] * phb[k*m_br+j];
					}
				}
			}

			Time2 = System.currentTimeMillis();

			if (count==1) {
				writer2.write(String.format("Matrix size: %d x %d\n\n", m_ar, m_ar));
				writer2.write("Result matrix: \n");
				for (i = 0; i < 1; i++) {
					for (j = 0; j < Math.min(10, m_br); j++) {
						writer2.write(phc[j] + " ");
					}
				}
				writer2.write("\n\n");
			}
			writer2.write(String.format("Time: %3.3f seconds\n", (double)(Time2-Time1)/1000));
		}
	}

	public static void main(String[] args) throws IOException {	

		char c;
		int lin, col, blockSize;
		int op;

		long[] values = new long[20];
		int ret;
		
		Scanner stdin = new Scanner(System.in);

		op=1;
		do{
			System.out.println("1. Multiplication");
			System.out.println("2. Line Multiplication");

			System.out.print("Selection?: ");
			op = stdin.nextInt();

			if(op==0) 
				break;

			System.out.print("Dimensions: lins=cols ? ");
			lin = stdin.nextInt();
			col = lin;

			switch (op){
				case 1: 
					/*OnMult(600, 600);
					OnMult(1000, 1000);
					OnMult(1400, 1400);
					OnMult(1800, 1800);
					OnMult(2200, 2200);
					OnMult(2600, 2600); 
					OnMult(3000, 3000); */
					OnMult(lin, col);
					writer.close();
					break;
				case 2:
					/*OnMultLine(600, 600); 
					OnMultLine(1000, 1000);
					OnMultLine(1400, 1400);
					OnMultLine(1800, 1800);
					OnMultLine(2200, 2200);
					OnMultLine(2600, 2600); 
					OnMultLine(3000, 3000); */
					OnMultLine(lin, col);
					writer2.close();
					break;
			}
		} while (op != 0);
	}

}