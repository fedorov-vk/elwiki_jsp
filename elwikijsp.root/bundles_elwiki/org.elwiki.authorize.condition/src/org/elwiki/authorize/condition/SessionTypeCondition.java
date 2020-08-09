package org.elwiki.authorize.condition;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.Condition;
import org.osgi.service.condpermadmin.ConditionInfo;

public class SessionTypeCondition implements Condition {

	private static final String CONDITION_TYPE = SessionTypeCondition.class.getName();

	public static ThreadLocal<String> checkedSessionType = new ThreadLocal<String>();

	private final String sessionType;

	static public Condition getCondition(final Bundle bundle, final ConditionInfo info) {
		if (!CONDITION_TYPE.equals(info.getType())) {
			throw new IllegalArgumentException("ConditionInfo must be of type \"" + CONDITION_TYPE + "\"");
		}
		return new SessionTypeCondition(bundle, info);
	}

	/**
	 * Private constructor to prevent objects of this type.
	 * 
	 * @param bundle
	 * @param info
	 */
	private SessionTypeCondition(Bundle bundle, ConditionInfo info) {
		String[] args = info.getArgs();
		if (args.length != 1) {
			throw new IllegalArgumentException("Illegal number of args: " + args.length);
		}
		sessionType = info.getArgs()[0];
	}

	@Override
	public boolean isPostponed() {
		return false;
	}

	@Override
	public boolean isSatisfied() {
		boolean status = sessionType.equals(checkedSessionType.get());
		return status;
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public boolean isSatisfied(Condition[] conditions, Dictionary<Object, Object> context) {
		return false;
	}

}