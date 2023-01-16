package io.finer.erp.sale.vo;

import io.finer.erp.sale.entity.SalInquiry;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SalInquiryWithOtherInfoVo extends SalInquiry {
    @ApiModelProperty(value = "物料logo")
    private String materialLogo;
}
