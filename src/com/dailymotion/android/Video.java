package com.dailymotion.android;

public class Video {
	private String id;
	private String title;
	private int rating;
	private boolean published;//status
	
	public Video(String id,String title,int rating,boolean published){
		this.id = id;
		this.title=title;
		this.rating=rating;
		this.published=published;
	}
	public void print() {
		//System.out.println("id:"+id+"\n"+"title:"+title+"\n"+"rating:"+rating+"\n"+"published:"+published+"\n");
		System.out.println("id:"+id+";"+"title:"+title+"\n");
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	
}
