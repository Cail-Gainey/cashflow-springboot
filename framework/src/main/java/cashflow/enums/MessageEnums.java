package cashflow.enums;

/**
 * @author Cail Gainey
 * @since 2025/1/25 15:04
 **/
public enum MessageEnums {
    GET_SUCCESS("查询成功"),
    GET_ERROR("查询失败"),
    SAVE_SUCCESS("添加成功"),
    SAVE_ERROR("添加失败"),
    DELETE_SUCCESS("删除成功"),
    DELETE_ERROR("删除失败"),
    UPDATE_SUCCESS("修改成功"),
    UPDATE_ERROR("修改失败"),
    NULL("值不能为空"),
    ID_NULL("id为空"),
    USER_NULL("用户为空"),
    USER_ID_NULL("用户ID为空"),
    USER_EXIST("用户已存在"),
    USER_NOT_EXIST("用户不存在"),
    USER_WAS_BAN("该账号已被封禁"),

    PASSWORD_LENGTH("密码长度至少为6个字符"),
    REGISTRATION_ERROR("注册失败"),
    LOGIN_ERROR("登录失败，邮箱或密码错误"),
    LOGIN_SUCCESS("登录成功"),
    LOGOUT_SUCCESS("退出成功"),

    EMAIL_SEND_SUCCESS("验证码已发送，请查收！"),
    EMAIL_INVALID("无效的邮箱格式"),
    EMAIL_ADDRESS_INVALID("无效的邮箱地址"),
    EMAIL_VERIFY_SUCCESS("验证码验证成功！"),
    EMAIL_VERIFY_ERROR("验证码验证失败！"),
    TOKEN_VERITY_ERROR("token验证失败，请重新登录"),
    TOKEN_VERIFY_SUCCESS("token验证成功"),
    TOKEN_INVALID_ERROR("token不合法！"),
    TOKEN_NULL("Token为空！"),
    HEADER_MISS("缺少请求头:"),

    CATEGORY_ParentIdNotExist("父分类不存在"),
    CATEGORY_CHILDREN_ERROR("子类不能继续添加"),
    CATEGORY_TYPE_ERROR("类型不一致"),
    CATEGORY_EXIST("该分类已存在"),
    CATEGORY_NOT_EXIST("该分类不存在"),
    CATEGORY_CANT_UPDATE_TO_CHILDREN("该分类下有二级分类，所以不能直接更改为二级分类"),
    CATEGORY_CANT_UPDATE_TO_PARENT("请选择一个一级分类"),
    CATEGORY_PARENT_CANT_DELETE("该分类下有二级分类，所以不能删除"),
    CATEGORY_CHILDREN_CANT_DELETE("该分类下存在账单，请先进行迁移"),

    FUND_EXIST("该资金已存在"),
    FUND_NO_FUND("找不到该资产！"),
    DEFAULT_FUND_CANT_DELETE("默认资产不能删除"),
    FUND_DETAIL_NOT_EXIST("资金明细不存在"),

    LEDGER_EXIST("该账本已存在"),
    LEDGER_NOT_EXIST("该账本不存在"),

    FILES_NOT_EXIST("文件不存在"),

    CODE_VERIFY_ERROR("验证码错误"),
    CODE_VERIFY_SUCCESS("验证码验证成功"),
    CODE_VERIFY_NULL("验证码不存在或已过期"),
    CODE_VERIFY_INPUT_NULL("验证码不存在或已过期"),
    PASSWORD_ENCRYPTION_ERROR("密码加密失败"),
    PASSWORD_DECRYPTION_ERROR("密码解密失败"),
    SERVER_FAILURE("服务器故障，请稍后再试！"),

    END("");


    private final String message;

    MessageEnums(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}