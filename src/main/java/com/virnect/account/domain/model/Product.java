package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ProductType;

@Entity
@Getter
@Audited
@Table(name = "products",
	   indexes = {
		   @Index(name = "idx_product_type", columnList = "product_type"),
		   @Index(name = "idx_status", columnList = "status"),
		   @Index(name = "idx_created_by", columnList = "created_by"),
		   @Index(name = "idx_updated_by", columnList = "updated_by"),
	   }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	@Size(max = 100)
	private String name;

	@Column(name = "product_type", nullable = false, length = 100)
	@Enumerated(EnumType.STRING)
	private ProductType productType;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private ApprovalStatus status = ApprovalStatus.REGISTER;

	@Builder
	public Product(ProductType productType, String name) {
		this.productType = productType;
		this.name = name;
	}

	public static Product updateProduct(String name, Product product) {
		product.name = name;

		return product;
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}
}

