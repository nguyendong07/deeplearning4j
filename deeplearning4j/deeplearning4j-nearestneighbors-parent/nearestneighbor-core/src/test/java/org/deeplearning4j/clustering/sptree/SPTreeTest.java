/*******************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package org.deeplearning4j.clustering.sptree;

import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.junit.Assert.*;

/**
 * @author Adam Gibson
 */
public class SPTreeTest {

    @Before
    public void setUp() {
        DataTypeUtil.setDTypeForContext(DataType.DOUBLE);
    }

    @Test
    public void testStructure() {
        INDArray data = Nd4j.create(new float[][] {{1, 2, 3}, {4, 5, 6}});
        SpTree tree = new SpTree(data);
        try (MemoryWorkspace ws = tree.workspace().notifyScopeEntered()) {
            assertEquals(Nd4j.create(new float[]{2.5f, 3.5f, 4.5f}), tree.getCenterOfMass());
            assertEquals(2, tree.getCumSize());
            assertEquals(8, tree.getNumChildren());
            assertTrue(tree.isCorrect());
        }
    }

    @Test
    public void testComputeEdgeForces() {
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
        double[] aData = new double[]{
                0.2999816948164936, 0.26252049735806526, 0.2673853427498767, 0.8604464129156685, 0.4802652829902563, 0.10959096539488711, 0.7950242948008909, 0.5917848948003486,
                0.2738285999345498, 0.9519684328285567, 0.9690024759209738, 0.8585615547624705, 0.8087760944312002, 0.5337951589543348, 0.5960876109129123, 0.7187130179825856,
                0.4629777327445964, 0.08665909175584818, 0.7748005397731237, 0.48020186965468536, 0.24927351841378798, 0.32272599988270445, 0.306414968984427, 0.6980212149215657,
                0.7977183964212472, 0.7673513094629704, 0.1679681724796478, 0.3107359484804584, 0.021701726051792103, 0.13797462786662518, 0.8618953518813538, 0.841333838365635,
                0.5284957375170422, 0.9703367685039823, 0.677388096913733, 0.2624474979832243, 0.43740966353106536, 0.15685545957858893, 0.11072929134449871, 0.06007395961283357,
                0.4093918718557811,  0.9563909195720572, 0.5994144944480242, 0.8278927844215804, 0.38586830957105667, 0.6201844716257464, 0.7603829079070265, 0.07875691596842949,
                0.08651136699915507, 0.7445210640026082, 0.6547649514127559, 0.3384719042666908, 0.05816723105860,0.6248951423054205, 0.7431868493349041};
        INDArray data = Nd4j.createFromArray(aData).reshape(11,5);
        INDArray rows = Nd4j.createFromArray(new int[]{
                         0,         9,        18,        27,        36,        45,        54,        63,        72,        81,        90,        99});
        INDArray cols = Nd4j.createFromArray(new int[]{
                4,         3,        10,         8,         6,         7,         1,         5,         9,         4,         9,         8,        10,         2,         0,         6,         7,         3,         6,         8,         3,         9,        10,         1,         4,         0,         5,        10,         0,         4,         6,         8,         9,         2,         5,         7,         0,        10,         3,         1,         8,         9,         6,         7,         2,         7,         9,         3,        10,         0,         4,         2,         8,         1,         2,         8,         3,        10,         0,         4,         9,         1,         5,         5,         9,         0,         3,        10,         4,         8,         1,         2,         6,         2,         0,         3,         4,         1,        10,         9,         7,        10,         1,         3,         7,         4,         5,         2,         8,         6,         3,         4,         0,         9,         6,         5,         8,         7,         1});
        INDArray vals = Nd4j.createFromArray(new double[]
                {    0.6806,    0.1978,    0.1349,    0.0403,    0.0087,    0.0369,    0.0081,    0.0172,    0.0014,    0.0046,    0.0081,    0.3375,    0.2274,    0.0556,    0.0098,    0.0175,    0.0027,    0.0077,    0.0014,    0.0023,    0.0175,    0.6569,    0.1762,    0.0254,    0.0200,    0.0118,    0.0074,    0.0046,    0.0124,    0.0012,    0.1978,    0.0014,    0.0254,    0.7198,    0.0712,    0.0850,    0.0389,    0.0555,    0.0418,    0.0286,    0.6806,    0.3375,    0.0074,    0.0712,    0.2290,    0.0224,    0.0189,    0.0080,    0.0187,    0.0097,    0.0172,    0.0124,    0.0418,    0.7799,    0.0521,    0.0395,    0.0097,    0.0030,    0.0023,  1.706e-5,    0.0087,    0.0027,    0.6569,    0.0850,    0.0080,    0.5562,    0.0173,    0.0015,  1.706e-5,    0.0369,    0.0077,    0.0286,    0.0187,    0.7799,    0.0711,    0.0200,    0.0084,    0.0012,    0.0403,    0.0556,    0.1762,    0.0389,    0.0224,    0.0030,    0.5562,    0.0084,    0.0060,    0.0028,    0.0014,    0.2274,    0.0200,    0.0555,    0.0189,    0.0521,    0.0015,    0.0711,    0.0028,    0.3911,    0.1349,    0.0098,    0.0118,    0.7198,    0.2290,    0.0395,    0.0173,    0.0200,    0.0060,    0.3911});
        SpTree tree = new SpTree(data);
        INDArray posF = Nd4j.create(11, 5);
        try (MemoryWorkspace ws = tree.workspace().notifyScopeEntered()) {
            tree.computeEdgeForces(rows, cols, vals, 11, posF);
        }
        INDArray expected = Nd4j.createFromArray(new double[]{     0.1427,   -0.3917,   -0.2200,    0.7132,   -1.7348,
               -0.1143,   -0.1051,   -0.0309,   -0.1271,   -0.0963,
               -0.0932,   -0.1204,   -0.0265,   -0.1244,   -0.0989,
               -0.0659,   -0.0683,   -0.0518,   -0.0863,   -0.1033,
               -0.1522,   -0.1231,   -0.0513,   -0.2113,   -0.0437,
               -0.0084,   -0.0820,   -0.0139,   -0.0580,   -0.0571,
               -0.0666,   -0.0600,   -0.0914,    0.0360,   -0.0343,
               -0.2791,   -0.0240,   -0.0924,   -0.0766,   -0.1657,
               -0.3111,   -0.1496,   -0.1450,   -0.1585,   -0.2062,
               -0.0899,   -0.0641,   -0.0670,   -0.1776,   -0.1193,
               -0.0703,   -0.0913,   -0.0486,   -0.1153,   -0.0210}).reshape(11,5);
        for (int i = 0; i < 11; ++i)
            assertArrayEquals(expected.getRow(i).toDoubleVector(), posF.getRow(i).toDoubleVector(), 1e-3);

        AtomicDouble sumQ = new AtomicDouble(0.0);
        try (MemoryWorkspace ws = tree.workspace().notifyScopeEntered()) {
            tree.computeNonEdgeForces(0, 0.5, Nd4j.zeros(5), sumQ);
        }
        assertEquals(8.65, sumQ.get(), 1e-2);
    }

    @Test
    //@Ignore
    public void testLargeTree() {
        int num = 100000;
        StopWatch watch = new StopWatch();
        watch.start();
        INDArray arr = Nd4j.linspace(1, num, num, Nd4j.dataType()).reshape(num, 1);
        SpTree tree = new SpTree(arr);
        watch.stop();
        System.out.println("Tree created in " + watch);
    }

}
