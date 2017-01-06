package cnx.cclink.unishare;

/**
 * Created by cclink on 2016/12/30.
 * 分享的内容
 */

public class ShareContent {
    public String shareTitle;       // 要分享的标题
    public String shareDetail;      // 要分享的详情（这里不同平台的叫法不一样，QQ叫summary，微博叫text，微信和易信叫description）
    public String shareImage;       // 要分享的图片
    public String shareUrl;         // 要分享的URL

    public ShareContent(String shareTitle, String shareDetail, String shareImage, String shareUrl) {
        this.shareTitle = shareTitle;
        this.shareDetail = shareDetail;
        this.shareImage = shareImage;
        this.shareUrl = shareUrl;
    }
}
