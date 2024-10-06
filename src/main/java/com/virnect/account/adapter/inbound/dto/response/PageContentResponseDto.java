package com.virnect.account.adapter.inbound.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel
public class PageContentResponseDto<T> {
	@ApiModelProperty(value = "페이징 컨텐츠")
	private final List<T> contents;
	@ApiModelProperty(value = "페이징 정보", position = 1)
	private final PageMeta pageMeta;

	public PageContentResponseDto(Page<T> pageResult, Pageable pageRequest) {
		this.contents = pageResult.getContent();
		this.pageMeta = PageMeta.from(pageResult, pageRequest);
	}
}
