package io.finer.erp.sale.vo;

import io.finer.erp.base.entity.BasCustomer;
import io.finer.erp.sale.entity.SalInquiry;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SalInquiryVo extends BasCustomer {
    @ApiModelProperty(value = "客户关联询盘列表")
    private List<SalInquiry> salInquiryList;
}
