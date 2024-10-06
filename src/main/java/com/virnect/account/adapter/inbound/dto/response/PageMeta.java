package com.virnect.account.adapter.inbound.dto.response;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class PageMeta {
	@ApiModelProperty(value = "현재 조회한 페이지 번호", notes = "현재 페이지 번호", example = "1")
	private final int currentPage;

	@ApiModelProperty(value = "현재 페이지별 데이터 수", notes = "현재 설정된 페이지별 나타날 데이터의 수", example = "2")
	private final int currentSize;

	@ApiModelProperty(value = "페이지별 데이터 수에 따른 전체 페이지 수", notes = "설정된 페이지당 데이터 표출 수에 의한 전체 페이지 수", example = "10")
	private final int totalPage;

	@ApiModelProperty(value = "조회 된 전체 데이터의 수", notes = "조회 된 전체 데이터의 수", example = "20")
	private final long totalElements;

	public PageMeta(int currentPage, int currentSize, int totalPage, long totalElements) {
		this.currentPage = currentPage;
		this.currentSize = currentSize;
		this.totalPage = totalPage;
		this.totalElements = totalElements;
	}

	public static <T> PageMeta from(Page<T> pageResult, Pageable pageable) {
		return new PageMeta(
			pageable.getPageNumber(), pageable.getPageSize(),
			pageResult.getTotalPages(), pageResult.getTotalElements()
		);
	}
}
