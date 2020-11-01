package me.zhiyao.faintecho.constants;

/**
 * @author WangZhiYao
 * @date 2020/10/31
 */
public enum UserSolitaireStatus {

    GAMING(1),

    CHANGE_MODEL(2),

    FINISHED(3);

    private final int value;

    UserSolitaireStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
