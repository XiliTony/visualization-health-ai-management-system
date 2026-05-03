package com.noitidart.api.pojo.em;

/**
 * 用户角色枚举
 */
public enum RoleEnum {

    ADMIN(1, "管理员"),
    USER(2, "用户");

    private final Integer role; // 角色编码
    private final String name;  // 角色名

    RoleEnum(Integer role, String name) {
        this.role = role;
        this.name = name;
    }

    public Integer getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    /**
     * 由角色编码获取角色名
     *
     * @param role 角色编码
     * @return String 角色名
     */
    public static String getNameByRole(Integer role) {
        for (RoleEnum value : RoleEnum.values()) {
            if (value.getRole().equals(role)) {
                return value.name;
            }
        }
        return null;
    }

    /**
     * 由角色名获取角色编码
     *
     * @param name 角色名
     * @return String 角色编码
     */
    public static Integer getRoleByName(String name) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.name.equals(name)) {
                return roleEnum.role;
            }
        }
        return null; // 或抛异常，表示无效角色名
    }

}
