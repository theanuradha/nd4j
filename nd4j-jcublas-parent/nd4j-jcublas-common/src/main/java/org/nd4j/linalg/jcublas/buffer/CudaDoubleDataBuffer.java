package org.nd4j.linalg.jcublas.buffer;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.DoubleBuffer;
import org.nd4j.linalg.ops.ElementWiseOp;
import org.nd4j.linalg.util.ArrayUtil;

/**
 * Cuda double  buffer
 *
 * @author Adam Gibson
 */
public class CudaDoubleDataBuffer extends BaseCudaDataBuffer {


    /**
     * Base constructor
     *
     * @param length      the length of the buffer
     */
    public CudaDoubleDataBuffer(int length) {
        super(length, Sizeof.DOUBLE);
        if(pointer() == null)
            alloc();
    }

    public CudaDoubleDataBuffer(double[] data) {
        this(data.length);
        setData(data);
    }


    @Override
    public void assign(int[] indices, float[] data, boolean contiguous,int inc) {
        if(indices.length != data.length)
            throw new IllegalArgumentException("Indices and data length must be the same");
        if(indices.length > length())
            throw new IllegalArgumentException("More elements than space to assign. This buffer is of length " + length() + " where the indices are of length " + data.length);

        if(contiguous) {
            int offset = indices[0];
            Pointer p = Pointer.to(data);
            set(offset,data.length,p,inc);

        }
        else
            throw new UnsupportedOperationException("Non contiguous is not supported");

    }

    @Override
    public void assign(int[] indices, double[] data, boolean contiguous,int inc) {
        if(indices.length != data.length)
            throw new IllegalArgumentException("Indices and data length must be the same");
        if(indices.length > length())
            throw new IllegalArgumentException("More elements than space to assign. This buffer is of length " + length() + " where the indices are of length " + data.length);

        if(contiguous) {
            int offset = indices[0];
            Pointer p = Pointer.to(data);
            set(offset,data.length,p,inc);
        }
        else
            throw new UnsupportedOperationException("Non contiguous is not supported");

    }

    @Override
    public double[] getDoublesAt(int offset, int length) {
        return getDoublesAt(0,1,length);
    }

    @Override
    public float[] getFloatsAt(int offset, int length) {
        return ArrayUtil.toFloats(getDoublesAt(offset,length));
    }

    @Override
    public double[] getDoublesAt(int offset, int inc, int length) {
        if(offset + length > length())
            length -= offset;

        double[] ret = new double[length];
        Pointer p = Pointer.to(ret);
        get(offset,inc,length,p);
        return ret;
    }

    @Override
    public float[] getFloatsAt(int offset, int inc, int length) {
        return ArrayUtil.toFloats(getDoublesAt(offset,1,length));
    }

    @Override
    public void assign(Number value, int offset) {
        int arrLength = length - offset;
        double[] data = new double[arrLength];
        for(int i = 0; i < data.length; i++)
            data[i] = value.doubleValue();
        set(offset,arrLength,Pointer.to(data));
    }

    @Override
    public void setData(int[] data) {
        setData(ArrayUtil.toDoubles(data));
    }

    @Override
    public void setData(float[] data) {
        setData(ArrayUtil.toDoubles(data));
    }

    @Override
    public void setData(double[] data) {

        if(data.length != length)
            throw new IllegalArgumentException("Unable to set vector, must be of length " + length() + " but found length " + data.length);

        if(pointer() == null)
            alloc();


        JCublas.cublasSetVector(
                length()
                ,elementSize()
                ,Pointer.to(data)
                ,1
                ,pointer()
                ,1);


    }

    @Override
    public byte[] asBytes() {
        return new byte[0];
    }

    @Override
    public int dataType() {
        return DataBuffer.DOUBLE;
    }

    @Override
    public float[] asFloat() {
        return new float[0];
    }

    @Override
    public double[] asDouble() {
        double[] ret = new double[length];
        Pointer p = Pointer.to(ret);
        JCublas.cublasGetVector(
                length,
                elementSize(),
                pointer(),
                1,
                p,
                1);
        return ret;
    }

    @Override
    public int[] asInt() {
        return new int[0];
    }


    @Override
    public double getDouble(int i) {
        double[] d = new double[1];
        Pointer p = Pointer.to(d);
        get(i,p);
        return d[0];
    }

    @Override
    public float getFloat(int i) {
        return (float) getDouble(i);
    }

    @Override
    public Number getNumber(int i) {
        return getDouble(i);
    }



    @Override
    public void put(int i, float element) {
        put(i,(double)element);
    }

    @Override
    public void put(int i, double element) {
        double[] d = new double[]{element};
        Pointer p = Pointer.to(d);
        set(i,p);

    }

    @Override
    public void put(int i, int element) {
        put(i,(double) element);
    }




    @Override
    public int getInt(int ix) {
        return (int) getDouble(ix);
    }

    @Override
    public DataBuffer dup() {
        CudaDoubleDataBuffer buffer = new CudaDoubleDataBuffer(length());
        copyTo(buffer);
        return buffer;
    }

    @Override
    public void flush() {

    }

    @Override
    public void apply(ElementWiseOp op, int offset) {
        if(offset >= length)
            throw new IllegalArgumentException("Illegal start " + offset + " greater than length of " + length);
        int arrLength = Math.abs(length - offset);
        double[] data = new double[arrLength];
        Pointer p = Pointer.to(data);
        get(offset,data.length,p);
        DataBuffer floatBuffer = new DoubleBuffer(data,false);
        floatBuffer.apply(op);
        p = Pointer.to(data);
        set(offset,arrLength,p);
    }

    @Override
    public void addi(Number n, int inc, int offset) {

    }

    @Override
    public void subi(Number n, int inc, int offset) {

    }

    @Override
    public void muli(Number n, int inc, int offset) {

    }

    @Override
    public void divi(Number n, int inc, int offset) {

    }

    @Override
    public void addi(DataBuffer buffer, int n, int offset, int yOffset, int incx, int incy) {

    }

    @Override
    public void subi(DataBuffer buffer, int n, int offset, int yOffset, int incx, int incy) {

    }

    @Override
    public void muli(DataBuffer buffer, int n, int offset, int yOffset, int incx, int incy) {

    }

    @Override
    public void divi(DataBuffer buffer, int n, int offset, int yOffset, int incx, int incy) {

    }


}