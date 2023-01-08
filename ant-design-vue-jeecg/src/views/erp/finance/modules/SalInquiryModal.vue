<template>
  <a-drawer
    :title="title"
    :width="800"
    placement="right"
    :closable="false"
    @close="close"
    :visible="visible">

    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" :model="model" :rules="validatorRules">

<!--        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billNo" label="单据编号">-->
<!--          <a-input placeholder="请输入单据编号" v-model="model.billNo" />-->
<!--        </a-form-model-item>-->
<!--        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billDate" label="单据日期">-->
<!--          <a-date-picker showTime valueFormat='YYYY-MM-DD HH:mm:ss' v-model="model.billDate" />-->
<!--        </a-form-model-item>-->
<!--        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="subject" label="subject">-->
<!--          <a-input placeholder="请输入subject" v-model="model.subject" />-->
<!--        </a-form-model-item>-->
        <a-form-model-item label="业务员" :labelCol="labelCol3" :wrapperCol="wrapperCol3" prop="operator" ref="operatorFmi">
          <a-tooltip placement="bottom">
            <j-select-user-by-dep v-model="model.operator" :multi="false"
                                  @change="val =>{this.resetSrc(); this.onOperatorChange(val); }"/>
          </a-tooltip>
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId" label="客户">
          <j-search-select-tag v-model="model.customerId" :async="true" dict="bas_customer,aux_name,id" placeholder="请选择"/>
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialId" label="商品">
          <j-search-select-tag v-model="model.materialId" :async="true" dict="bas_material,aux_name,id" placeholder="请选择"/>
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="quotedAmt" label="报价金额">
          <a-input-number v-model="model.quotedAmt" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialAmt" label="商品金额">
          <a-input-number v-model="model.materialAmt" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialCount" label="商品数量">
          <a-input-number v-model="model.materialCount" />
        </a-form-model-item>
<!--        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="attachment" label="附件">-->
<!--          <a-input placeholder="请输入附件" v-model="model.attachment" />-->
<!--        </a-form-model-item>-->
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark" label="备注">
          <a-input placeholder="请输入备注" v-model="model.remark" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billStage" label="单据阶段">
          <a-input placeholder="请输入单据阶段" v-model="model.billStage" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approver" label="审核人">
          <a-input placeholder="请输入审核人" v-model="model.approver" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="intention" label="下单意向">
          <a-input placeholder="请输入下单意向" v-model="model.intention" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approvalResultType" label="核批结果类型">
          <a-input placeholder="请输入核批结果类型" v-model="model.approvalResultType" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approvalRemark" label="核批意见">
          <a-input placeholder="请输入核批意见" v-model="model.approvalRemark" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="sysOrgCode" label="创建部门">
          <a-input placeholder="请输入创建部门" v-model="model.sysOrgCode" />
        </a-form-model-item>

      </a-form-model>
    </a-spin>

    <div class="drawer-bootom-button">
      <a-button type="primary" @click="handleOk">确定</a-button>
      <a-button type="primary" @click="handleCancel">取消</a-button>
    </div>
  </a-drawer>
</template>

<script>
import { httpAction } from '@/api/manage'
import moment from "moment"

export default {
  name: "SalInquiryModal",
  data () {
    return {
      title:"操作",
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      labelCol3: {span: 6},
      wrapperCol3: {span: 18},

      confirmLoading: false,
      validatorRules:{
        billNo:[{ required: true, message: '请输入单据编号!' }],
        billDate:[{ required: true, message: '请输入单据日期!' }],
        materialId:[{ required: true, message: '请输入商品id!' }],
        invoiceType:[{ required: true, message: '请输入发票类型!' }],
        quotedAmt:[{ required: true, message: '请输入报价金额!' }],
        materialAmt:[{ required: true, message: '请输入商品金额!' }],
        materialCount:[{ required: true, message: '请输入商品数量!' }],
        isEffective:[{ required: true, message: '请输入是否生效!' }],
        isClosed:[{ required: true, message: '请输入已关闭!' }],
        isVoided:[{ required: true, message: '请输入是否作废!' }],
      },
      url: {
        add: "/sale/salInquiry/add",
        edit: "/sale/salInquiry/edit",
      },
    }
  },
  created () {
  },
  methods: {
    add () {
      //初始化默认值
      this.edit({});
    },
    edit (record) {
      this.model = Object.assign({}, record);
      this.visible = true;
    },
    close () {
      this.$emit('close');
      this.visible = false;
      this.$refs.form.clearValidate();
    },
    handleOk () {
      const that = this;
      // 触发表单验证
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true;
          let httpurl = '';
          let method = '';
          if(!this.model.id){
            httpurl+=this.url.add;
            method = 'post';
          }else{
            httpurl+=this.url.edit;
            method = 'put';
          }
          httpAction(httpurl,this.model,method).then((res)=>{
            if(res.success){
              that.$message.success(res.message);
              that.$emit('ok');
            }else{
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
            that.close();
          })
        }else{
          return false;
        }
      })
    },
    handleCancel () {
      this.close()
    },


  }
}
</script>

<style lang="less" scoped>
/**Button按钮间距*/
.ant-btn {
  margin-left: 30px;
  margin-bottom: 30px;
  float: right;
}
/**抽屉按钮样式*/
.drawer-bootom-button {
  position: absolute;
  bottom: -8px;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}
</style>