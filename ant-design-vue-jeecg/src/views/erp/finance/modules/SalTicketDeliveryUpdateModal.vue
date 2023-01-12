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
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status" label="驾驶员姓名">
        <a-input v-model="model.driverName" />
      </a-form-model-item>
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billingMethod" label="电话">
        <a-input v-model="model.driverTel" />
      </a-form-model-item>
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentsMethod" label="车牌号">
        <a-input v-model="model.driverCarNumber" />
      </a-form-model-item>
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentsMethod" label="身份证号码">
        <a-input v-model="model.driverIdCard" />
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
  name: 'SalTicketDeliveryModal',
  data() {
    return {
      autoExpandParent: true,
      checkStrictly: true,
      title: '订单物流修改',
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
      url: {
        submit: '/sale/salTicket/deliveryUpdate'
      },

      model: {
        ticketId: '',
        driverName: '',
        driverTel: '',
        driverCarNumber: '',
        driverIdCard: '',
      }
    }
  },
  methods: {
    show(record) {
      this.model = record
      this.visible = true
    },
    close() {
      this.reset()
      this.$emit('close')
      this.visible = false
    },
    reset() {
      this.model = {}
      this.loading = false
    },
    handleCancel() {
      this.close()
    },
    validate() {
      if (this.model.driverName === '') {
        this.$message.warning("请输入驾驶人姓名!")
        return false
      }
      if (this.model.driverTel === '') {
        this.$message.warning("请输入驾驶人电话!")
        return false
      }
      if (this.model.driverCarNumber === '') {
        this.$message.warning("请输入驾驶人车牌号!")
        return false
      }
      if (this.model.driverIdCard === '') {
        this.$message.warning("请输入驾驶人身份证号!")
        return false
      }
      return true
    },
    handleSubmit(exit) {
      if (!this.validate()) {
        return
      }
      let that = this
      const data = this.model
      that.loading = true
      console.log('请求参数：', data)
      postAction(`${this.url.submit}`, data).then(res => {
        if (res.success) {
          that.$message.success(res.message)
          that.loading = false
          if (exit) {
            that.close()
          }
        } else {
          that.$message.error(res.message)
          that.loading = false
          if (exit) {
            that.close()
          }
        }
        this.loadData()
      })
    },
    loadData() {

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