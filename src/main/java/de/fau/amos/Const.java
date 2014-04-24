package de.fau.amos;

/**
 * Constans used in Parameters/Attributes
 *
 */
public class Const {

	public class URL{
		public static final String LOGIN_JSP="/WEB-INF/login/loginjsp.jsp";
		public static final String REGISTRATION_JSP="/WEB-INF/login/regjsp.jsp";
		public static final String CHANGE_PASSWORD_JSP="/WEB-INF/login/changejsp.jsp";
		public static final String LOGIN_PAGE="/index.jsp";
		public static final String REGISTRTION_PAGE="/WEB-INF/login/reg.jsp";
		public static final String CHANGE_PASSWORD_PAGE="/WEB-INF/login/change.jsp";
		public static final String INTERN_HOME="/intern/index.jsp";
	}


	public static class SessionAttributs{

		public static final String LOGGED_IN_USERNAME="loggedInUsername";
		
		/*
		 * dummy, simple copy and change names
		 */

		//		public static class AttributName{
		//			//don't change variables name, only change its value
		//			public static final String NAME="changeMeToTheNameThatShouldBeUsed";
		//			
		//			
		//			public static class Values{
		//				//change variables name AND value
		//				public static final String VALUE1="changeMeToTheValueThatShouldBeUsed";
		//				public static final String VALUE2="changeMeToTheValueThatShouldBeUsed";
		//				
		//				//feel free to add more
		//			}
		//		}



		public static class LoginState{
			public static final String NAME="loginState";
			public static class Valeus{
				public static final String LOGGED_IN="loggedIn";
				public static final String USERNAME_UNKNOWN="usernameUnknown";
				public static final String PASSWORD_WRONG="passwordWrong";
				public static final String NOT_LOGGED_IN="notLoggedIn";
				public static final String FAILURE="notLoggedIn";

			}
		}

		public static class RegistrationState{
			//don't change variables name, only change its value
			public static final String NAME="registrationState";
			public static final String PASSWORD_MISSMATCH="passwordMissmatch";
			
			public static class NoValues_Use_UserReturns{
			}
		}
		
		public static class RegistrationUsername{
			public static final String NAME="registrationUsername";
		}

		public static class ChangePasswordState{
			//don't change variables name, only change its value
			public static final String NAME="changePasswordState";


			public static class Values{
				//change variables name AND value
				public static final String VALUE1="changeMeToTheValueThatShouldBeUsed";
				public static final String VALUE2="changeMeToTheValueThatShouldBeUsed";

				//feel free to add more
			}
		}
	}














	public static class RequestParameters{

		public static final String LOGIN_USERNAME="loginUsername";
		public static final String LOGIN_PASSWORD="loginPassword";
		public static final String COMING_FROM_LOGINPAGE="comingFromLoginPage";
		
		public static final String REGISTER_USERNAME="registerUsername";
		public static final String REGISTER_PASSWORD_1="registerPassword1";
		public static final String REGISTER_PASSWORD_2="registerPassword2";

	}






	public static class UserReturns{
		public static final int FAILED=-1;
		public static final int SUCCESS=1;
		public static final int USERNAME_EXISTS=2;
		public static final int USERNAME_UNKNOWN=3;
		public static final int USERNAME_INVALID=4;
		public static final int PASSWORD_WRONG=5;
		public static final int PASSWORD_INVALID=6;


	}



}
