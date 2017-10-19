package com.tangsoft.xkr.jiujiaotianxia.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tangsoft.xkr.jiujiaotianxia.R;
import com.tangsoft.xkr.jiujiaotianxia.config.ConfigFile;
import com.tangsoft.xkr.jiujiaotianxia.model.ShareInfo;
import com.tangsoft.xkr.jiujiaotianxia.wxapi.WXPayEntryActivity;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;


/** 分享(qq,weixin,pyq) */
public class UtilShare {

	public static final String QQ_APPID = "1106175996";
	public static final String QQ_APPID_KEY  = "crRjwu6pQKRJP1L2";

	public static final String WEIBO_APPID = "1792374549";
	public static final String WEIBO_APPID_KEY  = "b1c2f85c9e7159fdfd2a27600f8a0398";

	public static IWXAPI mWxAPI;

    protected Tencent tencent;

	protected Activity activity;
	protected String shareContent;// 分享内容
	protected String showUrl;// 跳转链接
	protected String shareTitle;// 图片标题
	protected String picUrl;// 图片地址
	private static ImageLoader imageLoader;

	public static String WEIXIN = "weixin";
	public static String QQ = "qq";
	public static String PYQ = "pyq";
	public static String WEIBO = "weibo";
	public UtilShare(Activity activity, String picUrl,
                     String shareTitle, String shareContent, String showUrl, String type) {
		this.activity = activity;
		this.picUrl = picUrl;
		this.shareContent = shareContent;
		this.showUrl = showUrl;
		this.shareTitle = shareTitle;

		if (WEIXIN.equals(type))
			shareToWx(false);
		else if (QQ.equals(type))
			shareToQQ();
		else if (PYQ.equals(type))
			shareToWx(true);
		else if (WEIBO.equals(type))
			shareWeiBo();
	}

	/**
	 * @param info  发送的信息model
	 * @param type 分享类型
	 * */
	public UtilShare(Activity activity, ShareInfo info, String type) {
		this.activity = activity;
		this.picUrl = info.getImgUrl();
		this.shareContent = info.getSpreadContent();
		this.showUrl = info.getSpreadUrl();
		this.shareTitle = info.getTitle();
		getImageloader(activity);
		if (WEIXIN.equals(type))
			shareToWx(false);
		else if (QQ.equals(type))
			shareToQQ();
		else if (PYQ.equals(type))
			shareToWx(true);
		else if (WEIBO.equals(type))
			shareWeiBo();
	}

	/** 分享给QQ好友 */
	public void shareToQQ() {
		tencent = Tencent.createInstance(QQ_APPID, activity);

		Bundle params = new Bundle();
		params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, showUrl);
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
				QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, picUrl);
		tencent.shareToQQ(activity, params, new QQListener());

	}

	protected class QQListener implements IUiListener {

		@Override
		public void onCancel() {
		}

		@Override
		public void onComplete(Object arg0) {
		}

		@Override
		public void onError(UiError arg0) {
			if ("打开浏览器失败!".equals(arg0.errorMessage)) {
				Toast.makeText(activity, "您未安装腾讯qq客户端", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/** 分享信息存储类 */
	WXMediaMessage msg;

	/** 分享微信/朋友圈 */
	protected void shareToWx(final boolean isWxFriend) {

		/** 微信通信API接口 */
		mWxAPI = WXAPIFactory.createWXAPI(activity, WXPayEntryActivity.WX_APPID);

		if (mWxAPI.isWXAppInstalled()) {
			WXWebpageObject webPage = new WXWebpageObject();
			webPage.webpageUrl = showUrl;
			msg = new WXMediaMessage(webPage);

			if(isWxFriend){//是朋友圈分享title+content
				shareTitle = shareTitle + " - "+ shareContent;
			}
			if (shareTitle.length()>100) {
				shareTitle = shareTitle.substring(0,100);
			}
			if (shareContent.length()>100) {
				shareContent = shareContent.substring(0,100);
			}
			msg.title = shareTitle;
			msg.description = isWxFriend ?  shareTitle :shareContent;
			Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),R.mipmap.ic_launcher);
			sendShare(bitmap,isWxFriend,msg,mWxAPI, Bitmap.CompressFormat.JPEG);
			imageLoader.loadImage(picUrl, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {
				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {
					sendShare(null,isWxFriend,msg,mWxAPI, Bitmap.CompressFormat.PNG);
				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					sendShare(bitmap,isWxFriend,msg,mWxAPI, Bitmap.CompressFormat.JPEG);
				}

				@Override
				public void onLoadingCancelled(String s, View view) {
					//sendShare(null,isWxFriend,msg,wxApi,CompressFormat.PNG);
				}
			});
//			Glide.with(activity).asBitmap().load(picUrl).into(new SimpleTarget<Bitmap>() {
//				@Override
//				public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//					sendShare(resource,isWxFriend,msg,mWxAPI, Bitmap.CompressFormat.JPEG);
//				}
//			});


		} else {
			// 未安装微信客户端
			Toast.makeText(activity, "请先安装微信客户端", Toast.LENGTH_SHORT).show();
		}

	}

	/** 发送微信分享请求
	 * @param bitmap 分享图片bitmap ，默认图传null
	 * @param isWxFriend  朋友圈 true,  微信好友 false;
	 * @param msg   发送的信息
	 * @param wxApi
	 * @param format  图片压缩格式：网络JPEG，本地PNG
	 * */
	public void sendShare(Bitmap bitmap, boolean isWxFriend, WXMediaMessage msg, IWXAPI wxApi, Bitmap.CompressFormat format) {
		if (Bitmap.CompressFormat.PNG.equals(format) || bitmap == null) {
			bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
		}
		ByteArrayOutputStream output;
		try{
			output = new ByteArrayOutputStream();
			int  pressSize =  35;
			bitmap.compress(format, 45, output);
			while (output.size() > 30 * 1024) {
				output.reset();
				bitmap.compress(format, pressSize, output);
				if (pressSize <= 5) {
					pressSize -= 1;
				} else {
					pressSize-=10;
				}
				if(pressSize < 1){
					Logger.dShortToast("image too large");
					return;
				}
			}
			//bitmap.recycle();
			msg.thumbData = output.toByteArray();

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = String.valueOf(System.currentTimeMillis());
			req.message = msg;
			req.scene = isWxFriend ? SendMessageToWX.Req.WXSceneTimeline: SendMessageToWX.Req.WXSceneSession;
			wxApi.sendReq(req);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 下载图片  分享到新浪微博
	 */
	public void shareWeiBo(){
		imageLoader.loadImage(picUrl, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String s, View view) {
			}

			@Override
			public void onLoadingFailed(String s, View view, FailReason failReason) {
				Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
				sendShareSina(bitmap, Bitmap.CompressFormat.JPEG);
			}

			@Override
			public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//				sendShareSina(bitmap,isWxFriend,msg,mWxAPI, Bitmap.CompressFormat.JPEG);
				sendShareSina(bitmap, Bitmap.CompressFormat.JPEG);
			}

			@Override
			public void onLoadingCancelled(String s, View view) {
				//sendShare(null,isWxFriend,msg,wxApi,CompressFormat.PNG);
			}
		});

//		Glide.with(activity).asBitmap().load(picUrl).into(new SimpleTarget<Bitmap>() {
//			@Override
//			public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//				sendShareSina(resource, Bitmap.CompressFormat.JPEG);
//			}
//		});

	}

	/**
	 * 分享到新浪微博
	 * @param bitmap 图片
	 * @param format
	 */
	private void sendShareSina(Bitmap bitmap, Bitmap.CompressFormat format){
		try {
			IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, WEIBO_APPID_KEY);
			boolean isAppInstalled = mWeiboShareAPI.isWeiboAppInstalled();
			if(isAppInstalled){

				if (Bitmap.CompressFormat.PNG.equals(format) || bitmap == null) {
					bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
				}

				ByteArrayOutputStream output;
				output = new ByteArrayOutputStream();
				int  pressSize =  35;
				bitmap.compress(format, 45, output);
				while (output.size() > 25 * 1024) {
					output.reset();
					bitmap.compress(format, pressSize, output);
					if (pressSize <= 5) {
						pressSize -= 1;
					} else {
						pressSize-=10;
					}
					if(pressSize < 1){
						Logger.dShortToast("image too large");
						return;
					}
				}
				//bitmap.recycle();
				byte[] bytes = output.toByteArray();
				bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);


				mWeiboShareAPI.registerApp();

				TextObject textObject = new TextObject();
				textObject.text = TextUtils.isEmpty(shareContent) ? "无内容" : shareContent;

				// Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.icon);

				ImageObject imageObject = new ImageObject();
				imageObject.setImageObject(bitmap);

				WebpageObject mediaObject = new WebpageObject();
				mediaObject.identify = Utility.generateGUID();
				mediaObject.title = TextUtils.isEmpty(shareTitle) ? "无标题" : shareTitle;
				mediaObject.description = TextUtils.isEmpty(shareContent) ? "无内容" : shareContent;

				// 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
				mediaObject.setThumbImage(bitmap);
				mediaObject.actionUrl = showUrl;
				mediaObject.defaultText = "Webpage 默认文案";

				WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
				weiboMessage.textObject = textObject;
				weiboMessage.imageObject = imageObject;
				weiboMessage.mediaObject = mediaObject;

				SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
				request.transaction = String.valueOf(System.currentTimeMillis());
				request.multiMessage = weiboMessage;
				mWeiboShareAPI.sendRequest(activity,request); //发送请求消息到微博，唤起微博分享界面
			} else {
				// 未安装微信客户端
				Toast.makeText(activity, "请先安装微博客户端", Toast.LENGTH_SHORT).show();
			}
		} catch(Exception e) {
		    e.printStackTrace();
		}
	}

	/**
	 * imageloader 的配置
	 */
	public static ImageLoader getImageloader(Context context) {
		ImageLoader.getInstance().init(
				new ImageLoaderConfiguration

						.Builder(context)

						.memoryCacheExtraOptions(480, 800)
						// max width, max height，即保存的每个缓存文件的最大长宽

						.threadPoolSize(3)
						// 线程池内加载的数量

						.threadPriority(Thread.NORM_PRIORITY - 2)

						.denyCacheImageMultipleSizesInMemory()

						.memoryCache(new WeakMemoryCache())
						// You can pass your own memory cache
						// implementation/你可以通过自己的内存缓存实现
						// .memoryCacheSize(5 * 1024 * 1024)
						.discCacheSize(50 * 1024 * 1024)

						.discCacheFileNameGenerator(new Md5FileNameGenerator())
						// 将保存的时候的URI名称用MD5 加密

						.tasksProcessingOrder(QueueProcessingType.LIFO)

						.discCacheFileCount(500)
						// 缓存的文件数量

						.discCache(new UnlimitedDiscCache(UtilIoAndr.createDirInAndroid(context, ConfigFile.CACHE_DIR)))
						// 自定义缓存路径

						.defaultDisplayImageOptions(DisplayImageOptions.createSimple())

						.imageDownloader(
								new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout

						.writeDebugLogs() // Remove for release app

						.build());// 开始构建
		imageLoader = ImageLoader.getInstance();
		return imageLoader;
	}


}