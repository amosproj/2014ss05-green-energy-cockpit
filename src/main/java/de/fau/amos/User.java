/*
 * Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob Hübler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
 *
 * This file is part of the Green Energy Cockpit for the AMOS Project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */


package de.fau.amos;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;

public class User {

	private static ServletConfig config=null;
	private static File userData;
	private static final String userDataFileName="userData";

	/**
	 * inits User. Self-returning if allready inited, 
	 * should be called in doPost/doGet in every Servlet that is using static Methods from Class User.
	 * @param servletConfig Call with getServletConfig()
	 */
	public static void init(ServletConfig servletConfig){
		if(servletConfig==null||(config!=null&&config.equals(servletConfig))){
			return;
		}
		config=servletConfig;
		userData=new File(config.getServletContext().getRealPath("/WEB-INF"),userDataFileName);
	}

	/**
	 * Checks if a username has invalid charecters.
	 * @param username the username to be checked
	 * @return true, if the username can be used, otherwise false
	 */
	public static boolean isUsernameValid(String username){

		if(username==null||username.length()==0){
			return false;
		}

		boolean usernameInvalid=false;

		for(int i=0;i<username.length();i++){
			//username contains special charecters -> invalid
			char c=username.charAt(i);
			if(!( (c>='a'&&c<='z')||(c>='A'&&c<='Z')||(c>='0'&&c<='9') )){
				usernameInvalid=true;
				break;
			}

		}			
		return !usernameInvalid;
	}

	/**
	 * Checks if the given username matches the password
	 * @param username to be checked
	 * @param password to be checked
	 * @return int value matching static constants
	 */
	public static int exists(String username,String password){

		if(userData==null||!userData.exists()){
			return Const.UserReturns.FAILED;
		}
		if(!isUsernameValid(username)){
			return Const.UserReturns.USERNAME_INVALID;
		}

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(userData));

			String line;

			//step through file until match is found
			while((line=br.readLine())!=null){
				if(line.equals(username+"|"+password)){
					//found user in userData && password is correct -> access to be granted
					br.close();
					return Const.UserReturns.SUCCESS;
				}else if(line.startsWith(username+"|")){
					//found user in userData, but password is not correct -> access to be denied -> retry
					br.close();
					return Const.UserReturns.PASSWORD_WRONG;
				}
			}

			//reaching this means user was not found
			br.close();	
			return Const.UserReturns.USERNAME_UNKNOWN;	

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Const.UserReturns.FAILED;
		} catch (IOException e) {
			e.printStackTrace();
			return Const.UserReturns.FAILED;
		}

	}
	
	/**
	 * Adds username to the userData-File
	 * @param username to be added
	 * @param password to be assigned to the user
	 * @return int value matching static constants
	 */
	public static int add(String username,String password){
	
		if(userData==null||!userData.exists()){
			return Const.UserReturns.FAILED;
		}
		if(!isUsernameValid(username)){
			return Const.UserReturns.USERNAME_INVALID;
		}
		if(password==null||password.length()==0){
			return Const.UserReturns.PASSWORD_INVALID;
		}
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(userData));

			//save knownUsers for later rewriting userData-File
			ArrayList<String> knownUsers=new ArrayList<String>();
			String line;

			//step through file until match is found
			while((line=br.readLine())!=null){
				knownUsers.add(line);
				if(line.startsWith(username+"|")){
					//user allready exists in userData
					br.close();
					return Const.UserReturns.USERNAME_EXISTS;
				}
			}

			//reaching this means user was not found and can be added
			br.close();	
			
			BufferedWriter bw=new BufferedWriter(new FileWriter(userData));
			for(int i=0;i<knownUsers.size();i++){
				bw.write(knownUsers.get(i));
				bw.newLine();
			}
			bw.write(username+"|"+password);
			bw.flush();
			bw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Const.UserReturns.FAILED;
		} catch (IOException e) {
			e.printStackTrace();
			return Const.UserReturns.FAILED;
		}

		return Const.UserReturns.SUCCESS;
	}

	/**
	 * Changes usernames password.
	 * @param username thats password is to be changed
	 * @param oldPassword the old password username uses
	 * @param newPassword the new password username shall use
	 * @return int value matching static constants
	 */
	public static int changePassword(String username,String oldPassword,String newPassword){
		
		if(userData==null||!userData.exists()){
			return Const.UserReturns.FAILED;
		}
		if(!isUsernameValid(username)){
			return Const.UserReturns.USERNAME_INVALID;
		}
		if(newPassword==null||newPassword.length()==0){
			return Const.UserReturns.PASSWORD_INVALID;
		}
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(userData));

			//save knownUsers for later rewriting userData-File
			ArrayList<String> knownUsers=new ArrayList<String>();
			String line;

			//step through file until match is found
			while((line=br.readLine())!=null){
				if(line.equals(username+"|"+oldPassword)){
					//found user, oldPassword matches
					
					//replace entry
					knownUsers.add(username+"|"+newPassword);
				}else if(line.startsWith(username+"|")){
					//found user, oldPassword doesn't match
					//break here
					br.close();
					return Const.UserReturns.PASSWORD_WRONG;
				}else{
					knownUsers.add(line);
				}
			}

			//reaching this means user was not found and can be added
			br.close();	
			
			BufferedWriter bw=new BufferedWriter(new FileWriter(userData));
			for(int i=0;i<knownUsers.size();i++){
				bw.write(knownUsers.get(i));
				if(i!=knownUsers.size()-1){
					bw.newLine();
				}
			}
			bw.flush();
			bw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Const.UserReturns.FAILED;
		} catch (IOException e) {
			e.printStackTrace();
			return Const.UserReturns.FAILED;
		}

		return Const.UserReturns.SUCCESS;
	}
}
