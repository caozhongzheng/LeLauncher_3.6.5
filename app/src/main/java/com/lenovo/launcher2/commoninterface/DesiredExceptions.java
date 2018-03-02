package com.lenovo.launcher2.commoninterface;

import com.lenovo.launcher2.commoninterface.InfoFactory.BaseInfo;
import com.lenovo.launcher2.customizer.Debug;
import com.lenovo.launcher2.customizer.Debug.R2;

/**
 * Author : ChengLiang
 * */
class DesiredExceptions {
	/**
	 * Will be thrown while parses action is processing
	 */
	static class ParserInProcessException extends Exception {

		private static final long serialVersionUID = 1L;

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("You are trying "
						+ "to access the profiles' data while they are being collected."
						+ "You need to do some wait for this.");
			return super.getMessage();
		}
	}

	/**
	 * Will be thrown while trying to get data before parse
	 */
	static class ParseNotPerformedException extends Exception {

		private static final long serialVersionUID = 2L;

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("You should scheduled force parse firstly.");
			return super.getMessage();
		}
	}

	/**
	 * Will be thrown while the given base-info was null
	 */
	static class InvalidKeyNameException extends Exception {

		private static final long serialVersionUID = 3L;
		private String whose = null;

		public InvalidKeyNameException(String whose) {
			this.whose = whose;
		}

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("\nItem : '"
						+ this.whose
						+ "' was specified no key, please check your config xml file.");
			return super.getMessage();
		}
	}

	/**
	 * An attribute needs a name at least
	 */
	static class InvalidAttributeException extends Exception {
		private static final long serialVersionUID = 4L;
		private String val = null;

		public InvalidAttributeException(String val) {
			this.val = val;
		}

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("\nAn atrribute needs a name for value '" + val + ", "
						+ "please check your config xml file.");
			return super.getMessage();
		}
	}

	/**
	 * An attribute needs a name at least
	 */
	static class NameHasExistsException extends Exception {
		private static final long serialVersionUID = 8L;
		private String val = null;

		public NameHasExistsException(String val) {
			this.val = val;
		}

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("\nThe setting name '" + val
						+ "' has already exists in backup list.");
			return super.getMessage();
		}
	}

	/**
	 * An attribute needs a name at least
	 */
	static class NameNotSpecifiedException extends Exception {
		private static final long serialVersionUID = 9L;
		private String val = null;

		public NameNotSpecifiedException(String val) {
			this.val = val;
		}

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("\nYou must specified an NOT-NULL setting name for value: '"
						+ val + "'.");
			return super.getMessage();
		}
	}

	/**
	 * Now supported : WidgetInfo, SettingInfo, AppInfo, ConfigInfo
	 */
	static class NotSupportedTypeException extends Exception {
		private static final long serialVersionUID = 4L;
		private BaseInfo info = null;

		public NotSupportedTypeException(BaseInfo info) {
			this.info = info;
		}

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("\nDo not support to fetch attributes from the given type '"
						+ info.getClass().getSimpleName()
						+ "'. Expected types : "
						+ "WidgetInfo, SettingInfo, AppInfo, ConfigInfo");
			return super.getMessage();
		}
	}
}
