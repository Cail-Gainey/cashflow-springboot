package cashflow.enums;

/**
 * @author Cail Gainey
 * @since 2025/2/18 16:59
 **/
public enum AccountStatusEnums {
    BAN(-1, "封禁"),
    ILLEGAL_OPERATIONS(1, "登录与请求的账号不一致");

    private final Integer status;
    private final String description;

    AccountStatusEnums(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
