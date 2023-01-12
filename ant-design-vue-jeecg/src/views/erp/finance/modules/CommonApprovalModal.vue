<template>
  <a-drawer
    :title='title'
    :maskClosable='true'
    width='650'
    placement='right'
    :closable='true'
    @close='close'
    :visible='visible'
    style='overflow: auto;padding-bottom: 53px;'>

    <a-form>
      <a-form-model-item label="核批结果" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approvalResultType">
        <j-dict-select-tag v-model="approvalResultType" dictCode="x_approval_result_type" placeholder="请选择"/>
      </a-form-model-item>
      <a-form-model-item label="核批意见" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="approvalRemark">
        <a-textarea v-model="approvalRemark" rows="5"></a-textarea>
      </a-form-model-item>
    </a-form>

    <div class='drawer-bootom-button'>
      <a-popconfirm title='确定放弃编辑？' @confirm='close' okText='确定' cancelText='取消'>
        <a-button style='margin-right: .8rem'>取消</a-button>
      </a-popconfirm>
      <!--      <a-button @click='handleSubmit(false)' type='primary' :loading='loading' ghost style='margin-right: 0.8rem'>仅保存-->
      <!--      </a-button>-->
      <a-button @click='handleSubmit(true)' type='primary' :loading='loading'>保存并关闭</a-button>
    </div>

  </a-drawer>

</template>
<script>
import { postAction } from '@/api/manage'

export default {
  name: 'CommonApprovalModal',
  props: {
    ids: {
      type: Array,
      default: () => []
    },
    checkUrl: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      autoExpandParent: true,
      checkStrictly: true,
      title: '询盘审核',
      visible: false,
      loading: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      approvalRemark: '',
      approvalResultType: '',
      idList: []
    }
  },
  methods: {
    show(idList) {
      this.idList = idList
      this.visible = true
    },
    close() {
      this.reset()
      this.$emit('close')
      this.visible = false
    },
    reset() {
      this.approvalRemark = ''
      this.approvalResultType = ''
      this.idList = []
      this.loading = false
    },
    handleCancel() {
      this.close()
    },
    handleSubmit(exit) {
      let that = this

      if (that.approvalResultType === '') {
        this.$message.warning('请选择审核结果!')
        return
      }

      const formData = new FormData();
      formData.append('approvalRemark', that.approvalRemark);
      formData.append('approvalResultType', that.approvalResultType);
      formData.append('ids', that.idList.join(','));
      that.loading = true
      console.log('请求参数：', formData)
      postAction(`${this.checkUrl}`, formData).then(res => {
        if (res.success) {
          that.$message.success(res.message)
          that.loading = false
          if (exit) {
            that.close()
          }
        } else {
          that.$message.error(res.message)
          that.loading = false
          return
        }
        this.loadData()
      })
    },
    loadData() {
      if (this.idList.length === 0) {
        this.idList = this.ids
      }
    }
  },
  watch: {
    visible() {
      if (this.visible) {
        this.loadData()
      }
    }
  }
}

</script>
<style lang='less' scoped>
.drawer-bootom-button {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}

</style>