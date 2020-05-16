package solver;

import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.*;
import vuongdx.search.HillClimbingSearch;
import vuongdx.search.ISolver;

public class FSOClustering implements ISolver {

    public static  void main(String[] args) {
        FSOClustering s = readInputAndCreateSolverInstance("./data/gfso_50_1");
    }

    public static FSOClustering readInputAndCreateSolverInstance(String file) {
        
    }

    private int NFSO;
    private int[] x;
    private int[] y;
    private int[][] d;
    private int W;
    private int bw;
    private int R;
    private int L;

    private int minX;
    private int maxX;
    private int minY;
    private int maxY;

    private VarIntLS NHAP;
    private VarIntLS[] X;
    private VarIntLS[] Y;
    private VarIntLS[][] H;

    private double[][] E;
    private int[][] D;

    IFunction objective;

    LocalSearchManager lsm;
    ConstraintSystem cs;

    public FSOClustering(int NFSO, int[] x, int[] y, int[][] d, int W, int bw, int R, int L) {
        this.NFSO = NFSO;
        this.x = x;
        this.y = y;
        this.d = d;
        this.W = W;
        this.bw = bw;
        this.R = R;
        this.L = L;
    }

    @Override
    public void stateModel() {
        lsm = new LocalSearchManager();
        cs = new ConstraintSystem(lsm);

        NHAP = new VarIntLS(lsm, 0, NFSO);
        X = new VarIntLS[NFSO];
        Y = new VarIntLS[NFSO];
        H = new VarIntLS[NFSO][NFSO];

        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;

        for (int i = 0; i < NFSO; i++) {
            minX = Math.min(minX, x[i]);
            maxX = Math.max(maxX, x[i]);
            minY = Math.min(minY, y[i]);
            maxY = Math.max(maxY, y[i]);
        }

        // tmpH[u][i] cho biết HAP u có kết nối đến FSO i hay không
        VarIntLS[][] tmpH = new VarIntLS[NFSO][NFSO];
        for (int i = 0; i < NFSO; i++) {
            X[i] = new VarIntLS(lsm, minX, maxX);
            Y[i] = new VarIntLS(lsm, minY, maxY);
            for (int j = 0; j < NFSO; j++) {
                H[i][j] = new VarIntLS(lsm, 0, 1);
                tmpH[j][i] = H[i][j];
            }
        }

        for (int i = 0; i < NFSO; i++) {
            // Ràng buộc 4: FSO i kết nối với đúng 1 HAP
            // tmpMask[u] cho biết u < NHAP hay không (vì ta chỉ quan tâm đến các HAP có chỉ số < NHAP)
            VarIntLS[] tmpMask = new VarIntLS[NFSO];
            // tmpHi[u] = 1 nếu u < NHAP và có kết nối đến FSO i, ngược lại tmpH[u] = 0
            VarIntLS[] tmpHi = new VarIntLS[NFSO];
            // Xét tất cả HAP, bao gồm cả HAP tồn tại và HAP ảo (những HAP u >= NHAP)
            for (int u = 0; u < NFSO; u++) {
                tmpMask[u] = new VarIntLS(lsm, 0, 1);
                tmpHi[u] = new VarIntLS(lsm, 0, 1);
                // tmp là thể hiện VarIntLS của u
                VarIntLS tmp = new VarIntLS(lsm, u, u);
                cs.post(new IsEqual(tmp, u));
                // Nếu tmp (hay u) >= NHAP, HAP tmp (HAP u) không tồn tại
                cs.post(new Implicate(new LessOrEqual(NHAP, tmp), new IsEqual(tmpMask[u], 0)));
                // Nếu tmp (hay u) <= NHAP, HAP tmp (HAP u) tồn tại
                cs.post(new Implicate(new LessThan(tmp, NHAP), new IsEqual(tmpMask[u], 1)));
                // Kết hợp mảng H và mask để biết HAP u có < NHAP và có kết nối đến FSO i hay không
                cs.post(new IsEqual(new FuncMult(H[i][u], tmpMask[u]), tmpHi[u]));
            }
            // Tổng số kết nối của FSO đến các HAP tồn tại = 1
            cs.post(new IsEqual(new Sum(tmpHi), 1));

            // Rang buoc 5
            // tmpSum cho biết HAP i kết nối đến bao nhiêu FSO
            IFunction tmpSum = new Sum(tmpH[i]);
            // tmp là thể hiện VarIntLS của i
            VarIntLS tmp = new VarIntLS(lsm, i, i);
            cs.post(new IsEqual(tmp, i));
            // Neu HAP i ton tai thi tong so ket noi den no <= W
            cs.post(new Implicate(new LessThan(tmp, NHAP), new LessOrEqual(tmpSum, W)));

            // Ràng buộc 3: FSO i và HAP u chỉ kết nối được khi khoảng cách <= R
            for (int u = 0; u < NFSO; u++) {
                IConstraint hEqualOne = new IsEqual(H[i][u], 1);
                IFunction xDelta = new FuncMinus(X[u], x[i]);
                IFunction yDelta = new FuncMinus(Y[u], y[i]);
                IFunction sqrXDelta = new FuncMult(xDelta, xDelta);
                IFunction sqrYDelta = new FuncMult(yDelta, yDelta);
                IFunction sqrDistance = new FuncPlus(sqrXDelta, sqrYDelta);
                IConstraint distanceConstraint = new LessOrEqual(sqrDistance, R * R);
                VarIntLS tmpU = new VarIntLS(lsm, u, u);
                cs.post(new Implicate(new LessThan(tmpU, NHAP), new Implicate(hEqualOne, distanceConstraint)));
            }
        }

        // Hàm mục tiêu cần tối ưu
        objective = new FuncPlus(NHAP, 0);

        lsm.close();
    }

    @Override
    public void search() {
        int minNHAP = 0;
        int maxNHAP = NFSO;
        while (minNHAP < maxNHAP) {
            NHAP.setValuePropagate((minNHAP + maxNHAP) / 2);

            // cai dat tim kiem
            // bien quyet dinh: X, Y, H

            if (cs.violations() == 0) {
                maxNHAP = NHAP.getValue() - 1;
            } else {
                minNHAP = NHAP.getValue() + 1;
            }
        }
    }

    @Override
    public void printResult() {
        System.out.println("Violations: " + cs.violations());
        System.out.println("NHAP: " + NHAP.getValue());
        for (int i = 0; i < NHAP.getValue(); i++) {
            System.out.println("HAP " + i + ": (" + X[i].getValue() + ", " + Y[i].getValue() + ")");
            System.out.print("FSO: {");
            for (int j = 0; j < NFSO; j++) {
                if (H[j][i].getValue() == 1) {
                    System.out.print(" " + j);
                }
            }
            System.out.println("}");
        }
    }
}
