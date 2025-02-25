package cashflow.common;


import cashflow.enums.MessageEnums;
import lombok.Data;

import java.util.Optional;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:04
 **/
@Data
public class R<T> {
    private boolean flag;
    private T data;
    private String msg;

    private R(boolean flag, T data, String msg) {
        this.flag = flag;
        this.data = data;
        this.msg = Optional.ofNullable(msg).orElse(flag ? "操作成功" : "操作失败");
    }

    public static <T> R<T> success() {
        return new R<>(true, null, "操作成功");
    }

    public static <T> R<T> success(T data, String msg) {
        return new R<>(true, data, msg);
    }

    public static <T> R<T> success(T data) {
        return new R<>(true, data, "操作成功");
    }

    public static <T> R<T> success(T data, MessageEnums enums) {
        return new R<>(true, data, enums.getMessage());
    }

    public static <T> R<T> success(MessageEnums enums) {
        return new R<>(true, null, enums.getMessage());
    }

    public static <T> R<T> error(String msg) {
        return new R<>(false, null, msg);
    }

    public static <T> R<T> error() {
        return new R<>(false, null, null);
    }

    public static <T> R<T> error(MessageEnums enums) {
        return new R<>(false, null, enums.getMessage());
    }
}