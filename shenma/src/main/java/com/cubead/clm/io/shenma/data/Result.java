package com.cubead.clm.io.shenma.data;

import java.util.List;
import java.util.UUID;

public class Result<T> {
	private boolean success;
	private List<T> data;
	private UUID tag;
	
	public Result() {}
	
	public Result(boolean success, List<T> data) {
		this.success = success;
		this.data = data;
	}

	public UUID getTag() {
		return tag;
	}

	public void setTag(UUID tag) {
		this.tag = tag;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
