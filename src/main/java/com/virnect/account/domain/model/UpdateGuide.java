package com.virnect.account.domain.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideRequestDto;
import com.virnect.account.domain.enumclass.FileType;
import com.virnect.account.domain.enumclass.ServiceType;

@Entity
@Getter
@Audited
@Table(name = "update_guides",
	indexes = {
		@Index(name = "idx_name", columnList = "name"),
		@Index(name = "idx_is_exposed", columnList = "is_exposed")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateGuide extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "service_type", length = 50, nullable = false)
	@Enumerated(EnumType.STRING)
	private ServiceType serviceType;

	@Column(name = "file_type", length = 50, nullable = false)
	@Enumerated(EnumType.STRING)
	private FileType fileType;

	@Column(name = "file_url", length = 250, nullable = false)
	private String fileUrl;

	@Column(name = "date_by_update", nullable = false)
	private String dateByUpdate;

	@Column(name = "title", length = 50, nullable = false)
	private String title;

	@Lob
	@Basic(optional = false)
	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "sub_title", length = 50)
	private String subTitle;

	@Lob
	@Basic
	@Column(name = "sub_description")
	private String subDescription;

	@Column(name = "is_exposed", nullable = false)
	private Boolean isExposed;

	private UpdateGuide(
		String name, ServiceType serviceType, FileType fileType, String fileUrl, String dateByUpdate,
		String title, String subTitle, String description, String subDescription
	) {
		this.name = name;
		this.serviceType = serviceType;
		this.fileType = fileType;
		this.fileUrl = fileUrl;
		this.dateByUpdate = dateByUpdate;
		this.title = title;
		this.description = description;
		this.subTitle = subTitle;
		this.subDescription = subDescription;
		this.isExposed = false;
	}

	public static UpdateGuide from(UpdateGuideRequestDto updateGuideRequestDto) {
		return new UpdateGuide(
			updateGuideRequestDto.getName(),
			updateGuideRequestDto.valueOfServiceType(),
			updateGuideRequestDto.valueOfFileType(),
			updateGuideRequestDto.getFileUrl(),
			updateGuideRequestDto.getDateByUpdate(),
			updateGuideRequestDto.getTitle(),
			updateGuideRequestDto.getSubTitle(),
			updateGuideRequestDto.getDescription(),
			updateGuideRequestDto.getSubDescription()
		);
	}

	public void update(UpdateGuideRequestDto updateGuideRequestDto) {
		this.name = updateGuideRequestDto.getName();
		this.serviceType = updateGuideRequestDto.valueOfServiceType();
		this.fileType = updateGuideRequestDto.valueOfFileType();
		this.fileUrl = updateGuideRequestDto.getFileUrl();
		this.dateByUpdate = updateGuideRequestDto.getDateByUpdate();
		this.title = updateGuideRequestDto.getTitle();
		this.description = updateGuideRequestDto.getDescription();
		this.subTitle = updateGuideRequestDto.getSubTitle();
		this.subDescription = updateGuideRequestDto.getSubDescription();
	}

	public void updateExposure(boolean isExposed) {
		this.isExposed = isExposed;
	}
}
