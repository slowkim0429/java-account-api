package com.virnect.account.adapter.inbound.dto.request.validate;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.virnect.account.domain.enumclass.ItemType;

public class ItemTypeSubsetValidator implements ConstraintValidator<ItemTypeSubset, String> {
	private ItemType[] anyOfItemType;

	@Override
	public void initialize(ItemTypeSubset constraintAnnotation) {
		this.anyOfItemType = constraintAnnotation.anyOf();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return Arrays.stream(anyOfItemType).anyMatch(itemType -> itemType.name().equals(value));
	}
}
