package com.noitidart.api.pojo.em;

/**
 * 健康项权限枚举类
 */
public enum IsGlobalEnum {

    PUBLIC(true),
    PRIVATE(false);

    private final Boolean status;  // 状态

    IsGlobalEnum(Boolean status) {
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
    }
}
