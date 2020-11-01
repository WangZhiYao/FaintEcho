package me.zhiyao.faintecho.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author WangZhiYao
 * @date 2020/10/31
 */
@Data
@AllArgsConstructor
public class Conversation {

    @TableId(type = IdType.AUTO)
    private Integer conversationId;
    private String user;
    private int type;
    private String message;
    private long createTime;
}
