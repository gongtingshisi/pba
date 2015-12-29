package com.progress.bookreading;

import java.util.Date;

import kg.gtss.utils.Log;
import kg.gtss.utils.StringUtil;
import kg.gtss.utils.TimeUtils;

import com.gtss.douban.DouBanBooks;

/**
 * 用于一次阅读记录的封装类:包括这本书信息,,从那一页开始读,这次读了多少页,阅读时候的日期
 * */
public class ReadingRecord {

	public ReadingRecord(String title, String author, int page, int base,
			int read, long date) {
		this.title = title;
		this.author = author;
		this.page = page;
		this.base = base;
		this.read = read;
		this.date = date;
	}

	public ReadingRecord(String title, String author, int read, long date) {
		this(title, author, 0, 0, read, date);
	}

	/**
	 * 书信息
	 * */
	/*
	 * public DouBanBooks book = new DouBanBooks(); final public static String
	 * TYPE_book = "book";
	 */
	/**
	 * 书名
	 * */
	public String title;
	final public static String TYPE_title = "title";

	/**
	 * 作者
	 * */
	public String author;
	final public static String TYPE_author = "author";

	/**
	 * 总共多少页
	 * */
	public int page;
	final static public String TYPE_page = "page";

	/**
	 * 从哪一页开始读
	 * */
	public int base;
	final public static String TYPE_base = "base";
	/**
	 * 此次阅读了的页数
	 * */
	public int read;
	final public static String TYPE_read = "read";
	/**
	 * 哪一天阅读
	 * */
	public long date;
	final public static String TYPE_date = "date";

	/**
	 * 用来唯一匹配一本书的阅读历史的颜色
	 * */
	public int color;
	final public static String TYPE_color = "color";

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{Book:" + title + ",Author:" + author + ",Page:" + page
				+ ",From:" + base + ",Read:" + read + ",Date:"
				+ TimeUtils.getSimpleDate(date) + " ,color:" + color + "}";
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (!(o instanceof ReadingRecord) || null == o)
			return false;
		ReadingRecord r = (ReadingRecord) o;
		return StringUtil.stringEquals(title, r.title) && date != r.date;
	}

}
