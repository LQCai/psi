<template>
  <a-form-model ref="form" :model="model" style="margin-top: 24px">
    <a-row>
      <a-col :span="12">
          <a-row>
            <a-col :span="24">
              <a-form-model-item label="备注" :labelCol="labelCol1" :wrapperCol="wrapperCol2" prop="remark">
                <a-textarea v-model="model.remark" :readOnly="disabled" rows="1" autoSize></a-textarea>
              </a-form-model-item>
            </a-col>
            <a-col :span="24">
              <a-form-model-item label="附件" :labelCol="labelCol1" :wrapperCol="wrapperCol2" prop="attachment">
                <j-upload v-model="model.attachment" :disabled="disabled"></j-upload>
              </a-form-model-item>
            </a-col>
          </a-row>
      </a-col>
    </a-row>
  </a-form-model>
</template>

<script>
  import { BillFormGridMixin } from '../mixins/BillFormMixin'

  export default {
    name: "BillFooterNotApproval",
    mixins: [BillFormGridMixin],
    props: {
      model: {
        type: Object,
        required: true
      },
      //表单禁用
      disabled: {
        type: Boolean,
        default: false,
        required: false
      },
      action: {
        type: String,
        required: true
      }
    },

  }
</script>

<style lang="less" scoped>
  @import "../../common/less/BillForm.less";
</style>
