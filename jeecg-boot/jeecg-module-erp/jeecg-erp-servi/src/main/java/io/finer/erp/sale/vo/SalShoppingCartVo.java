package io.finer.erp.sale.vo;

import io.finer.erp.sale.entity.SalShoppingCart;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class SalShoppingCartVo extends SalShoppingCart {
    @Excel(
            name = "核批结果类型",
            width = 15.0D,
            dicCode = "x_approval_result_type"
    )
    @Dict(
            dicCode = "x_approval_result_type"
    )
    @ApiModelProperty(value = "核批结果类型")
    private String inquiryApprovalResultType;
}
