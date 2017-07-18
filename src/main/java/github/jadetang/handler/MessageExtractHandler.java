package github.jadetang.handler;

/** in charge of consume message
 * @author sanguan.tangsicheng on 2017/7/18 上午8:17
 */
public interface MessageExtractHandler {


    /**
     * adding a channel to extract message from with fix transfer size
     * @param channelId
     * @param transferSize
     */
    void channelToExtract(String channelId, int transferSize);


    /**
     * register a meesage handler
     * @param messageHandler
     */
    void registerMessageHandler(MessageHandler messageHandler);

}
