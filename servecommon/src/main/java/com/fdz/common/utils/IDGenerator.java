package com.fdz.common.utils;

public class IDGenerator {

    private final long originTimestamp = 1288834974657L;
    private final long machineBits = 8L;
    private final long bussinessBits = 5L;
    /**
     * 机器ID最大数
     */
    private final long maxMachineId = -1L ^ (-1L << machineBits);

    /**
     * 机器ID最大数
     */
    private final long maxBussinessId = -1L ^ (-1L << bussinessBits);

    /**
     * 序号位数
     */
    private final long sequenceBits = 9L;
    /**
     * 序号最大
     */
    private final long maxSequenceId = -1L ^ (-1L << sequenceBits);

    private long machineId;

    private long bussinessId;

    private long sequence = 0L;

    private long lastTimestamp = -1L;

    /**
     * 初始化机器码和业务码。
     *
     * @param machineId
     * @param bussinessId
     */
    public IDGenerator(long machineId, long bussinessId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException(String.format("machine Id can't be greater than %d or less than 0", maxMachineId));
        }
        if (bussinessId > maxBussinessId || bussinessId < 0) {
            throw new IllegalArgumentException(String.format("bussiness Id can't be greater than %d or less than 0", maxBussinessId));
        }
        this.machineId = machineId;
        this.bussinessId = bussinessId;
    }


    public synchronized long getId() {
        long timestamp = getCurrentTimeStamp();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp != timestamp) {
            sequence = 0L;
        } else {
            sequence = (sequence + 1) & maxSequenceId;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        }
        lastTimestamp = timestamp;
        return ((timestamp - originTimestamp) << (sequenceBits + bussinessBits + machineBits)) |
                (machineId << (sequenceBits + bussinessBits)) | (bussinessId << sequenceBits) | sequence;
    }

    public synchronized long getId(long currentTimeStamp) {
        long timestamp = currentTimeStamp;
//        if (timestamp < lastTimestamp) {
//            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
//        }
        if (lastTimestamp != timestamp) {
            sequence = 0L;
        } else {
            sequence = (sequence + 1) & maxSequenceId;
            if (sequence == 0) {
                timestamp = getNextMillis(lastTimestamp, timestamp);
            }
        }
        lastTimestamp = timestamp;
        return ((timestamp - originTimestamp) << (sequenceBits + bussinessBits + machineBits)) |
                (machineId << (sequenceBits + bussinessBits)) | (bussinessId << sequenceBits) | sequence;
    }


    protected long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    protected long getNextMillis(long lastTimestamp, long currentTimeStamp) {
        long timestamp = currentTimeStamp;
        while (timestamp <= lastTimestamp) {
            timestamp = timestamp + 1L;
        }
        return timestamp;
    }

    protected long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimeStamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimeStamp();
        }
        return timestamp;
    }


}
