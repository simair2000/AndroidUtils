package com.ti2.mvp.proto.common;

import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Field;

public class BaseJsonObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String toJson() {
		return new Gson().toJson(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
		Field[] fields = this.getClass().getFields();
		sb.append(" [");
		if(fields != null && fields.length > 0) {
			int index = 0;
			for(Field field : fields) {
				try {
					if(index != 0) {
						sb.append(", ");
					}
					sb.append(field.getName() + "=" + field.get(getClass()));
					index++;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
