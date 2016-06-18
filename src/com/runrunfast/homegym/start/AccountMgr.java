package com.runrunfast.homegym.start;

public class AccountMgr {
	private final String TAG = "AccountMgr";
	
	private static Object lockObject = new Object();
	private volatile static AccountMgr instance;
	
	public static AccountMgr getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new AccountMgr();
				}
			}
		}
		return instance;
	}
	
	private AccountMgr(){
		
	}
	
	public boolean checkLoginLegal(){
		
		return true;
	}
	
}
