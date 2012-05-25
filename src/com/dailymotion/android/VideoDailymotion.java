package com.dailymotion.android;

public class VideoDailymotion {
	private String id;
	private String title;
	private int rating;
	private int nbOfView;
	private boolean published;//status

	public VideoDailymotion(String id,String title,int rating,int nbOfView,boolean published){
		this.id = id;
		this.title=title;
		this.rating=rating;
		this.nbOfView=nbOfView;
		this.published=published;
	}
	public String stringize() {
		String returned = "id:"+id+";title:"+title+";Viewed:"+nbOfView+"\n";
		return returned;
	}	
	public void print() {
		//System.out.println("id:"+id+"\n"+"title:"+title+"\n"+"rating:"+rating+"\n"+"published:"+published+"\n");
		System.out.println(this.stringize());
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
	public int getNbOfView() {
		return nbOfView;
	}
	public void setNbOfView(int nbOfView) {
		this.nbOfView = nbOfView;
	}
	
}
