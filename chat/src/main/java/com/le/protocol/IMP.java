package com.le.protocol;

/**
 * @Auther: xll
 * @Desc: 自定义协议
 *
 * 上行命令：指服务器向客户端发送的消息内容
 * SYSTEM 系统命令，例如[命令][命令发送时间][接收人] - 系统提示内容
 * 例如：[SYSTEM][124343423123][Tom 老师] – Student 加入聊天室
 *
 * 下行命令：指客户端想服务器发送的命令
 * LOGIN 登录动作：[命令][命令发送时间][命令发送人]
 * 例如：[LOGIN][124343423123][Tom 老师]
 * LOGOUT 退出登录动作：[命令][命令发送时间][命令发送人]
 * 例如：[LOGOUT][124343423123][Tom 老师]
 * CHAT 聊天:[命令][命令发送时间][命令发送人][命令接收人] – 聊天内容
 * 例如：[CHAT][124343423123][Tom 老师][ALL] - 大家好，我是 Tom 老师！
 * FLOWER 发送送鲜花特效:[命令][命令发送时间][命令发送人][命令接收人]
 * 例如：[FLOWER][124343423123][you][ALL]
 */
public enum IMP {
    /** 系统消息 */
    SYSTEM("SYSTEM"),
    /** 登录指令 */
    LOGIN("LOGIN"),
    /** 登出指令 */
    LOGOUT("LOGOUT"),
    /** 聊天消息 */
    CHAT("CHAT"),
    /** 送鲜花 */
    FLOWER("FLOWER");

    /** 判断消息是否是协议内消息 */
    public static boolean isIMP(String msg) {
        return msg.matches("^\\[(SYSTEM|LOGIN|LOGIN|CHAT)|(FLOWER)\\]");
    }

    private String name;

    IMP (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
