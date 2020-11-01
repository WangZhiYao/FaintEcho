package me.zhiyao.faintecho.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.zhiyao.faintecho.builder.TextBuilder;
import me.zhiyao.faintecho.constants.IdiomSolitaireModel;
import me.zhiyao.faintecho.constants.UserSolitaireStatus;
import me.zhiyao.faintecho.db.model.Conversation;
import me.zhiyao.faintecho.db.model.Idiom;
import me.zhiyao.faintecho.db.service.ConversationService;
import me.zhiyao.faintecho.db.service.IdiomService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author WangZhiYao
 * @date 2020/10/31
 */
@Slf4j
@Service
@AllArgsConstructor
public class IdiomSolitaireService {

    private static final String KEYWORDS_ACTIVE = "成语接龙";
    private static final String KEYWORDS_CHANGE_MODEL = "选择模式";
    private static final String KEYWORDS_AGAIN = "再来一盘";

    private static final int CONVERSATION_TYPE_INPUT = 1;
    private static final int CONVERSATION_TYPE_OUTPUT = 2;

    private static final String IDIOM_SOLITAIRE_MODEL_INSTRUCTIONS = "请输入序号选择成语接龙模式：\n" +
            "1.鶸模式(上一个成语的最后一个字与下一个成语的第一个字读音相同即可)\n" +
            "2.正常人模式(上一个成语的最后一个与下一个成语的第一个字的字与读音必须相同)\n" +
            "30 秒未回复模式选择将退出成语接龙模式";

    private static final String FINISH_OPTIONS = "输入 “" + KEYWORDS_CHANGE_MODEL + "” 重新设置模式\n" +
            "输入 “" + KEYWORDS_AGAIN + "” 重新开始\n" +
            "回复任意或 30 秒未回复将退出成语接龙模式";

    private final IdiomService mIdiomService;
    private final ConversationService mConversationService;

    private final ConcurrentHashMap<String, List<String>> mUserIdiomSolitaireHistoryMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IdiomSolitaireModel> mUserIdiomSolitaireModelMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserSolitaireStatus> mUserSolitaireStatusMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ScheduledFuture<?>> mUserPurgeServiceMap = new ConcurrentHashMap<>();

    private final ScheduledExecutorService mPurgeService = Executors.newSingleThreadScheduledExecutor();

    public boolean isUserActiveIdiomModel(WxMpXmlMessage wxMessage) {
        if (KEYWORDS_ACTIVE.equals(wxMessage.getContent())) {
            String user = wxMessage.getFromUser();
            log.info("用户：" + user + " 进入成语接龙");
            mUserSolitaireStatusMap.put(user, UserSolitaireStatus.CHANGE_MODEL);
            mUserPurgeServiceMap.put(user, mPurgeService.schedule(new PurgeUserRunnable(user), 30, TimeUnit.SECONDS));
            mConversationService.save(new Conversation(null, user, CONVERSATION_TYPE_INPUT,
                    wxMessage.getContent(), System.currentTimeMillis()));
            return true;
        } else {
            return false;
        }
    }

    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        mConversationService.save(new Conversation(null, wxMessage.getFromUser(), CONVERSATION_TYPE_INPUT,
                IDIOM_SOLITAIRE_MODEL_INSTRUCTIONS, System.currentTimeMillis()));
        return new TextBuilder().build(IDIOM_SOLITAIRE_MODEL_INSTRUCTIONS, wxMessage, wxMpService);
    }

    public boolean isUserIdiomModel(String user) {
        return mUserSolitaireStatusMap.containsKey(user);
    }

    public WxMpXmlOutMessage idiomSolitaire(WxMpXmlMessage wxMessage, WxMpService wxMpService) {
        String user = wxMessage.getFromUser();

        mConversationService.save(new Conversation(null, user, CONVERSATION_TYPE_INPUT,
                wxMessage.getContent(), System.currentTimeMillis()));


        String message = null;

        UserSolitaireStatus status = mUserSolitaireStatusMap.get(user);

        if (status == null) {
            message = "触及到了状态盲区";
        } else {
            switch (status) {
                case GAMING:
                    message = onGaming(wxMessage);
                    break;
                case CHANGE_MODEL:
                    message = onChangeModel(wxMessage);
                    break;
                case FINISHED:
                    message = onFinished(wxMessage);
                    break;
            }
        }

        mConversationService.save(new Conversation(null, user, CONVERSATION_TYPE_OUTPUT,
                message, System.currentTimeMillis()));

        log.info("回复用户：" + user + "：" + message);

        return new TextBuilder().build(message, wxMessage, wxMpService);
    }

    private String onGaming(WxMpXmlMessage wxMessage) {
        String message;

        String user = wxMessage.getFromUser();
        String content = wxMessage.getContent();

        log.info("收到用户：" + user + " 成语接龙：" + wxMessage.getContent());

        Idiom inputIdiom = mIdiomService.getIdiom(content);

        if (inputIdiom == null) {
            message = "该词并不是成语：" + content + "\n鶸你输了哦\n" + FINISH_OPTIONS;
            mUserSolitaireStatusMap.put(user, UserSolitaireStatus.FINISHED);
            mUserPurgeServiceMap.put(user, mPurgeService.schedule(new PurgeUserRunnable(user), 30, TimeUnit.SECONDS));
            return message;
        }

        List<String> historyList = mUserIdiomSolitaireHistoryMap.getOrDefault(user, new ArrayList<>());

        if (historyList.contains(content)) {
            message = "用到重复成语：" + content + ", 鶸你输了哦\n" + FINISH_OPTIONS;
            mUserSolitaireStatusMap.put(user, UserSolitaireStatus.FINISHED);
            mUserPurgeServiceMap.put(user, mPurgeService.schedule(new PurgeUserRunnable(user), 30, TimeUnit.SECONDS));
            return message;
        }

        Idiom answer = null;

        IdiomSolitaireModel userModel = mUserIdiomSolitaireModelMap.get(user);
        switch (userModel) {
            case EASY:
                answer = getEasyAnswer(historyList, inputIdiom);
                break;
            case NORMAL:
                answer = getNormalAnswer(historyList, inputIdiom);
                break;
        }

        if (answer != null) {
            message = String.format("%s\n读音：%s\n释义：%s\n出处：%s\n示例：%s",
                    answer.getValue(), answer.getPinyin(), answer.getParaphrase(),
                    answer.getSource(), answer.getExample());
        } else {
            message = "妹妹你难到我了，我输了，一定是代码有问题\n" + FINISH_OPTIONS;
            mUserSolitaireStatusMap.put(user, UserSolitaireStatus.FINISHED);
            mUserPurgeServiceMap.put(user, mPurgeService.schedule(new PurgeUserRunnable(user), 30, TimeUnit.SECONDS));
        }

        return message;
    }

    private String onChangeModel(WxMpXmlMessage wxMessage) {
        String message = IDIOM_SOLITAIRE_MODEL_INSTRUCTIONS;

        String user = wxMessage.getFromUser();
        String content = wxMessage.getContent();

        log.info("收到用户：" + user + " 更换模式：" + wxMessage.getContent());

        IdiomSolitaireModel model = null;

        try {
            model = IdiomSolitaireModel.getModel(Integer.parseInt(content));
        } catch (NumberFormatException ex) {
            log.error("成语接龙选择模式错误：" + wxMessage.getContent());
        }

        if (model != null) {
            IdiomSolitaireModel currentModel = mUserIdiomSolitaireModelMap.get(user);
            if (currentModel == null) {
                switch (model) {
                    case EASY:
                        message = "太菜了，你出";
                        break;
                    case NORMAL:
                        message = "噢哟想挑战自我？来，你出";
                        break;
                }
            } else {
                if (currentModel.equals(model)) {
                    message = "一样的难度还换个P，接着玩";
                } else {
                    if (currentModel.getValue() > model.getValue()) {
                        message = "太菜了，你出";
                    } else {
                        message = "噢哟想挑战自我？来，你出";
                    }
                }
            }

            mUserIdiomSolitaireModelMap.put(user, model);
            mUserSolitaireStatusMap.put(user, UserSolitaireStatus.GAMING);
            ScheduledFuture<?> future = mUserPurgeServiceMap.get(user);
            if (future != null) {
                future.cancel(true);
            }
        }

        return message;
    }

    private String onFinished(WxMpXmlMessage wxMessage) {
        String message = "你是傻的，已经退出成语接龙模式";

        String user = wxMessage.getFromUser();
        String content = wxMessage.getContent();

        log.info("收到用户：" + user + " 完成游戏：" + wxMessage.getContent());

        if (content.equals(KEYWORDS_AGAIN)) {
            message = "鶸，来就来，你先出";
            mUserIdiomSolitaireHistoryMap.remove(user);
            mUserSolitaireStatusMap.put(user, UserSolitaireStatus.GAMING);
            ScheduledFuture<?> future = mUserPurgeServiceMap.get(user);
            if (future != null) {
                future.cancel(true);
            }
            return message;
        }

        if (content.equals(KEYWORDS_CHANGE_MODEL)) {
            message = IDIOM_SOLITAIRE_MODEL_INSTRUCTIONS;
            mUserIdiomSolitaireHistoryMap.remove(user);
            mUserSolitaireStatusMap.put(user, UserSolitaireStatus.CHANGE_MODEL);
            ScheduledFuture<?> future = mUserPurgeServiceMap.get(user);
            if (future != null) {
                future.cancel(true);
            }
            return message;
        }

        exitIdiomSolitaire(user);

        return message;
    }

    private Idiom getEasyAnswer(List<String> historyList, Idiom inputIdiom) {
        List<Idiom> candidateIdioms = mIdiomService.getIdiomByStartPinyin(inputIdiom.getEndPinyin());
        return getAnswer(historyList, candidateIdioms);
    }

    private Idiom getNormalAnswer(List<String> historyList, Idiom inputIdiom) {
        List<Idiom> candidateIdioms = mIdiomService
                .getIdiomByStartCharAndPinyin(inputIdiom.getEndChar(), inputIdiom.getEndPinyin());
        return getAnswer(historyList, candidateIdioms);
    }

    private Idiom getAnswer(List<String> historyList, List<Idiom> candidateIdioms) {
        if (candidateIdioms == null || candidateIdioms.isEmpty()) {
            return null;
        }

        Idiom answer = null;

        Collections.shuffle(candidateIdioms);

        for (Idiom candidateIdiom : candidateIdioms) {
            boolean exists = false;

            for (String history : historyList) {
                if (candidateIdiom.getValue().equals(history)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                answer = candidateIdiom;
                break;
            }
        }

        return answer;
    }

    private void exitIdiomSolitaire(String user) {
        mUserIdiomSolitaireHistoryMap.remove(user);
        mUserIdiomSolitaireModelMap.remove(user);
        mUserSolitaireStatusMap.remove(user);
        log.info("用户：" + user + " 退出成语接龙");
    }

    private class PurgeUserRunnable implements Runnable {

        private final String user;

        private PurgeUserRunnable(String user) {
            this.user = user;
        }

        @Override
        public void run() {
            exitIdiomSolitaire(user);
        }
    }
}
