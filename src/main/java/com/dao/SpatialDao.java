package com.dao;

public interface SpatialDao {
	
	public byte[] getContents(String type,int x,int y,int z);
	
	public byte[] getPoints(String table,int x,int y,int z);
	

}
