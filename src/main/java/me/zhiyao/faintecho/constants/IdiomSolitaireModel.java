package me.zhiyao.faintecho.constants;

import java.util.Arrays;

/**
 * @author WangZhiYao
 * @date 2020/10/31
 */
public enum IdiomSolitaireModel {

    EASY(1),

    NORMAL(2);

    private final int value;

    IdiomSolitaireModel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static IdiomSolitaireModel getModel(int value) {
        return Arrays.stream(values())
                .filter(idiomSolitaireModel -> idiomSolitaireModel.value == value)
                .findFirst()
                .orElse(null);
    }
}
