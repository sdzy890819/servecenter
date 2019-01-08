package com.fdz.common.enums;

import lombok.Getter;

@Getter
public enum InterfaceExecStatus {

    WAIT((byte) 0, "等待中"), PROCESSING((byte) 1, "处理中"), SUCCESS((byte) 2, "成功"), FAIL((byte) 3, "已失败"), FAIL_FINISH((byte) 4, "失败后不再重试");

    private byte status;

    private String msg;

    InterfaceExecStatus(byte status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static InterfaceExecStatus get(int status) {
        for (InterfaceExecStatus interfaceExecStatus : InterfaceExecStatus.values()) {
            if (interfaceExecStatus.getStatus() == status) {
                return interfaceExecStatus;
            }
        }
        return null;
    }
}
