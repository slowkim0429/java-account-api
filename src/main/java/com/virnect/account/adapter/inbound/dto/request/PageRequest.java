package com.virnect.account.adapter.inbound.dto.request;

import org.springframework.data.domain.Sort;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public final class PageRequest {
	@ApiModelProperty(hidden = true)
	private static final int MAX_SIZE = 1000;
	@ApiModelProperty(value = "페이지 번호(기본 0)", example = "0")
	private int page = 0;
	@ApiModelProperty(value = "페이지 당 데이터 항목 수(기본 20, 최대 50)", position = 1, example = "20")
	private int size = 20;

	public void setPage(int page) {
		this.page = Math.max(page, 0);
	}

	public void setSize(int size) {
		this.size = Math.min(size, MAX_SIZE);
	}

	public int getPage() {
		return page;
	}

	public int getSize() {
		return size;
	}

	public org.springframework.data.domain.PageRequest of() {
		return org.springframework.data.domain.PageRequest.of(page, size, Sort.Direction.DESC, "id");
	}

	@Override
	public String toString() {
		return "PageRequest{" +
			"page=" + page +
			", size=" + size +
			'}';
	}
}
