package nova.core.util.transform;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.util.Arrays;

/**
 * A class that represents a matrix.
 */

//TODO: Add unit testing
public class Matrix extends Operator<Matrix, Matrix> implements Cloneable {
	// number of rows
	private final int rows;
	// number of columns
	private final int columns;
	// rows-by-columns array
	private final double[][] mat;

	// create rows-by-columns matrix of 0's
	public Matrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		mat = new double[rows][columns];
	}

	public Matrix(int size) {
		this(size, size);
	}

	// create matrix based on 2d array
	public Matrix(double[][] data) {
		rows = data.length;
		columns = data[0].length;
		this.mat = data.clone();
	}

	// create and return a random rows-by-columns matrix with values between 0 and 1
	public static Matrix random(int M, int N) {
		Matrix A = new Matrix(M, N);
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				A.mat[i][j] = Math.random();
		return A;
	}

	// create and return the columns-by-columns identity matrix
	public static Matrix identity(int size) {
		Matrix I = new Matrix(size, size);
		for (int i = 0; i < size; i++)
			I.mat[i][i] = 1;
		return I;
	}

	public double get(int i, int j) {
		return apply(i, j);
	}

	public double apply(int i, int j) {
		return mat[i][j];
	}

	//TODO: Should we make it immutable, and return a new matrix instead?
	public void update(int i, int j, double value) {
		mat[i][j] = value;
	}

	/**
	 * Swap rows i and j
	 */
	private void swap(int i, int j) {
		double[] temp = mat[i];
		mat[i] = mat[j];
		mat[j] = temp;
	}

	/**
	 * Create and return the transpose of the invoking matrix
	 */
	public Matrix transpose() {
		Matrix A = new Matrix(columns, rows);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				A.mat[j][i] = this.mat[i][j];
		return A;
	}

	@Override
	public Matrix add(double other) {
		Matrix A = new Matrix(columns, rows);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				A.mat[i][j] = mat[i][j] + other;
		return A;
	}

	/**
	 * @return C = A + B
	 */
	@Override
	public Matrix add(Matrix B) {
		Matrix A = this;
		assert B.rows == A.rows && B.columns == A.columns;

		Matrix C = new Matrix(rows, columns);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				C.mat[i][j] = A.mat[i][j] + B.mat[i][j];
		return C;
	}

	@Override
	public Matrix multiply(double other) {
		Matrix A = new Matrix(columns, rows);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < columns; j++)
				A.mat[i][j] = mat[i][j] * other;
		return A;
	}

	/**
	 * Matrix-matrix multiplication
	 *
	 * @return C = A * B
	 */
	@Override
	public Matrix multiply(Matrix B) {
		Matrix A = this;
		assert A.columns == B.rows;

		Matrix C = new Matrix(A.rows, B.columns);
		for (int i = 0; i < C.rows; i++)
			for (int j = 0; j < C.columns; j++)
				for (int k = 0; k < A.columns; k++)
					C.mat[i][j] += (A.mat[i][k] * B.mat[k][j]);
		return C;
	}

	public boolean isRowVector() {
		return rows == 1;
	}

	public boolean isColumnVector() {
		return columns == 1;
	}

	public boolean isSquare() {
		return columns == rows;
	}

	/**
	 * Augments this matrix with another, appending B's columns on the right side of this matrix.
	 *
	 * @param B The matrix to augment
	 * @return The augmented matrix
	 */
	public Matrix augment(Matrix B) {
		assert rows == B.rows;
		Matrix C = new Matrix(rows, columns + B.columns);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++)
				C.mat[i][j] = mat[i][j];

			for (int j = columns; j < C.columns; j++)
				C.mat[i][j] = B.mat[i][j - columns];
		}
		return C;
	}

	/**
	 * Finds the inverse of the matrix using Gaussian elimination.
	 *
	 * @return The inverse of the matrix
	 */
	@Override
	public Matrix reciprocal() {
		assert isSquare();
		return null;
	}

	/**
	 * Solves a matrix-vector equation, Ax = b.
	 *
	 * @return x = A^-1 b, assuming A is square and has full rank
	 */
	public Matrix solve(Matrix rhs) {
		assert rows == columns && rhs.rows == columns && rhs.columns == 1;

		// create copies of the mat
		Matrix A = this.clone();
		Matrix b = rhs.clone();

		// Gaussian elimination with partial pivoting
		for (int i = 0; i < columns; i++) {

			// find pivot row and swap
			int max = i;
			for (int j = i + 1; j < columns; j++)
				if (Math.abs(A.mat[j][i]) > Math.abs(A.mat[max][i])) {
					max = j;
				}
			A.swap(i, max);
			b.swap(i, max);

			// singular
			if (A.mat[i][i] == 0.0) {
				throw new RuntimeException("Matrix is singular.");
			}

			// pivot within b
			for (int j = i + 1; j < columns; j++)
				b.mat[j][0] -= b.mat[i][0] * A.mat[j][i] / A.mat[i][i];

			// pivot within A
			for (int j = i + 1; j < columns; j++) {
				double m = A.mat[j][i] / A.mat[i][i];
				for (int k = i + 1; k < columns; k++) {
					A.mat[j][k] -= A.mat[i][k] * m;
				}
				A.mat[j][i] = 0.0;
			}
		}

		// back substitution
		Matrix x = new Matrix(columns, 1);
		for (int j = columns - 1; j >= 0; j--) {
			double t = 0.0;
			for (int k = j + 1; k < columns; k++)
				t += A.mat[j][k] * x.mat[k][0];
			x.mat[j][0] = (b.mat[j][0] - t) / A.mat[j][j];
		}
		return x;

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Matrix) {
			Matrix B = (Matrix) obj;
			Matrix A = this;
			assert B.rows == A.rows && B.columns == A.columns;

			for (int i = 0; i < rows; i++)
				for (int j = 0; j < columns; j++)
					if (A.mat[i][j] != B.mat[i][j]) {
						return false;
					}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		Hasher hasher = Hashing.goodFastHash(32).newHasher();
		for (double[] array : mat)
			for (double d : array)
				hasher.putDouble(d);

		return hasher.hash().asInt();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Matrix[" + rows + "," + columns + "]\n");
		for (int i = 0; i < rows; i++)
			sb.append(Arrays.toString(mat[i])).append("\n");
		return sb.toString();
	}

	@Override
	public Matrix clone() {
		return new Matrix(mat);
	}
}
