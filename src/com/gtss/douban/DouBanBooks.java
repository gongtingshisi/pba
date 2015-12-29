package com.gtss.douban;

import java.util.ArrayList;

import kg.gtss.personalbooksassitant.R;

/**
 * A data struct class to state a book,which stored info from DouBan server.
 * 這是根據api返回的json形式。 API Key 01d488a704c29d13057d99f50e47d739 Secret
 * 47bafd624c6acbe0
 * */

public class DouBanBooks {
	/**
	 * 红心喜欢
	 * */
	public boolean favorite;
	final public static String TYPE_favorite = "favorite";

	final public static String ID = "_id";
	/**
	 * 书本评价相关
	 * */
	public Rating rating = new Rating();
	final public static String TYPE_rating = "rating";
	/**
	 * 书本副标题
	 * */
	public String subtitle;
	final public static String TYPE_subtitle = "subtitle";
	/**
	 * 作者：姓名，国籍
	 * */
	public ArrayList<String> author = new ArrayList<String>();
	final public static String TYPE_author = "author";// author
	/**
	 * 出版日期
	 * */
	public String pubdate;
	final public static String TYPE_pubdate = "pubdate";
	/**
	 * 书籍标签
	 * */
	public ArrayList<Tags> tags = new ArrayList<Tags>();
	final public static String TYPE_tags = "tags";

	/**
	 * 書籍原名，可能有多個名字
	 * */
	public String origin_title;
	final public static String TYPE_origin_title = "origin_title";
	/**
	 * 图书图片url
	 * */
	public String image;
	/**
	 * 图书图片url
	 * */
	final public static String TYPE_image = "image";

	/**
	 * 精裝还是平裝
	 * */
	public String binding;
	final public static String TYPE_binding = "binding";
	/**
	 * 译者,或者是多个人
	 * */
	public ArrayList<String> translator = new ArrayList<String>();
	final public static String TYPE_translater = "translator";
	/**
	 * 目盧
	 * */
	// public String[] catalog;
	public String catalog;
	final public static String TYPE_catalog = "catalog";
	/**
	 * 書籍頁數
	 */
	public int pages;
	final public static String TYPE_pages = "pages";
	/**
	 * 圖書圖片資源
	 * */
	public Images images = new Images();
	final public static String TYPE_images = "images";
	/**
	 * 書本對應豆瓣詳情頁url.其實是http://book.douban.com/subject/+id
	 * */
	public String alt;
	final public static String TYPE_alt = "alt";
	/**
	 * 豆瓣為這本書分配的id
	 * */
	public long id;
	final public static String TYPE_id = "id";
	/**
	 * 出版社
	 * */
	public String publisher;
	final public static String TYPE_publisher = "publisher";
	/**
	 * isbn10
	 * */
	public String isbn10;
	final public static String TYPE_isbn10 = "isbn10";
	/**
	 * isbn13
	 * */
	public String isbn13;
	final public static String TYPE_isbn13 = "isbn13";
	/**
	 * 書名
	 * */
	public String title;
	final public static String TYPE_title = "title";
	/**
	 * 書本對應豆瓣api返回信息頁。其實是http://api.douban.com/v2/book/+id
	 * */
	public String url;
	final public static String TYPE_url = "url";
	/**
	 * 原著名稱
	 * */
	public String alt_title;
	final public static String TYPE_alt_title = "alt_title";
	/** 作者介紹 */
	public String author_intro;
	final public static String TYPE_author_intro = "author_intro";
	/**
	 * 書本概要介紹
	 * */
	public String summary;
	final public static String TYPE_summary = "summary";
	/**
	 * 價格，中國是元結尾的字串，在類似amazon可能是美元
	 * */
	public String price;
	final public static String TYPE_price = "price";

	/**
	 * 收录日期
	 * */
	public String date;
	final public static String TYPE_date = "date";

	/**
	 * 图片在本地存储的资源Uri
	 * */
	public String imguri;
	public static final String TYPE_tags_img_uri = "imguri";

	/**
	 * 构造函数
	 * */
	public DouBanBooks() {
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String str = new String("[");
		str += (TYPE_title + ":" + title + "; ");
		str += (TYPE_author + ":" + author.get(0) + "; ");
		str += (TYPE_summary + ":" + summary);
		str += "]";
		return str;
	}

	/**
	 * Evaluation. 书本评价相关
	 * */
	public class Rating {
		public Rating() {
		}

		public Rating(double max, int numRaters, double average, double min) {
			this.average = average;
			this.max = max;
			this.min = min;
			this.numRaters = numRaters;
		}

		/**
		 * 最高评价
		 * */
		public double max;
		public static final String TYPE_rating_max = "max";
		/**
		 * 已评价人次
		 * */
		public int numRaters;
		public static final String TYPE_rating_numRaters = "numRaters";
		/**
		 * 评价均分
		 * */
		public double average;
		public static final String TYPE_rating_average = "average";
		/**
		 * 最低得分
		 * */
		public double min;
		public static final String TYPE_rating_min = "min";
	}

	/**
	 * 作者，对于类似外文著作，有中英文名字。对于其他语种，可能会有三种语言名字，也可能是合著，有多個作者。或者，這個還可以擴充國籍之類的信息
	 * */
	// public class Author {
	// public String[] name;
	// public static final String TYPE_author_name = "name";
	// }

	/**
	 * 标签
	 * */
	public static class Tags {
		public Tags(int count, String name, String title) {
			this.count = count;
			this.name = name;
			this.title = title;
		}

		/**
		 * 被作为标签人次
		 * */
		public int count;
		public static final String TYPE_tags_count = "count";
		/**
		 * 标签名
		 * */
		public String name;
		public static final String TYPE_tags_name = "name";
		/**
		 * 标签名，和name好像一样
		 * */
		public String title;
		public static final String TYPE_tags_title = "title";

	}

	/**
	 * 圖書圖片資源
	 * */
	public class Images {
		public Images() {
		}

		public Images(String small, String large, String medium) {
			this.small = small;
			this.large = large;
			this.medium = medium;
		}

		/**
		 * small
		 * */
		String small;
		public static final String TYPE_images_small = "small";
		/**
		 * large
		 * */
		String large;
		public static final String TYPE_images_large = "large";
		/**
		 * medium
		 * */
		String medium;
		public static final String TYPE_images_medium = "medium";
	}
}
