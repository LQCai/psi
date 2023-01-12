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
      
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="no" label="订单号">
          <a-input placeholder="请输入订单号" v-model="model.no" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="noDate" label="订单日期">
          <a-date-picker v-model="model.noDate" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="srcNo" label="源单号">
          <a-input placeholder="请输入源单号" v-model="model.srcNo" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="operator" label="业务员">
          <a-input placeholder="请输入业务员" v-model="model.operator" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="customerId" label="客户">
          <a-input placeholder="请输入客户" v-model="model.customerId" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialId" label="商品id">
          <a-input placeholder="请输入商品id" v-model="model.materialId" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialAmt" label="商品金额">
          <a-input-number v-model="model.materialAmt" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="quotedAmt" label="报价金额">
          <a-input-number v-model="model.quotedAmt" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialCount" label="商品数量">
          <a-input-number v-model="model.materialCount" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="totalAmt" label="总金额">
          <a-input-number v-model="model.totalAmt" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark" label="备注">
          <a-input placeholder="请输入备注" v-model="model.remark" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status" label="状态(0 = 待审核, 1 = 待发货, 2 = 已发货)">
          <a-input-number v-model="model.status" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billingMethod" label="结算方式(1 = 货到付款, 2 = 款到发货)">
          <a-input-number v-model="model.billingMethod" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentsMethod" label="付款方式(1 = 汇票, 2 = 汇款, 3 = 现金)">
          <a-input-number v-model="model.paymentsMethod" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isInvoices" label="是否开票(1 = 是, 2 = 否)">
          <a-input-number v-model="model.isInvoices" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approver" label="审核人">
          <a-input placeholder="请输入审核人" v-model="model.approver" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approvalResultType" label="审核结果">
          <a-input placeholder="请输入审核结果" v-model="model.approvalResultType" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approvalRemark" label="审核意见">
          <a-input placeholder="请输入审核意见" v-model="model.approvalRemark" />
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
    name: "SalTicketModal",
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

        confirmLoading: false,
        validatorRules:{
        no:[{ required: true, message: '请输入订单号!' }],
        noDate:[{ required: true, message: '请输入订单日期!' }],
        srcNo:[{ required: true, message: '请输入源单号!' }],
        materialId:[{ required: true, message: '请输入商品id!' }],
        materialAmt:[{ required: true, message: '请输入商品金额!' }],
        quotedAmt:[{ required: true, message: '请输入报价金额!' }],
        materialCount:[{ required: true, message: '请输入商品数量!' }],
        totalAmt:[{ required: true, message: '请输入总金额!' }],
        status:[{ required: true, message: '请输入状态(0 = 待审核, 1 = 待发货, 2 = 已发货)!' }],
        billingMethod:[{ required: true, message: '请输入结算方式(1 = 货到付款, 2 = 款到发货)!' }],
        paymentsMethod:[{ required: true, message: '请输入付款方式(1 = 汇票, 2 = 汇款, 3 = 现金)!' }],
        isInvoices:[{ required: true, message: '请输入是否开票(1 = 是, 2 = 否)!' }],
        },
        url: {
          add: "/sale/salTicket/add",
          edit: "/sale/salTicket/edit",
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